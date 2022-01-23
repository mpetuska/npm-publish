package dev.petuska.npm.publish.util

import java.util.regex.Pattern

private val WORD_SEPARATOR = Pattern.compile("\\W+")

fun String.toCamelCase(lower: Boolean = false): String {
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
    if (lower && first) {
      chunk = chunk.capitalize()
      first = false
    } else {
      chunk = chunk.capitalize()
    }
    builder.append(chunk)
  }
  var rest: String = subSequence(pos, length).toString()
  rest = if (lower && first) {
    rest.decapitalize()
  } else {
    rest.capitalize()
  }
  builder.append(rest)
  return builder.toString()
}
