package dev.petuska.npm.publish.dsl

import com.google.gson.GsonBuilder
import com.google.gson.annotations.Expose
import dev.petuska.npm.publish.util.npmFullName
import org.jetbrains.kotlin.gradle.internal.ensureParentDirsCreated
import java.io.File
import java.io.Serializable
import kotlin.reflect.KProperty

typealias Record<T> = MutableMap<String, T?>

/**
 * Utility class for building Json Trees
 */
open class JsonObject<T>(seed: Record<T>? = null) : Record<T> by seed ?: mutableMapOf(), Serializable {
  /**
   * Creates a Json Object
   */
  fun jsonObject(block: JsonObject<Any>.() -> Unit) = JsonObject(block)

  /**
   * Creates a Json Array
   */
  fun <V> jsonArray(vararg elements: V) = mutableListOf(*elements)

  /**
   * Creates a Json Set
   */
  fun <V> jsonSet(vararg elements: V) = mutableSetOf(*elements)

  /**
   * Assigns a json value as `[this] = [value]`
   */
  infix fun String.to(value: T?) {
    this@JsonObject[this] = value
  }

  /**
   * Stringifies the current state of the object into a Json string
   */
  override fun toString(): String = gson.toJson(this)

  companion object {
    internal val gson = GsonBuilder()
      .setPrettyPrinting()
      .create()

    /**
     * Creates a Json Object
     */
    operator fun <V> invoke(seed: Record<V>? = null, block: JsonObject<V>.() -> Unit) = JsonObject<V>(seed).apply(block)

    /**
     * Creates a Json Array
     */
    operator fun <V> invoke(vararg elements: V) = mutableListOf(*elements)
  }
}

/**
 * Writes the current state of the object into a file as json string
 */
fun <T : JsonObject<*>> T.writeTo(packageJsonFile: File) = this.also {
  packageJsonFile.ensureParentDirsCreated()
  packageJsonFile.writer().use {
    JsonObject.gson.toJson(this as MutableMap<*, *>, it)
  }
}

operator fun <R> JsonObject<Any>.getValue(thisRef: JsonObject<Any>, property: KProperty<*>): R? {
  @Suppress("UNCHECKED_CAST")
  return thisRef[property.name] as? R
}

operator fun <R> JsonObject<Any>.setValue(thisRef: JsonObject<Any>, property: KProperty<*>, value: R?) {
  thisRef[property.name] = value
}

/**
 * A class representing [package.json](https://docs.npmjs.com/files/package.json) schema. Custom fields can be added as regular map entries.
 */
class PackageJson() : JsonObject<Any>() {
  constructor(name: String, version: String?, scope: String? = null, config: PackageJson.() -> Unit = {}) : this() {
    this.name = npmFullName(name, scope)
    this.version = version
    this.apply(config)
  }

  /**
   * [name](https://docs.npmjs.com/files/package.json#name)
   */
  var name: String? by this

  /**
   * [version](https://docs.npmjs.com/files/package.json#version)
   */
  var version: String? by this

  /**
   * [description](https://docs.npmjs.com/files/package.json#description-1)
   */
  var description: String? by this

  /**
   * [keywords](https://docs.npmjs.com/files/package.json#keywords)
   */
  var keywords: MutableList<String>? by this

  /**
   * [homepage](https://docs.npmjs.com/files/package.json#homepage)
   */
  var homepage: String? by this

  /**
   * [bugs](https://docs.npmjs.com/files/package.json#bugs)
   */
  var bugs: Record<Any>? by this

  /**
   * [bugs](https://docs.npmjs.com/files/package.json#bugs)
   */
  fun bugs(config: Bugs.() -> Unit = {}) = Bugs(bugs, config).also { bugs = it }

  /**
   * [licence](https://docs.npmjs.com/files/package.json#license)
   */
  var licence: String? by this

  /**
   * [author](https://docs.npmjs.com/files/package.json#people-fields-author-contributors)
   */
  var author: Record<Any>? by this

  /**
   * [author](https://docs.npmjs.com/files/package.json#people-fields-author-contributors)
   */
  fun author(config: Person.() -> Unit = {}) = Person(author, config).also { author = it }

  /**
   * [contributors](https://docs.npmjs.com/files/package.json#people-fields-author-contributors)
   */
  var contributors: MutableList<Person>? by this

