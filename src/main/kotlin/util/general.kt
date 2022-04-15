package dev.petuska.npm.publish.util

import org.gradle.api.Action
import org.gradle.api.plugins.ExtensionContainer
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import java.util.logging.Logger

internal fun String?.notFalse() = !equals("false", true)

internal fun npmFullName(name: String, scope: String?) =
  "${scope?.let { "@${it.trim()}/" } ?: ""}${name.trim()}"

internal fun <T, P : Property<T>> P.configure(config: Action<T>) {
  set(map { it.apply(config::execute) })
}

internal fun <T, P : Provider<T>> P.configure(config: Action<T>): Provider<T> = map { config.execute(it); it }

@Suppress("UNCHECKED_CAST")
internal fun <T> Any?.unsafeCast(): T = this as T

internal fun <T> Property<T>.finalise(): Provider<T> {
  finalizeValue()
  return this
}

internal val <T> Property<T>.final: T get() = finalise().get()
internal val <T> Property<T>.finalOrNull: T? get() = finalise().orNull

internal inline fun <reified T> ExtensionContainer.configure(crossinline action: T.() -> Unit) {
  configure(T::class.java) { it.apply(action) }
}

internal val logger = Logger.getLogger("dev.petuska.npm.publish")
