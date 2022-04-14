package dev.petuska.npm.publish

import dev.petuska.npm.publish.test.util.*
import io.kotest.core.spec.style.scopes.*
import io.kotest.matchers.booleans.*
import io.kotest.matchers.ints.*
import io.kotest.matchers.nulls.*
import org.jetbrains.kotlin.gradle.plugin.*

class NpmPublishPluginITest : ITest() {

  private fun StringSpecScope.autoconfigureTest(kPlugin: String, compiler: KotlinJsCompilerType) {
    val project = if (kPlugin == "multiplatform") kMppProjectOf(compiler) else kJsProjectOf(compiler)
    project.npmPublish.run {
      val pkg = packages.findByName("js")
      pkg.shouldNotBeNull()
      pkg.main.isPresent.shouldBeTrue()
      pkg.types.isPresent.shouldBeFalse()
      pkg.files.count().shouldBeGreaterThan(0)
      pkg.dependencies.count().shouldBeGreaterThan(0)
      project.tasks.findByName(assembleTaskName(pkg.name)).shouldNotBeNull()
      project.tasks.findByName(packTaskName(pkg.name)).shouldNotBeNull()
    }
  }

  init {
    "can autoconfigure with K/MPP IR" {
      autoconfigureTest("multiplatform", KotlinJsCompilerType.IR)
    }

    "can autoconfigure with K/MPP Legacy" {
      autoconfigureTest("multiplatform", KotlinJsCompilerType.LEGACY)
    }

    "can autoconfigure with K/JS Legacy" {
      autoconfigureTest("js", KotlinJsCompilerType.LEGACY)
    }
  }
}
