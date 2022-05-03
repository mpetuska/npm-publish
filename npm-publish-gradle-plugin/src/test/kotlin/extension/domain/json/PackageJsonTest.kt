package extension.domain.json

import dev.petuska.npm.publish.test.util.ITest
import dev.petuska.npm.publish.util.unsafeCast
import io.kotest.matchers.shouldBe
import org.jetbrains.kotlin.gradle.plugin.KotlinJsCompilerType
import org.junit.jupiter.api.Test

internal class PackageJsonTest : ITest() {
  @Test
  fun instantiation() {
    val project = kJsProjectOf(KotlinJsCompilerType.IR)
    val pkg = project { npmPublish.packages.getByName(targetName) }
    pkg.packageJson {
      it.main.set("main")
    }
    pkg.packageJson.get().main.get() shouldBe "main"
  }

  @Test
  fun customNestedObject() {
    val project = kJsProjectOf(KotlinJsCompilerType.IR)
    val pkg = project { npmPublish.packages.getByName(targetName) }
    pkg.packageJson {
      with(it) {
        "custom" by {
          "value" by true
        }
      }
    }
    val final = pkg.packageJson.get().finalise()
    final["custom"].unsafeCast<Map<String, Any>>()["value"] shouldBe true
  }
}
