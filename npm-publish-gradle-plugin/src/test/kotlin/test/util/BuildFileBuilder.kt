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
    fun id(id: String) {
      +"""id("$id")"""
    }

    fun npmPublish() {
      id("dev.petuska.npm.publish")
    }

    fun kotlinMultiplatform() {
      id("org.jetbrains.kotlin.multiplatform")
    }

    fun kotlinJs() {
      id("org.jetbrains.kotlin.js")
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
