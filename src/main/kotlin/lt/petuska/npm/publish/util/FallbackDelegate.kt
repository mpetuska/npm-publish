package lt.petuska.npm.publish.util

import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty
import kotlin.reflect.KProperty1

internal sealed class FallbackDelegate<V, F>() : ReadWriteProperty<Any, V> {
  protected open var default: V? = null

  class LinkedFallbackDelegate<V, F>(
    private val fallbackObj: F,
    private val fallbackProperty: KProperty1<F, V>,
    override var default: V? = null
  ) : FallbackDelegate<V, F>() {
    override fun getValue(thisRef: Any, property: KProperty<*>): V {
      return default ?: fallbackProperty.get(fallbackObj)
    }

    override fun setValue(thisRef: Any, property: KProperty<*>, value: V) {
      this.default = value
    }
  }

  class NullFallbackDelegate<V, F>(
    override var default: V? = null
  ) : FallbackDelegate<V, F>() {
    override fun getValue(thisRef: Any, property: KProperty<*>): V {
      return default
        ?: error(
          "Fallback Delegate for prop ${property.name} does not have a default value " +
            "nor a fallback object to extract the fallback property from."
        )
    }

    override fun setValue(thisRef: Any, property: KProperty<*>, value: V) {
      this.default = value
    }
  }
}

internal inline fun <reified R, V> R?.fallbackDelegate(prop: KProperty1<R, V>, default: V? = null) = if (this != null) {
  FallbackDelegate.LinkedFallbackDelegate(this, prop, default)
} else {
  FallbackDelegate.NullFallbackDelegate<V, R>(default)
}
