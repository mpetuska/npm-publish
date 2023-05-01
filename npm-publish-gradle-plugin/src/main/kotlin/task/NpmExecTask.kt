package dev.petuska.npm.publish.task

import org.apache.tools.ant.taskdefs.condition.Os
import org.gradle.api.Action
import org.gradle.api.file.RegularFile
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.process.ExecResult
import org.gradle.process.ExecSpec

/**
 * Basic task for executing various npm commands. Provides access to npm and node executables.
 */
@Suppress("LeakingThis")
public abstract class NpmExecTask : NodeExecTask() {

  /**
   * NPM CLI executable. Use as argument to node executable as this is a JS script.
   */
  @get:InputFile
  @get:PathSensitive(PathSensitivity.NAME_ONLY)
  public val npm: Provider<RegularFile> = nodeHome.file(
    if (Os.isFamily(Os.FAMILY_WINDOWS)) {
      "node_modules/"
    } else {
      "lib/node_modules/"
    } + "npm/bin/npm-cli.js"
  )

  /**
   * Executes an NPM command
   * @param args to be passed in to the NPM executable
   * @param config to be applied to the execution process
   * @return execution result
   */
  public fun npmExec(args: Collection<String?>, config: Action<ExecSpec> = Action {}): ExecResult =
    nodeExec(listOf("${npm.get().asFile}") + args, config)
}
