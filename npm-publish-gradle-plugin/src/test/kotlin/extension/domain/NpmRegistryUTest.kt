package dev.petuska.npm.publish.extension.domain

import dev.petuska.npm.publish.config.publishTaskName
import dev.petuska.npm.publish.test.util.UTest
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.TestFactory

class NpmRegistryUTest : UTest() {
  @TestFactory
  fun publishTaskNameTests(): Collection<DynamicTest> {
    val expected = "publishTestPackagePackageToTestRegistryRegistry"
    return packageNames.zip(registryNames).map { (p, r) ->
      DynamicTest.dynamicTest("::publishTaskName can handle '$p' package name and '$r' registry name") {
        publishTaskName(p, r) shouldBe expected
      }
    }
  }
}
