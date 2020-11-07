package lt.petuska.npm.publish.task

import lt.petuska.npm.publish.delegate.fallbackDelegate
import lt.petuska.npm.publish.dsl.NpmPublication
import lt.petuska.npm.publish.dsl.NpmPublishExtension
import lt.petuska.npm.publish.npmPublishing
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction
import java.io.File
import javax.inject.Inject

/**
 * A publishing task that publishes a given publication to a given repository.
 *
 * @constructor A publication and repository configurations.
 */
open class NpmPackTask @Inject constructor(
  publication: NpmPublication
) : NpmExecTask() {
  override var nodeJsDir by publication.fallbackDelegate(NpmPublication::nodeJsDir)

  /**
   * The directory where the assembled and ready-to-publish package is.
   * Defaults to [NpmPublication.destinationDir]
   */
  @get:Internal
  var packageDir: File by publication.fallbackDelegate(NpmPublication::destinationDir)

  /**
   * Output directory to pack the publication to.
   * Defaults to [NpmPackTask.packageDir] parent
   */
  @get:Internal
  var destinationDir: File by fallbackDelegate<NpmPackTask, File, File>(NpmPackTask::packageDir) { parentFile }

  /**
   * See Also: [lt.petuska.npm.publish.dsl.NpmPublishExtension.dry]
   */
  @get:Internal
  var dry by project.npmPublishing.fallbackDelegate(NpmPublishExtension::dry)

  init {
    group = "build"
    description = "Packs ${publication.name} NPM module"
  }

  @TaskAction
  private fun doAction() {
    npmExec(
      listOf(
        "pack",
        packageDir,
        if (dry) "--dry-run" else null
      )
    ) {
      workingDir = destinationDir
    }
  }
}
