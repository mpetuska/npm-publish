# v2.1.0
## Versions
* Kotlin: 1.5.21
* Gradle: 7.2.0
* JDK: 11

## Changes
* Fixed [#24](https://github.com/mpetuska/npm-publish/issues/24): `types` configuration property was ignored
* Added support for setting configuration properties via environment variables too
* Kotlin version bumped to `1.5.21`

# v2.0.4
## Versions
* Kotlin: 1.5.10
* Gradle: 7.1.1
* JDK: 11

## Changes
* Updated versions
* Dynamic version ranges for increased compatibility
* Fixed a bug that always expected version to be a string
* Removed some duplicated code in sandbox
* New sandbox module to check that plugin can work with both, nodejs() and browser() flavours at the same time
* New `ts-consumer` module in the sandbox to showcase how to include packed K/JS modules into plain TS module