## Changelog
NPM Dependency resolution bugfixes

### Breaking Changes
N/A

### New Features
* New JS/IR model support for 1.4.20

### Fixes
* Fixed a bug that prevented normal npm dependencies from resolving when declared
  in sourceSet dependencies or top-level `publication.dependencies` block
* Fixed an issue with kotlin-gradle-plugin dependency declaration that always upgraded
  consumer's kotlin version to the one used by the plugin.
