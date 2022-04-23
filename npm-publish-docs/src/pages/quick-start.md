Here's a bare minimum setup when using the plugin standalone or together with one of the kotlin plugins.

=== "Standalone"
    ```kotlin title="build.gradle.kts" linenums="1"
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
          authToken = "obfuscated"
        }
      }
    }
    ```
=== "Kotlin/JS"
    ```kotlin title="build.gradle.kts" linenums="1"
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
          authToken = "obfuscated"
        }
      }
    }
    ```

    1.  `nodejs()` works too

=== "Kotlin/MPP"
    ```kotlin title="build.gradle.kts" linenums="1"
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
          authToken = "obfuscated"
        }
      }
    }
    ```

    1.  `nodejs()` works too
This setup would produce the following tasks:

* `assembleJsNpmPackage: NpmAssembleTask`
* `packJsNpmPackage: NpmPackTask`
* `publishJsPackageToNpmjsRegistry: NpmPublishTask`

!!! info
    One publish task is created for every `package` + `registry` combination
