package dev.petuska.npm.publish.test.util

import dev.petuska.npm.publish.NpmPublishPlugin
import dev.petuska.npm.publish.extension.NpmPublishExtension
import org.gradle.api.Project
import org.gradle.api.internal.project.ProjectInternal
import org.gradle.testfixtures.ProjectBuilder
import org.jetbrains.kotlin.gradle.dsl.KotlinJsProjectExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinJsCompilerType
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Tags
import org.junit.jupiter.api.io.TempDir
import java.io.File

@Tags(Tag("integration"))
@Suppress("UnnecessaryAbstractClass")
abstract class ITest {
  class TestProject(project: Project) : Project by project {
    val projectInternal = project as ProjectInternal
    operator fun <T> T.invoke(action: T.() -> Unit) {
      apply(action)
    }

    operator fun <R> invoke(action: TestProject.() -> R) = run(action)

    val npmPublish by lazy { extensions.getByType(NpmPublishExtension::class.java) }
    val kotlinMpp by lazy { extensions.getByType(KotlinMultiplatformExtension::class.java) }
    val kotlinJs by lazy { extensions.getByType(KotlinJsProjectExtension::class.java) }
    val targetName = "js"
    val customPackageName = "testCustom"
  }

  @TempDir
  private lateinit var tempDir: File

  protected fun projectOf(
    init: (projectDir: File) -> Unit = {},
    properties: Map<String, Any> = mapOf(),
    action: TestProject.(gradleUserHome: File) -> Unit = {}
  ): TestProject {
    val gradleUserHome = tempDir.resolve("gradleHome").also(File::mkdirs)
    val projectDir = tempDir.resolve("gradleHome").also(File::mkdirs)
    return ProjectBuilder.builder().withProjectDir(projectDir.also(init)).withName("test-project")
      .withGradleUserHomeDir(gradleUserHome).build().let(::TestProject).also {
        properties.forEach { (k, v) ->
          it.extensions.extraProperties.set(k, v)
        }
        it.plugins.apply(NpmPublishPlugin::class.java)
        it.npmPublish.registries.register("npmjs") { registry ->
          registry.uri.set(it.uri("https://npmjs.org"))
        }
        it.action(gradleUserHome)
      }
  }

  protected fun kJsProjectOf(
    compiler: KotlinJsCompilerType,
    init: (projectDir: File) -> Unit = {},
    properties: Map<String, Any> = mapOf(),
  ): TestProject {
    return projectOf(init, properties + (KotlinJsCompilerType.jsCompilerProperty to compiler.name.lowercase())) {
      plugins.apply("org.jetbrains.kotlin.js")
      kotlinJs {
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

  protected fun kMppProjectOf(
    compiler: KotlinJsCompilerType,
    init: (projectDir: File) -> Unit = {},
    properties: Map<String, Any> = mapOf(),
  ): TestProject {
    return projectOf(init, properties) {
      plugins.apply("org.jetbrains.kotlin.multiplatform")
      kotlinMpp {
        js(targetName, compiler) {
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
