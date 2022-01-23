package dev.petuska.npm.publish.task

import dev.petuska.npm.publish.util.Builder
import org.apache.tools.ant.taskdefs.condition.Os
import org.gradle.api.tasks.InputFile
import org.gradle.process.ExecResult
import org.gradle.process.ExecSpec
import java.io.File
import javax.inject.Inject

/**
 * Basic task for executing various npm commands. Provides access to npm and node executables.
 *
 * @constructor base NodeJS directory to extract executables from.
 */
open class NpmExecTask @Inject constructor(nodeJsDir: File?) : NodeExecTask(nodeJsDir) {
  constructor() : this(null)

/** NPM CLI executable. Use as argument to node executable as this is a JS script. */
  @get:InputFile
  val npm by lazy {
    if (Os.isFamily(Os.FAMILY_WINDOWS)) {
      this.nodeJsDir!!.resolve("node_modules").resolve("npm").resolve("bin").resolve("npm-cli.js")
    } else {
      this.nodeJsDir!!
        .resolve("lib")
        .resolve("node_modules")
        .resolve("npm")
        .resolve("bin")
        .resolve("npm-cli.js")
    }
  }

  fun npmExec(args: Collection<Any?>, config: Builder<ExecSpec> = {}): ExecResult =
    nodeExec(listOf(npm) + args, config)
}
