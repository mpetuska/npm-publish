plugins {
  id("com.github.jakemarsden.git-hooks") apply (System.getenv("CI") !in arrayOf(null, "0", "false", "n", "N"))
}

gitHooks {
  setHooks(
    mapOf(
      "pre-commit" to "detekt --auto-correct",
      "pre-push" to "detekt"
    )
  )
}
