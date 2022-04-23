package task

import dev.petuska.container.task.ContainerExecTask
import org.gradle.api.tasks.UntrackedTask
import java.io.File

@Suppress("LeakingThis")
@UntrackedTask(because = "Must always run")
abstract class MikeExec(command: String) : ContainerExecTask("mike") {
  init {
    group = "mike"
    image.setFinal("docker.io/mpetuska/mkdocs-material-mike")
    executable.setFinal(command)
    version.convention("latest")
    containerArgs.add("--entrypoint=mike")

    System.getProperty("user.home")?.let(::File)?.run {
      resolve(".gitconfig").let { containerVolumes.put(it, it) }
      resolve(".ssh/").let { containerVolumes.put(it, it) }
    }
    containerVolumes.put(project.rootDir, project.rootDir)
    environment.put("HOME", System.getProperty("user.home"))
    environment.put("USER", System.getProperty("user.name"))
    environment.put("GIT_AUTHOR_NAME", "mike")
    environment.put("GIT_AUTHOR_EMAIL", "mike@mike.mike")
  }

  @UntrackedTask(because = "Must always run")
  abstract class Serve : MikeExec("serve")

  @UntrackedTask(because = "Must always run")
  abstract class Delete : MikeExec("delete")

  @UntrackedTask(because = "Must always run")
  abstract class List : MikeExec("list")

  @UntrackedTask(because = "Must always run")
  abstract class Deploy : MikeExec("deploy")

  @UntrackedTask(because = "Must always run")
  abstract class Alias : MikeExec("alias")
}
