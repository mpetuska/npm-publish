package dev.petuska.npm.publish

import dev.petuska.npm.publish.config.assembleTaskName
import dev.petuska.npm.publish.config.packTaskName
import dev.petuska.npm.publish.test.util.ITest
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.ints.shouldBeGreaterThan
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import org.jetbrains.kotlin.gradle.plugin.KotlinJsCompilerType
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.TestFactory

class NpmPublishPluginITest : ITest() {

  private fun autoconfigureTest(kPlugin: String, compiler: KotlinJsCompilerType, present: Boolean) {
    val project = if (kPlugin == "multiplatform") kMppProjectOf(compiler) else kJsProjectOf(compiler)
    project.npmPublish.run {
      val pkg = packages.findByName("js")
      if (present) {
        pkg.shouldNotBeNull()
        pkg.main.isPresent.shouldBeTrue()
        pkg.types.isPresent.shouldBeFalse()
        pkg.files.count().shouldBeGreaterThan(0)
        pkg.dependencies.count().shouldBeGreaterThan(0)
        project.tasks.findByName(assembleTaskName(pkg.name)).shouldNotBeNull()
        project.tasks.findByName(packTaskName(pkg.name)).shouldNotBeNull()
      } else {
        pkg.shouldBeNull()
      }
    }
  }

  @TestFactory
  fun tests(): List<DynamicTest> = listOf(
    DynamicTest.dynamicTest("can autoconfigure with K/MPP IR") {
      autoconfigureTest(
        "multiplatform", KotlinJsCompilerType.IR, true
      )
    },
    DynamicTest.dynamicTest("can autoconfigure with K/JS IR") {
      autoconfigureTest(
        "js", KotlinJsCompilerType.IR, true
      )
    },
    DynamicTest.dynamicTest("rejects K/MPP Legacy") {
      autoconfigureTest(
        "multiplatform", KotlinJsCompilerType.LEGACY, false
      )
    },
    DynamicTest.dynamicTest("rejects K/JS Legacy") { autoconfigureTest("js", KotlinJsCompilerType.LEGACY, false) },
  )
}
