package lt.petuska.npm.publish.util

class KotlinBuilder(private val stringBuilder: StringBuilder = StringBuilder()) : Appendable by stringBuilder {
  operator fun String.invoke(config: KotlinBuilder.() -> Unit) = this@KotlinBuilder.also {
    appendln("$this {")
    config()
    appendln("}")
  }

  operator fun String.invoke(vararg args: Any) = this@KotlinBuilder.also {
    +"""$this(${args.joinToString(",") { "$it" }})"""
  }

  infix fun String.to(value: String) = this@KotlinBuilder.also {
    +"""$this = "$value""""
  }

  infix fun String.to(value: Any) = this@KotlinBuilder.also {
    +"$this = $value"
  }

  operator fun String.unaryPlus() {
    appendln(this)
  }

  override fun toString(): String {
    return stringBuilder.toString()
  }
}
