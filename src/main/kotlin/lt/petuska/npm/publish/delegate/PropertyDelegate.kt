package lt.petuska.npm.publish.delegate

import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

internal class PropertyDelegate<V>(
  private val properties: Map<String, *>,
  private val prefix: String?,
  private val converter: (String) -> V?,
  private val default: V
) : ReadWriteProperty<Any, V> {
  private var value: V? = null

  override fun getValue(thisRef: Any, property: KProperty<*>): V {
    value = value ?: properties["$PROP_BASE${prefix?.removeSuffix(".")?.removePrefix(".")?.let { ".$it" } ?: ""}.${property.name}"]
      ?.toString()?.let(converter)
    return value ?: default
  }

  override fun setValue(thisRef: Any, property: KProperty<*>, value: V) {
    this.value = properties["$PROP_BASE${prefix?.removeSuffix(".")?.removePrefix(".")?.let { ".$it" } ?: ""}.${property.name}"]
      ?.toString()?.let(converter) ?: value
  }

  companion object {
    private const val PROP_BASE = "npm.publish"
  }
}
