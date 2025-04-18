[![Slack chat](https://img.shields.io/badge/kotlinlang-%23npm--publish-darkgreen?logo=slack&style=flat-square)](https://kotlinlang.slack.com/channels/npm-publish)
[![Mkdocs docs](https://img.shields.io/badge/docs-mkdocs-blue?style=flat-square&logo=kotlin&logoColor=white)](https://npm-publish.petuska.dev)
[![Version gradle-plugin-portal](https://img.shields.io/maven-metadata/v?label=gradle%20plugin%20portal&logo=gradle&metadataUrl=https%3A%2F%2Fplugins.gradle.org%2Fm2%2Fdev.petuska%2Fnpm-publish-gradle-plugin%2Fmaven-metadata.xml&style=flat-square)](https://plugins.gradle.org/plugin/dev.petuska.npm.publish)
[![Version maven-central](https://img.shields.io/maven-central/v/dev.petuska/npm-publish-gradle-plugin?logo=apache-maven&style=flat-square)](https://mvnrepository.com/artifact/dev.petuska/npm-publish-gradle-plugin/latest)

# NPM-PUBLISH GRADLE PLUGIN

> [!IMPORTANT]  
> Unfortunately I no longer have the capacity to maintain this plugin. If anyone out there is willing to take over the effort and for the repository, feel free to contact me to add references to your forks in this archived repository.

Gradle plugin enabling NPM publishing (essentially `maven-publish` for NPM packages). Integrates seamlessly with
Kotlin/JS/MPP plugin if applied, providing auto configurations.

> Check the latest [release](https://github.com/mpetuska/npm-publish/releases/latest) for verified JVM, Kotlin and
> Gradle tooling versions

## Setup

Here's a bare minimum setup when using together with one of the kotlin plugins. This setup would produce the following
tasks:

* `assembleJsPackage`
* `packJsPackage`
* `publishJsPackageToNpmjsRegistry`

```kotlin title="build.gradle.kts"
plugins {
    id("dev.petuska.npm.publish") version "<VERSION>"
    kotlin("multiplatform") version "<VERSION>>" // Optional, also supports "js"
}

kotlin {
    js(IR) {
        binaries.library()
        browser() // or nodejs()
    }
}

npmPublish {
    registries {
        // For registries expecting an authentiation token, use authToken
        register("npmjs") {
            uri.set("https://registry.npmjs.org")
            authToken.set("obfuscated")
        }
        
        // For registries expecting a username and password, use auth or username + password
        register("nexus") {
            uri.set("https://nexus.example.com/repository/npm-internal")
            username.set("obfuscated")
            password.set("obfuscated")
            // Or:
            // auth.set("base64-encoded-string")
        }
    }
}
```

Full documentation can be found
on [npm-publish.petuska.dev](https://npm-publish.petuska.dev/latest/user-guide/quick-start/)

## Contributing

See [CONTRIBUTING](.github/CONTRIBUTING.md)

Thanks to all the people who contributed to npm-publish!

<a href="https://github.com/mpetuska/npm-publish/graphs/contributors">
  <img src="https://contrib.rocks/image?repo=mpetuska/npm-publish" />
</a>
