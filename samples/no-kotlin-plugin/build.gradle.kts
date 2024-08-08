plugins {
  id("dev.petuska.npm.publish")
  id("com.netflix.nebula.node") version "+"
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

node {
  download = true
}

npmPublish {
  // Not needed thanks to `com.netflix.nebula.node` plugin
  // nodeHome.set(File("/path/to/your/node/home"))
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
