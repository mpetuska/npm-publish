package test.sandbox

@JsExport
fun sayHello(name: String = "Mr. PP Trump"): String = "Hello from $name".also { println(it) }

fun main(args: Array<String>) {
  println(args)
  sayHello()
}
