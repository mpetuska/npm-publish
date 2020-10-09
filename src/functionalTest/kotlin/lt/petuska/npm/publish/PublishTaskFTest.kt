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
            kotlinJs {
              "js" {
                "browser"()
              }
              "sourceSets.apply" {
                "named(\"main\")" {
                  "dependencies" {
                    "implementation"("devNpm(\"axios\", \"*\")")
                    "api"("npm(\"snabbdom\", \"*\")")
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
            kotlinMpp {
              "js(\"CustomJS\")" {
                "browser"()
              }
              "sourceSets.apply" {
                "named(\"CustomJSMain\")" {
                  "dependencies" {
                    "implementation"("devNpm(\"axios\", \"*\")")
                    "api"("npm(\"snabbdom\", \"*\")")
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