  /**
   * [contributors](https://docs.npmjs.com/files/package.json#people-fields-author-contributors)
   */
  fun MutableList<Person>.contributor(config: Person.() -> Unit = {}) = Person(config = config).also { add(it) }

  /**
   * [files](https://docs.npmjs.com/files/package.json#files)
   */
  var files: MutableList<String>? by this

  /**
   * [main](https://docs.npmjs.com/files/package.json#main)
   */
  var main: String? by this

  /**
   * [types](https://www.typescriptlang.org/docs/handbook/declaration-files/publishing.html)
   */
  var types: String? by this

  /**
   * [browser](https://docs.npmjs.com/files/package.json#browser)
   */
  var browser: String? by this

  /**
   * [bin](https://docs.npmjs.com/files/package.json#bin)
   */
  var bin: Record<String>? by this

  /**
   * [man](https://docs.npmjs.com/files/package.json#man)
   */
  var man: Record<String>? by this

  /**
   * [directories](https://docs.npmjs.com/files/package.json#directories)
   */
  var directories: Record<Any>? by this

  /**
   * [directories](https://docs.npmjs.com/files/package.json#directories)
   */
  fun directories(config: Directories.() -> Unit = {}) = Directories(directories, config).also { directories = it }

  /**
   * [repository](https://docs.npmjs.com/files/package.json#repository)
   */
  var repository: Record<Any>? by this

  /**
   * [repository](https://docs.npmjs.com/files/package.json#repository)
   */
  fun repository(config: Repository.() -> Unit = {}) = Repository(repository, config).also { repository = it }

  /**
   * [scripts](https://docs.npmjs.com/files/package.json#scripts)
   */
  var scripts: Record<String>? by this

  /**
   * [config](https://docs.npmjs.com/files/package.json#config)
   */
  var config: Record<Any>? by this

  /**
   * [dependencies](https://docs.npmjs.com/files/package.json#dependencies)
   */
  var dependencies: Record<String>? by this

  /**
   * [dependencies](https://docs.npmjs.com/files/package.json#dependencies)
   */
  fun dependencies(config: JsonObject<String>.() -> Unit = {}) =
    JsonObject(dependencies, config).also { dependencies = it }

  /**
   * [devDependencies](https://docs.npmjs.com/files/package.json#devdependencies)
   */
  var devDependencies: Record<String>? by this

  /**
   * [devDependencies](https://docs.npmjs.com/files/package.json#devdependencies)
   */
  fun devDependencies(config: JsonObject<String>.() -> Unit = {}) =
    JsonObject(devDependencies, config).also { devDependencies = it }

  /**
   * [peerDependencies](https://docs.npmjs.com/files/package.json#peerdependencies)
   */
  var peerDependencies: Record<String>? by this

  /**
   * [peerDependencies](https://docs.npmjs.com/files/package.json#peerdependencies)
   */
  fun peerDependencies(config: JsonObject<String>.() -> Unit = {}) =
    JsonObject(peerDependencies, config).also { peerDependencies = it }

  /**
   * [optionalDependencies](https://docs.npmjs.com/files/package.json#optionaldependencies)
   */
  var optionalDependencies: Record<String>? by this

  /**
   * [optionalDependencies](https://docs.npmjs.com/files/package.json#optionaldependencies)
   */
  fun optionalDependencies(config: JsonObject<String>.() -> Unit = {}) =
    JsonObject(optionalDependencies, config).also { optionalDependencies = it }

  /**
   * [bundledDependencies](https://docs.npmjs.com/files/package.json#bundleddependencies)
   * Top priority if set, disregards all other configurations
   */
  var bundledDependencies: MutableSet<String>? by this

  @field:Expose(serialize = false, deserialize = false)
  internal var bundledDependenciesSpec: BundledDependenciesSpec? = null

  /**
   * Appends bundled dependencies configuration.
   * Ignored if [bundledDependencies] property is set
   * For auto-generated publications, kotlin-test* dependencies are excluded by default
   *
   * @param mustBundle a list of dependencies to bundle regardless of the config
   * @param config include/exclude spec to filter out bundled dependencies
   */
  fun bundledDependencies(vararg mustBundle: String, config: BundledDependenciesSpec.() -> Unit) {
    val target = bundledDependenciesSpec ?: BundledDependenciesSpec()
    target.apply(config)
    target.apply {
      mustBundle.forEach { +it }
    }
    bundledDependenciesSpec = target
  }

