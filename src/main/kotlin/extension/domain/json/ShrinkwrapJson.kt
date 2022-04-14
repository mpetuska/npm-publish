@file:Suppress("LeakingThis")

package dev.petuska.npm.publish.extension.domain.json

import org.gradle.api.*
import org.gradle.api.provider.*
import org.gradle.api.tasks.*

@Suppress("unused")
abstract class ShrinkwrapJson : JsonObject<Any>() {
  init {
    lockfileVersion.set(1)
    requires.set(true)
  }

  /** [name](https://docs.npmjs.com/files/package.json#name) */
  @get:Input
  abstract val name: Property<String>

  /** [version](https://docs.npmjs.com/files/package.json#version) */
  @get:Input
  abstract val version: Property<String>

  /** Defaults to 1. */
  @get:Input
  abstract val lockfileVersion: Property<Int>

  /** Defaults to true */
  @get:Input
  abstract val requires: Property<Boolean>

  /** Shrinkwrap dependencies container. */
  @get:Nested
  abstract val dependencies: Property<JsonObject<ShrinkwrapDependency>>

  fun dependencies(action: Action<JsonObject<ShrinkwrapDependency>>) {
    dependencies.configure(action)
  }

  override fun finalise(): MutableMap<String, Any> = super.finalise().apply {
    name.finalOrNull?.let { put("name", it) }
    version.finalOrNull?.let { put("version", it) }
    lockfileVersion.finalOrNull?.let { put("lockfileVersion", it) }
    requires.finalOrNull?.let { put("requires", it) }
    dependencies.finalOrNull?.let { put("dependencies", it.finalise()) }
  }

  abstract class Dependencies : JsonObject<ShrinkwrapDependency>() {
    /** Creates and adds a shrinkwrap dependency. */
    fun dependency(name: String, version: String, bundled: Boolean) {
      set(
        name,
        instance<ShrinkwrapDependency>().also {
          it.version.set(version)
          it.bundled.set(bundled)
        }
      )
    }
  }

  abstract inner class ShrinkwrapDependency : JsonObject<Any>() {
    abstract val version: Property<String>
    abstract val bundled: Property<Boolean>

    override fun finalise(): MutableMap<String, Any> = super.finalise().apply {
      version.finalOrNull?.let { put("version", it) }
      bundled.finalOrNull?.let { put("bundled", it) }
    }
  }
}
