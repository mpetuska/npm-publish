import task.MkDocsExec

plugins {
  id("plugin.base")
}

tasks {
  val src = projectDir.resolve("src")
  val mkdocsDir = buildDir.resolve("mkdocs")
  val outDir = mkdocsDir.resolve("site")
  val dokkaHtml = getByPath(":npm-publish-gradle-plugin:dokkaHtml")
  val docsAssemble = register("docsAssemble", Copy::class) {
    dependsOn(dokkaHtml)
    destinationDir = mkdocsDir.resolve("source").also(outputs::dir)
    val readme = rootDir.resolve("README.md").also(inputs::file)
    val changelog = rootDir.resolve("CHANGELOG.md").also(inputs::file)
    val srcDir = destinationDir.resolve("src").also(outputs::dir)
    val dokkaDir = srcDir.resolve("api").also(outputs::dir)
    val themeDir = destinationDir.resolve("theme").also(outputs::dir)
    into(srcDir.relativeTo(destinationDir)) {
      from(src.resolve("assets"))
      from(src.resolve("pages"))
      from(readme) {
        rename("README.md", "index.md")
      }
      from(changelog)
    }
    into(themeDir.relativeTo(destinationDir)) {
      from(src.resolve("theme"))
    }
    into(dokkaDir.relativeTo(destinationDir)) {
      from(dokkaHtml.outputs)
    }
    from(src.resolve("mkdocs.yml")) {
      expand(
        mapOf(
          "srcDir" to srcDir.absolutePath,
          "themeDir" to themeDir.absolutePath,
          "outDir" to outDir.absolutePath,
        )
      )
    }

    inputs.dir(src)
  }
  register("mkdocsServe", MkDocsExec.Serve::class) {
    dependsOn(docsAssemble)
    workingDir.set(layout.dir(docsAssemble.map { it.destinationDir }))
  }
  register("mkdocsBuild", MkDocsExec.Build::class) {
    dependsOn(docsAssemble)
    workingDir.set(layout.dir(docsAssemble.map { it.destinationDir }))
    containerVolumes.add(outDir)
    outputs.dir(outDir)
  }
  register("mkdocsDeploy", MkDocsExec.GhDeploy::class) {
    dependsOn(docsAssemble)
    workingDir.set(layout.dir(docsAssemble.map { it.destinationDir }))
    containerVolumes.add(outDir)
    environment.put("GIT_DISCOVERY_ACROSS_FILESYSTEM", "true")
  }
}
