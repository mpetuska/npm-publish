package dev.petuska.npm.publish.extension.domain.json

import org.gradle.api.provider.*
import org.gradle.api.tasks.*

/** [people field](https://docs.npmjs.com/files/package.json#people-fields-author-contributors) */
abstract class Person : JsonObject<Any>() {
  @get:Input
  @get:Optional
  abstract val name: Property<String>

  @get:Input
  @get:Optional
  abstract val email: Property<String>

  @get:Input
  @get:Optional
  abstract val url: Property<String>

  override fun finalise(): MutableMap<String, Any> = super.finalise().apply {
    name.finalOrNull?.let { put("name", it) }
    email.finalOrNull?.let { put("email", it) }
    url.finalOrNull?.let { put("url", it) }
  }
}
