package dev.petuska.npm.publish.delegate

import dev.petuska.npm.publish.util.propertyOrNull
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
    value = value ?: property.findValue()?.let(converter)
    return value ?: default
  }

  override fun setValue(thisRef: Any, property: KProperty<*>, value: V) {
    this.value = property.findValue()?.let(converter) ?: value
  }

  private fun KProperty<*>.findValue(): String? = findProperty() ?: findEnv()

  private fun KProperty<*>.findProperty(): String? {
    return project.propertyOrNull<V>(buildPropertyKey())?.toString()
  }

  private fun KProperty<*>.findEnv(): String? {
    return System.getenv(buildPropertyKey().toUpperCase().replace("[.\\- ]".toRegex(), "_"))?.toString()
  }

  private fun KProperty<*>.buildPropertyKey() =
    "$PROP_BASE${
    prefix?.removeSuffix(".")
      ?.removePrefix(".")
      ?.let { ".$it" } ?: ""
    }.$name"

  companion object {
    private const val PROP_BASE = "npm.publish"
  }
}
