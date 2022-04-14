package dev.petuska.npm.publish.extension.domain.json

import org.gradle.api.provider.*
import org.gradle.api.tasks.*

/** [publish config](https://docs.npmjs.com/files/package.json#publishconfig) */
abstract class PublishConfig : JsonObject<Any>() {
  @get:Input
  @get:Optional
  abstract val registry: Property<String>

  @get:Input
  @get:Optional
  abstract val access: Property<String>

  @get:Input
  @get:Optional
  abstract val tag: Property<String>

  override fun finalise(): MutableMap<String, Any> = super.finalise().apply {
    registry.finalOrNull?.let { put("registry", it) }
    access.finalOrNull?.let { put("access", it) }
    tag.finalOrNull?.let { put("tag", it) }
  }
}
