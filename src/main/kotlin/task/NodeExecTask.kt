package dev.petuska.npm.publish.task

import dev.petuska.npm.publish.delegate.gradleNullableProperty
import dev.petuska.npm.publish.util.Builder
import org.apache.tools.ant.taskdefs.condition.Os
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.InputFile
import org.gradle.process.ExecResult
import org.gradle.process.ExecSpec
import java.io.File
import javax.inject.Inject

/**
 * Basic task for executing various node commands. Provides access to node executable.
 * @param nodeJsDir base NodeJS directory to extract executables from.
 */
open class NodeExecTask @Inject constructor(nodeJsDir: File?) : DefaultTask() {
  constructor() : this(null)

/**
   * Base NodeJS directory used to extract other node executables from. Defaults to 'NODE_HOME' env
   * variable.
   */
  @get:InputDirectory
  open val nodeJsDir by project.gradleNullableProperty(
    nodeJsDir ?: System.getenv("NODE_HOME")?.let(::File)
  )

/** Main Node executable. Allows for executing any js script from your builds. */
  @get:InputFile
  val node by lazy {
    // For some unknown reason, the node distribution's structure is different on Windows and UNIX.
    if (Os.isFamily(Os.FAMILY_WINDOWS)) {
      this.nodeJsDir!!.resolve("node.exe")
    } else {
      this.nodeJsDir!!.resolve("bin").resolve("node")
    }
  }

  fun nodeExec(args: Collection<Any?>, config: Builder<ExecSpec> = {}): ExecResult = project.exec {
    val cmd = listOfNotNull(node, *args.toTypedArray()).toTypedArray()
    it.commandLine(*cmd)
    it.config()
  }
}
