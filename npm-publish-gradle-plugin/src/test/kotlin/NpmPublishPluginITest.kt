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
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.TestFactory

@Disabled(
  """
  Broken since gradle 8: 
    Querying the mapped value of map(flatmap(flatmap(provider(?)))) 
      before task ':compileProductionLibraryKotlinJs' has completed is not supported
"""
)
class NpmPublishPluginITest : ITest() {

  private fun autoconfigureTest(kPlugin: String, compiler: KotlinJsCompilerType, present: Boolean) {
    val project = if (kPlugin == "multiplatform") kMppProjectOf(compiler) else kJsProjectOf(compiler)
    project.projectInternal.evaluate()
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
      false.shouldBeTrue()
    }
  }

  @TestFactory
  fun tests(): List<DynamicTest> = listOf(
    DynamicTest.dynamicTest("can autoconfigure with K/MPP IR") {
      autoconfigureTest("multiplatform", KotlinJsCompilerType.IR, true)
    },
    DynamicTest.dynamicTest("can autoconfigure with K/JS IR") {
      autoconfigureTest("js", KotlinJsCompilerType.IR, true)
    },
    DynamicTest.dynamicTest("rejects K/MPP Legacy") {
      @Suppress("DEPRECATION")
      autoconfigureTest("multiplatform", KotlinJsCompilerType.LEGACY, false)
    },
    DynamicTest.dynamicTest("rejects K/JS Legacy") {
      @Suppress("DEPRECATION")
      autoconfigureTest("js", KotlinJsCompilerType.LEGACY, false)
    },
  )
}
