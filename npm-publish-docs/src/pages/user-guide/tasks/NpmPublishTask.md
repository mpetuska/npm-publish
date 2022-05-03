## Summary

A task to publish a given [NpmPackage](../configuration/package.md) to a
given [NpmRegistry](../configuration/registry.md).
The task can be created and configured in a `build.gradle.kts` file by registering it with correct type.

```kotlin title="build.gradle.kts"
tasks {
  register("name", dev.petuska.npm.publish.task.NpmPublishTask::class) {
    ...
  }
}
```

## Properties

=== "Properties"

    | Property                    | Type        | Default | When Kotlin plugin is present |
    |:----------------------------|-------------|:--------|-------------------------------|
    | [`registry`](#registry)     | NpmRegistry |         |                               |
    | [`packageDir`](#packagedir) | Directory   |         |                               |
    | [`dry`](#dry)               | Boolean     | false   |                               |

=== "Keys"

    | Property                        | CLI     | System/Gradle | Environment |
    |:--------------------------------|---------|:--------------|-------------|
    | [`registry`](#registry)         |         |               |             |
    | [`packageDir`](#destinationdir) |         |               |             |
    | [`dry`](#dry)                   | `--dry` |               |             |

=== "Usage"

    ```kotlin title="build.gradle.kts"
    tasks {
      register("name", dev.petuska.npm.publish.task.NpmPublishTask::class) {
        registry {
          ...
        }
        packageDir.set(layout.projectDirectory.dir("src/main/js"))
        dry.set(true)
      }
    }
    ```

### `registry`

A registry to publish to

### `packageDir`

The directory where the assembled and ready-to-publish package is

### `dry`

Controls dry-tun mode for the execution.