  /**
   * [engines](https://docs.npmjs.com/files/package.json#engines)
   */
  var engines: Record<String>? by this

  /**
   * [os](https://docs.npmjs.com/files/package.json#os)
   */
  var os: MutableList<String>? by this

  /**
   * [cpu](https://docs.npmjs.com/files/package.json#cpu)
   */
  var cpu: MutableList<String>? by this

  /**
   * [private](https://docs.npmjs.com/files/package.json#private)
   */
  var private: Boolean? by this

  /**
   * [publishConfig](https://docs.npmjs.com/files/package.json#publishconfig)
   */
  var publishConfig: Record<Any>? by this

  /**
   * [publishConfig](https://docs.npmjs.com/files/package.json#publishconfig)
   */
  fun publishConfig(config: PublishConfig.() -> Unit = {}) =
    PublishConfig(publishConfig, config).also { publishConfig = it }

  inner class BundledDependenciesSpec(config: (BundledDependenciesSpec.() -> Unit)? = null) {
    private val specs: MutableList<(MutableSet<String>) -> Unit> = mutableListOf()

    init {
      config?.invoke(this)
    }

    /**
     * Includes a given dependency
     */
    operator fun String.unaryPlus() {
      specs.add { it.add(this) }
    }

    /**
     * Excludes a given dependency
     */
    operator fun String.unaryMinus() {
      specs.add { it.remove(this) }
    }

    /**
     * Includes a given dependencies by regex. Should not be used with regex exclusion.
     */
    operator fun Regex.unaryPlus() {
      specs.add { it.removeIf { dep -> !matches(dep) } }
    }

    /**
     * Excludes given dependencies by regex. Should not be used with regex inclusion.
     */
    operator fun Regex.unaryMinus() {
      specs.add { it.removeIf { dep -> matches(dep) } }
    }

    /**
     * Applies this spec to the given dependencies set.
     */
    fun applyTo(set: MutableSet<String>): MutableSet<String> = set.apply {
      specs.forEach { it(this) }
    }
  }

  /**
   * [bugs](https://docs.npmjs.com/files/package.json#bugs)
   */
  inner class Bugs(seed: Record<Any>? = null, config: Bugs.() -> Unit = {}) : JsonObject<Any>(seed) {
    var url: String? by this
    var email: String? by this

    init {
      config()
    }
  }

  /**
   * [people field](https://docs.npmjs.com/files/package.json#people-fields-author-contributors)
   */
  inner class Person(seed: Record<Any>? = null, config: Person.() -> Unit = {}) : JsonObject<Any>(seed) {
    var name: String? by this
    var email: String? by this
    var url: String? by this

    init {
      config()
    }
  }

  /**
   * [directories](https://docs.npmjs.com/files/package.json#directories)
   */
  inner class Directories(seed: Record<Any>? = null, config: Directories.() -> Unit = {}) : JsonObject<Any>(seed) {
    /**
     * [lib](https://docs.npmjs.com/files/package.json#directorieslib)
     */
    var lib: String? by this

    /**
     * [bin](https://docs.npmjs.com/files/package.json#directoriesbin)
     */
    var bin: String? by this

    /**
     * [man](https://docs.npmjs.com/files/package.json#directoriesman)
     */
    var man: String? by this

    /**
     * [doc](https://docs.npmjs.com/files/package.json#directoriesdoc)
     */
    var doc: String? by this

    /**
     * [example](https://docs.npmjs.com/files/package.json#directoriesexample)
     */
    var example: String? by this

    /**
     * [test](https://docs.npmjs.com/files/package.json#directoriestest)
     */
    var test: String? by this

    init {
      config()
    }
  }

  /**
   * [repository](https://docs.npmjs.com/files/package.json#repository)
   */
  inner class Repository(seed: Record<Any>? = null, config: Repository.() -> Unit = {}) : JsonObject<Any>(seed) {
    var type: String? by this
    var url: String? by this
    var directory: String? by this

    init {
      config()
    }
  }

  /**
   * [publish config](https://docs.npmjs.com/files/package.json#publishconfig)
   */
  inner class PublishConfig(seed: Record<Any>? = null, config: PublishConfig.() -> Unit = {}) : JsonObject<Any>(seed) {
    var registry: String? by this
    var access: String? by this
    var tag: String? by this

    init {
      config()
    }
  }
}
