package dev.petuska.npm.publish.extension.domain.json

import dev.petuska.npm.publish.util.unsafeCast
import org.gradle.api.Action

/**
 * A type of [JsonObject] that holds [Any] values
 */
public abstract class GenericJsonObject : JsonObject<Any>() {
  /**
   * Set a custom object value for this [JsonObject]
   * @receiver property key
   * @param value configuration to apply to a new [GenericJsonObject] instance
   */
  public infix fun String.by(value: Action<GenericJsonObject>) {
    this by instance(GenericJsonObject::class).also { value.execute(it.unsafeCast()) }
  }

  /**
   * Set a custom object value for this [JsonObject]
   * @receiver property key
   * @param value configuration to apply to a new [GenericJsonObject] instance
   */
  public infix fun String.by(value: GenericJsonObject.() -> Unit) {
    this by instance(GenericJsonObject::class).also { value.invoke(it) }
  }
}
