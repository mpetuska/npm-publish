package dev.petuska.npm.publish.task

import dev.petuska.npm.publish.extension.NpmPublishExtension
import dev.petuska.npm.publish.extension.domain.NpmRegistry
import dev.petuska.npm.publish.util.configure
import org.gradle.api.Action
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.api.publish.plugins.PublishingPlugin.PUBLISH_TASK_GROUP
import org.gradle.api.tasks.*
import org.gradle.api.tasks.options.Option

/**
 * A publishing task that publishes a given package to a given registry.
 */
@Suppress("LeakingThis")
@UntrackedTask(because = "Must always run")
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
   * Optional tag to label the published package version
   * @see [NpmPublishExtension.dry]
   */
  @get:Input
  @get:Optional
  @get:Option(option = "tag", description = "Optional tag to label the published package version")
  public abstract val tag: Property<String>

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
    dry.convention(registry.flatMap(NpmRegistry::dry))
    registry.convention(
      project.provider {
        project.objects.newInstance(NpmRegistry::class.java, name)
      }
    )
  }

  @Suppress("unused")
  @TaskAction
  private fun doAction() {
    val pDir = packageDir.asFile.get()
    val reg = registry.get()
    val uri = reg.uri.get()
    val repo = "${uri.authority.trim()}${uri.path.trim()}/"
    val d = dry.get()
    debug {
      "Publishing package at ${pDir.path} to ${reg.name} registry ${if (d) "with" else "without"} --dry-run flag"
    }
    val args = buildList {
      add("publish")
      add(pDir)
      add(listOf("--access", reg.access.get()))
      add(listOf("--registry", "${uri.scheme.trim()}://$repo"))
      if (reg.otp.isPresent) add(listOf("--otp", reg.otp.get()))
      if (reg.authToken.isPresent) add("--//$repo:_authToken=${reg.authToken.get()}")
      if (d) add("--dry-run")
      if (tag.isPresent) add(listOf("--tag", tag.get()))
      add("${uri.scheme.trim()}://$repo")
    }
    npmExec(args) { it.workingDir(packageDir.get()) }.rethrowFailure()
    if (!d) info { "Published package at ${pDir.path} to ${reg.name} registry" }
  }
}
