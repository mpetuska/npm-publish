# WIP
## Build Versions
* Kotlin: 1.4.31
* Gradle: 7.3.3
* JDK: 11
## Changes
* Kotlin plugin version bumped to `1.6.10`
* Simplified `JsonObject` DSL builders by rebasing it onto `dev.petuska:kon`
* Implemented proper nested `package.json` template merging
* Rolled back to good old `ktlint` formatting


# v2.1.1
## Build Versions
* Kotlin: 1.4.31
* Gradle: 7.2.0
* JDK: 11
## Changes
* Kotlin plugin version bumped to `1.5.30`
* Sample GH action introduced to showcase CI publishing
* Reworked ts-consumer to showcase module inter-dependencies


# v2.1.0
## Build Versions
* Kotlin: 1.4.31
* Gradle: 7.2.0
* JDK: 11
## Changes
* Fixed [#24](https://github.com/mpetuska/npm-publish/issues/24): `types` configuration property was ignored
* Added support for setting configuration properties via environment variables too
* Kotlin plugin version bumped to `1.5.21`


# v2.0.4
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
