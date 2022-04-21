package dev.petuska.npm.publish.extension.domain

import dev.petuska.npm.publish.test.util.ITest
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.TestFactory

class NpmPackageITest : ITest() {
  private fun propertyDefaultTest(key: String, value: String, selector: NpmPackage.() -> String?) {
    val packageName = "customTest"
    val propKey = "npm.publish.package.$packageName.$key"
    projectOf(properties = mapOf(propKey to value)) {
      npmPublish.packages.register(packageName)
      withClue("Value should be set via '$propKey' gradle property") {
        npmPublish.packages.getByName(packageName).selector() shouldBe value
      }
    }
    projectOf {
      npmPublish.packages.register(packageName)
      withClue("Value should not be set via '$propKey' gradle property") {
        npmPublish.packages.getByName(packageName).selector() shouldNotBe value
      }
    }
  }

  @TestFactory
  fun tests(): List<DynamicTest> = listOf<Triple<String, String, NpmPackage.() -> String?>>(
    Triple("scope", "test") { scope.orNull },
    Triple("packageName", "test") { packageName.orNull },
    Triple("version", "test") { version.orNull },
    Triple("main", "test") { main.orNull },
    Triple("types", "test") { types.orNull },
    Triple("readme", "test") { readme.orNull?.asFile?.name },
  ).map { (k, v, s) ->
    DynamicTest.dynamicTest("default for NpmPackage::$k") {
      propertyDefaultTest(k, v, s)
    }
  }
}
