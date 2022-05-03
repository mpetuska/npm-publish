package dev.petuska.npm.publish

import dev.petuska.npm.publish.test.util.FTest
import dev.petuska.npm.publish.test.util.invoke
import io.kotest.matchers.shouldNotBe
import org.junit.jupiter.api.Test

class NpmPublishPluginFTest : FTest() {
  @Test
  fun `can apply the plugin`() {
    val result = executeBuild("tasks", init = { dir ->
      dir.resolve("src/main/kotlin/main.kt").apply { parentFile.mkdirs() }.writeText("fun main() {}")
    }) {
      plugins {
        kotlinMultiplatform()
      }

      "kotlin" {
        "js(IR)" {
          +"browser()"
          +"binaries.library()"
        }
        "sourceSets" {
          """named("jsMain")""" {
            "dependencies" {
              +"""api("dev.petuska:kon:1.1.4")"""
            }
          }
        }
      }

      "npmPublish" {
        +"organization.set(group.toString())"
        "packages" {
          """named("js")""" {
            "dependencies" {
              +"""normal("axios", "*")"""
            }
          }
        }
      }
    }
    result shouldNotBe null
  }
}
