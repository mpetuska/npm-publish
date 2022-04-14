package dev.petuska.npm.publish.task

import org.apache.tools.ant.taskdefs.condition.*
import org.gradle.api.*
import org.gradle.api.file.*
import org.gradle.api.provider.*
import org.gradle.api.tasks.*
import org.gradle.process.*

/**
 * Basic task for executing various node commands. Provides access to node executable.
 */
@Suppress("LeakingThis")
abstract class NodeExecTask : DefaultTask() {

  /**
   * Base NodeJS directory used to extract other node executables from. Defaults to 'NODE_HOME' env
   * variable.
   */
  @get:InputDirectory
  @get:PathSensitive(PathSensitivity.NAME_ONLY)
  abstract val nodeHome: DirectoryProperty

  /** Main Node executable. Allows for executing any js script from your builds. */
  @get:InputFile
  @get:PathSensitive(PathSensitivity.NAME_ONLY)
  val node: Provider<RegularFile> = nodeHome.file(
    if (Os.isFamily(Os.FAMILY_WINDOWS)) {
      "node.exe"
    } else {
      "bin/node"
    }
  )

  init {
    nodeHome.convention(project.layout.projectDirectory.dir(project.providers.environmentVariable("NODE_HOME")))
  }

  fun nodeExec(args: Collection<Any?>, config: Action<ExecSpec> = Action {}): ExecResult = project.exec {
    val cmd = listOfNotNull(node.get(), *args.toTypedArray()).toTypedArray()
    it.commandLine(*cmd)
    config.execute(it)
  }
}
