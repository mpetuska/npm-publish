package dev.petuska.npm.publish.delegate

import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty1
import org.gradle.api.Project

internal infix fun <R, V> ReadWriteProperty<R, V?>.or(fallback: ReadWriteProperty<R, V>) =
    ChainedProperty(this, fallback)

internal fun <R, V> R.fallbackDelegate(prop: KProperty1<R, V>) =
    FallbackDelegate(this) { prop.get(this) }

internal fun <R, P, V> R.fallbackDelegate(prop: KProperty1<R, P>, transform: P.() -> V) =
    FallbackDelegate(this) { prop.get(this).transform() }

internal fun <R, V> R.fallbackDelegate(prop: KProperty1<R, V?>, default: V) =
    FallbackDelegate(this, default) { prop.get(this) }

internal fun <R, V> R.fallbackDelegate(projection: R.() -> V) = FallbackDelegate(this, projection)

internal fun <R, V> R.fallbackDelegate(default: V, projection: R.() -> V) =
    FallbackDelegate(this, default, projection)

internal fun <V> Project.propertyDelegate(prefix: String? = null, converter: (String) -> V?) =
    propertyDelegate(prefix, null, converter)

internal fun <V> Project.propertyDelegate(
    prefix: String? = null,
    default: V,
    converter: (String) -> V?
) = PropertyDelegate(project, prefix, converter, default)

internal inline fun <reified V : Any> Project.gradleNullableProperty(default: V? = null) =
    GradleProperty.Nullable(this, V::class, default)

internal inline fun <reified V : Any> Project.gradleProperty(default: V) =
    GradleProperty.NotNullable(this, V::class, default)
