import sandbox.GreetingArgs
import sandbox.greet

@JsModule("is-odd")
external object IsOdd

@JsExport
fun empty() = greet(object : GreetingArgs {
  override val name: String = "Joe"
  override val sureName: String = "Mama"
}).also { console.log(IsOdd) }