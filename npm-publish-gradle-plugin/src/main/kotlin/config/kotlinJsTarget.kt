package dev.petuska.npm.publish.config

import dev.petuska.npm.publish.extension.domain.*
import dev.petuska.npm.publish.extension.domain.NpmDependency.Type
import dev.petuska.npm.publish.extension.domain.json.PackageJson
import dev.petuska.npm.publish.util.ProjectEnhancer
import dev.petuska.npm.publish.util.unsafeCast
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.Copy
import org.jetbrains.kotlin.gradle.targets.js.dsl.KotlinJsBinaryMode
import org.jetbrains.kotlin.gradle.targets.js.dsl.KotlinJsTargetDsl
import org.jetbrains.kotlin.gradle.targets.js.ir.Executable
import org.jetbrains.kotlin.gradle.targets.js.ir.JsIrBinary
import org.jetbrains.kotlin.gradle.targets.js.ir.KotlinJsIrTarget
import org.jetbrains.kotlin.gradle.targets.js.ir.Library
import org.jetbrains.kotlin.gradle.targets.js.npm.NpmDependency.Scope.DEV
import org.jetbrains.kotlin.gradle.targets.js.npm.NpmDependency.Scope.NORMAL
import org.jetbrains.kotlin.gradle.targets.js.npm.NpmDependency.Scope.OPTIONAL
import org.jetbrains.kotlin.gradle.targets.js.npm.NpmDependency.Scope.PEER
import org.jetbrains.kotlin.gradle.tasks.Kotlin2JsCompile
import java.io.File
import org.jetbrains.kotlin.gradle.targets.js.npm.NpmDependency as KJsNpmDependency

internal fun ProjectEnhancer.configure(target: KotlinJsTargetDsl) {
  if (target !is KotlinJsIrTarget) {
    info { "${target.name} Kotlin/JS target is not using IR compiler - skipping..." }
  } else {

    extension.packages.register(target.name) { pkg ->
      val binary = provider<JsIrBinary> {
        when (val it = target.binaries.find { it.mode == KotlinJsBinaryMode.PRODUCTION }) {
          is Library -> it
          is Executable -> error(
            "Kotlin/JS executable binaries are not valid npm package targets. " +
              "Consider switching to Kotlin/JS library binary:\n" + """
            kotlin {
              js(IR) {
                binaries.library()
              }
            }
            """.trimIndent()
          )
          null -> null
          !is JsIrBinary -> error(
            "Legacy binaries are no longer supported. " +
              "Please consider switching to the new Kotlin/JS IR compiler backend"
          )
          else -> error("Unrecognised Kotlin/JS binary type: ${it::class.java.name}")
        }
      }
      val compileKotlinTask = binary.flatMap<Kotlin2JsCompile>(JsIrBinary::linkTask)
      val processResourcesTask = target.compilations.named("main").flatMap {
        tasks.named(it.processResourcesTaskName, Copy::class.java)
      }
      val outputFile = compileKotlinTask.flatMap(Kotlin2JsCompile::outputFileProperty)
      val typesFile = outputFile.map { File(it.parentFile, "${it.nameWithoutExtension}.d.ts") }

      tasks.named(assembleTaskName(pkg.name)) {
        it.dependsOn(compileKotlinTask, processResourcesTask)
      }

      pkg.main.sysProjectEnvPropertyConvention(
        pkg.prefix + "main",
        outputFile.map(File::getName).orElse(pkg.packageJson.flatMap(PackageJson::main))
      )
      pkg.types.sysProjectEnvPropertyConvention(
        pkg.prefix + "types",
        typesFile.map<String> { it.takeIf(File::exists)?.name.unsafeCast() }
          .orElse(pkg.packageJson.flatMap(PackageJson::types))
      )
      pkg.dependencies.addAllLater(resolveDependencies(target.name, binary))
      pkg.files { files ->
        files.from(outputFile.map(File::getParentFile))
//        files.from(typesFile)
        files.from(processResourcesTask.map(Copy::getDestinationDir))
      }
    }
    info { "Automatically registered [${target.name}] NpmPackage for [${target.name}] Kotlin/JS target" }
  }
}

private fun ProjectEnhancer.resolveDependencies(
  targetName: String,
  binary: Provider<JsIrBinary>
) = binary.map { bin ->
  bin.compilation.relatedConfigurationNames.flatMap { conf ->
    val mainName = "${targetName}Main${conf.substringAfter(targetName)}"
    val normDeps = configurations.findByName(conf)?.dependencies?.toSet() ?: setOf()
    val mainDeps = configurations.findByName(mainName)?.dependencies?.toSet() ?: setOf()
    (normDeps + mainDeps).filterIsInstance<KJsNpmDependency>()
  }
}.map { dependencies ->
  dependencies.map { dependency ->
    objects.newInstance(NpmDependency::class.java, dependency.name).apply {
      type.set(
        when (dependency.scope) {
          NORMAL -> Type.NORMAL
          DEV -> Type.DEV
          OPTIONAL -> Type.OPTIONAL
          PEER -> Type.PEER
        }
      )
      version.set(dependency.version)
    }
  }
}
