package lt.petuska.npm.publish

import io.kotest.core.spec.style.WordSpec
import lt.petuska.npm.publish.util.gradleExec
import lt.petuska.npm.publish.util.kotlinJs
import lt.petuska.npm.publish.util.kotlinMpp
import lt.petuska.npm.publish.util.npmRepository
import lt.petuska.npm.publish.util.publishTaskName

class PublishTaskFTest : WordSpec(
  {
    "Running publishNpmPublication [JS]" should {
      "succeed [JS]" {
        gradleExec(
          {
            it.resolve("src/main/kotlin/index.kt").apply {
              parentFile.mkdirs()
              writeText("fun main(){}")
            }
          },
          {
            kotlinJs {
              "js" {
                "browser"()
              }
              "sourceSets" {
                "named"("main") {
                  "dependencies" {
                    "implementation"(arg { "devNpm"("axios", "*") })
                    "api"(arg { "npm"("snabbdom", "*") })
                  }
                }
              }
              npmRepository()
            }
          },
          publishTaskName("js"),
          "--stacktrace",
          "-Pnpm.publish.dry=true"
        )
      }
      "succeed [MPP]" {
        gradleExec(
          {
            it.resolve("src/CustomJSMain/kotlin/index.kt").apply {
              parentFile.mkdirs()
              writeText("fun main(){}")
            }
          },
          {
            kotlinMpp {
              "js"("CustomJS") {
                "browser"()
              }
              "sourceSets" {
                "named"("CustomJSMain") {
                  "dependencies" {
                    "implementation"(arg { "devNpm"("axios", "*") })
                    "api"(arg { "npm"("snabbdom", "*") })
                  }
                }
              }
              npmRepository()
            }
          },
          publishTaskName("CustomJS"),
          "--stacktrace",
          "-Pnpm.publish.dry=true"
        )
      }
    }
  }
)
