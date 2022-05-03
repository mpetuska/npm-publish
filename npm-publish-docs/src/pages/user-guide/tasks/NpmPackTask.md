## Summary

A task to pack a `.tgz` archive for the given package. Extends [NpmExecTask](NpmExecTask.md).

The task can be created and configured in a `build.gradle.kts` file by registering it with correct type.

```kotlin title="build.gradle.kts"
tasks {
  register("name", dev.petuska.npm.publish.task.NpmPackTask::class) {
    ...
  }
}
```

## Properties

=== "Properties"

    | Property                    | Type              | Default                                           | When Kotlin plugin is present |
    |:----------------------------|-------------------|:--------------------------------------------------|-------------------------------|
    | [`packageDir`](#packagedir) | DirectoryProperty |                                                   |                               |
    | [`dry`](#dry)               | Boolean           | false                                             |                               |
    | [`outputFile`](#outputfile) | RegularFile       | `$buildDir/packages/<scope>-<name>-<version>.tgz` |                               |

=== "Keys"

    | Property                    | CLI            | System/Gradle | Environment |
    |:----------------------------|----------------|:--------------|-------------|
    | [`packageDir`](#packagedir) |                |               |             |
    | [`dry`](#dry)               | `--dry`        |               |             |
    | [`outputFile`](#outputfile) | `--outputFile` |               |             |

=== "Usage"

    ```kotlin title="build.gradle.kts"
    tasks {
      register("name", dev.petuska.npm.publish.task.NpmPackTask::class) {
        packageDir.set(layout.projectDirectory.dir("src/main/js"))
        dry.set(true)
        outputFile.set(layout.buildDirectory.dir("js/main"))
      }
    }
    ```

### `packageDir`

The directory where the assembled and ready-to-pack package is.

### `dry`

Controls dry-tun mode for the execution. When enabled, npm pack command will be run with a switch that does everything
it normally would except creating the tarball.

### `outputFile`

Output file to pack the publication to.
