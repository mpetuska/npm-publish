plugins {
  id("mkdocs")
}

mkdocs {
  publish {
    existingVersionsFile = "https://raw.githubusercontent.com/mpetuska/npm-publish/refs/heads/gh-pages/versions.json"
    docPath = "${project.version}".split(".").take(2).joinToString(".")
    setVersionAliases("latest")
    rootRedirectTo = "latest"
  }
}

python {
  envPath = rootDir.resolve(".gradle/python").absolutePath
}

tasks {
  mkdocsAssemble {
    into("docs") {
      into("api") {
        from(getByPath(":npm-publish-gradle-plugin:dokkatooGeneratePublicationHtml"))
      }
    }
  }
}
