package dev.petuska.npm.publish.task

import dev.petuska.npm.publish.util.PluginLogger
import org.apache.tools.ant.taskdefs.condition.Os
import org.gradle.api.Action
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.options.Option
import org.gradle.process.ExecOperations
import org.gradle.process.ExecResult
import org.gradle.process.ExecSpec
import java.io.File
import javax.inject.Inject

/**
 * Basic task for executing various node commands. Provides access to node executable.
 */
@Suppress("LeakingThis")
public abstract class NodeExecTask : DefaultTask(), PluginLogger {

  @get:Inject
  internal abstract val execOps: ExecOperations

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
  public abstract val node: RegularFileProperty

  init {
    nodeHome.convention(project.layout.projectDirectory.dir(project.providers.environmentVariable("NODE_HOME")))
    node.convention(
      nodeHome.file(
        if (Os.isFamily(Os.FAMILY_WINDOWS)) {
          "node.exe"
        } else {
          "bin/node"
        }
      )
    )
  }

  protected fun exec(args: Collection<String?>, config: Action<ExecSpec> = Action {}): ExecResult = execOps.exec {
    val cmd = args.toTypedArray().filterNotNull()
    info { "Executing: ${cmd.joinToString(" ")}" }
    @Suppress("SpreadOperator")
    it.commandLine(*cmd.toTypedArray())
    config.execute(it)
  }

  /**
   * Executes a Node command
   * @param args to be passed in to the Node executable
   * @param config to be applied to the execution process
   * @return execution result
   */
  @Suppress("SpreadOperator")
  public fun nodeExec(
    args: Collection<String?>,
    config: Action<ExecSpec> = Action {}
  ): ExecResult = exec(listOf(node.get().asFile.absolutePath) + args, config)
}
