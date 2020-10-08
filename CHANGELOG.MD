## Changelog
Major name/repo/id/registry migration to future proof the plugin 
as the mission now has changed to support all NPM projects, not just Kotlin.

### Breaking Changes
* Migrated from `lt.petuska.kpm.publish` to `lt.petuska.npm.publish` since it's no longer kotlin-specific
* All `Kpm*` references updated to `Npm*` (to migrate just run IDE refactoring replacing 
  all `kpm` occurrences to `npm` while preserving case)
* Extension renamed from `kpmPublish` to `npmPublishing` to better reflect `maven-publish` conventions

### New Features
N/A

### Fixes
N/A
