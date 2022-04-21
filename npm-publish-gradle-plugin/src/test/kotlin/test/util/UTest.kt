package dev.petuska.npm.publish.test.util

import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Tags

@Tags(Tag("unit"))
@Suppress("UnnecessaryAbstractClass")
abstract class UTest {
  protected val packageNames = listOf(
    "testPackage",
    "TestPackage",
    "test package",
    "test Package",
    "Test Package",
    "test-package",
  )
  protected val registryNames = listOf(
    "testRegistry",
    "TestRegistry",
    "test registry",
    "test Registry",
    "Test Registry",
    "test-registry",
  )
}
