package dev.petuska.npm.publish.dsl

import com.google.gson.GsonBuilder
import com.google.gson.annotations.Expose
import dev.petuska.kon.core.TypedKON
import dev.petuska.kon.core.TypedKObject
import dev.petuska.kon.core.karr
import dev.petuska.kon.core.kobj
import dev.petuska.npm.publish.util.Builder
import dev.petuska.npm.publish.util.npmFullName
import org.jetbrains.kotlin.gradle.internal.ensureParentDirsCreated
import java.io.File
import java.io.Serializable
import kotlin.reflect.KProperty

@Suppress("UNCHECKED_CAST")
internal fun JsonObject<Any>.overrideFrom(other: JsonObject<Any>): JsonObject<Any> = also {
  other.entries.forEach { (key, new) ->
    val old = this[key]
    this[key] = when {
      old == null -> new
      old is JsonObject<*> && new is JsonObject<*> -> (old as JsonObject<Any>).overrideFrom(new as JsonObject<Any>)
      else -> new
    }
  }
}

@DslMarker
annotation class PackageJsonDsl

/** Utility class for building Json Trees */
open class JsonObject<T>(seed: TypedKON<T?>? = null) : TypedKObject<T?> by kobj(seed ?: mutableMapOf()), Serializable {
  /** Creates a Json Object */
  @PackageJsonDsl
  fun jsonObject(block: Builder<JsonObject<Any>>) = JsonObject(block)

/** Creates a Json Array */
  @PackageJsonDsl
  fun <V> jsonArray(vararg elements: V) = karr(*elements)

/** Creates a Json Set */
  @PackageJsonDsl
  fun <V> jsonSet(vararg elements: V) = mutableSetOf(*elements)

/** Stringifies the current state of the object into a Json string */
  override fun toString(): String = gson.toJson(this)

  companion object {
    internal val gson = GsonBuilder().setPrettyPrinting().create()

/** Creates a Json Object */
    @PackageJsonDsl
    operator fun <V> invoke(seed: TypedKON<V?>? = null, block: Builder<JsonObject<V>>) =
      JsonObject(seed).apply(block)

/** Creates a Json Array */
    @PackageJsonDsl
    operator fun <V> invoke(vararg elements: V) = karr(*elements)
  }
}

/** Writes the current state of the object into a file as json string */
fun <T : JsonObject<*>> T.writeTo(packageJsonFile: File) =
  this.also {
    packageJsonFile.ensureParentDirsCreated()
    packageJsonFile.writer().use { JsonObject.gson.toJson(this as MutableMap<*, *>, it) }
  }

operator fun <R : Any?> JsonObject<Any>.getValue(thisRef: JsonObject<Any>, property: KProperty<*>): R? {
  @Suppress("UNCHECKED_CAST")
  return thisRef[property.name] as? R
}

operator fun <R : Any> JsonObject<Any>.setValue(
  thisRef: JsonObject<Any>,
  property: KProperty<*>,
  value: R?
) {
  thisRef[property.name] = value
}

/**
 * A class representing [package.json](https://docs.npmjs.com/files/package.json) schema. Custom
 * fields can be added as regular map entries.
 */
