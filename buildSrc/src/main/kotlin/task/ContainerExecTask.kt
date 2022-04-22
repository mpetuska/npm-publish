package task

import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.MapProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.SetProperty
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.options.Option
import org.gradle.deployment.internal.DeploymentRegistry
import org.gradle.process.internal.ExecHandleFactory
import task.runner.ContainerRunner
import util.PrefixedLogger
import java.io.File
import java.io.OutputStream
import javax.inject.Inject

@Suppress("LeakingThis", "TooManyFunctions")
abstract class ContainerExecTask(private val executable: String) : DefaultTask(),
  PrefixedLogger {
  @get:Input
  abstract val image: Property<String>

  @get:Input
  @get:Optional
  abstract val mode: Property<Mode>

  @get:Input
  abstract val version: Property<String>

  @get:Input
  abstract val environment: MapProperty<String, Any>

  @get:Input
  @get:Optional
  abstract val ignoreExitValue: Property<Boolean>

  @get:Input
  abstract val args: ListProperty<String>

  @Option(option = "arg", description = "Argument to be passed to the executable")
  fun arg(args: List<String>) {
    this.args.addAll(args)
  }

  @Option(option = "args", description = "Arguments to be passed to the executable")
  fun args(args: String) {
    this.args.addAll(args.split(" "))
  }

  @get:Input
  abstract val containerArgs: ListProperty<String>

  @get:Internal
  abstract val workingDir: DirectoryProperty

  @get:Inject
  protected abstract val execHandleFactory: ExecHandleFactory

  init {
    workingDir.convention(project.layout.dir(project.provider { temporaryDir.resolve("pwd") }))
    version.convention("latest")
  }

  @get:Input
  abstract val containerVolumes: SetProperty<File>

  private val _containerVolumes = mutableSetOf<File>()

  private fun getContainerVolume(hostFile: File, containerFile: File = hostFile): String? = if (hostFile.exists()) {
    "-v=${hostFile.absolutePath}:${containerFile.absolutePath}"
      .let { if (resolvedMode == Mode.DOCKER) it else "$it:Z" }
  } else null

  protected fun addContainerVolume(hostFile: File) {
    hostFile.mkdirs()
    _containerVolumes.add(hostFile)
  }

  @TaskAction
  @Suppress("UnusedPrivateMember")
  private fun action() {
    resolveMode()
    beforeAction()
    execute()
    afterAction()
  }

  private lateinit var resolvedMode: Mode

  private fun resolveMode(): Mode {
    resolvedMode = mode.orElse(
      project.provider {
        fun hasExecutable(executable: String): Boolean = project.exec {
          isIgnoreExitValue = true
          commandLine("which", executable)
          environment(this@ContainerExecTask.environment.get())
          errorOutput = OutputStream.nullOutputStream()
          standardOutput = OutputStream.nullOutputStream()
        }.exitValue == 0
        if (hasExecutable(executable)) {
          info { "Native $executable detected" }
          Mode.NATIVE
        } else if (hasExecutable("podman")) {
          info { "Podman detected. Using it to run $executable via a rootless container" }
          Mode.PODMAN
        } else if (hasExecutable("docker")) {
          info { "Docker detected. Using it to run $executable via a root container" }
          Mode.DOCKER
        } else {
          error("No $executable, podman or docker executable found")
        }
      }
    ).get()
    return resolvedMode
  }

  private fun execute() {
    val isContinuous = project.gradle.startParameter.isContinuous
    val runner = ContainerRunner(
      image = image.get(),
      execHandleFactory = execHandleFactory,
      executable = executable,
      commandArgs = prepareCommandArgs(resolvedMode),
      containerArgs = prepareContainerArgs(resolvedMode),
      workingDir = workingDir.get(),
      ignoreExitValue = ignoreExitValue.getOrElse(isContinuous),
      environment = System.getenv() + environment.get(),
      mode = resolvedMode,
      version = version.get(),
    )
    if (isContinuous) {
      val deploymentRegistry = services.get(DeploymentRegistry::class.java)
      val deploymentHandle = deploymentRegistry.get("jekyll", ContainerRunner.Handle::class.java)
      if (deploymentHandle == null) {
        deploymentRegistry.start(
          "jekyll",
          DeploymentRegistry.ChangeBehavior.BLOCK,
          ContainerRunner.Handle::class.java, runner
        )
      }
    } else {
      runner.execute(services).assertNormalExitValue()
    }
  }

  protected open fun beforeAction() {
    addContainerVolume(workingDir.asFile.get())
    addContainerVolume(project.rootDir.resolve(".git"))
  }

  protected open fun afterAction(): Unit = Unit

  protected open fun prepareCommandArgs(mode: Mode): List<String> {
    return this.args.get()
  }

  protected open fun prepareContainerArgs(mode: Mode): List<String> {
    val args = mutableListOf(
      "-i",
      "--rm",
      "--init",
      "--network=host",
      "-w=${workingDir.asFile.get().absolutePath}",
    )
    getContainerVolume(
      project.buildDir.resolve(".bundle/$name").also(File::mkdirs),
      File("/usr/local/bundle")
    )?.let(args::add)
    if (mode == Mode.PODMAN) args += "-e=JEKYLL_ROOTLESS=1"
    environment.get().forEach { (k, v) ->
      args += "-e=$k=$v"
    }
    args += (_containerVolumes + containerVolumes.get()).mapNotNull(::getContainerVolume)
    return args + containerArgs.get()
  }

  enum class Mode {
    NATIVE, PODMAN, DOCKER
  }
}
