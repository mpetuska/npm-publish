Here's a bare minimum setup when using the plugin standalone or together with one of the kotlin plugins.

=== "Kotlin/MPP"
    ```kotlin title="build.gradle.kts"
    plugins {
      id("dev.petuska.npm.publish") version "<VERSION>"
      kotlin("multiplatform") version "<VERSION>>"
    }

    kotlin {
      js(IR) {
        binaries.library()
        browser() // (1)
      }
    }
    
    npmPublish {
      registries {
        register("npmjs") {
          uri.set(uri("https://registry.npmjs.org")) // (2)
          authToken.set("obfuscated")
        }
      }
    }
    ```

    1. `nodejs()` works too
    2. `uri` can also be set from String as `uri.set("https://registry.npmjs.org")` 
       in which case the plugin will construct an URI instance from the string for you

=== "Kotlin/JS"
    ```kotlin title="build.gradle.kts"
    plugins {
      id("dev.petuska.npm.publish") version "<VERSION>"
      kotlin("js") version "<VERSION>>"
    }
    
    kotlin {
      js(IR) {
        binaries.library()
        browser() // (1)
      }
    }
    
    npmPublish {
      registries {
        register("npmjs") {
          uri.set(uri("https://registry.npmjs.org")) // (2)
          authToken.set("obfuscated")
        }
      }
    }
    ```

    1. `nodejs()` works too
    2. `uri` can also be set from String as `uri.set("https://registry.npmjs.org")` 
       in which case the plugin will construct an URI instance from the string for you

=== "Standalone"
    ```kotlin title="build.gradle.kts"
    plugins {
      id("dev.petuska.npm.publish") version "<VERSION>"
    }

    npmPublish {
      packages {
        register("js") {
          ...
        }
      }
      registries {
        register("npmjs") {
          uri.set(uri("https://registry.npmjs.org")) // (1)
          authToken.set("obfuscated")
        }
      }
    }
    ```

    1. `uri` can also be set from String as `uri.set("https://registry.npmjs.org")` 
       in which case the plugin will construct an URI instance from the string for you

This setup would produce the following tasks:

* `assembleJsNpmPackage: NpmAssembleTask`
* `packJsNpmPackage: NpmPackTask`
* `publishJsPackageToNpmjsRegistry: NpmPublishTask`

!!! info
    One publish task is created for every `package` + `registry` combination. 
    The task is named as `publish<PackageName>PackageTo<RegistryName>Registry`
