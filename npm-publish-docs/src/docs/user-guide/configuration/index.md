## Summary

Most of the configurations are hooked up to flow downstream to the eventual task configurations in the priority
order, where each layer looks up the chain for default value if an explicit value is not configured for it.
This allows setting default values for a lot of downstream configurations once and overriding them only where needed.

## Configuration Layers

The configuration values are resolved in the following descending priority order:

1. [Properties](properties.md)
    1. CLI options (`--arg=value`)
    2. System properties (`-Dprop=value`)
    3. Gradle properties (`-Pprop=value`, `ORG_GRADLE_PROJECT_prop=value` env
       variable, `-Dorg.gradle.project.prop=value`
       system property or `prop=value` stored in `gradle.properties` file)
    4. Env variables (`PROP=value`)

2. [Extension](extension.md)
3. [Package](package.md) / [Registry](registry.md)
4. [Tasks](../tasks/index.md)

!!! important
    The layering only works for properties that have no explicit values set via the DSL
    as doing so overrides any layering or default behaviour.