class PackageJson() : JsonObject<Any>() {
  constructor(
    name: String,
    version: String?,
    scope: String? = null,
    config: Builder<PackageJson> = {}
  ) : this() {
    this.name = npmFullName(name, scope)
    this.version = version
    this.apply(config)
  }

/** [name](https://docs.npmjs.com/files/package.json#name) */
  var name: String? by this

/** [version](https://docs.npmjs.com/files/package.json#version) */
  var version: String? by this

/** [description](https://docs.npmjs.com/files/package.json#description-1) */
  var description: String? by this

/** [keywords](https://docs.npmjs.com/files/package.json#keywords) */
  var keywords: MutableList<String>? by this

/** [homepage](https://docs.npmjs.com/files/package.json#homepage) */
  var homepage: String? by this

/** [bugs](https://docs.npmjs.com/files/package.json#bugs) */
  var bugs: TypedKON<Any?>? by this

/** [bugs](https://docs.npmjs.com/files/package.json#bugs) */
  fun bugs(config: Builder<Bugs> = {}) = Bugs(bugs, config).also { bugs = it }

/** [license](https://docs.npmjs.com/files/package.json#license) */
  var license: String? by this

/** [author](https://docs.npmjs.com/files/package.json#people-fields-author-contributors) */
  var author: TypedKON<Any?>? by this

/** [author](https://docs.npmjs.com/files/package.json#people-fields-author-contributors) */
  @PackageJsonDsl
  fun author(config: Builder<Person> = {}) = Person(author, config).also { author = it }

/** [contributors](https://docs.npmjs.com/files/package.json#people-fields-author-contributors) */
  var contributors: MutableList<Person>? by this

/** [contributors](https://docs.npmjs.com/files/package.json#people-fields-author-contributors) */
  @PackageJsonDsl
  fun MutableList<Person>.contributor(config: Builder<Person> = {}) =
    Person(config = config).also { add(it) }

/** [files](https://docs.npmjs.com/files/package.json#files) */
  var files: MutableList<String>? by this

/** [main](https://docs.npmjs.com/files/package.json#main) */
  var main: String? by this

/** [types](https://www.typescriptlang.org/docs/handbook/declaration-files/publishing.html) */
  var types: String? by this

/** [browser](https://docs.npmjs.com/files/package.json#browser) */
  var browser: String? by this

/** [bin](https://docs.npmjs.com/files/package.json#bin) */
  var bin: TypedKON<String>? by this

/** [man](https://docs.npmjs.com/files/package.json#man) */
  var man: TypedKON<String>? by this

/** [directories](https://docs.npmjs.com/files/package.json#directories) */
  var directories: TypedKON<Any?>? by this

/** [directories](https://docs.npmjs.com/files/package.json#directories) */
  @PackageJsonDsl
  fun directories(config: Builder<Directories> = {}) =
    Directories(directories, config).also { directories = it }

/** [repository](https://docs.npmjs.com/files/package.json#repository) */
  var repository: TypedKON<Any?>? by this

/** [repository](https://docs.npmjs.com/files/package.json#repository) */
  @PackageJsonDsl
  fun repository(config: Builder<Repository> = {}) =
    Repository(repository, config).also { repository = it }

/** [scripts](https://docs.npmjs.com/files/package.json#scripts) */
  var scripts: TypedKON<String>? by this

/** [config](https://docs.npmjs.com/files/package.json#config) */
  var config: TypedKON<Any>? by this

/** [dependencies](https://docs.npmjs.com/files/package.json#dependencies) */
  var dependencies: TypedKON<String?>? by this

/** [dependencies](https://docs.npmjs.com/files/package.json#dependencies) */
  @PackageJsonDsl
  fun dependencies(config: Builder<JsonObject<String>> = {}) =
    JsonObject(dependencies, config).also { dependencies = it }

/** [devDependencies](https://docs.npmjs.com/files/package.json#devdependencies) */
  var devDependencies: TypedKON<String?>? by this

/** [devDependencies](https://docs.npmjs.com/files/package.json#devdependencies) */
  @PackageJsonDsl
  fun devDependencies(config: Builder<JsonObject<String>> = {}) =
    JsonObject(devDependencies, config).also { devDependencies = it }

/** [peerDependencies](https://docs.npmjs.com/files/package.json#peerdependencies) */
  var peerDependencies: TypedKON<String?>? by this

/** [peerDependencies](https://docs.npmjs.com/files/package.json#peerdependencies) */
  @PackageJsonDsl
  fun peerDependencies(config: Builder<JsonObject<String>> = {}) =
    JsonObject(peerDependencies, config).also { peerDependencies = it }

/** [optionalDependencies](https://docs.npmjs.com/files/package.json#optionaldependencies) */
  var optionalDependencies: TypedKON<String?>? by this

/** [optionalDependencies](https://docs.npmjs.com/files/package.json#optionaldependencies) */
  @PackageJsonDsl
  fun optionalDependencies(config: Builder<JsonObject<String>> = {}) =
    JsonObject(optionalDependencies, config).also { optionalDependencies = it }

/**
   * [bundledDependencies](https://docs.npmjs.com/files/package.json#bundleddependencies) Top
   * priority if set, disregards all other configurations
   */
  var bundledDependencies: MutableSet<String>? by this

