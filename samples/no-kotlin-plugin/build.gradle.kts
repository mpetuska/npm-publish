plugins {
  id("convention.base")
  id("dev.petuska.npm.publish")
}

tasks {
  // This is just a stub to imitate some js/ts generation task
  register("generateJsFile") {
    val outDir = buildDir.resolve("genJs")
    // Note that it declares outputs
    outputs.dir(outDir)
    doFirst {
      outDir.mkdirs()
      buildDir.resolve("genJs/custom.js").writeText("console.log('hello world')")
    }
  }
}

npmPublish {
  nodeHome.set(File("${System.getProperty("user.home")}/.gradle/nodejs/node-v16.13.0-linux-x64/"))
  packages {
    register("standalone") {
      version.set("4.20.69")
      packageName.set("my-private-package")
      files {
        // Since the task declares outputs, we can just source directly from it
        // Additionally, it will also add it as a dependency to relevant npm-publish tasks automatically
        from(tasks.named("generateJsFile"))
        from(projectDir.resolve("src"))
      }
    }
  }
  registries {
    register("nexus") {
      uri.set(uri("http://companynexus.com"))
    }
  }
}
