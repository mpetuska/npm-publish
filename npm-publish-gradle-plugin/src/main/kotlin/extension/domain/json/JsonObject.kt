package dev.petuska.npm.publish.extension.domain.json

import dev.petuska.npm.publish.util.*
import org.gradle.api.provider.*
import org.gradle.api.tasks.*

/**
 * Generic json object container supporting extra properties and Gradle serialisation
 */
public abstract class JsonObject<T : Any> : WithGradleFactories() {

  @get:Input
  protected abstract val extras: MapProperty<String, T>

  /**
   * Set a custom value
   * @param key of the value
   * @param value to set under the given key
   */
  public operator fun set(key: String, value: T) {
    extras.put(key, value)
  }

  /**
   * Set a custom value provider to be resolved when [finalise] is invoked
   * @param key of the value
   * @param value to set under the given key
   */
  public operator fun set(key: String, value: Provider<T>) {
    extras.put(key, value)
  }

  protected val <V : T> Property<V>.finalOrNull: V?
    get() {
      finalizeValue()
      return orNull
    }

  protected val <V : T> ListProperty<V>.final: List<V>?
    get() {
      finalizeValue()
      return orNull?.takeIf(Collection<T>::isNotEmpty)
    }

  protected val <V : T> SetProperty<V>.final: Set<V>?
    get() {
      finalizeValue()
      return orNull?.takeIf(Collection<T>::isNotEmpty)
    }

  /**
   * Resolves the underlying json value to a [MutableMap] instance,
   * recursively merging known and custom properties and resolving all [Provider] values
   */
  public open fun finalise(): MutableMap<String, T> = mutableMapOf<String, T>().apply {
    extras.finalizeValue()
    extras.orNull?.map { (k, v) ->
      val value = when (v) {
        is Provider<*> -> v.orNull
        is JsonObject<*> -> v.finalise()
        else -> v
      }
      put(k, value.unsafeCast())
    }
  }
}
