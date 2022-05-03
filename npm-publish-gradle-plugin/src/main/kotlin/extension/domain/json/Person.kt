package dev.petuska.npm.publish.extension.domain.json

import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional

/**
 * [people field](https://docs.npmjs.com/files/package.json#people-fields-author-contributors)
 */
public abstract class Person : GenericJsonObject() {
  /**
   * [people field](https://docs.npmjs.com/files/package.json#people-fields-author-contributors)
   */
  @get:Input
  @get:Optional
  public abstract val name: Property<String>

  /**
   * [people field](https://docs.npmjs.com/files/package.json#people-fields-author-contributors)
   */
  @get:Input
  @get:Optional
  public abstract val email: Property<String>

  /**
   * [people field](https://docs.npmjs.com/files/package.json#people-fields-author-contributors)
   */
  @get:Input
  @get:Optional
  public abstract val url: Property<String>

  override fun finalise(): MutableMap<String, Any> = super.finalise().apply {
    name.finalOrNull?.let { put("name", it) }
    email.finalOrNull?.let { put("email", it) }
    url.finalOrNull?.let { put("url", it) }
  }
}
