## Summary

Basic task for executing various npm commands. Extends [NodeExecTask](NodeExecTask.md) and provides access to npm
executable.

The task can be created and configured in a `build.gradle.kts` file by registering it with correct type.

```kotlin title="build.gradle.kts"
tasks {
  register("name", dev.petuska.npm.publish.task.NpmExecTask::class) {
    ...
  }
}
```

## Properties

=== "Properties"

    | Property                        | Type                | Default                  | When Kotlin plugin is present   |
    |:--------------------------------|---------------------|:-------------------------|---------------------------------|
    | [`npm`](#npm)                   | RegularFileProperty | `$nodeHome/bin/npm`      |                                 |

=== "Keys"

    | Property                | CLI          | System/Gradle | Environment |
    |:------------------------|--------------|:--------------|-------------|

=== "Usage"

    ```kotlin title="build.gradle.kts"
    tasks {
      register("name", dev.petuska.npm.publish.task.NpmExecTask::class) {
        npm.set(layout.projectDirectory.dir("/usr/share/node/bin/npm"))
        doLast {
          val args = listOf("--help")
          npmExec(args)
        }
      }
    }
    ```

### `npm`

Direct npm executable.
