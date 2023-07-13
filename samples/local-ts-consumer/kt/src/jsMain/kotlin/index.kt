@file:OptIn(ExperimentalJsExport::class)

package dev.petuska.samples.ts

fun greeting(name: String): String = "Hello $name"

@JsExport
fun greet(name: String) {
  println(greeting(name))
}
