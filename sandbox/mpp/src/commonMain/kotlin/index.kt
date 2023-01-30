package sandbox

external interface GreetingArgs {
  val name: String
  val sureName: String
}

@JsExport
fun buildGreeting(args: GreetingArgs): String {
  return "Hi ${args.name} ${args.sureName}! Here's kotlin.test.Test::class.simpleName: ${kotlin.test.Test::class.simpleName}"
}

@JsExport
fun greet(args: GreetingArgs) {
  println(buildGreeting(args))
}
