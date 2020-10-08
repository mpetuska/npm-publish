package lt.petuska.npm.publish.util

import org.gradle.api.Project
import org.gradle.api.provider.Property
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KClass
import kotlin.reflect.KProperty

internal sealed class GradleProperty<V>(
  private val property: Property<V>,
  private val handleNullable: (V?, KProperty<*>) -> V = { v, p ->
    v ?: throw IllegalStateException("Null value on property $p")
  }
) : ReadWriteProperty<Any, V> {
  override fun getValue(thisRef: Any, property: KProperty<*>): V {
    return handleNullable(this.property.orNull, property)
  }

  override fun setValue(thisRef: Any, property: KProperty<*>, value: V) {
    this.property.set(value)
  }

  class Nullable<V : Any>(
    project: Project,
    type: KClass<V>,
    default: V? = null
  ) : GradleProperty<V?>(
    project.objects.property(type.java).apply {
      set(default)
    },
    { it, _ -> it }
  )

  class NotNullable<V : Any>(
    project: Project,
    type: KClass<V>,
    default: V
  ) : GradleProperty<V>(
    project.objects.property(type.java).apply {
      set(default)
    }
  )
}

internal inline fun <reified V : Any> Project.gradleNullableProperty(default: V? = null) =
  GradleProperty.Nullable(this, V::class, default)

internal inline fun <reified V : Any> Project.gradleProperty(default: V) =
  GradleProperty.NotNullable(this, V::class, default)
