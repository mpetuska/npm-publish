@file:Suppress("PackageDirectoryMismatch")

import org.gradle.accessors.dm.LibrariesForLibs
import org.gradle.api.Project
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.kotlin.dsl.the
import java.nio.charset.Charset

internal inline val Project.libs get() = the<LibrariesForLibs>()

infix fun <T> Property<T>.by(value: T) = set(value)
infix fun <T> Property<T>.by(value: Provider<T>) = set(value)

object Git {
  val headCommitHash by lazy {
    val child = Runtime.getRuntime().exec(arrayOf("git","rev-parse", "--verify","HEAD"))
    child.waitFor()
    child.inputStream.readAllBytes().toString(Charset.defaultCharset()).trim()
  }
}

object Env {
  val CI = System.getenv("CI") !in arrayOf(null, "0", "false", "n", "N")
}
