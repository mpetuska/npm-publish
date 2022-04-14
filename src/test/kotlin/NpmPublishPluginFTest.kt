package dev.petuska.npm.publish

import dev.petuska.npm.publish.test.util.*
import io.kotest.matchers.*

class NpmPublishPluginFTest : FTest() {
  init {
    "can apply the plugin" {
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
}
