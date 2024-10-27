plugins {
  id("mkdocs")
}

mkdocs {
  publish {
    existingVersionsFile = "https://raw.githubusercontent.com/mpetuska/npm-publish/refs/heads/gh-pages/versions.json"
    versionTitle = "${project.version}".split(".").take(2).joinToString(".")
    setVersionAliases("latest")
    rootRedirectTo = "latest"
  }
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
