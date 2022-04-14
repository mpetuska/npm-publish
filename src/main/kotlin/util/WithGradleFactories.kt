package dev.petuska.npm.publish.util

import org.gradle.api.*
import org.gradle.api.model.*
import org.gradle.api.provider.*
import org.gradle.api.provider.Provider
import javax.inject.*

abstract class WithGradleFactories {
  @get:Inject
  protected abstract val objects: ObjectFactory

  @get:Inject
  protected abstract val providers: ProviderFactory

  protected inline fun <reified T> instance(vararg args: Any?): T = objects.newInstance(T::class.java, *args)
  protected inline fun <reified T> instanceProvider(vararg args: Any?): Provider<T> =
    providers.provider { instance(*args) }

  protected inline fun <reified T> Property<T>.configure(config: Action<T>, vararg args: Any?) {
    set(instanceProvider<T>(*args).map { it.also(config::execute) })
  }
}
