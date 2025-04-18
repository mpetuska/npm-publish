package dev.petuska.npm.publish.config

import dev.petuska.npm.publish.extension.NpmPublishExtension
import dev.petuska.npm.publish.extension.domain.NpmDependency
import dev.petuska.npm.publish.extension.domain.json.PackageJson
import dev.petuska.npm.publish.task.NpmAssembleTask
import dev.petuska.npm.publish.util.*
import dev.petuska.npm.publish.util.sysProjectEnvPropertyConvention
import dev.petuska.npm.publish.util.toCamelCase
import dev.petuska.npm.publish.util.unsafeCast
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.Copy
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinJsCompilation
import org.jetbrains.kotlin.gradle.targets.js.dsl.KotlinJsBinaryMode
import org.jetbrains.kotlin.gradle.targets.js.dsl.KotlinJsTargetDsl
import org.jetbrains.kotlin.gradle.targets.js.ir.JsIrBinary
import org.jetbrains.kotlin.gradle.targets.js.ir.KotlinJsIrTarget
import org.jetbrains.kotlin.gradle.targets.js.ir.Library
import org.jetbrains.kotlin.gradle.targets.js.npm.NpmDependency.Scope.*
import org.jetbrains.kotlin.gradle.targets.js.npm.PublicPackageJsonTask
import org.jetbrains.kotlin.gradle.targets.js.npm.npmProject
import org.jetbrains.kotlin.gradle.tasks.Kotlin2JsCompile
import org.jetbrains.kotlin.gradle.utils.named
import java.io.File
import org.jetbrains.kotlin.gradle.targets.js.npm.NpmDependency as KJsNpmDependency

@Suppress("LongMethod")
internal fun Project.configure(target: KotlinJsTargetDsl): Unit = with(PluginLogger.wrap(logger)) {
  if (target !is KotlinJsIrTarget) {
    warn { "${target.name} Kotlin/JS target is not using IR compiler - skipping..." }
  } else {
    extensions.getByType(NpmPublishExtension::class.java).packages.register(target.name) { pkg ->
      val binary = provider<JsIrBinary> {
        when (val it = target.binaries.find { it.mode == KotlinJsBinaryMode.PRODUCTION }) {
          null -> null
          is Library -> it
          // Kotlin 2.1.20 introduces LibraryWasm and ExecutableWasm.
          else -> {
            warn {
              """
                Kotlin/JS ${it::class.simpleName} binaries are not valid npm package targets.
                Consider switching to Kotlin/JS library binary:
                  kotlin {
                    js(IR) {
                      binaries.library()
                    }
                  }
              """.trimIndent()
            }
            null
          }
        }
      }
      val compileKotlinTask = binary.flatMap<Kotlin2JsCompile>(JsIrBinary::linkTask)
      val publicPackageJsonTask = binary.flatMap {
        tasks.named<PublicPackageJsonTask>(it.compilation.npmProject.publicPackageJsonTaskName)
      }
      val mainCompilation = target.compilations.named("main")
      val processResourcesTask = mainCompilation.flatMap {
        tasks.named<Copy>(it.processResourcesTaskName)
      }

      val outputFile = binary.flatMap(JsIrBinary::mainFile)
      val typesFile = outputFile.map {
        it.asFile.parentFile.resolve("${it.asFile.nameWithoutExtension}.d.ts")
      }

      tasks.named(assembleTaskName(pkg.name), NpmAssembleTask::class.java) {
        it.dependsOn(compileKotlinTask, processResourcesTask, publicPackageJsonTask)
        it.dependencySourcePackageJson.set(
          publicPackageJsonTask.map(PublicPackageJsonTask::packageJsonFile).let(layout::file)
        )
      }

      pkg.main.convention(
        sysProjectEnvPropertyConvention(
          pkg.prefix + "main",
          outputFile.map { it.asFile.name }.orElse(pkg.packageJson.flatMap(PackageJson::main))
        )
      )
      pkg.types.convention(
        sysProjectEnvPropertyConvention(
          pkg.prefix + "types",
          typesFile.map<String> { it.takeIf(File::exists)?.name.unsafeCast() }
            .orElse(pkg.packageJson.flatMap(PackageJson::types))
        )
      )
      pkg.dependencies.addAllLater(resolveDependencies(target.name, binary))
      pkg.files { files ->
        files.from(outputFile.map { it.asFile.getParentFile() })
        files.from(processResourcesTask.map(Copy::getDestinationDir))
      }
    }
    info { "Automatically registered [${target.name}] NpmPackage for [${target.name}] Kotlin/JS target" }
  }
}

private fun KotlinJsCompilation.relatedConfigurationNames() = listOfNotNull(
  apiConfigurationName,
  implementationConfigurationName,
  compileOnlyConfigurationName,
  runtimeOnlyConfigurationName,
  compileDependencyConfigurationName,
  runtimeDependencyConfigurationName,
)

private fun Project.resolveDependencies(
  targetName: String,
  binary: Provider<JsIrBinary>
): Provider<List<NpmDependency>> = binary.map { bin ->
  bin.compilation
    .relatedConfigurationNames()
    .flatMap { conf ->
      sequenceOf(
        conf,
        "${targetName}Main${conf.substringAfter("${targetName}Compilation").capitalised()}",
        conf.substringAfter("compilation").toCamelCase(true),
      )
        .mapNotNull(configurations::findByName)
        .flatMap(Configuration::getDependencies)
        .filterIsInstance<KJsNpmDependency>()
        .distinct()
        .map { dependency ->
          objects.newInstance(NpmDependency::class.java, dependency.name).apply {
            type.set(
              when (dependency.scope) {
                NORMAL -> NpmDependency.Type.NORMAL
                DEV -> NpmDependency.Type.DEV
                OPTIONAL -> NpmDependency.Type.OPTIONAL
                PEER -> NpmDependency.Type.PEER
              }
            )
            version.set(dependency.version)
          }
        }.toList()
    }
}
