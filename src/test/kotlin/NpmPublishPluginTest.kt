/*
 * This Kotlin source file was generated by the Gradle 'init' task.
 */
package dev.petuska.npm.publish

import dev.petuska.npm.publish.dsl.NpmPublishExtension.Companion.EXTENSION_NAME
import io.kotest.core.spec.style.WordSpec
import io.kotest.core.spec.style.scopes.WordSpecTerminalContext
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.gradle.testfixtures.ProjectBuilder

class NpmPublishPluginTest : WordSpec(
  {
    "Using the Plugin ID" should {
      "Apply the Plugin" {
        val project = ProjectBuilder.builder().build()
        project.pluginManager.apply("dev.petuska.npm.publish")

        project.plugins.getPlugin("dev.petuska.npm.publish") shouldNotBe null
      }
    }

    fun WordSpecTerminalContext.registeringTest(kotlinPlugin: String, registers: Boolean) {
      val project = ProjectBuilder.builder().build()
      project.pluginManager.apply("dev.petuska.npm.publish")
      project.pluginManager.apply(kotlinPlugin)

      (
        try {
          project.npmPublishing()
        } catch (e: Exception) {
          println(e)
          e.printStackTrace()
          null
        } != null
        ) shouldBe registers
    }

    "Applying the Plugin" should {
      "Register the '$EXTENSION_NAME' extension if JS plugin is applied" {
        registeringTest("org.jetbrains.kotlin.js", true)
      }
      "Register the '$EXTENSION_NAME' extension if MPP plugin is applied" {
        registeringTest("org.jetbrains.kotlin.multiplatform", true)
      }
    }
  }
)
