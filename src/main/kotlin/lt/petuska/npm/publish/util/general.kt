package lt.petuska.npm.publish.util

fun String.notFalse() = !equals("false", true)

fun npmFullName(name: String, scope: String?) = "${scope?.let { "@${it.trim()}/" } ?: ""}${name.trim()}"
