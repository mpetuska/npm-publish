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
    | [`nodeHome`](#nodehome)         | Directory     | `NODE_HOME` env variable | `kotlinNodeJsSetup` task output |
    | [`nodeBin`](#nodeBin)           | RegularFile   | `$nodeHome/bin/node`     |                                 |
    | [`npmBin`](#npmBin)             | RegularFile   | `$nodeHome/bin/npm`      |                                 |
    | [`readme`](#readme)             | RegularFile   |                          |                                 |
    | [`npmIgnore`](#npmignore)       | RegularFile   | `$projectDir/.npmignore` |                                 |
    | [`npmrc`](#npmrc)               | RegularFile   | `$projectDir/.npmrc`     |                                 |
    | [`organization`](#organization) | String        |                          |                                 |
    | [`version`](#version)           | String        | `Project::version`       |                                 |
    | [`access`](#access)             | NpmAccess     | `NpmAccess.PUBLIC`       |                                 |
    | [`dry`](#dry)                   | Boolean       | `false`                  |                                 |
    | [`packages`](#packages)         | NpmPackages   |                          |                                 |
    | [`registries`](#registries)     | NpmRegistries |                          |                                 |

=== "Keys"

    | Property                        | CLI | System/Gradle              | Environment                |
    |:--------------------------------|-----|:---------------------------|----------------------------|
    | [`nodeHome`](#nodehome)         |     | `npm.publish.nodeHome`     | `NPM_PUBLISH_NODEHOME`     |
    | [`nodeBin`](#nodeBin)           |     | `npm.publish.nodeBin`      | `NPM_PUBLISH_NODEBIN`      |
    | [`npmBin`](#npmBin)             |     | `npm.publish.npmBin`       | `NPM_PUBLISH_NPMBIN`       |
    | [`readme`](#readme)             |     | `npm.publish.readme`       | `NPM_PUBLISH_README`       |
    | [`npmIgnore`](#npmignore)       |     | `npm.publish.npmIgnore`    | `NPM_PUBLISH_NPMIGNORE`    |
    | [`npmrc`](#npmrc)               |     | `npm.publish.npmrc`        | `NPM_PUBLISH_NPMRC`        |
    | [`organization`](#organization) |     | `npm.publish.organization` | `NPM_PUBLISH_ORGANIZATION` |
    | [`version`](#version)           |     | `npm.publish.version`      | `NPM_PUBLISH_VERSION`      |
    | [`access`](#access)             |     | `npm.publish.access`       | `NPM_PUBLISH_ACCESS`       |
    | [`dry`](#dry)                   |     | `npm.publish.dry`          | `NPM_PUBLISH_DRY`          |
    | [`packages`](#packages)         |     |                            |                            |
    | [`registries`](#registries)     |     |                            |                            |

=== "Usage"

    ```kotlin title="build.gradle.kts"
    npmPublish {
      nodeHome.set(File("/path/to/node"))
      nodeBin.set(File("/path/to/node/bin/node"))
      npmBin.set(File("/path/to/node/bin/npm"))
      readme.set(rootDir.resolve("README.md"))
      npmIgnore.set(projectDir.resolve(".npmIgnore"))
      npmrc.set(projectDir.resolve(".npmrc"))
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

!!! info
    This is set automatically when certain other plugins are applied
    - `org.jetbrains.kotlin.multiplatform` & `org.jetbrains.kotlin.js`: `kotlinNodeJsSetup` task output
    - `com.netflix.nebula:nebula-node-plugin`: `nodeSetup` task output

### `nodeBin`

Default node executable to be used when executing node commands. Usually configured by default.

### `npmBin`

Default npm executable to be used when executing npm commands. Usually configured by default.

### `readme`

A location of the default `README.md` file. If set, it will be used as a default for all packages that do not have one
set
explicitly. The file name is ignored and renamed to `README.md` when assembling.

### `npmIgnore`

A location of the default `.npmignore` file. If set, it will be used as a default for all packages that do not have one
set explicitly.

### `npmrc`

A location of the default `.npmrc` file. If set, it will be used as a default for all registries that do not have one
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
