package lt.petuska.kpm.publish.util

import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty
import kotlin.reflect.KProperty1

internal class FallbackDelegate<V, F>(
  private val fallbackObj: F,
  private val fallbackProperty: KProperty1<F, V>,
  private var default: V? = null
) : ReadWriteProperty<Any, V> {
  override fun getValue(thisRef: Any, property: KProperty<*>): V {
    return default ?: fallbackProperty.get(fallbackObj)
  }

  override fun setValue(thisRef: Any, property: KProperty<*>, value: V) {
    this.default = value
  }
}

internal inline fun <reified R, V> R.fallbackDelegate(prop: KProperty1<R, V>, default: V? = null) =
  FallbackDelegate(this, prop, default)
