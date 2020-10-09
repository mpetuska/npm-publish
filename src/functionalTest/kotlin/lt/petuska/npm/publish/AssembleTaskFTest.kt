package lt.petuska.npm.publish

import io.kotest.core.spec.style.WordSpec
import lt.petuska.npm.publish.util.assembleTaskName
import lt.petuska.npm.publish.util.gradleExec
import lt.petuska.npm.publish.util.kotlinJs
import lt.petuska.npm.publish.util.kotlinMpp
import lt.petuska.npm.publish.util.npmRepository

class AssembleTaskFTest : WordSpec(
  {
    "Running assembleNpmPublication" should {
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
          assembleTaskName("js"),
          "--stacktrace"
        )
      }
      "succeed [MPP]" {
        gradleExec(
          {
            kotlinMpp {
              "js" {
                "browser"()
              }
              "sourceSets.apply" {
                "named(\"jsMain\")" {
                  "dependencies" {
                    "implementation"("devNpm(\"axios\", \"*\")")
                    "api"("npm(\"snabbdom\", \"*\")")
                  }
                }
              }
              npmRepository()
            }
          },
          assembleTaskName("js"),
          "--stacktrace"
        )
      }
    }
  }
)
