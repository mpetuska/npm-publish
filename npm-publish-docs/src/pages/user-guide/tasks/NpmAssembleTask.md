## Summary

A task to assemble all required files for a given [NpmPackage](../configuration/package.md).
The task can be created and configured in a `build.gradle.kts` file by registering it with correct type.

```kotlin title="build.gradle.kts"
tasks {
  register("name", dev.petuska.npm.publish.task.NpmAssembleTask::class) {
    ...
  }
}
```

## Properties

=== "Properties"

    | Property                            | Type        | Default                                           | When Kotlin plugin is present |
    |:------------------------------------|-------------|:--------------------------------------------------|-------------------------------|
    | [`package`](#package)               | NpmPackage  |                                                   |                               |
    | [`destinationDir`](#destinationdir) | Directory   | `$buildDir/packages/<package.name>`               |                               |

=== "Keys"

    | Property                            | CLI                | System/Gradle | Environment |
    |:------------------------------------|--------------------|:--------------|-------------|
    | [`package`](#package)               |                    |               |             |
    | [`destinationDir`](#destinationdir) | `--destinationDir` |               |             |

=== "Usage"

    ```kotlin title="build.gradle.kts"
    tasks {
      register("name", dev.petuska.npm.publish.task.NpmAssembleTask::class) {
        `package` {
          ...
        }
        destinationDir.set(layout.buildDirectory.dir("js/main"))
      }
    }
    ```

### `package`

The configuration of the package to assemble.

### `destinationDir`

Output directory to assemble the package to.
