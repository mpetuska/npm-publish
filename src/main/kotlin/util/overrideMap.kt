package dev.petuska.npm.publish.util

internal fun Map<String, Any>.mergeWith(other: Map<String, Any>) {
}

@Suppress("UNCHECKED_CAST")
internal fun MutableMap<String, Any>.overrideFrom(other: Map<String, Any>): MutableMap<String, Any> = apply {
  other.entries.forEach { (key, new) ->
    val old = this[key]
    this[key] = when {
      old == null -> new
      old is Map<*, *> && new is Map<*, *> -> (old as Map<String, Any>).toMutableMap()
        .overrideFrom(new as Map<String, Any>)
      else -> new
    }
  }
}
