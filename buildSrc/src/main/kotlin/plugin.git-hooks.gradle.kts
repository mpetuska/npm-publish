plugins {
  id("com.github.jakemarsden.git-hooks")
}

gitHooks {
  setHooks(
    mapOf(
      "pre-commit" to "detekt --auto-correct",
      "pre-push" to "detekt"
    )
  )
}
