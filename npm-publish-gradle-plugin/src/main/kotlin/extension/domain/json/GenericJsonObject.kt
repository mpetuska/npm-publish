package dev.petuska.npm.publish.extension.domain.json

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
    this by json(value)
  }

  /**
   * Set a custom object value for this [JsonObject]
   * @receiver property key
   * @param value configuration to apply to a new [GenericJsonObject] instance
   */
  public infix fun String.by(value: GenericJsonObject.() -> Unit) {
    this by json(value)
  }
}
