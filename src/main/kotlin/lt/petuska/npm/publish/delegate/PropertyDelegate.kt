package lt.petuska.npm.publish.delegate

import lt.petuska.npm.publish.util.propertyOrNull
import org.gradle.api.Project
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

internal class PropertyDelegate<V>(
  private val project: Project,
  private val prefix: String?,
  private val converter: (String) -> V?,
  private val default: V
) : ReadWriteProperty<Any, V> {
  private var value: V? = null

  override fun getValue(thisRef: Any, property: KProperty<*>): V {
    value = value ?: project.propertyOrNull<V>("$PROP_BASE${prefix?.removeSuffix(".")?.removePrefix(".")?.let { ".$it" } ?: ""}.${property.name}")
      ?.toString()?.let(converter)
    return value ?: default
  }

  override fun setValue(thisRef: Any, property: KProperty<*>, value: V) {
    this.value = project.propertyOrNull<V>("$PROP_BASE${prefix?.removeSuffix(".")?.removePrefix(".")?.let { ".$it" } ?: ""}.${property.name}")
      ?.toString()?.let(converter) ?: value
  }

  companion object {
    private const val PROP_BASE = "npm.publish"
  }
}
