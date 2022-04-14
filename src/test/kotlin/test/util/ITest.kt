package dev.petuska.npm.publish.test.util

import dev.petuska.npm.publish.*
import dev.petuska.npm.publish.extension.*
import io.kotest.core.*
import io.kotest.core.spec.style.*
import io.kotest.engine.spec.*
import org.gradle.api.*
import org.gradle.testfixtures.*
import org.jetbrains.kotlin.gradle.dsl.*
import org.jetbrains.kotlin.gradle.plugin.*
import java.io.*

abstract class ITest : StringSpec() {
  init {
    tags(Tag("integration"))
  }

  protected val Project.npmPublish: NpmPublishExtension
    get() = extensions.findByType(NpmPublishExtension::class.java)
      ?: throw IllegalStateException("${NpmPublishExtension.NAME} is not registered or of incorrect type")

  protected fun projectOf(
    init: (projectDir: File) -> Unit = {},
    action: Project.(gradleUserHome: File) -> Unit
  ): Project {
    val gradleUserHome = tempdir()
    return ProjectBuilder.builder()
      .withProjectDir(tempdir().also(init))
      .withName("test-project")
      .withGradleUserHomeDir(gradleUserHome)
      .build()
      .also {
        it.plugins.apply(NpmPublishPlugin::class.java)
        it.npmPublish.registries.register("npmjs") { registry ->
          registry.uri.set(it.uri("https://npmjs.org"))
        }
        it.action(gradleUserHome)
      }
  }

  protected fun kJsProjectOf(compiler: KotlinJsCompilerType, init: (projectDir: File) -> Unit = {}): Project {
    return projectOf(init) {
      plugins.apply("org.jetbrains.kotlin.js")
      plugins.withId("org.jetbrains.kotlin.js") {
        extensions.findByType(KotlinJsProjectExtension::class.java)?.apply {
          js(compiler) {
            browser()
            if (compiler == KotlinJsCompilerType.IR) binaries.library()
            compilations.named("main") {
              it.dependencies {
                api(npm("axios", "*"))
              }
            }
          }
        }
      }
    }
  }

  protected fun kMppProjectOf(compiler: KotlinJsCompilerType, init: (projectDir: File) -> Unit = {}): Project {
    return projectOf(init) {
      plugins.apply("org.jetbrains.kotlin.multiplatform")
      plugins.withId("org.jetbrains.kotlin.multiplatform") {
        extensions.findByType(KotlinMultiplatformExtension::class.java)?.apply {
          js(compiler) {
            browser()
            if (compiler == KotlinJsCompilerType.IR) binaries.library()
            compilations.named("main") {
              it.dependencies {
                api(npm("axios", "*"))
              }
            }
          }
        }
      }
    }
  }
}
