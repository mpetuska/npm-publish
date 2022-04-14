# 2.1.2
## Build Versions
* Kotlin: 1.5.31
* Gradle: 7.4.2
* JDK: 11
## Breaking Changes
* `publication` and `repository` DSLs removed. Use regula gradle's `register` DSL
* All API reworked to use gradle provider API. Replace all `=` assignments to `by` infix DSL or proper `.set()` invocation.
* `NpmPublication::nodeJsDir` renamed to `nodeHome` and moved to `NpmPublishExtension`. It is now shared across all publications.
* `NpmPublication::destinationDir` moved to `NpmPackageAssembleTask`.
* `NpmRepository::dry` moved to `NpmPackTask` and `NpmPublishTask` with `--dry` cli option added. Both still default to `NpmPublishExtension::dry`
## Changes
* Kotlin plugin version bumped to `1.6.20`

# 2.1.2
## Build Versions
* Kotlin: 1.4.31
* Gradle: 7.3.3
* JDK: 11
## Changes
* Kotlin plugin version bumped to `1.6.10`
* Simplified `JsonObject` DSL builders by rebasing it onto `dev.petuska:kon`
* Implemented proper nested `package.json` template merging
* Rolled back to good old `ktlint` formatting


# 2.1.1
## Build Versions
* Kotlin: 1.4.31
* Gradle: 7.2.0
* JDK: 11
## Changes
* Kotlin plugin version bumped to `1.5.30`
* Sample GH action introduced to showcase CI publishing
* Reworked ts-consumer to showcase module inter-dependencies


# 2.1.0
## Build Versions
* Kotlin: 1.4.31
* Gradle: 7.2.0
* JDK: 11
## Changes
* Fixed [#24](https://github.com/mpetuska/npm-publish/issues/24): `types` configuration property was ignored
* Added support for setting configuration properties via environment variables too
* Kotlin plugin version bumped to `1.5.21`


# 2.0.4
## Build Versions
* Kotlin: 1.4.31
* Gradle: 7.1.1
* JDK: 11
## Changes
* Updated versions
* Dynamic version ranges for increased compatibility
* Fixed a bug that always expected version to be a string
* Removed some duplicated code in sandbox
* New sandbox module to check that plugin can work with both, nodejs() and browser() flavours at the same time
* New `ts-consumer` module in the sandbox to showcase how to include packed K/JS modules into plain TS module
