package dev.petuska.npm.publish.extension.domain

import dev.petuska.npm.publish.extension.NpmPublishExtension
import dev.petuska.npm.publish.task.NpmPackTask
import dev.petuska.npm.publish.task.NpmPublishTask
import dev.petuska.npm.publish.util.NamedInput
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import java.net.URI

/**
 * Npm registry configuration
 */
@Suppress("unused", "LeakingThis")
public abstract class NpmRegistry : NamedInput {
  /**
   * Registry access
   * @see [NpmPublishExtension.access]
   */
  @get:Input
  public abstract val access: Property<NpmAccess>

  /**
   * NPM registry uri to publish packages to.
   * Should include schema domain and path if required
   */
  @get:Input
  public abstract val uri: Property<URI>

  /**
   * Optional OTP to use when authenticating with the registry
   */
  @get:Input
  @get:Optional
  public abstract val otp: Property<String>

  /**
   * Auth token to use when authenticating with the registry.
   * [More info](https://docs.npmjs.com/about-access-tokens)
   */
  @get:Input
  @get:Optional
  public abstract val authToken: Property<String>

  /**
   * Specifies if a dry-run should be added to the npm command arguments by default. Dry run does all the
   * normal run des except actual file uploading. Defaults to `false`.
   * @see [NpmPublishExtension.dry]
   * @see [NpmPackTask.dry]
   * @see [NpmPublishTask.dry]
   */
  @get:Input
  @get:Optional
  public abstract val dry: Property<Boolean>

  /**
   * Convenience DSL to set [URI] properties via [String]
   * @param uri to use when constructing [URI] instance
   */
  public fun Property<URI>.set(uri: String) {
    set(URI(uri))
  }

  init {
    access.convention(NpmAccess.PUBLIC)
    dry.convention(false)
  }
}

public typealias NpmRegistries = NamedDomainObjectContainer<NpmRegistry>
