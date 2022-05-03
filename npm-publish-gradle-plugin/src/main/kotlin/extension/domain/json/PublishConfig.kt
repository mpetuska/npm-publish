package dev.petuska.npm.publish.extension.domain.json

import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional

/**
 * [publish config](https://docs.npmjs.com/files/package.json#publishconfig)
 */
public abstract class PublishConfig : GenericJsonObject() {
  /**
   * [publish config](https://docs.npmjs.com/files/package.json#publishconfig)
   */
  @get:Input
  @get:Optional
  public abstract val registry: Property<String>

  /**
   * [publish config](https://docs.npmjs.com/files/package.json#publishconfig)
   */
  @get:Input
  @get:Optional
  public abstract val access: Property<String>

  /**
   * [publish config](https://docs.npmjs.com/files/package.json#publishconfig)
   */
  @get:Input
  @get:Optional
  public abstract val tag: Property<String>

  override fun finalise(): MutableMap<String, Any> = super.finalise().apply {
    registry.finalOrNull?.let { put("registry", it) }
    access.finalOrNull?.let { put("access", it) }
    tag.finalOrNull?.let { put("tag", it) }
  }
}
