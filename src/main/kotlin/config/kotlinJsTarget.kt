package dev.petuska.npm.publish.config

import dev.petuska.npm.publish.extension.domain.*
import dev.petuska.npm.publish.extension.domain.NpmDependency.Scope
import dev.petuska.npm.publish.util.*
import org.gradle.api.provider.*
import org.gradle.api.tasks.*
import org.jetbrains.kotlin.gradle.targets.js.dsl.*
import org.jetbrains.kotlin.gradle.targets.js.ir.*
import org.jetbrains.kotlin.gradle.targets.js.npm.NpmDependency.Scope.DEV
import org.jetbrains.kotlin.gradle.targets.js.npm.NpmDependency.Scope.NORMAL
import org.jetbrains.kotlin.gradle.targets.js.npm.NpmDependency.Scope.OPTIONAL
import org.jetbrains.kotlin.gradle.targets.js.npm.NpmDependency.Scope.PEER
import org.jetbrains.kotlin.gradle.tasks.*
import java.io.*
import org.jetbrains.kotlin.gradle.targets.js.npm.NpmDependency as KJsNpmDependency

internal fun ProjectEnhancer.configure(target: KotlinJsTargetDsl) {
  extension.packages.register(target.name) { pkg ->
    val binary = provider<JsBinary> { target.binaries.find { it.mode == KotlinJsBinaryMode.PRODUCTION } }
    configureDependencies(pkg, target, binary)
    val compileKotlinTask = binary.flatMap<Kotlin2JsCompile> {
      when (it) {
        is Library -> it.linkTask
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
        !is JsIrBinary -> error(
          "${it::class.java} legacy binaries are no longer supported. " +
            "Please consider switching to the new Kotlin/JS IR compiler backend"
        )
        else -> error("Unrecognised Kotlin/JS binary type: ${it::class.java}")
      }
    }
    val processResourcesTask = target.compilations.named("main").flatMap {
      tasks.named(it.processResourcesTaskName, Copy::class.java)
    }
    tasks.named(assembleTaskName(pkg.name)) {
      it.dependsOn(compileKotlinTask, processResourcesTask)
    }
    val outputFile = compileKotlinTask.flatMap(Kotlin2JsCompile::outputFileProperty)
    val typesFile = outputFile.map { File(it.parentFile, "${it.nameWithoutExtension}.d.ts") }
    pkg.main.set(outputFile.map(File::getName))
    pkg.types.set(typesFile.map { it.takeIf(File::exists)?.name.unsafeCast() })
    pkg.files { files ->
      files.from(outputFile)
      files.from(typesFile)
      files.from(processResourcesTask.map(Copy::getDestinationDir))
    }
  }
}

private fun ProjectEnhancer.configureDependencies(
  pkg: NpmPackage,
  target: KotlinJsTargetDsl,
  binary: Provider<JsBinary>
) {
  val dependencies = binary.map { bin ->
    bin.compilation.relatedConfigurationNames.flatMap { conf ->
      val mainName = "${target.name}Main${conf.substringAfter(target.name)}"
      val normDeps = configurations.findByName(conf)?.dependencies?.toSet() ?: setOf()
      val mainDeps = configurations.findByName(mainName)?.dependencies?.toSet() ?: setOf()
      (normDeps + mainDeps).filterIsInstance<KJsNpmDependency>()
    }
  }.map { dependencies ->
    dependencies.map { dependency ->
      objects.newInstance(NpmDependency::class.java, dependency.name).apply {
        scope.set(
          when (dependency.scope) {
            NORMAL -> Scope.NORMAL
            DEV -> Scope.DEV
            OPTIONAL -> Scope.OPTIONAL
            PEER -> Scope.PEER
          }
        )
        version.set(dependency.version)
      }
    }
  }

  pkg.dependencies.addAllLater(dependencies)
}
