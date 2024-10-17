plugins {
  id("mkdocs")
}

mkdocs {
  strict = false
  sourcesDir = layout.buildDirectory.dir(name).get().asFile.absolutePath
  publish {
    existingVersionsFile = "https://raw.githubusercontent.com/mpetuska/npm-publish/refs/heads/gh-pages/versions.json"
    versionTitle = "${project.version}".split(".").take(2).joinToString(".")
    setVersionAliases("latest")
    rootRedirectTo = "latest"
  }
}

tasks {
  val mkdocsMergeConfig by registering {
    inputs.dir(layout.projectDirectory.dir("src/config/"))
    val outputFile = layout.buildDirectory.file("$name/mkdocs.yml")
    outputs.file(outputFile)
    doLast {
      val output = outputFile.get().asFile
      inputs.files.forEach {
        output.appendText(it.readText())
      }
    }
  }
  val mkdocsAssemble by registering(Copy::class) {
    destinationDir = File(mkdocs.sourcesDir)
    into("docs") {
      from(layout.projectDirectory.dir("src/docs/"))
      from(rootProject.layout.projectDirectory.dir("CHANGELOG.md"))
      from(rootProject.layout.projectDirectory.dir("README.md")) {
        rename("README.md", "index.md")
      }
      from(rootProject.layout.projectDirectory.dir("LICENSE")) {
        rename("LICENSE", "LICENSE.md")
      }
      into(".github") {
        from(rootProject.layout.projectDirectory.dir(".github/CONTRIBUTING.md"))
      }
      into("api") {
        from(getByPath(":npm-publish-gradle-plugin:dokkaHtml"))
      }
    }
    into("theme") {
      from(layout.projectDirectory.dir("src/theme/"))
    }
    from(mkdocsMergeConfig)
  }
  mkdocsBuild {
    dependsOn(mkdocsAssemble)
  }
  mkdocsServe {
    dependsOn(mkdocsBuild)
  }
}
