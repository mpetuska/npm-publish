package dev.petuska.npm.publish.dsl

import dev.petuska.npm.publish.util.npmFullName

class NpmShrinkwrapJson(
  name: String,
  version: String,
  scope: String? = null,
  config: NpmShrinkwrapJson.() -> Unit = {}
) : JsonObject<Any>() {
  /**
   * [name](https://docs.npmjs.com/files/package.json#name)
   */
  var name: String? by this

  /**
   * [version](https://docs.npmjs.com/files/package.json#version)
   */
  var version: String? by this

  /**
   * Defaults to 1.
   */
  var lockfileVersion: Int? by this

  /**
   * Defaults to true
   */
  var requires: Boolean? by this

  /**
   * Shrinkwrap dependencies container.
   */
  var dependencies: JsonObject<ShrinkwrapDependency>? by this

  /**
   * Creates or extends shrinkwrap dependencies config.
   */
  fun dependencies(config: JsonObject<ShrinkwrapDependency>.() -> Unit = {}) =
    (dependencies ?: JsonObject()).apply(config).also { dependencies = it }

  /**
   * Creates and adds a shrinkwrap dependency.
   */
  fun JsonObject<ShrinkwrapDependency>.dependency(
    name: String,
    version: String,
    bundled: Boolean
  ) = ShrinkwrapDependency {
    this.version = version
    this.bundled = bundled
  }.also {
    this[name] = it
  }

  init {
    this.name = npmFullName(name, scope)
    this.version = version
    this.lockfileVersion = 1
    this.requires = true
    this.apply(config)
  }

  inner class ShrinkwrapDependency(config: ShrinkwrapDependency.() -> Unit = {}) : JsonObject<Any>() {
    var version: String? by this
    var bundled: Boolean? by this

    init {
      config()
    }
  }
}
