package sandbox

external interface GreetingArgs {
  val name: String
  val sureName: String
}

@JsModule("is-even")
@JsName("default")
private external fun isEven(number: Number): Boolean

@JsExport
fun checkEven(number: Int) = isEven(number)

@JsExport
fun buildGreeting(args: GreetingArgs): String {
  return "Hi ${args.name} ${args.sureName}! Here's kotlin.test.Test::class.simpleName: ${kotlin.test.Test::class.simpleName}"
}

@JsExport
fun greet(args: GreetingArgs) {
  println(buildGreeting(args))
}
