package task

import dev.petuska.container.task.ContainerExecTask
import org.gradle.api.tasks.UntrackedTask

@UntrackedTask(because = "Must always run")
abstract class MkDocsExec(private val command: String) : ContainerExecTask("mkdocs") {
  init {
    group = "mkdocs"
    image.setFinal("docker.io/mpetuska/mkdocs-material-mike")
    executable.setFinal("mkdocs")
    version.convention("latest")
  }

  override fun prepareContainerExecutable(mode: Mode, executable: String): String = command

  override fun prepareCommandArgs(mode: Mode): List<String> =
    if (mode != Mode.NATIVE) super.prepareCommandArgs(mode) else listOf(command) + super.prepareCommandArgs(mode)

  @UntrackedTask(because = "Must always run")
  abstract class Serve : MkDocsExec("serve")
  abstract class Build : MkDocsExec("build")

  @UntrackedTask(because = "Must always run")
  abstract class GhDeploy : MkDocsExec("gh-deploy")
}
