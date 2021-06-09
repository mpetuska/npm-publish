package test.sandbox

import kotlin.js.JsExport

@JsExport
fun sayHello(name: Name = "Mr. PP Trump"): Name = "Hello from $name".also { println(it) }

@JsExport
fun sayFormalHello(person: Person): Name = "Hello from ${person.name} ${person.sureName}".also { println(it) }

typealias Name = String

// Interfaces must be external, otherwise TS declarations will export properties as fields,
// while backing JS will have them as getters (Person.get_name()) instead of fields (Person.name)
@JsExport
external interface Person {
  val name: String
  val sureName: String
}

fun main() {
  println("Hey")
}