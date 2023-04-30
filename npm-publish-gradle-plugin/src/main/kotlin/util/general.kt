package dev.petuska.npm.publish.util

import org.gradle.api.Action
import org.gradle.api.plugins.ExtensionContainer
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider

internal fun String?.notFalse() = !(
  equals("false", true) ||
    equals("0", true) ||
    equals("n", true) ||
    equals("N", true) ||
    equals("f", true) ||
    equals("F", true)
  )

internal fun npmFullName(name: String, scope: String?) =
  "${scope?.let { "@${it.trim()}/" } ?: ""}${name.trim()}"

internal fun <T : Any, P : Property<T>> P.configure(config: Action<T>) {
  set(map { it.apply(config::execute) })
}

internal fun <T : Any, P : Provider<T>> P.configure(config: Action<T>): Provider<T> = map { config.execute(it); it }

@Suppress("UNCHECKED_CAST")
internal fun <T> Any?.unsafeCast(): T = this as T

internal inline fun <reified T> ExtensionContainer.configure(crossinline action: T.() -> Unit) {
  configure(T::class.java) { it.apply(action) }
}
