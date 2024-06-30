package dev.petuska.npm.publish.extension.domain

import dev.petuska.npm.publish.test.util.ITest
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.TestFactory

class NpmRegistryITest : ITest() {
  private fun propertyDefaultTest(key: String, value: String, selector: NpmRegistry.() -> String?) {
    val registryName = "customTest"
    val propKey = "npm.publish.registry.$registryName.$key"
    projectOf(properties = mapOf(propKey to value)) {
      npmPublish.registries.register(registryName)
      withClue("Value should be set via '$propKey' gradle property") {
        npmPublish.registries.getByName(registryName).selector() shouldBe value
      }
    }
    projectOf {
      npmPublish.registries.register(registryName)
      withClue("Value should not be set via '$propKey' gradle property") {
        npmPublish.registries.getByName(registryName).selector() shouldNotBe value
      }
    }
  }

  @TestFactory
  fun tests(): List<DynamicTest> = listOf<Triple<String, String, NpmRegistry.() -> String?>>(
    Triple("access", "restricted") { access.orNull?.name?.lowercase() },
    Triple("uri", "https://test.com") { uri.orNull?.toString() },
    Triple("otp", "test") { otp.orNull },
    Triple("authToken", "test") { authToken.orNull },
    Triple("auth", "test") { auth.orNull },
    Triple("username", "test") { username.orNull },
    Triple("password", "test") { password.orNull },
  ).map { (k, v, s) ->
    DynamicTest.dynamicTest("default for NpmRegistry::$k") {
      propertyDefaultTest(k, v, s)
    }
  }
}
