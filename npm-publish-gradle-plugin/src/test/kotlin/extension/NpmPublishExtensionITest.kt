package dev.petuska.npm.publish.extension

import dev.petuska.npm.publish.test.util.ITest
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.TestFactory

class NpmPublishExtensionITest : ITest() {
  private fun propertyDefaultTest(key: String, value: String, selector: NpmPublishExtension.() -> String?) {
    val propKey = "npm.publish.$key"
    projectOf(properties = mapOf(key to value)) {
      withClue("Value should be set via '$propKey' gradle property") {
        npmPublish.selector() shouldBe value
      }
    }
    projectOf {
      withClue("Value should not be set via '$propKey' gradle property") {
        npmPublish.selector() shouldNotBe value
      }
    }
  }

  @TestFactory
  fun tests(): List<DynamicTest> = listOf<Triple<String, String, NpmPublishExtension.() -> String?>>(
    Triple("readme", "test") { readme.orNull?.asFile?.name },
    Triple("organization", "test") { organization.orNull },
    Triple("access", "restricted") { access.orNull?.name?.lowercase() },
    Triple("dry", "true") { dry.orNull.toString() },
    Triple("version", "test") { version.orNull },
    Triple("nodeHome", "test") { nodeHome.orNull?.asFile?.name },
  ).map { (k, v, s) ->
    dynamicTest("default for NpmPublishExtension::$k") {
      propertyDefaultTest("npm.publish.$k", v, s)
    }
  }
}
