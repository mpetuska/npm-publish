package jekyll

import org.gradle.api.tasks.TaskAction

abstract class JekyllBuildTask : JekyllExecTask() {
  init {
    ignoreExitValue.convention(false)
    outputDir.convention(sourceDir.flatMap {
      project.layout.buildDirectory.dir("jekyll/${it.asFile.name}")
    })
  }

  @TaskAction
  override fun action() {
    runJekyll(
      listOf(),
      listOf("build") + args.get()
    )
  }
}
