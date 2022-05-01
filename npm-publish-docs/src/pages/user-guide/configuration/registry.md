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

## Properties

=== "Properties"

| Property                  | Type      | Default                                              | When Kotlin plugin is present |
|:--------------------------|-----------|:-----------------------------------------------------|-------------------------------|
| [`access`](#access)       | NpmAccess | [`NpmPublishExtension::access`](extension.md#access) |                               |
| [`uri`](#uri)             | URI       |                                                      |                               |
| [`otp`](#otp)             | String    |                                                      |                               |
| [`authToken`](#authtoken) | String    |                                                      |                               |

=== "Usage"

    ```kotlin title="build.gradle.kts"
    npmPublish {
      registries {
        register("npmjs") {
          access.set(RESTRICTED)
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

### `uri`

NPM registry uri to publish packages to. Should include schema domain and path if required. Can be set from `URI`
or `String`

### `otp`

Optional OTP to use when authenticating with the registry

### `authToken`

Auth token to use when authenticating with the registry.
[More info](https://docs.npmjs.com/about-access-tokens)
