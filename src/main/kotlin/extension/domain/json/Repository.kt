package dev.petuska.npm.publish.extension.domain.json

import org.gradle.api.provider.*
import org.gradle.api.tasks.*

/** [repository](https://docs.npmjs.com/files/package.json#repository) */
abstract class Repository : JsonObject<Any>() {
  @get:Input
  @get:Optional
  abstract val type: Property<String>

  @get:Input
  @get:Optional
  abstract val url: Property<String>

  @get:Input
  @get:Optional
  abstract val directory: Property<String>

  override fun finalise(): MutableMap<String, Any> = super.finalise().apply {
    type.finalOrNull?.let { put("type", it) }
    url.finalOrNull?.let { put("url", it) }
    directory.finalOrNull?.let { put("directory", it) }
  }
}
