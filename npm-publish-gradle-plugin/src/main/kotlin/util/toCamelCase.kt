package dev.petuska.npm.publish.util

import java.util.regex.Pattern

private val WORD_SEPARATOR = Pattern.compile("\\W+")

internal fun String.toCamelCase(lower: Boolean = false): String {
  val builder = StringBuilder()
  val matcher = WORD_SEPARATOR.matcher(this)
  var pos = 0
  var first = true
  while (matcher.find()) {
    var chunk: String = subSequence(pos, matcher.start()).toString()
    pos = matcher.end()
    if (chunk.isEmpty()) {
      continue
    }
    chunk = chunk.replaceFirstChar {
      when {
        first && lower -> it.toLowerCase().also { first = false }
        it.isLowerCase() -> it.toTitleCase()
        else -> it
      }
    }
    builder.append(chunk)
  }
  var rest: String = subSequence(pos, length).toString()
  rest = rest.replaceFirstChar {
    when {
      first && lower -> it.toLowerCase().also { first = false }
      it.isLowerCase() -> it.toTitleCase()
      else -> it
    }
  }
  builder.append(rest)
  return builder.toString()
}

private fun String.replaceFirstChar(replacer: (Char) -> Char): String = if (isNotEmpty()) {
  replacer(get(0)) + drop(1)
} else this
