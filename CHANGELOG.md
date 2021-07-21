# v2.0.4
## Versions
* Kotlin: 1.5.21
* Gradle: 7.1.1
* JDK: 11

## Changes
* Updated versions
* Dynamic version ranges for increased compatibility
* Fixed a bug that always expected version to be a string
* Removed some duplicated code in sandbox
* New sandbox module to check that plugin can work with both, nodejs() and browser() flavours at the same time
* New `ts-consumer` module in the sandbox to showcase how to include packed K/JS modules into plain TS module