package dev.petuska.npm.publish.config

import com.google.gson.Gson
import com.google.gson.JsonObject
import dev.petuska.npm.publish.extension.domain.NpmDependency
import dev.petuska.npm.publish.extension.domain.json.PackageJson
import dev.petuska.npm.publish.util.ProjectEnhancer
import dev.petuska.npm.publish.util.toCamelCase
import dev.petuska.npm.publish.util.unsafeCast
import org.gradle.api.artifacts.Configuration
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.Copy
import org.gradle.configurationcache.extensions.capitalized
import org.jetbrains.kotlin.gradle.targets.js.dsl.KotlinJsBinaryMode
import org.jetbrains.kotlin.gradle.targets.js.dsl.KotlinJsTargetDsl
import org.jetbrains.kotlin.gradle.targets.js.ir.Executable
import org.jetbrains.kotlin.gradle.targets.js.ir.JsIrBinary
import org.jetbrains.kotlin.gradle.targets.js.ir.KotlinJsIrTarget
import org.jetbrains.kotlin.gradle.targets.js.ir.Library
import org.jetbrains.kotlin.gradle.targets.js.npm.NpmDependency.Scope.*
import org.jetbrains.kotlin.gradle.targets.js.npm.PublicPackageJsonTask
import org.jetbrains.kotlin.gradle.targets.js.npm.npmProject
import org.jetbrains.kotlin.gradle.tasks.Kotlin2JsCompile
import java.io.File
import org.jetbrains.kotlin.gradle.targets.js.npm.NpmDependency as KJsNpmDependency

@Suppress("LongMethod")
internal fun ProjectEnhancer.configure(target: KotlinJsTargetDsl) {
  if (target !is KotlinJsIrTarget) {
    warn { "${target.name} Kotlin/JS target is not using IR compiler - skipping..." }
  } else {
    extension.packages.register(target.name) { pkg ->
      val binary = provider<JsIrBinary> {
        when (val it = target.binaries.find { it.mode == KotlinJsBinaryMode.PRODUCTION }) {
          is Library -> it
          is Executable -> {
            warn {
              """
                Kotlin/JS executable binaries are not valid npm package targets.
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

          null -> null
          !is JsIrBinary -> error(
            "Legacy binaries are no longer supported. " +
              "Please consider switching to the new Kotlin/JS IR compiler backend"
          )

          else -> error("Unrecognised Kotlin/JS binary type: ${it::class.java.name}")
        }
      }
      val compileKotlinTask = binary.flatMap<Kotlin2JsCompile>(JsIrBinary::linkTask)
      val publicPackageJsonTask = binary
        .flatMap { tasks.named(it.compilation.npmProject.publicPackageJsonTaskName, PublicPackageJsonTask::class.java) }
      val processResourcesTask = target.compilations.named("main").flatMap {
        tasks.named(it.processResourcesTaskName, Copy::class.java)
      }
      val outputFile = compileKotlinTask.flatMap(Kotlin2JsCompile::outputFileProperty)
      val typesFile = outputFile.map { File(it.parentFile, "${it.nameWithoutExtension}.d.ts") }

      pkg.assembleTask.configure {
        it.dependsOn(compileKotlinTask, processResourcesTask, publicPackageJsonTask)
        it.extraDependencies.addAll(resolveDependencies(publicPackageJsonTask))
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
      pkg.dependencies.addAllLater(resolveDependencies(publicPackageJsonTask))
      pkg.files { files ->
        files.from(outputFile.map(File::getParentFile))
        files.from(processResourcesTask.map(Copy::getDestinationDir))
      }
    }
    info { "Automatically registered [${target.name}] NpmPackage for [${target.name}] Kotlin/JS target" }
  }
}

private fun ProjectEnhancer.resolveDependencies(publicPackageJsonTask: Provider<PublicPackageJsonTask>) =
  publicPackageJsonTask.map {
    val json = Gson().fromJson(it.packageJsonFile.readText(), JsonObject::class.java)
    fun JsonObject.parse(scope: NpmDependency.Type) = asMap().mapValues { (_, v) -> v.asString }.map { (n, v) ->
      objects.newInstance(NpmDependency::class.java, n).apply {
        type.set(scope)
        version.set(v)
      }
    }
    json.getAsJsonObject("dependencies").parse(NpmDependency.Type.NORMAL) +
      json.getAsJsonObject("peerDependencies").parse(NpmDependency.Type.PEER) +
      json.getAsJsonObject("optionalDependencies").parse(NpmDependency.Type.OPTIONAL)
  }

private fun ProjectEnhancer.resolveDependencies(
  targetName: String,
  binary: Provider<JsIrBinary>
): Provider<List<NpmDependency>> = binary.map { bin ->
  bin.compilation.relatedConfigurationNames.flatMap { conf ->
    listOf(
      conf,
      "${targetName}Main${conf.substringAfter("${targetName}Compilation").capitalized()}",
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
      }
  }
}
