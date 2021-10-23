plugins {
  id("com.github.jakemarsden.git-hooks")
}

gitHooks { setHooks(mapOf("pre-commit" to "spotlessApply", "pre-push" to "spotlessCheck")) }
