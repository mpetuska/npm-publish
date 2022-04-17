package jekyll

import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction

abstract class JekyllServeTask : JekyllExecTask() {
  @get:Input
  abstract val port: Property<Int>

  @get:Input
  abstract val liveReloadPort: Property<Int>

  @get:Input
  abstract val liveReload: Property<Boolean>

  init {
    ignoreExitValue.convention(true)
    port.convention(4000)
    liveReloadPort.convention(35729)
    liveReload.convention(true)
    outputDir.convention(project.layout.dir(project.provider { temporaryDir.resolve("output") }))
    outputs.upToDateWhen { false }
  }

  @TaskAction
  override fun action() {
    port.finalizeValue()
    liveReload.finalizeValue()
    liveReloadPort.finalizeValue()
    runJekyll(
      listOf("-p=[::1]:${port.get()}:${port.get()}", "-p=[::1]:${liveReloadPort.get()}:${liveReloadPort.get()}"),
      listOfNotNull(
        "serve",
        "-p",
        "${port.get()}",
        if (liveReload.get()) "-l" else null,
        "--livereload-port",
        "${liveReloadPort.get()}"
      ) + args.get()
    )
  }
}
