package dev.petuska.npm.publish.extension.domain.json

import dev.petuska.npm.publish.util.*
import org.gradle.api.provider.*
import org.gradle.api.tasks.*

abstract class JsonObject<T : Any> : WithGradleFactories() {

  @get:Input
  protected abstract val extras: MapProperty<String, T>

  operator fun set(key: String, value: T) {
    extras.put(key, value)
  }

  operator fun set(key: String, value: Provider<T>) {
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

//  protected inline fun <reified T> Property<T>.configure(action: Action<T>) {
//    val instance = providers.provider { objects.newInstance(T::class.java) }
//    set(orElse(instance).map { action.execute(it); it })
//  }

  open fun finalise(): MutableMap<String, T> = mutableMapOf<String, T>().apply {
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
