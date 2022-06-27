package dev.petuska.npm.publish.test.util

class BuildFileBuilder(config: BuildFileBuilder.() -> Unit) : ScriptBuilder() {
  private val imports = mutableSetOf<String>()
  val plugins = Plugins(2)
  val repositories = Repositories(2)

  fun import(id: String) {
    imports.add(id)
  }

  override fun toString(): String {
    return ScriptBuilder().apply {
      imports.forEach { +"import $it" }
      "plugins" {
        +plugins.toString()
      }
      "repositories" {
        +repositories.toString()
      }
      +super.toString()
    }.toString()
  }

  init {
    plugins {
      npmPublish()
    }
    repositories {
      mavenCentral()
      gradlePluginPortal()
    }
    config()
  }

  class Plugins(baseIndent: Int = 0) : ScriptBuilder(baseIndent) {
    fun id(id: String, version: String? = null) {
      +("""id("$id")""" + (version?.let { """ version "$it"""" } ?: ""))
    }

    fun npmPublish(version: String? = null) {
      id("dev.petuska.npm.publish", version)
    }

    fun kotlinMultiplatform(version: String? = null) {
      id("org.jetbrains.kotlin.multiplatform", version)
    }

    fun kotlinJs(version: String? = null) {
      id("org.jetbrains.kotlin.js", version)
    }
  }

  class Repositories(baseIndent: Int = 0) : ScriptBuilder(baseIndent) {
    fun mavenCentral() {
      +"mavenCentral()"
    }

    fun gradlePluginPortal() {
      +"gradlePluginPortal()"
    }

    fun maven(uri: String) {
      +"""maven("$uri")"""
    }
  }
}
