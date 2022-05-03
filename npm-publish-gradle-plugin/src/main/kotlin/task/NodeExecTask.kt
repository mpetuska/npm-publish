package dev.petuska.npm.publish.task

import dev.petuska.npm.publish.util.PluginLogger
import org.apache.tools.ant.taskdefs.condition.Os
import org.gradle.api.Action
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFile
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.options.Option
import org.gradle.process.ExecResult
import org.gradle.process.ExecSpec
import java.io.File

/**
 * Basic task for executing various node commands. Provides access to node executable.
 */
@Suppress("LeakingThis")
public abstract class NodeExecTask : DefaultTask(), PluginLogger {

  /**
   * Base NodeJS directory used to extract other node executables from. Defaults to 'NODE_HOME' env
   * variable.
   */
  @get:InputDirectory
  @get:PathSensitive(PathSensitivity.NAME_ONLY)
  public abstract val nodeHome: DirectoryProperty

  /**
   * Sets [nodeHome]
   * @param path to the node directory
   */
  @Option(option = "nodeNome", description = "Base NodeJS directory path")
  public fun nodeHome(path: String) {
    nodeHome.set(File(path))
  }

  /** Main NodeJS executable. Allows for executing any js script from your builds. */
  @get:InputFile
  @get:PathSensitive(PathSensitivity.NAME_ONLY)
  public val node: Provider<RegularFile> = nodeHome.file(
    if (Os.isFamily(Os.FAMILY_WINDOWS)) {
      "node.exe"
    } else {
      "bin/node"
    }
  )

  init {
    nodeHome.convention(project.layout.projectDirectory.dir(project.providers.environmentVariable("NODE_HOME")))
  }

  /**
   * Executes a Node command
   * @param args to be passed in to the Node executable
   * @param config to be applied to the execution process
   * @return execution result
   */
  @Suppress("SpreadOperator")
  public fun nodeExec(args: Collection<Any?>, config: Action<ExecSpec> = Action {}): ExecResult = project.exec {
    val cmd = listOfNotNull(node.get(), *args.toTypedArray()).toTypedArray()
    it.commandLine(*cmd)
    config.execute(it)
  }
}
