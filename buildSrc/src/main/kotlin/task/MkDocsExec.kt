package task

import org.gradle.api.tasks.UntrackedTask

@UntrackedTask(because = "Must always run")
abstract class MkDocsExec(executable: String) : ContainerExecTask(executable) {
  override val prefix: String = "mkdocs"

  init {
    group = "mkdocs"
    image.convention("squidfunk/mkdocs-material")
  }

  @UntrackedTask(because = "Must always run")
  abstract class Serve : MkDocsExec("serve")
  abstract class Build : MkDocsExec("build")

  @UntrackedTask(because = "Must always run")
  abstract class GhDeploy : MkDocsExec("gh-deploy")
}
