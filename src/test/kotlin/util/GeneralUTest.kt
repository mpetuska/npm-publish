package dev.petuska.npm.publish.util

import dev.petuska.npm.publish.test.util.UTest
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.TestFactory

class GeneralUTest : UTest() {
  @TestFactory
  fun notFalseTests(): Collection<DynamicTest> = mapOf(
    "true" to true,
    "TRUE" to true,
    "y" to true,
    "Y" to true,
    "t" to true,
    "T" to true,
    "1" to true,
    "random" to true,
    "0" to false,
    "false" to false,
    "FALSE" to false,
    "f" to false,
    "F" to false,
    "n" to false,
    "N" to false,
    null to true,
  ).map { (input, expected) ->
    DynamicTest.dynamicTest("String::notFalse recognises '$input' as notFalse = $expected") {
      input.notFalse() shouldBe expected
    }
  }

  @TestFactory
  fun npmFullNameTests(): Collection<DynamicTest> = listOf(
    Triple("scope", "package", "@scope/package"),
    Triple(null, "package", "package"),
  ).map { (scope, packageName, expected) ->
    DynamicTest.dynamicTest("::npmFullName converts scope '$scope' & packageName '$packageName' to '$expected'") {
      npmFullName(name = packageName, scope = scope) shouldBe expected
    }
  }
}
