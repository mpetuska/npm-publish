package dev.petuska.npm.publish.util

import org.gradle.api.Project

fun String.notFalse() = !equals("false", true)

fun npmFullName(name: String, scope: String?) = "${scope?.let { "@${it.trim()}/" } ?: ""}${name.trim()}"
fun <T> Project.propertyOrNull(name: String): T? = if (hasProperty(name)) {
  @Suppress("UNCHECKED_CAST")
  property(name) as? T
} else null
