package lt.petuska.npm.publish.task

import lt.petuska.npm.publish.dsl.NpmPublication
import lt.petuska.npm.publish.dsl.NpmPublishExtension
import lt.petuska.npm.publish.dsl.NpmRepository
import lt.petuska.npm.publish.util.fallbackDelegate
import lt.petuska.npm.publish.util.gradleProperty
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction
import javax.inject.Inject

/**
 * A publishing task that publishes a given publication to a given repository.
 *
 * @constructor A publication and repository configurations.
 */
open class NpmPublishTask @Inject constructor(
  publication: NpmPublication,
  repository: NpmRepository
) : NpmExecTask() {
  @get:InputDirectory
  override var nodeJsDir by publication.fallbackDelegate(NpmPublication::nodeJsDir)

  /**
   * @see [NpmRepository.registry]
   */
  @get:Input
  var registry by repository.fallbackDelegate(NpmRepository::registry)

  /**
   * @see [NpmRepository.access]
   */
  @get:Input
  var access by repository.fallbackDelegate(NpmRepository::access)

  /**
   * @see [NpmRepository.authToken]
   */
  @get:Input
  @get:Optional
  var authToken by repository.fallbackDelegate(NpmRepository::authToken)

  /**
   * The directory where the assembled and ready-to-publish package is.
   * Defaults to [NpmPublication.destinationDir]
   */
  @get:InputDirectory
  var packageDir by publication.fallbackDelegate(NpmPublication::destinationDir)

  /**
   * @see [NpmRepository.otp]
   */
  @get:Input
  @get:Optional
  var otp by repository.fallbackDelegate(NpmRepository::otp)

  /**
   * Specifies if a dry-run should be added to the npm command arguments. Dry run does all the normal run des except actual file uploading.
   * Defaults to `npm.publish.dry` project property if set or `false` otherwise.
   */
  @get:Input
  var dry by project.gradleProperty(
    (project.properties[NpmPublishExtension.DRY_RUN_PROP] as String?)?.toBoolean()
      ?: false
  )

  // /**
  //  * Main configuration of the repository to publish to.
  //  * If no repository is passed to a constructor, a default one will be constructed with basic project properties.
  //  */
  // @get:Nested
  // var repository by project.gradleProperty(repository ?: NpmRepository(project.name, project, project.npmPublishing))

  init {
    group = "publishing"
    description = "Publishes ${publication.name} NPM module to ${repository.name} NPM repository"
  }

  @TaskAction
  private fun doAction() {
    val repo = "${registry!!.authority.trim()}${registry!!.path.trim()}/"
    project.exec {
      val cmd = listOfNotNull(
        node,
        npm,
        "publish",
        packageDir,
        "--access", "$access",
        "--registry", "${registry!!.scheme.trim()}://$repo",
        "--//$repo:_authToken=$authToken",
        if (otp != null) "--otp $otp" else null,
        if (dry) "--dry-run" else null
      ).toTypedArray()
      it.commandLine(*cmd)
    }
  }
}
