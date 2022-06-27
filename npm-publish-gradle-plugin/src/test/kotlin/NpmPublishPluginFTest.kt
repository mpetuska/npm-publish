package dev.petuska.npm.publish

import dev.petuska.npm.publish.test.util.FTest
import dev.petuska.npm.publish.test.util.invoke
import io.kotest.matchers.shouldNotBe
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

class NpmPublishPluginFTest : FTest() {
  @Test
  @Disabled
  fun `can apply the plugin`() {
    val result = executeBuild("assemble", init = { dir ->
      dir.resolve("src/jsMain/kotlin/main.kt").apply { parentFile.mkdirs() }.writeText("fun main() {}")
    }) {
      plugins {
        kotlinMultiplatform("1.7.0")
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
