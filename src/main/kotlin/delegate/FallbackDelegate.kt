package dev.petuska.npm.publish.delegate

import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

internal class FallbackDelegate<V, F>(
    private val fallbackObj: F,
    private val projection: F.() -> V
) : ReadWriteProperty<Any, V> {
  constructor(
      fallbackObj: F,
      default: V,
      projection: F.() -> V?
  ) : this(fallbackObj, { fallbackObj.projection() ?: default })

  private var value: V? = null

  override fun getValue(thisRef: Any, property: KProperty<*>): V {
    return value ?: projection.invoke(fallbackObj)
  }

  override fun setValue(thisRef: Any, property: KProperty<*>, value: V) {
    this.value = value
  }
}
