package test.sandbox

@JsExport
fun sayHello(name: Name = "Mr. PP Trump"): Name = "Hello from $name".also { println(it) }

@JsExport
fun sayFormalHello(person: Person): Name = "Hello from ${person.name} ${person.sureName}".also { println(it) }

@JsExport
fun sayFormalBuggedHello(person: BuggedPerson): Name {
  val safeStr = "Safe hello from ${person.getSafeName()}"
  println(safeStr)
  val str = "Hello from ${person.name} ${person.sureName}"
  println(str)
  return str
}

typealias Name = String

// Interfaces must be external, otherwise TS declarations will export properties as fields,
// while backing JS will have them as getters (Person.get_name()) instead of fields (Person.name)
@JsExport
external interface Person {
  val name: String
  val sureName: String
}

@JsExport
interface ExtendedPerson : Person {
  override val name: String
}

@JsExport
interface BuggedPerson {
  fun getSafeName(): String
  val name: String
  val sureName: String
}
