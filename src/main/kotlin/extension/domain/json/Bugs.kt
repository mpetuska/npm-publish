package dev.petuska.npm.publish.extension.domain.json

import org.gradle.api.provider.*
import org.gradle.api.tasks.*

/** [bugs](https://docs.npmjs.com/files/package.json#bugs) */
abstract class Bugs : JsonObject<Any>() {
  @get:Input
  @get:Optional
  abstract val url: Property<String>

  @get:Input
  @get:Optional
  abstract val email: Property<String>

  override fun finalise(): MutableMap<String, Any> = super.finalise().apply {
    url.finalOrNull?.let { put("url", it) }
    email.finalOrNull?.let { put("email", it) }
  }
}
