package jekyll

import jekyll.JekyllExecTask.Mode.DOCKER
import jekyll.JekyllExecTask.Mode.NATIVE
import jekyll.JekyllExecTask.Mode.PODMAN
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.MapProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import java.io.File
import java.io.OutputStream

abstract class JekyllExecTask : DefaultTask() {
  @get:Input
  @get:Optional
  abstract val mode: Property<Mode>

  @get:Input
  @get:Optional
  abstract val version: Property<String>

  @get:Input
  @get:Optional
  abstract val ignoreExitValue: Property<Boolean>

  @get:Input
  abstract val args: ListProperty<Any>

  @get:Input
  abstract val environment: MapProperty<String, Any>

  @get:Internal
  abstract val workingDir: DirectoryProperty

  @get:InputDirectory
  abstract val sourceDir: DirectoryProperty

  @get:OutputDirectory
  abstract val outputDir: DirectoryProperty

  init {
    group = "jekyll"
    version.convention("4.2.0")
    ignoreExitValue.convention(false)
    workingDir.convention(project.layout.dir(project.provider { temporaryDir.resolve("pwd").also(File::mkdirs) }))
    outputDir.convention(project.layout.dir(project.provider { temporaryDir.resolve("output").also(File::mkdirs) }))
  }

  @TaskAction
  protected open fun action() {
    runJekyll(listOf(), args.get())
  }

  protected fun detectMode(): Mode {
    return if (hasExecutable("jekyll")) {
      logger.info("Native Jekyll detected")
      NATIVE
    } else if (hasExecutable("podman")) {
      logger.info("Podman detected. Using it to run Jekyll via a rootless container")
      PODMAN
    } else if (hasExecutable("docker")) {
      logger.info("Docker detected. Using it to run Jekyll via a root container")
      DOCKER
    } else {
      error("No jekyll, podman or docker executable found")
    }
  }

  private fun hasExecutable(executable: String): Boolean = project.exec {
    isIgnoreExitValue = true
    environment(this@JekyllExecTask.environment.get())
    commandLine("which", executable)
    errorOutput = OutputStream.nullOutputStream()
    standardOutput = OutputStream.nullOutputStream()
  }.exitValue == 0

  protected fun runJekyll(containerArgs: List<String>, args: List<Any>) {
    ignoreExitValue.finalizeValue()
    version.finalizeValue()
    workingDir.finalizeValue()
    sourceDir.finalizeValue()
    outputDir.finalizeValue()
    environment.finalizeValue()
    mode.finalizeValue()
    return when (val mode = mode.orNull ?: detectMode()) {
      NATIVE -> runNative(args)
      PODMAN -> runContainer(mode.name.toLowerCase(), true, containerArgs, args)
      DOCKER -> runContainer(mode.name.toLowerCase(), false, containerArgs, args)
    }
  }

  private fun runNative(args: List<Any>) {
    val pwd = workingDir.get().asFile
    project.exec {
      isIgnoreExitValue = ignoreExitValue.get()
      workingDir(pwd)
      environment(this@JekyllExecTask.environment.get())
      executable(NATIVE.name.toLowerCase())
      val cmd = args + listOf(
        "--disable-disk-cache",
        "-s",
        sourceDir.get().asFile.absolutePath,
        "-d",
        outputDir.get().asFile.absolutePath
      )
      logger.info("Executing[$executable]: jekyll ${cmd.joinToString(" ")}")
      args(cmd)
    }
  }

  private fun runContainer(
    executable: String,
    rootless: Boolean,
    containerArgs: List<Any>,
    args: List<Any>
  ) {
    val pwd = workingDir.get().asFile
    project.exec {
      isIgnoreExitValue = ignoreExitValue.get()
      workingDir(pwd)
      environment(this@JekyllExecTask.environment.get())
      executable(executable)
      var cmd = listOf(
        "run",
        "-i",
        "--rm",
        "--init",
        "-e=JEKYLL_ROOTLESS=${if (rootless) "1" else "0"}",
        "-v=${pwd.absolutePath}:/srv/jekyll/pwd${if (rootless) "" else ":Z"}",
        "-v=${sourceDir.get().asFile.absolutePath}:/srv/jekyll/source${if (rootless) "" else ":Z"}",
        "-v=${outputDir.get().asFile.absolutePath}:/srv/jekyll/output${if (rootless) "" else ":Z"}",
        "-w=/srv/jekyll/pwd"
      ) + containerArgs + listOf(
        "docker.io/jekyll/jekyll:${version.get()}",
        "jekyll",
      )
      val jekyllArgs = args + listOf(
        "--disable-disk-cache",
        "-s",
        "/srv/jekyll/source",
        "-d",
        "/srv/jekyll/output"
      )
      logger.info("Executing[$executable]: jekyll ${jekyllArgs.joinToString(" ")}")
      args(cmd + jekyllArgs)
    }
  }

  enum class Mode { NATIVE, PODMAN, DOCKER }
}
