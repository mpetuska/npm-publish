## Summary

The packages configure the npm bundles.
For each configured `NpmPackage` `assemble<PackageName>Package` [NpmAssembleTask](../tasks/NpmAssembleTask.md)
and `pack<PackageName>Package` [NpmPackTask](../tasks/NpmPackTask.md) tasks will be
generated and added as dependencies to the `assemble` and `pack` lifecycle tasks respectively.

The packages can be accessed and configured from a `build.gradle.kts` file via `npmPublish::packages` invocation.

```kotlin title="build.gradle.kts"
npmPublish {
  packages {
    ...
  }
}
```

## `package.json` Resolution

During the package assembly, a `package.json` file is generated from various configuration options.

For convenience, some key properties of the file are exposed as a top-level package configurations (such
as [`main`](#main)).
These configurations can be overridden via [`packageJson`](#packagejson) DSL, which exposes the entire `package.json`
file structure and allows unrestricted customisation.

Alternatively, you could opt in to use [`packageJsonFile`](#packagejsonfile) instead,
which short-circuits the entire resolution process and instead uses provided static package.json file, fully
disregarding all the other `package.json` configurations.

Finally, [`packageJsonTemplateFile`](#packagejsontemplatefile) option provides a way to get the best of both worlds.
When set, the provided template file will be used as a baseline on which top-level and [`packageJson`](#packagejson) DSL
configurations are applied. This way you could setup a static part of your `package.json` file from within the template
and only manage dynamic parts such as version and dependencies from the package DSL.

## Dependency Resolution

NPM dependencies detected/declared for each publication are resolved into relevant `package.json` dependency block by
this priority order (descending priority):

1. Optional
2. Peer
3. Dev
4. Normal

This ensures that any given dependency does not appear in multiple dependency scopes.

## Properties

=== "Properties"

    | Property                                              | Type                       | Default                                                          | When Kotlin plugin is present                                           |
    |:------------------------------------------------------|----------------------------|:-----------------------------------------------------------------|-------------------------------------------------------------------------|
    | [`scope`](#scope)                                     | String                     | [`NpmPublishExtension::organization`](extension.md#organization) |                                                                         |
    | [`packageName`](#packagename)                         | String                     | `Project::name`                                                  |                                                                         |
    | [`version`](#version)                                 | String                     | [`NpmPublishExtension::version`](extension.md#version)           |                                                                         |
    | [`main`](#main)                                       | String                     |                                                                  | Target's `Kotlin2JsCompile::outputFile`                                 |
    | [`types`](#types)                                     | String                     |                                                                  | Target's `Kotlin2JsCompile::outputFile::nameWithoutExtension` + `.d.ts` |
    | [`readme`](#readme)                                   | RegularFile                | [`NpmPublishExtension::readme`](extension.md#readme)             |                                                                         |
    | [`npmIgnore`](#npmignore)                             | RegularFile                | [`NpmPublishExtension::npmIgnore`](extension.md#npmignore)       |                                                                         |
    | [`files`](#files)                                     | ConfigurableFileCollection |                                                                  | Target's `Kotlin2JsCompile` and `processResources` task outputs         |
    | [`packageJson`](#packagejson)                         | PackageJson                |                                                                  |                                                                         |
    | [`packageJsonFile`](#packagejsonfile)                 | RegularFile                |                                                                  |                                                                         |
    | [`packageJsonTemplateFile`](#packagejsontemplatefile) | RegularFile                |                                                                  |                                                                         |
    | [`dependencies`](#dependencies)                       | NpmDependencies            |                                                                  | Target compilations' dependencies                                       |

=== "Keys"

    | Property                                              | CLI | System/Gradle                                        | Environment                                          |
    |:------------------------------------------------------|-----|:-----------------------------------------------------|------------------------------------------------------|
    | [`scope`](#scope)                                     |     | `npm.publish.package.<name>.scope`                   | `NPM_PUBLISH_PACKAGE_<NAME>_SCOPE`                   |
    | [`packageName`](#packagename)                         |     | `npm.publish.package.<name>.packageName`             | `NPM_PUBLISH_PACKAGE_<NAME>_PACKAGENAME`             |
    | [`version`](#version)                                 |     | `npm.publish.package.<name>.version`                 | `NPM_PUBLISH_PACKAGE_<NAME>_VERSION`                 |
    | [`main`](#main)                                       |     | `npm.publish.package.<name>.main`                    | `NPM_PUBLISH_PACKAGE_<NAME>_MAIN`                    |
    | [`types`](#types)                                     |     | `npm.publish.package.<name>.types`                   | `NPM_PUBLISH_PACKAGE_<NAME>_TYPES`                   |
    | [`readme`](#readme)                                   |     | `npm.publish.package.<name>.readme`                  | `NPM_PUBLISH_PACKAGE_<NAME>_README`                  |
    | [`npmIgnore`](#npmignore)                             |     | `npm.publish.package.<name>.npmIgnore`               | `NPM_PUBLISH_PACKAGE_<NAME>_NPMIGNORE`               |
    | [`files`](#files)                                     |     |                                                      |                                                      |
    | [`packageJson`](#packagejson)                         |     |                                                      |                                                      |
    | [`packageJsonFile`](#packagejsonfile)                 |     | `npm.publish.package.<name>.packageJsonFile`         | `NPM_PUBLISH_PACKAGE_<NAME>_PACKAGEJSONFILE`         |
    | [`packageJsonTemplateFile`](#packagejsontemplatefile) |     | `npm.publish.package.<name>.packageJsonTemplateFile` | `NPM_PUBLISH_PACKAGE_<NAME>_PACKAGEJSONTEMPLATEFILE` |
    | [`dependencies`](#dependencies)                       |     |                                                      |                                                      |

=== "Usage"

    ```kotlin title="build.gradle.kts"
    npmPublish {
      packages {
        register("js") {
          scope.set(group as String)
          packageName.set("coolio")
          version.set("4.20.69")
          main.set("main.js")
          types.set("main.d.ts")
          readme.set(rootDir.resolve("README.md"))
          npmIgnore.set(rootDir.resolve(".npmignore"))
          files {
            from("some/path")
          }
          packageJson {
            ...
          }
          packageJsonFile.set(projectDir.resolve("package.json"))
          packageJsonTemplateFile.set(projectDir.resolve("package.template.json"))
          dependencies {
            optional("axios", "*")
            peer("axios", "*")
            dev("axios", "*")
            normal("axios", "*")
          }
        }
      }
    }
    ```

### `scope`

Optional npm scope. If set, the package name will be constructed as `@{scope}/{packageName}`

### `packageName`

NPM package name. Can be overridden via [`packageJson`](#packagejson) DSL's `name` property

### `version`

NPM package name. Can be overridden via [`packageJson`](#packagejson) DSL's `version` property

### `main`

Main `.js` entry file relative to the `package.json` file.
Can be overridden via [`packageJson`](#packagejson) DSL's `main` property

### `types`

Main `.d.ts` entry file relative to the `package.json` file.
Can be overridden via [`packageJson`](#packagejson) DSL's `types` property

### `readme`

A location of the `README.md` file.
If set, the file will be moved to package assembly root and renamed to README.md (regardless of the actual name).

### `npmIgnore`

A location of the `.npmignore` file.

### `files`

Specification of files that should be assembled for this package.
It uses standard Gradle's CopySpec to declare copying hierarchies and filtering rules.
[More info](https://docs.gradle.org/current/userguide/working_with_files.html#sec:copying_directories_example)

### `packageJson`

`package.json` customisation container. All values set here will override top-level configurations
like [`types`](#types)

### `packageJsonFile`

Path to a static `package.json` file.
If set, fully disregards all related `package.json` configurations and is used as-is.

The container is modelled on top of Map-like structure with additional standard `package.json` fields added as explicit
properties. This means that not only you can configure standard `package.json` file in a type-safe manner, but also add
arbitrary fields of your own.

```kotlin title=build.gradle.kts
npmPublish {
  packages {
    named("js") {
      packageJson {
        "customNestedObject" by {
          "nestedField" by "ok"
        }
        "customArray" by arrayOf(1,2,3)
        "customObjectArray" by arrayOf(json {
          "nestedField" by 1
        })
        "customField" by true
      }
    }
  }
}
```

### `packageJsonTemplateFile`

Path to a baseline `package.json` template file.
Similar to [`packageJsonFile`](#packagejsonfile) except allows the options to be overridden by
the [`packageJson`](#packagejson) and top-level options.

### `dependencies`

Package's npm dependency container. It can be configured by invoking the property and using provided
, `optional`, `peer`, `dev` and `normal` DSLs.

```kotlin
npmPublish {
  packages {
    named("js") {
      dependencies {
        optional("axios", "*")
        peer("axios", "*")
        dev("axios", "*")
        normal("axios", "*")
      }
    }
  }
}
```

!!! info
Dependencies are deduplicated during resolution to void
multiple occurrences of a dependency across different dependency scopes
