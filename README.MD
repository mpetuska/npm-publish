# NPM-PUBLISH GRADLE PLUGIN

Gradle plugin enabling NPM publishing (essentially `maven-publish` for NPM packages). Integrates seamlessly with Kotlin/JS/MPP plugin if applied,
providing auto configurations.

## Setup
Here's a bare minimum setup when using together with one of the kotlin plugins

```kotlin
plugins {
  id("lt.petuska.npm.publish") version "<VERSION>"
  kotlin("multiplatform") version "1.4.0" // Optional, also supports "js"
}

kotlin {
  js {
    browser() // or nodejs()
  }
}

npmPublishing {
  repositories {
    repository("npmjs") {      
      registry = uri("https://registry.npmjs.org")
      authToken = "asdhkjsdfjvhnsdrishdl"
    }
  }
}
```

### Configuration
You can add publications and npm repositories or override existing configuration defaults via npmPublishing extension.
When kotlin JS/MPP plugin is applied, this plugin will automatically create a publication for each JS target you JS/MPP project has.

```kotlin
npmPublishing {
  readme = file("README.MD") // (optional) Default readme file
  organization = "my.org" // (Optional) Used as default scope for all publications
  access = NpmAccess.PUBLIC // or NpmAccess.RESTRICTED. Specifies package visibility, defaults to NpmAccess.PUBLIC
  
  repositories {
    repository("npmjs") {      
      registry = uri("https://registry.npmjs.org") // Registry to publish to
      authToken = "asdhkjsdfjvhnsdrishdl" // NPM registry authentication token
      otp = "gfahsdjglknamsdkpjnmasdl" // NPM registry authentication OTP
    }
    repository("bintray") {   
      access = NpmAccess.RESTRICTED   
      registry = ("https://dl.bintray.com/mpetuska/lt.petuska.npm") // Registry to publish to
      authToken = "sngamascdgb" // NPM registry authentication token
      otp = "miopuhimpdfsazxfb" // (Optional) NPM registry authentication OTP
    }
  }
  publications {
    val jsOne by getting { // Publication build for target declared as `kotlin { js("jsOne") { nodejs() } }`
      scope = "not.my.org" // Overriding package scope that defaulted to organization property from before
    }
    publication("customPublication") { //Custom publication
      nodeJsDir = file("~/nodejs") // NodeJs home directory. Defaults to $NODE_HOME if present or kotlinNodeJsSetup output for default publications
      moduleName = "my-module-name-override" // Defaults to project name
      scope = "other.comp"
      readme = file("docs/OTHER.MD")
      destinationDir = file("$buildDir/vipPackage") // Package collection directory, defaults to File($buildDir/publications/npm/$name")
      main = "my-module-name-override-js.js" // Main output file name, set automatically for default publications

      // Entirely Optional

      dependencies {
        npm("snabbdom", "*")
        npmDev("typescript", "*")
        npmOptional("webpack", "*")
        npmPeer("react", "*")
      }           
      files { assemblyDir -> // Specifies what files should be packaged. Preconfigured for default publications, yet can be extended if needed
        from("$assemblyDir/../dir")
        // Rest of your CopySpec     
      }
      pakageJsonFile = file("static/package.template.json") // If set will be used as-is ignoring further configurations while getting renamed to package.json regardless of the actual name.
      packagejson = { // Full package.json override
        main = "./dist/yet-another-override.js"
      }
      packageJson { // Will be patched on top of default generated package.json
        private = false
        keywords = jsonArray(
          "kotlin"
        )
        publishConfig {
          tag = "latest"
        }
        "customField" to jsonObject {
          "customValues" to jsonArray(1, 2, 3)
        }
      }
    }
  }
}
```

There are also few project properties you can use from cmd line (`./gradlew task -Pprop.name=propValue`), `gradle.properties` or `local.properties`:
* `npm.publish.authToken.<repoName>` To pass in authToken
* `npm.publish.otp.<repoName>` To pass in OTP
* `npm.publish.dry` To run npm publishing with `--dry-run` (does everything except uploading the files)
