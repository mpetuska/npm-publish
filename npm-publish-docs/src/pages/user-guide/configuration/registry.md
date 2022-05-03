## Summary

The registries configure publication targets and their authentication. For each configured `NpmRegistry`
and `NpmPackage` combination,
a unique `publish<PackageName>PackageTo<RegistryName>Registry` [NpmPublishTask](../tasks/NpmPublishTask.md) will be
generated and added as a dependency to the `publish` lifecycle task.

The registries can be accessed and configured from a `build.gradle.kts` file via `npmPublish::registries` invocation.

```kotlin title="build.gradle.kts"
npmPublish {
  registries {
    ...
  }
}
```

## Shortcuts

Some shortcuts are also available for common npm registries. These shortcuts simply name the registry and sets the
appropriate uri for you. The names of the registries match the DSL name. Finally, the shortcuts are repeatable and will
detect and configure existing registries on subsequent invocations.

```kotlin title="build.gradle.kts"
npmPublish {
  registries {
    npmjs {
      authToken.set("obfuscated")
    }
    github {
      authToken.set("obfuscated")
    }
  }
}
```

## Properties

=== "Properties"

    | Property                  | Type      | Default                                              | When Kotlin plugin is present |
    |:--------------------------|-----------|:-----------------------------------------------------|-------------------------------|
    | [`access`](#access)       | NpmAccess | [`NpmPublishExtension::access`](extension.md#access) |                               |
    | [`dry`](#dry)             | NpmAccess | [`NpmPublishExtension::dry`](extension.md#dry)       |                               |
    | [`uri`](#uri)             | URI       |                                                      |                               |
    | [`otp`](#otp)             | String    |                                                      |                               |
    | [`authToken`](#authtoken) | String    |                                                      |                               |

=== "Keys"

    | Property                  | CLI | System/Gradle                           | Environment                             |
    |:--------------------------|-----|:----------------------------------------|-----------------------------------------|
    | [`access`](#access)       |     | `npm.publish.registry.<name>.access`    | `NPM_PUBLISH_REGISTRY_<NAME>_ACCESS`    |
    | [`dry`](#dry)             |     | `npm.publish.registry.<name>.dry`       | `NPM_PUBLISH_REGISTRY_<NAME>_DRY`       |
    | [`uri`](#uri)             |     | `npm.publish.registry.<name>.uri`       | `NPM_PUBLISH_REGISTRY_<NAME>_URI`       |
    | [`otp`](#otp)             |     | `npm.publish.registry.<name>.otp`       | `NPM_PUBLISH_REGISTRY_<NAME>_OTP`       |
    | [`authToken`](#authToken) |     | `npm.publish.registry.<name>.authToken` | `NPM_PUBLISH_REGISTRY_<NAME>_AUTHTOKEN` |

=== "Usage"

    ```kotlin title="build.gradle.kts"
    npmPublish {
      registries {
        register("npmjs") {
          access.set(RESTRICTED)
          dry.set(true)
          uri.set(uri("https://registry.npmjs.org")) // (1)
          otp.set("obfuscated")
          authToken.set("obfuscated")
        }
      }
    }
    ```

    1. `uri` can also be set from String as `uri.set("https://registry.npmjs.org")` 
        in which case the plugin will construct an URI instance from the string for you

### `access`

Registry access.
[More info](https://docs.npmjs.com/package-scope-access-level-and-visibility)

### `dry`

Overrides [NpmPublishExtension::dry](extension.md#dry) value for this registry

### `uri`

NPM registry uri to publish packages to. Should include schema domain and path if required. Can be set from `URI`
or `String`

### `otp`

Optional OTP to use when authenticating with the registry

### `authToken`

Auth token to use when authenticating with the registry.
[More info](https://docs.npmjs.com/about-access-tokens)
