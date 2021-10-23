package dev.petuska.npm.publish.task

import dev.petuska.npm.publish.delegate.fallbackDelegate
import dev.petuska.npm.publish.dsl.NpmPublication
import dev.petuska.npm.publish.dsl.NpmPublishExtension
import dev.petuska.npm.publish.dsl.NpmRepository
import dev.petuska.npm.publish.npmPublishing
import javax.inject.Inject
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction

/**
 * A publishing task that publishes a given publication to a given repository.
 *
 * @constructor A publication and repository configurations.
 */
open class NpmPublishTask
@Inject
constructor(publication: NpmPublication, repository: NpmRepository) : NpmExecTask() {
  override var nodeJsDir by publication.fallbackDelegate(NpmPublication::nodeJsDir)

  /** @see [NpmRepository.registry] */
  @get:Internal var registry by repository.fallbackDelegate(NpmRepository::registry)

  /** @see [NpmRepository.access] */
  @get:Internal var access by repository.fallbackDelegate(NpmRepository::access)

  /** @see [NpmRepository.authToken] */
  @get:Internal var authToken by repository.fallbackDelegate(NpmRepository::authToken)

  /**
   * The directory where the assembled and ready-to-publish package is. Defaults to
   * [NpmPublication.destinationDir]
   */
  @get:Internal var packageDir by publication.fallbackDelegate(NpmPublication::destinationDir)

  /** @see [NpmRepository.otp] */
  @get:Internal var otp by repository.fallbackDelegate(NpmRepository::otp)

  /** See Also: [dev.petuska.npm.publish.dsl.NpmPublishExtension.dry] */
  @get:Internal var dry by project.npmPublishing.fallbackDelegate(NpmPublishExtension::dry)

  init {
    group = "publishing"
    description = "Publishes ${publication.name} NPM module to ${repository.name} NPM repository"
  }

  @TaskAction
  private fun doAction() {
    val repo = "${registry!!.authority.trim()}${registry!!.path.trim()}/"
    npmExec(
        listOf(
            "publish",
            packageDir,
            "--access",
            "$access",
            "--registry",
            "${registry!!.scheme.trim()}://$repo",
            "--//$repo:_authToken=$authToken",
            if (otp != null) "--otp $otp" else null,
            if (dry) "--dry-run" else null)) { workingDir = packageDir }
  }
}
