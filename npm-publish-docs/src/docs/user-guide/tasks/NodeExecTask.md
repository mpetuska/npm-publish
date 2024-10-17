## Summary

Basic task for executing various node commands. Provides access to node executable.

The task can be created and configured in a `build.gradle.kts` file by registering it with correct type.

```kotlin title="build.gradle.kts"
tasks {
  register("name", dev.petuska.npm.publish.task.NodeExecTask::class) {
    ...
  }
}
```

## Properties

=== "Properties"

    | Property                        | Type              | Default                  | When Kotlin plugin is present   |
    |:--------------------------------|-------------------|:-------------------------|---------------------------------|
    | [`nodeHome`](#nodehome)         | DirectoryProperty | `NODE_HOME` env variable | `kotlinNodeJsSetup` task output |

=== "Keys"

    | Property                | CLI          | System/Gradle | Environment |
    |:------------------------|--------------|:--------------|-------------|
    | [`nodeHome`](#nodehome) | `--nodeHome` |               | `NODE_HOME` |

=== "Usage"

    ```kotlin title="build.gradle.kts"
    tasks {
      register("name", dev.petuska.npm.publish.task.NodeExecTask::class) {
        nodeHome.set(layout.projectDirectory.dir("/usr/share/node"))
        doLast {
          val args = listOf("--help")
          nodeExec(args)
        }
      }
    }
    ```

### `nodeHome`

Base NodeJS directory used to extract other node executables from.
