package dev.petuska.npm.publish.task

import dev.petuska.npm.publish.extension.NpmPublishExtension
import dev.petuska.npm.publish.extension.domain.NpmRegistry
import dev.petuska.npm.publish.util.configure
import dev.petuska.npm.publish.util.final
import dev.petuska.npm.publish.util.finalise
import org.gradle.api.Action
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.api.publish.plugins.PublishingPlugin.PUBLISH_TASK_GROUP
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.Nested
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.options.Option

/**
 * A publishing task that publishes a given package to a given repository.
 */
@Suppress("LeakingThis")
public abstract class NpmPublishTask : NpmExecTask() {
  /**
   * A registry to publish to
   * @see [NpmRegistry]
   */
  @get:Nested
  public abstract val registry: Property<NpmRegistry>

  /**
   * The directory where the assembled and ready-to-publish package is
   * @see [NpmAssembleTask]
   */
  @get:InputDirectory
  public abstract val packageDir: DirectoryProperty

  /**
   * Controls dry-tun mode for the execution.
   * @see [NpmPublishExtension.dry]
   */
  @get:Input
  @get:Option(option = "dry", description = "Execute in dry-run mode")
  public abstract val dry: Property<Boolean>

  /**
   * Configuration DSL allowing to modify a registry config
   * @param action to apply
   */
  @Suppress("unused")
  public fun registry(action: Action<NpmRegistry>) {
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
    val pDir = packageDir.final.asFile
    val reg = registry.final
    val uri = reg.uri.final
    val repo = "${uri.authority.trim()}${uri.path.trim()}/"
    val d = dry.final
    debug {
      "Publishing package at ${pDir.path} to ${reg.name} registry ${if (d) "with" else "without"} --dry-run flag"
    }
    npmExec(
      listOf(
        "publish",
        pDir,
        "--access",
        "${reg.access.final}",
        "--registry",
        "${uri.scheme.trim()}://$repo",
        if (reg.authToken.finalise().isPresent) "--//$repo:_authToken=${reg.authToken.get()}" else null,
        if (reg.otp.finalise().isPresent) "--otp ${reg.otp.get()}" else null,
        if (d) "--dry-run" else null
      )
    ) { it.workingDir(packageDir.final) }
    if (!d) info { "Published package at ${pDir.path} to ${reg.name} registry" }
  }
}
