package lt.petuska.npm.publish.delegate

import org.gradle.api.Project
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty
import kotlin.reflect.KProperty1

internal class ChainedProperty<R, V>(
  private var main: ReadWriteProperty<R, V?>,
  private val fallback: ReadWriteProperty<R, V>
) : ReadWriteProperty<R, V> {
  override fun getValue(thisRef: R, property: KProperty<*>): V {
    return main.getValue(thisRef, property) ?: fallback.getValue(thisRef, property)
  }

  override fun setValue(thisRef: R, property: KProperty<*>, value: V) {
    main.setValue(thisRef, property, value)
  }
}

internal infix fun <R, V> ReadWriteProperty<R, V?>.or(fallback: ReadWriteProperty<R, V>) = ChainedProperty(this, fallback)

internal fun <R, V> R.fallbackDelegate(prop: KProperty1<R, V>) = FallbackDelegate(this) {
  prop.get(this)
}

internal fun <R, P, V> R.fallbackDelegate(prop: KProperty1<R, P>, transform: P.() -> V) = FallbackDelegate(this) {
  prop.get(this).transform()
}

internal fun <R, V> R.fallbackDelegate(prop: KProperty1<R, V?>, default: V) = FallbackDelegate(this, default) {
  prop.get(this)
}

internal fun <R, V> R.fallbackDelegate(projection: R.() -> V) = FallbackDelegate(this, projection)
internal fun <R, V> R.fallbackDelegate(default: V, projection: R.() -> V) = FallbackDelegate(this, default, projection)

internal fun <V> Project.propertyDelegate(prefix: String? = null, converter: (String) -> V?) = propertyDelegate(prefix, null, converter)
internal fun <V> Project.propertyDelegate(prefix: String? = null, default: V, converter: (String) -> V?) = PropertyDelegate(properties, prefix, converter, default)

internal inline fun <reified V : Any> Project.gradleNullableProperty(default: V? = null) =
  GradleProperty.Nullable(this, V::class, default)

internal inline fun <reified V : Any> Project.gradleProperty(default: V) =
  GradleProperty.NotNullable(this, V::class, default)
