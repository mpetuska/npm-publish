## Changelog
Various quality of life improvements for CI/CD and widely extended bundled dependencies control

### Breaking Changes
* `npm.publish.authToken.<repository>` property renamed to `npm.publish.repository.<repository>.authToken`
* `npm.publish.otp.<repository>` property renamed to `npm.publish.repository.<repository>.otp`
* `packageJson.bundledDependencies` type changes from `MutableList` to `MutableSet`

### New Features
* `publication("name") {...}` DSL will now try to find an existing publication with the same name before creating a new one
* Boolean properties can now be passed in without specifying true as `-Pnpm.publish.dry` (`=false` is still respected)
* All DSL configuration properties exposed as project properties to allow overrides from CMD line (see full list in README.MD)
* New NpmPack utility task that can be ran to produce a tarball of your npm package (instead of publishing it)
* New `packageJson.bundledDependencies` property and DSL to allow fine-grained control of what dependencies should be bundled
* `kotlin-test*` dependencies excluded from bundling by default
* New `npmPublishing.version` field exposing default version override for all npm publications
* Improved npm dependency resolution (see README.MD)

### Fixes
* `version` property now defaults to `project.version` or `project.rootProject.version`, whichever found first
* packageAssemble task will not run if kotlinCompile task did not produce output (due to missing sources or otherwise)
* packageAssemble task will always run if kotlinCompile task produced output
* pack/publish tasks will only run if packageAssemble task ran
