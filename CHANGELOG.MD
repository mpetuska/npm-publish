## Changelog

### Breaking Changes
* Default npmjs repository removed
* Access field changed from string to enum
* Configurations [registry, access, otp, authToken] removed from publications and added into new 
  repositories configurations
* Publish tasks created for each configured repository only per each publication
* Assemble tasks created for valid publications (with nodeJsDir resolved)
* Publish tasks only created for repositories with the registry & authToken set/found
* `kpm.publish.authToken` property changed to dynamic `kpm.publish.authToken.<repoName>` for each repository
* `kpm.publish.otp` property changed to dynamic `kpm.publish.otp.<repoName>` for each repository

### New Features
* Decoupled from Kotlin plugin to allow using this extension with arbitrary projects (not necessarily kotlin)
* New optional field `publication.nodeJsDir` for custom publications to allow specifying node distribution
* Node distribution now defaults to `$NODE_HOME` env variable if found
* New `files{...}` dsl enabling arbitrary file inclusion/exclusion for publications
* New `dependencies{...}` dsl enabling arbitrary npm dependencies configuration for publications
* New `main` publication property to set main js file in package.json

### Fixes
* Naming conventions issues when using with kotlin js plugin
