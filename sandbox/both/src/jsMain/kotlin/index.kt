package sandbox

public external interface GreetingArgs {
  public val name: String
  public val sureName: String
}

@JsModule("is-even")
@JsName("default")
private external fun isEven(number: Number): Boolean

@JsExport
public fun checkEven(number: Int): Boolean = isEven(number)

@JsExport
public fun buildGreeting(args: GreetingArgs): String {
  return "Hi ${args.name} ${args.sureName}! Here's kotlin.test.Test::class.simpleName: ${kotlin.test.Test::class.simpleName}"
}

@JsExport
public fun greet(args: GreetingArgs) {
  println(buildGreeting(args))
}
