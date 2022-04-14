package dev.petuska.npm.publish.task

import dev.petuska.npm.publish.extension.domain.*
import dev.petuska.npm.publish.util.*
import org.gradle.api.*
import org.gradle.api.file.*
import org.gradle.api.provider.*
import org.gradle.api.publish.plugins.PublishingPlugin.PUBLISH_TASK_GROUP
import org.gradle.api.tasks.*
import org.gradle.api.tasks.options.*

/**
 * A publishing task that publishes a given publication to a given repository.
 *
 * @constructor A publication and repository configurations.
 */
@Suppress("LeakingThis")
abstract class NpmPublishTask : NpmExecTask() {
  /** @see [NpmRepository.registry] */
  @get:Nested
  abstract val registry: Property<NpmRegistry>

  /**
   * The directory where the assembled and ready-to-publish package is.
   */
  @get:InputDirectory
  abstract val packageDir: DirectoryProperty

  /** See Also: [dev.petuska.npm.publish.dsl.NpmPublishExtension.dry] */
  @get:Input
  @get:Option(option = "dry", description = "Execute in dry-run mode")
  abstract val dry: Property<Boolean>

  /** Configuration DSL allowing to modify a given registry config. */
  @Suppress("unused")
  fun registry(action: Action<NpmRegistry>) {
    registry.configure(action)
  }

  init {
    group = PUBLISH_TASK_GROUP
    description = "Publishes NPM package to NPM registry"
    dry.convention(false)
    registry.convention(
      project.provider {
        project.objects.newInstance(NpmRegistry::class.java, name)
      }
    )
  }

  @Suppress("unused")
  @TaskAction
  private fun doAction() {
    val reg = registry.final
    val uri = reg.uri.final
    val repo = "${uri.authority.trim()}${uri.path.trim()}/"
    npmExec(
      listOf(
        "publish",
        packageDir.final,
        "--access",
        "${reg.access.final}",
        "--registry",
        "${uri.scheme.trim()}://$repo",
        if (reg.authToken.finalise().isPresent) "--//$repo:_authToken=${reg.authToken.get()}" else null,
        if (reg.otp.finalise().isPresent) "--otp ${reg.otp.get()}" else null,
        if (dry.final) "--dry-run" else null
      )
    ) { it.workingDir(packageDir.final) }
  }
}
