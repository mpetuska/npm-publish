package dev.petuska.npm.publish.extension.domain.json

import org.gradle.api.Action
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.SetProperty
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Nested
import org.gradle.api.tasks.Optional

/**
 * A class representing [package.json](https://docs.npmjs.com/files/package.json) schema. Custom
 * fields can be added as regular map entries.
 */
@Suppress("MemberVisibilityCanBePrivate")
public abstract class PackageJson : JsonObject<Any>() {

  /** [name](https://docs.npmjs.com/files/package.json#name) */
  @get:Input
  @get:Optional
  public abstract val name: Property<String>

  /** [version](https://docs.npmjs.com/files/package.json#version) */
  @get:Input
  @get:Optional
  public abstract val version: Property<String>

  /** [description](https://docs.npmjs.com/files/package.json#description-1) */
  @get:Input
  @get:Optional
  public abstract val description: Property<String>

  /** [keywords](https://docs.npmjs.com/files/package.json#keywords) */
  @get:Input
  @get:Optional
  public abstract val keywords: SetProperty<String>

  /** [homepage](https://docs.npmjs.com/files/package.json#homepage) */
  @get:Input
  @get:Optional
  public abstract val homepage: Property<String>

  /** [license](https://docs.npmjs.com/files/package.json#license) */
  @get:Input
  @get:Optional
  public abstract val license: Property<String>

  /** [main](https://docs.npmjs.com/files/package.json#main) */
  @get:Input
  @get:Optional
  public abstract val main: Property<String>

  /** [types](https://www.typescriptlang.org/docs/handbook/declaration-files/publishing.html) */
  @get:Input
  @get:Optional
  public abstract val types: Property<String>

  /** [browser](https://docs.npmjs.com/files/package.json#browser) */
  @get:Input
  @get:Optional
  public abstract val browser: Property<String>

  /** [private](https://docs.npmjs.com/files/package.json#private) */
  @get:Input
  @get:Optional
  public abstract val private: Property<Boolean>

  /** [files](https://docs.npmjs.com/files/package.json#files) */
  @get:Input
  @get:Optional
  public abstract val files: SetProperty<String>

  /** [os](https://docs.npmjs.com/files/package.json#os) */
  @get:Input
  @get:Optional
  public abstract val os: ListProperty<String>

  /** [cpu](https://docs.npmjs.com/files/package.json#cpu) */
  @get:Input
  @get:Optional
  public abstract val cpu: ListProperty<String>

  /** [bugs](https://docs.npmjs.com/files/package.json#bugs) */
  @get:Nested
  @get:Optional
  public abstract val bugs: Property<Bugs>

  /** [author](https://docs.npmjs.com/files/package.json#people-fields-author-contributors) */
  @get:Nested
  @get:Optional
  public abstract val author: Property<Person>

  /** [contributors](https://docs.npmjs.com/files/package.json#people-fields-author-contributors) */
  @get:Nested
  @get:Optional
  public abstract val contributors: SetProperty<Person>

  /** [bin](https://docs.npmjs.com/files/package.json#bin) */
  @get:Nested
  @get:Optional
  public abstract val bin: Property<JsonObject<String>>

  /** [man](https://docs.npmjs.com/files/package.json#man) */
  @get:Nested
  @get:Optional
  public abstract val man: Property<JsonObject<String>>

  /** [directories](https://docs.npmjs.com/files/package.json#directories) */
  @get:Nested
  @get:Optional
  public abstract val directories: Property<Directories>

  /** [repository](https://docs.npmjs.com/files/package.json#repository) */
  @get:Nested
  @get:Optional
  public abstract val repository: Property<Repository>

  /** [scripts](https://docs.npmjs.com/files/package.json#scripts) */
  @get:Nested
  @get:Optional
  public abstract val scripts: Property<JsonObject<String>>

  /** [config](https://docs.npmjs.com/files/package.json#config) */
  @get:Nested
  @get:Optional
  public abstract val config: Property<JsonObject<Any>>

  /** [engines](https://docs.npmjs.com/files/package.json#engines) */
  @get:Nested
  @get:Optional
  public abstract val engines: Property<JsonObject<String>>

  /** [publishConfig](https://docs.npmjs.com/files/package.json#publishconfig) */
  @get:Nested
  @get:Optional
  public abstract val publishConfig: Property<PublishConfig>

  /** [dependencies](https://docs.npmjs.com/files/package.json#dependencies) */
  @get:Nested
  @get:Optional
  public abstract val dependencies: Property<JsonObject<String>>

  /** [devDependencies](https://docs.npmjs.com/files/package.json#devdependencies) */
  @get:Nested
  @get:Optional
  public abstract val devDependencies: Property<JsonObject<String>>

  /** [peerDependencies](https://docs.npmjs.com/files/package.json#peerdependencies) */
  @get:Nested
  @get:Optional
  public abstract val peerDependencies: Property<JsonObject<String>>

  /** [optionalDependencies](https://docs.npmjs.com/files/package.json#optionaldependencies) */
  @get:Nested
  @get:Optional
  public abstract val optionalDependencies: Property<JsonObject<String>>

  /**
   * [bundledDependencies](https://docs.npmjs.com/files/package.json#bundleddependencies)
   * Top priority if set, disregards all the other configurations
   */
  @get:Input
  @get:Optional
  public abstract val bundledDependencies: SetProperty<String>

  @Suppress("ComplexMethod")
  override fun finalise(): MutableMap<String, Any> = super.finalise().apply {
    name.finalOrNull?.let { put("name", it) }
    version.finalOrNull?.let { put("version", it) }
    description.finalOrNull?.let { put("description", it) }
    homepage.finalOrNull?.let { put("homepage", it) }
    license.finalOrNull?.let { put("license", it) }
    main.finalOrNull?.let { put("main", it) }
    types.finalOrNull?.let { put("types", it) }
    browser.finalOrNull?.let { put("browser", it) }
    private.finalOrNull?.let { put("private", it) }
    author.finalOrNull?.let { put("author", it.finalise()) }
    keywords.final?.let { put("keywords", it) }
    files.final?.let { put("files", it) }
    os.final?.let { put("os", it) }
    cpu.final?.let { put("cpu", it) }
    bugs.finalOrNull?.let { put("bugs", it.finalise()) }
    contributors.final?.let { put("contributors", it.map(Person::finalise)) }
    bin.finalOrNull?.let { put("bin", it.finalise()) }
    man.finalOrNull?.let { put("man", it.finalise()) }
    directories.finalOrNull?.let { put("directories", it.finalise()) }
    repository.finalOrNull?.let { put("repository", it.finalise()) }
    scripts.finalOrNull?.let { put("scripts", it.finalise()) }
    config.finalOrNull?.let { put("config", it.finalise()) }
    engines.finalOrNull?.let { put("engines", it.finalise()) }
    publishConfig.finalOrNull?.let { put("publishConfig", it.finalise()) }
    dependencies.finalOrNull?.let { put("dependencies", it.finalise()) }
    devDependencies.finalOrNull?.let { put("devDependencies", it.finalise()) }
    peerDependencies.finalOrNull?.let { put("peerDependencies", it.finalise()) }
    optionalDependencies.finalOrNull?.let { put("optionalDependencies", it.finalise()) }
    bundledDependencies.final?.let { put("bundledDependencies", it) }
  }

  // region DSL

  /**
   * Override and configure the author field
   * @see [author]
   */
  public fun author(action: Action<Person>) {
    author.configure(action)
  }

  /**
   * Override and configure the repository field
   * @see [repository]
   */
  public fun repository(action: Action<Repository>) {
    repository.configure(action)
  }

  // endregion
}
