package dev.petuska.npm.publish.task

import org.apache.tools.ant.taskdefs.condition.*
import org.gradle.api.*
import org.gradle.api.file.*
import org.gradle.api.provider.*
import org.gradle.api.tasks.*
import org.gradle.process.*

/**
 * Basic task for executing various npm commands. Provides access to npm and node executables.
 */
@Suppress("LeakingThis")
abstract class NpmExecTask : NodeExecTask() {

  /**
   * NPM CLI executable. Use as argument to node executable as this is a JS script.
   */
  @get:InputFile
  @get:PathSensitive(PathSensitivity.NAME_ONLY)
  val npm: Provider<RegularFile> = nodeHome.file(
    if (Os.isFamily(Os.FAMILY_WINDOWS)) {
      "node_modules/"
    } else {
      "lib/node_modules/"
    } + "npm/bin/npm-cli.js"
  )

  fun npmExec(args: Collection<Any?>, config: Action<ExecSpec> = Action {}): ExecResult =
    nodeExec(listOf(npm.get()) + args, config)
}
