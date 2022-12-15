# GitHub Packages Publishing Sample

This project showcases how to setup the plugin to publish your library to github packages.

## Things to note

- `organisation` property set to match the repository owner's username
- Since the package name is not the same as repo name, we set the repository in `package.json`
- `github` registry is declared, but not configured in the DSL (all configuration is managed via gradle.properties and
  -P properties at build time)

## Usage

```shell
../gradlew publishJsPackageToGithubRegistry -Pnpm.publish.registry.github.dry=false -Pnpm.publish.registry.github.authToken=<GitHub_Auth_Token>
```