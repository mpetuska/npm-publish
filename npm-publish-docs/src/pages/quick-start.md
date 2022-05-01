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
    
    npmPublishing {
      registries {
        npmjs {
          authToken.set("obfuscated")
        }
      }
    }
    ```

    1.  `nodejs()` works too

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
    
    npmPublishing {
      registries {
        npmjs {
          authToken.set("obfuscated")
        }
      }
    }
    ```

    1.  `nodejs()` works too

=== "Standalone"
    ```kotlin title="build.gradle.kts"
    plugins {
      id("dev.petuska.npm.publish") version "<VERSION>"
    }

    npmPublishing {
      packages {
        register("js") {
          ...
        }
      }
      registries {
        npmjs {
          authToken.set("obfuscated")
        }
      }
    }
    ```

This setup would produce the following tasks:

* `assembleJsNpmPackage: NpmAssembleTask`
* `packJsNpmPackage: NpmPackTask`
* `publishJsPackageToNpmjsRegistry: NpmPublishTask`

!!! info
    One publish task is created for every `package` + `registry` combination
