## Summary

Most of the configuration options are set to be configurable via various CLI inputs and follow
a [configuration layers](index.md#configuration-layers) order.
Such CLI properties can be passed in four different ways -
[Gradle CLI Options](#gradle-cli-options),
[System Properties](#system-properties),
[Gradle Extra Properties](#gradle-extra-properties),
[Gradle Properties](#gradle-properties) and
[Environment Variables]().

## Gradle CLI Options

Gradle CLI options are only available for the tasks and can be passed in by specifying their keys prefixed with double
hyphens (`--`) right after the task name.
Boolean properties can omit the value when representing `true` states.

```bash title="/bin/bash"
./gradlew publishJsPackageToNpmjsRegistry --dry --authToken="obfuscated"
```

## System Properties

System properties are only available for the configurations and can be passed in by specifying their keys prefixed
with `-D` anywhere on the command line.
Boolean properties can omit the value when representing `true` states.

```bash title="/bin/bash"
./gradlew publishJsPackageToNpmjsRegistry -Dnpm.publish.registry.npmjs.dry -Dnpm.publish.registry.npmjs.authToken="obfuscated"
```

## Gradle Extra Properties

Gradle extra properties are only available for the configurations and can be passed in via `extra` project extension.
This can be useful for multi-module builds that needs to configure other modules from submodules.
Boolean properties can omit the value when representing `true` states.

```kotlin title="build.gradle.kts"
extra.properties["npm.publish.registry.npmjs.dry"] = null
extra.properties["npm.publish.registry.npmjs.authToken"] = "obfuscated"
```

## Gradle Properties

Gradle properties are only available for the configurations and can be passed in by specifying their keys prefixed
with `-P` anywhere on the command line or via the `gradle.properties` file.
Boolean properties can omit the value when representing `true` states.

=== "CLI"
    ```bash title="/bin/bash"
    ./gradlew publishJsPackageToNpmjsRegistry -Pnpm.publish.registry.npmjs.dry -Pnpm.publish.registry.npmjs.authToken="obfuscated"
    ```

=== "gradle.properties"
    ```properties title="gradle.properties"
    npm.publish.registry.npmjs.dry=
    npm.publish.registry.npmjs.authToken="obfuscated"
    ```

## Environment Variables

Environment variables are only available for the configurations and can be passed in by specifying their uppercase keys
in the current process's environment.
Boolean properties can omit the value when representing `true` states.

```bash title="/bin/bash"
export NPM_PUBLISH_REGISTRY_NPMJS_DRY=
export NPM_PUBLISH_REGISTRY_NPMJS_AUTHTOKEN="obfuscated"
./gradlew publishJsPackageToNpmjsRegistry
```
