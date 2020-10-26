package lt.petuska.npm.publish.task

import lt.petuska.npm.publish.delegate.gradleNullableProperty
import org.apache.tools.ant.taskdefs.condition.Os
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.InputFile
import java.io.File
import javax.inject.Inject

/**
 * Basic task for executing various npm commands. Provides access to npm and node executables.
 *
 * @constructor base NodeJS directory to extract executables from.
 */
abstract class NpmExecTask @Inject constructor(
  nodeJsDir: File?
) : DefaultTask() {
  constructor() : this(null)

  /**
   * Base NodeJS directory used to extract other node executables from. Defaults to 'NODE_HOME' env variable.
   */
  @get:InputDirectory
  open val nodeJsDir by project.gradleNullableProperty(nodeJsDir ?: System.getenv("NODE_HOME")?.let(::File))

  /**
   * Main Node executable. Allows for executing any js script from your builds.
   */
  @get:InputFile
  val node by lazy {
    // For some unknown reason, the node distribution's structure is different on Windows and UNIX.
    if (Os.isFamily(Os.FAMILY_WINDOWS)) {
      this.nodeJsDir!!
        .resolve("node.exe")
    } else {
      this.nodeJsDir!!
        .resolve("bin")
        .resolve("node")
    }
  }

  /**
   * NPM CLI executable. Use as argument to node executable as this is a JS script.
   */
  @get:InputFile
  val npm by lazy {
    if (Os.isFamily(Os.FAMILY_WINDOWS)) {
      this.nodeJsDir!!
        .resolve("node_modules")
        .resolve("npm")
        .resolve("bin")
        .resolve("npm-cli.js")
    } else {
      this.nodeJsDir!!
        .resolve("lib")
        .resolve("node_modules")
        .resolve("npm")
        .resolve("bin")
        .resolve("npm-cli.js")
    }
  }
}
