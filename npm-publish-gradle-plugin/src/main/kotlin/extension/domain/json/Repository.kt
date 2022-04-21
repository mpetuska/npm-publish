package dev.petuska.npm.publish.extension.domain.json

import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional

/**
 * [repository](https://docs.npmjs.com/files/package.json#repository)
 */
public abstract class Repository : JsonObject<Any>() {
  /**
   * [repository](https://docs.npmjs.com/files/package.json#repository)
   */
  @get:Input
  @get:Optional
  public abstract val type: Property<String>

  /**
   * [repository](https://docs.npmjs.com/files/package.json#repository)
   */
  @get:Input
  @get:Optional
  public abstract val url: Property<String>

  /**
   * [repository](https://docs.npmjs.com/files/package.json#repository)
   */
  @get:Input
  @get:Optional
  public abstract val directory: Property<String>

  override fun finalise(): MutableMap<String, Any> = super.finalise().apply {
    type.finalOrNull?.let { put("type", it) }
    url.finalOrNull?.let { put("url", it) }
    directory.finalOrNull?.let { put("directory", it) }
  }
}
