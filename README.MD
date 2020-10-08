# kpm-publish

Gradle plugin enabling NPM publishing for Kotlin/JS (including MPP)

## Setup
```kotlin
plugins {
  id("lt.petuska.kpm.publish") version "<VERSION>"
  kotlin("js") version "1.4.0"
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
The plugin will create a publication for each JS target you JS/MPP project has. You can add additional targets or override
existing configuration defaults via kpmPublish extension:
```kotlin
kpmPublish {
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
      destinationDir = file("$buildDir/vipPackage") // Package collection directory, defaults to File($buildDir/publications/kpm/$name")
      main = "my-module-name-override-js.js" // Main output file name, set automatically for default publications

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
    }
  }
}
```

There are also few project properties you can use from cmd line (`./gradlew task -Pprop.name=propValue`):
* `kpm.publish.authToken.<repoName>` To pass in authToken
* `kpm.publish.otp.<repoName>` To pass in OTP
* `kpm.publish.dry` To run npm publishing with `--dry-run` (does everything except uploading the files)
