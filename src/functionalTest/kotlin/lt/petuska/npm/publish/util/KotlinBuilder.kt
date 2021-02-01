package lt.petuska.npm.publish.util

class KotlinBuilder(block: KotlinBuilder.() -> Unit = {}) {
  private val blocks = mutableListOf<KBuilderBlock<*>>()

  constructor(str: String) : this({ appendln(str) })

  init {
    block()
  }

  infix operator fun invoke(block: KotlinBuilder.() -> Unit) = this.apply(block)

  operator fun String.invoke(vararg args: Any, block: (KotlinBuilder.() -> Unit)? = null) = this@KotlinBuilder.also {
    blocks.add(
      KotlinBuilder {
        append(this@invoke)
        if (args.isNotEmpty()) {
          append("(${args.joinToString(",") { "${it.arg}" }})")
        } else if (block == null) {
          append("()")
        }
        block?.let {
          appendln(" {")
          append("${KotlinBuilder(block)}")
          append("}")
        }
        appendln()
      }.kb
    )
  }

  infix fun String.to(value: Any) = this@KotlinBuilder.also {
    +"${this@to} = ${value.arg}"
  }

  operator fun String.unaryPlus() = this@KotlinBuilder.also {
    blocks.add("${this@unaryPlus}\n".kb)
  }

  fun append(str: String) {
    blocks.add(str.kb)
  }

  fun appendln(str: String = "") {
    blocks.add("$str\n".kb)
  }

  infix fun String.infix(other: Any) = this@KotlinBuilder.apply {
    +"${this@infix} $other"
  }

  infix fun KotlinBuilder.infix(other: Any) = this@KotlinBuilder.also {
    val last = it.blocks.removeAt(blocks.size - 1).toString().removeSuffix("\n")
    last infix other
  }

  infix fun KotlinBuilder.infix(other: KotlinBuilder) = this@KotlinBuilder.also {
    val last = it.blocks.removeAt(blocks.size - 1).toString().removeSuffix("\n")
    other infix last
  }

  infix fun Arg<*>.infix(other: KotlinBuilder) = this@KotlinBuilder.also {
    val last = it.blocks.removeAt(blocks.size - 1).toString().removeSuffix("\n")
    other infix last
  }

  infix fun String.chain(other: Any) = this@KotlinBuilder.apply {
    +"${this@chain}.$other"
  }

  infix fun KotlinBuilder.chain(other: Any) = this@KotlinBuilder.also {
    val last = it.blocks.removeAt(blocks.size - 1).toString().removeSuffix("\n")
    last chain other
  }

  infix fun KotlinBuilder.chain(other: KotlinBuilder) = this@KotlinBuilder.also {
    val last = it.blocks.removeAt(blocks.size - 1).toString().removeSuffix("\n")
    other chain last
  }

  fun arg(block: KotlinBuilder.() -> Unit) = KotlinBuilder(block).toString().removeSuffix("\n").fn

  override fun toString(): String {
    return blocks.joinToString("")
  }

  val Any.arg
    get() = when (this) {
      is Arg<*> -> this
      is String -> Arg.RawString(this)
      else -> Arg.Block(this)
    }
  val String.raw get() = Arg.RawString(this)
  val String.fn get() = Arg.Block(this)
  val String.kb get() = KBuilderBlock.Raw(this)
  val kb get() = KBuilderBlock.Block(this)

  sealed class KBuilderBlock<T> {
    abstract val actual: T

    class Raw(override val actual: String) : KBuilderBlock<String>()
    class Block(override val actual: KotlinBuilder) : KBuilderBlock<KotlinBuilder>()

    override fun toString(): String {
      return actual.toString()
    }

    override fun equals(other: Any?): Boolean {
      return actual?.equals(other) ?: other == null
    }

    override fun hashCode(): Int {
      return actual.hashCode()
    }
  }

  sealed class Arg<T>(private val stringify: () -> String) {
    abstract val actual: T

    class Block<T>(override val actual: T) : Arg<T>({ "$actual" })
    class RawString(override val actual: String) : Arg<String>({ "\"$actual\"" })

    override fun toString(): String {
      return stringify()
    }

    override fun equals(other: kotlin.Any?): Boolean {
      return actual?.equals(other) ?: other == null
    }

    override fun hashCode(): Int {
      return actual.hashCode()
    }
  }
}
