## Summary

The plugin registers a top level `npmPublish: NpmPublishExtension` extension for the project as a main entrypoint for
most of the plugin configuration options.

The extension can be accessed and configured from a `build.gradle.kts` file by invoking its name.

```kotlin title="build.gradle.kts"
npmPublish {
  ...
}
```

## Properties

=== "Properties"

    | Property                        | Type          | Default                  | When Kotlin plugin is present   |
    |:--------------------------------|---------------|:-------------------------|---------------------------------|
    | [`nodeHome`](#nodehome)         | File          | `NODE_HOME` env variable | `kotlinNodeJsSetup` task output |
    | [`readme`](#readme)             | File          |                          |                                 |
    | [`npmIgnore`](#npmignore)       | File          | `$projectDir/.npmignore` |                                 |
    | [`organization`](#organization) | String        |                          |                                 |
    | [`version`](#version)           | String        | `Project::version`       |                                 |
    | [`access`](#access)             | NpmAccess     | `NpmAccess.PUBLIC`       |                                 |
    | [`dry`](#dry)                   | Boolean       | `false`                  |                                 |
    | [`packages`](#packages)         | NpmPackages   |                          |                                 |
    | [`registries`](#registries)     | NpmRegistries |                          |                                 |

=== "CLI Keys"

    | Property                        | CLI | System/Gradle                     | Environment                |
    |:--------------------------------|-----|:----------------------------------|----------------------------|
    | [`nodeHome`](#nodehome)         |     | `npm.publish.<name>.nodeHome`     | `NPM_PUBLISH_NODEHOME`     |
    | [`readme`](#readme)             |     | `npm.publish.<name>.readme`       | `NPM_PUBLISH_README`       |
    | [`npmIgnore`](#npmignore)       |     | `npm.publish.<name>.npmIgnore`    | `NPM_PUBLISH_NPMIGNORE`    |
    | [`organization`](#organization) |     | `npm.publish.<name>.organization` | `NPM_PUBLISH_ORGANIZATION` |
    | [`version`](#version)           |     | `npm.publish.<name>.version`      | `NPM_PUBLISH_VERSION`      |
    | [`access`](#access)             |     | `npm.publish.<name>.access`       | `NPM_PUBLISH_ACCESS`       |
    | [`dry`](#dry)                   |     | `npm.publish.<name>.dry`          | `NPM_PUBLISH_DRY`          |
    | [`packages`](#packages)         |     |                                   |                            |
    | [`registries`](#registries)     |     |                                   |                            |


=== "Usage"

    ```kotlin title="build.gradle.kts"
    npmPublish {
      nodeHome.set(File("/path/to/node"))
      readme.set(rootDir.resolve("REAMDE.md"))
      npmIgnore.set(projectDir.resolve(".npmIgnore"))
      organization.set("${project.group}")
      version.set("${project.version}")
      access.set(RESTRICTED)
      dry.set(true)
      packages {
        ...
      }
      registries {
        ...
      }
    }
    ```

### `nodeHome`

Default NodeJS directory to be used when executing npm commands.

### `readme`

A location of the default `README.md` file. If set, it will be used as a default for all packages that do not have one
set
explicitly. The file name is ignored and renamed to `README.md` when assembling.

### `npmIgnore`

A location of the default `.npmignore` file. If set, it will be used as a default for all packages that do not have one
set explicitly.

### `organization`

Default package scope. If set, it will be used as a default for all packages that do not have one set explicitly.

### `version`

Default package version. If set, it will be used as a default for all packages that do not have one set explicitly.

### `access`

Default package access when publishing to npm registries.
[More info](https://docs.npmjs.com/package-scope-access-level-and-visibility)

### `dry`

Specifies if a dry-run should be added to the npm command arguments by default. Dry run does all the normal run does,
but without making any modifications to local or remote files.

### `packages`

A container for npm package configurations. 
See [Package](package.md) for detailed description of the container entities.

### `registries`

A container for npm registry configurations. 
See [Registry](registry.md) for detailed description of the container entities.
