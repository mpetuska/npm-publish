# npm-publish

Gradle plugin enabling NPM publishing for Kotlin/JS (including MPP)

## Setup
```kotlin
plugins {
  id("lt.petuska.npm.publish") version "<VERSION>"
  kotlin("js") version "1.4.0" // Optional
}

kotlin {
  js {
    browser() // or nodejs()
  }
  dependencies {
    implementation(npm("axios", "*"))
    api(npm("snabbdom", "*"))
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
      packagejson = { // Full package.json override
        main = "./dist/yet-another-override.js"
      }
      packageJson { // Will be patched on top of default generated package.json
        private = false
      }
    }
  }
}
```

There are also few project properties you can use from cmd line (`./gradlew task -Pprop.name=propValue`):
* `npm.publish.authToken.<repoName>` To pass in authToken
* `npm.publish.otp.<repoName>` To pass in OTP
* `npm.publish.dry` To run npm publishing with `--dry-run` (does everything except uploading the files)