  @field:Expose(serialize = false, deserialize = false)
  internal var bundledDependenciesSpec: BundledDependenciesSpec? = null

/**
   * Appends bundled dependencies configuration. Ignored if [bundledDependencies] property is set
   * For auto-generated publications, kotlin-test* dependencies are excluded by default
   *
   * @param mustBundle a list of dependencies to bundle regardless of the config
   * @param config include/exclude spec to filter out bundled dependencies
   */
  @PackageJsonDsl
  fun bundledDependencies(vararg mustBundle: String, config: Builder<BundledDependenciesSpec>) {
    val target = bundledDependenciesSpec ?: BundledDependenciesSpec()
    target.apply(config)
    target.apply { mustBundle.forEach { +it } }
    bundledDependenciesSpec = target
  }

/** [engines](https://docs.npmjs.com/files/package.json#engines) */
  var engines: TypedKON<String>? by this

/** [os](https://docs.npmjs.com/files/package.json#os) */
  var os: MutableList<String>? by this

/** [cpu](https://docs.npmjs.com/files/package.json#cpu) */
  var cpu: MutableList<String>? by this

/** [private](https://docs.npmjs.com/files/package.json#private) */
  var private: Boolean? by this

/** [publishConfig](https://docs.npmjs.com/files/package.json#publishconfig) */
  var publishConfig: TypedKON<Any?>? by this

/** [publishConfig](https://docs.npmjs.com/files/package.json#publishconfig) */
  @PackageJsonDsl
  fun publishConfig(config: Builder<PublishConfig> = {}) =
    PublishConfig(publishConfig, config).also { publishConfig = it }

  inner class BundledDependenciesSpec(config: (Builder<BundledDependenciesSpec>)? = null) {
    private val specs: MutableList<(MutableSet<String>) -> Unit> = mutableListOf()

    init {
      config?.invoke(this)
    }

/** Includes a given dependency */
    @PackageJsonDsl
    operator fun String.unaryPlus() {
      specs.add { it.add(this) }
    }

/** Excludes a given dependency */
    @PackageJsonDsl
    operator fun String.unaryMinus() {
      specs.add { it.remove(this) }
    }

/** Includes a given dependencies by regex. Should not be used with regex exclusion. */
    @PackageJsonDsl
    operator fun Regex.unaryPlus() {
      specs.add { it.removeIf { dep -> !matches(dep) } }
    }

/** Excludes given dependencies by regex. Should not be used with regex inclusion. */
    @PackageJsonDsl
    operator fun Regex.unaryMinus() {
      specs.add { it.removeIf { dep -> matches(dep) } }
    }

/** Applies this spec to the given dependencies set. */
    internal fun applyTo(set: MutableSet<String>): MutableSet<String> =
      set.apply { specs.forEach { it(this) } }
  }

/** [bugs](https://docs.npmjs.com/files/package.json#bugs) */
  inner class Bugs(seed: TypedKON<Any?>? = null, config: Builder<Bugs> = {}) : JsonObject<Any>(seed) {
    var url: String? by this
    var email: String? by this

    init {
      config()
    }
  }

/** [people field](https://docs.npmjs.com/files/package.json#people-fields-author-contributors) */
  inner class Person(seed: TypedKON<Any?>? = null, config: Builder<Person> = {}) : JsonObject<Any>(seed) {
    var name: String? by this
    var email: String? by this
    var url: String? by this

    init {
      config()
    }
  }

/** [directories](https://docs.npmjs.com/files/package.json#directories) */
  inner class Directories(seed: TypedKON<Any?>? = null, config: Builder<Directories> = {}) : JsonObject<Any>(seed) {
    /** [lib](https://docs.npmjs.com/files/package.json#directorieslib) */
    var lib: String? by this

/** [bin](https://docs.npmjs.com/files/package.json#directoriesbin) */
    var bin: String? by this

/** [man](https://docs.npmjs.com/files/package.json#directoriesman) */
    var man: String? by this

/** [doc](https://docs.npmjs.com/files/package.json#directoriesdoc) */
    var doc: String? by this

/** [example](https://docs.npmjs.com/files/package.json#directoriesexample) */
    var example: String? by this

/** [test](https://docs.npmjs.com/files/package.json#directoriestest) */
    var test: String? by this

    init {
      config()
    }
  }

/** [repository](https://docs.npmjs.com/files/package.json#repository) */
  inner class Repository(seed: TypedKON<Any?>? = null, config: Builder<Repository> = {}) : JsonObject<Any>(seed) {
    var type: String? by this
    var url: String? by this
    var directory: String? by this

    init {
      config()
    }
  }

/** [publish config](https://docs.npmjs.com/files/package.json#publishconfig) */
  inner class PublishConfig(seed: TypedKON<Any?>? = null, config: Builder<PublishConfig> = {}) :
    JsonObject<Any>(seed) {
    var registry: String? by this
    var access: String? by this
    var tag: String? by this

    init {
      config()
    }
  }
}
