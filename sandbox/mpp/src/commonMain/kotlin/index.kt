package sandbox

import io.ktor.client.HttpClient

external interface GreetingArgs {
  val name: String
  val sureName: String
}

@JsExport
fun buildGreeting(args: GreetingArgs): String {
  return "Hi ${args.name} ${args.sureName}! Here's HttpClient::class.simpleName: ${HttpClient::class.simpleName}"
}

@JsExport
fun greet(args: GreetingArgs) {
  println(buildGreeting(args))
}
