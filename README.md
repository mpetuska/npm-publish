# kpm-publish

Gradle plugin enabling NPM publishing for Kotlin/JS (including MPP)

## Setup
```kotlin
plugins {
  id("lt.petuska.kpm.publish") version "0.0.1"
  kotlin("js") version "1.4.0"
}

kotlin {
  target {
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
  registry = "https://registry.npmjs.org" // (Optional) Default registry to publish to, defaults to "https://registry.npmjs.org"
  authToken = "asdhkjsdfjvhnsdrishdl" // NPM registry authentication token
  otp = "gfahsdjglknamsdkpjnmasdl" // NPM registry authentication OTP, can be overridden for each publication
  access = "public" // or restricted. Specifies package visibility, defaults to "public"
  
  publications {
    val jsOne by getting { // Publication build for target declared as `kotlin { js("jsOne") { nodejs() } }`
      scope = "not.my.org" // Overriding package scope that defaulted to organization property from before
    }
    publication("customPublication") { //Custom publication
      compilation = jsOne.compilation // Set the kotlin JS compilation to get JS files from
      moduleName = "my-module-name-override" // Defaults to project name
      scope = "other.comp"
      readme = file("docs/OTHER.MD")
      registry = "https://registry.mycomp.com/private/"
      authToken = "otherauthtokenihjzsd"
      destinationDir = file("$buildDir/vipPackage") // Package collection directory, defaults to File($buildDir/publications/kpm/$name")
      otp = null
      access = "restricted"
    }
  }
}
```

There are also few project properties you can use from cmd line (`./gradlew task -Pprop.name=propValue`):
* `kpm.publish.authToken` To pass in authToken
* `kpm.publish.otp` To pass in OTP
* `kpm.publish.dry` To run npm publishing with `--dry-run` (does everything except uploading the files)
