# Standalone project without kotlin plugin

This project showcases how to setup the plugin to assemble your standalone project.

## Things to note

- Stub generation task `generateJsFile` is declaring its outputs
- This allows us to source it directly via package's `files {}` API and get automatic task dependencies

## Usage

```shell
../gradlew assembleStandalonePackage
../gradlew packStandalonePackage
../gradlew publishStandalonePackageToNexusRegistry -Pnpm.publish.registry.nexus.dry=false -Pnpm.publish.registry.nexus.authToken=<Token>
```