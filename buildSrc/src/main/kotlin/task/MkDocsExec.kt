package task

import dev.petuska.container.task.ContainerExecTask
import org.gradle.api.tasks.UntrackedTask

@UntrackedTask(because = "Must always run")
abstract class MkDocsExec(command: String) : ContainerExecTask("mkdocs") {
  init {
    group = "mkdocs"
    image.setFinal("docker.io/mpetuska/mkdocs-material-mike")
    executable.setFinal(command)
    version.convention("latest")
  }

  @UntrackedTask(because = "Must always run")
  abstract class Serve : MkDocsExec("serve")
  abstract class Build : MkDocsExec("build")

  @UntrackedTask(because = "Must always run")
  abstract class GhDeploy : MkDocsExec("gh-deploy")
}
