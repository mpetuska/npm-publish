[![Slack chat](https://img.shields.io/badge/kotlinlang-%23npm--publish-green?logo=slack&style=flat-square)](https://kotlinlang.slack.com/channels/npm-publish)
[![Dokka docs](https://img.shields.io/badge/docs-dokka-orange?style=flat-square)](http://mpetuska.github.io/npm-publish/api)
[![Version gradle-plugin-portal](https://img.shields.io/maven-metadata/v?label=gradle%20plugin%20portal&logo=gradle&metadataUrl=https%3A%2F%2Fplugins.gradle.org%2Fm2%2Fdev.petuska%2Fnpm-publish%2Fmaven-metadata.xml&style=flat-square)](https://plugins.gradle.org/plugin/dev.petuska.npm.publish)
[![Version maven-central](https://img.shields.io/maven-central/v/dev.petuska/npm-publish?logo=apache-maven&style=flat-square)](https://mvnrepository.com/artifact/dev.petuska/npm-publish/latest)

# NPM-PUBLISH GRADLE PLUGIN

Gradle plugin enabling NPM publishing (essentially `maven-publish` for NPM packages). Integrates seamlessly with
Kotlin/JS/MPP plugin if applied, providing auto configurations.

> The plugin was last tested with `JDK 11`, `Kotlin 1.6.21` & `Gradle 7.4.2`

## Setup

Here's a bare minimum setup when using together with one of the kotlin plugins. This setup would produce the following
tasks:

* `assembleJsNpmPublication`
* `packJsNpmPublication`
* `publishJsNpmPublicationToNpmjs`
* `assembleIrNpmPublication`
* `packIrNpmPublication`
* `publishIrNpmPublicationToNpmjs`

```kotlin title="build.gradle.kts" linenums="1"
plugins {
  id("dev.petuska.npm.publish") version "<VERSION>"
  kotlin("multiplatform") version "1.6.10" // Optional, also supports "js"
}

kotlin {
  // Legacy mode
  js(Legacy) {
    browser() // or nodejs()
  }
  // OR IR mode
  js(IR) {
    binaries.library()
    browser() // or nodejs()
  }
}

npmPublishing {
  repositories {
    repository("npmjs") {
      registry = uri("https://registry.npmjs.org")
      authToken = "asdhkjsdfjvhnsdrishdl"
    }
  }
}
```

### Configuration

You can add publications and npm repositories or override existing configuration defaults via npmPublishing extension.
When kotlin JS/MPP plugin is applied, this plugin will automatically create a publication for each JS target you JS/MPP
project has.

#### DSL

```kotlin title="build.gradle.kts" linenums="1"
npmPublishing {
  readme = file("README.MD") // (optional) Default readme file
  organization = "my.org" // (Optional) Used as default scope for all publications
  access = PUBLIC // or RESTRICTED. Specifies package visibility, defaults to PUBLIC
  /*
    Enables kotlin jar dependencies (including their transitive dependencies) to be resolved bundled automatically for autogenerated publications.
    Defaults to true and can be overridden for each publication.
    
    Is disabled for IR binaries as they already come with all kotlin dependencies bundled into js output file
   */
  bundleKotlinDependencies = true
  /*
    Adds all bundled dependencies to npm-shrinkwrap.json. Defaults to true and can be overridden for each publication.
    Does not generate a file, even if enabled if there are no bundledDependencies resolved
   */
  shrinkwrapBundledDependencies = true
  /*
    (Optional) Enables run npm publishing with `--dry-run` (does everything except uploading the files). Defaults to false.
   */
  dry = false
  /*
    Overriding default version. Defaults to project.version or rootProject.version, whichever found first
   */
  version = "1.0.0"
  /*
    NodeJs home directory. Defaults to $NODE_HOME if present or kotlinNodeJsSetup output for default publications
   */
  nodeHome = file("~/nodejs")

  repositories {
    register("npmjs") {
      registry = uri("https://registry.npmjs.org") // Registry to publish to
      authToken = "asdhkjsdfjvhnsdrishdl" // NPM registry authentication token
      otp = "gfahsdjglknamsdkpjnmasdl" // NPM registry authentication OTP
    }
    register("bintray") {
      access = RESTRICTED
      registry = ("https://dl.bintray.com/mpetuska/dev.petuska.npm") // Registry to publish to
      authToken = "sngamascdgb" // NPM registry authentication token
      otp = "miopuhimpdfsazxfb" // (Optional) NPM registry authentication OTP
    }
  }
  publications {
    val jsOne by getting { // Publication build for target declared as `kotlin { js("jsOne") { nodejs() } }`
      scope = "not.my.org" // Overriding package scope that defaulted to organization property from before
      version = "1.0.0-custom" // Overriding version for this publication. Defaults to extension default version
    }
    register("customPublication") { //Custom publication
      bundleKotlinDependencies = true // Overrides the global default for this publication
      shrinkwrapBundledDependencies = true // Overrides the global default for this publication
      moduleName = "my-module-name-override" // Defaults to project name
      scope = "other.comp" // Defaults to global organisation
      readme = file("docs/OTHER.MD") // Defaults to global readme
      main = "my-module-name-override-js.js" // Main output file name, set automatically for default publications
      types = "my-module-name-override-js.d.ts" // TS types output file name, set automatically for default publications

      // Entirely Optional

      dependencies {
        npm("snabbdom", "*")
        npmDev("typescript", "*")
        npmOptional("webpack", "*")
        npmPeer("react", "*")
      }
      files { assemblyDir -> // Specifies what files should be packaged. Preconfigured for default publications, yet can be extended if needed
        from("$assemblyDir/../dir")
        // Rest of your CopySpec     
      }
      pakageJsonFile =
        file("static/package.template.json") // If set will be used as-is ignoring further configurations while getting renamed to package.json regardless of the actual name.
      packageJsonTemplateFile =
        file("templates/package.template.json") // Will be used as a template for default settings, `packageJson` DSL can override its settings.
      packagejson = { // Full package.json override
        main = "./dist/yet-another-override.js"
        types = "./dist/yet-another-override.d.ts"
      }
      packageJson { // Will be patched on top of default generated package.json
        private = false
        bundledDependencies = jsonSet("kotlin") // Suppresses and replaces autogenerated bundled dependencies
        bundledDependencies("kotlin") { // Always includes "kotlin" dependency and filters out the rest by the spec
          -"kotlin-test" // Exclude "kotlin-test" dependency
          +"kotlin-test" // Include "kotlin-test" dependency
          -"kotlin.*".toRegex() // Exclude all dependencies starting with "kotlin"
          +"kotlin.*".toRegex() // Include all dependencies starting with "kotlin"
        }
        keywords = jsonArray(
          "kotlin"
        )
        publishConfig {
          tag = "latest"
        }
        "customField" to jsonObject {
          "customValues" to jsonArray(1, 2, 3)
        }
      }
    }
  }
}
```

#### Properties

Most of the DSL configuration options can also be set/overridden via gradle
properties (`./gradlew task -Pprop.name=propValue`), `gradle.properties` or `local.properties`. These properties can
also be set via environment variables by replacing `.`, ` ` & `-` with `_` and capitalising all names. Properties are
resolved in the following priority:

1. System Properties
2. Gradle Properties
3. Environment Variables
4. DSL
5. Defaults

##### Extension

* `npm.publish.readme (NPM_PUBLISH_README)`
* `npm.publish.organization (NPM_PUBLISH_ORGANIZATION)`
* `npm.publish.access (NPM_PUBLISH_ACCESS)`
* `npm.publish.dry (NPM_PUBLISH_DRY)`
* `npm.publish.version (NPM_PUBLISH_VERSION)`
* `npm.publish.nodeHome (NPM_PUBLISH_NODEHOME)`

##### Package

* `npm.publish.publication.<name>.scope (NPM_PUBLISH_PUBLICATION_<NAME>_SCOPE)`
* `npm.publish.publication.<name>.packageName (NPM_PUBLISH_PUBLICATION_<NAME>_PACKAGENAME)`
* `npm.publish.publication.<name>.version (NPM_PUBLISH_PUBLICATION_<NAME>_VERSION)`
* `npm.publish.publication.<name>.main (NPM_PUBLISH_PUBLICATION_<NAME>_MAIN)`
* `npm.publish.publication.<name>.types (NPM_PUBLISH_PUBLICATION_<NAME>_TYPES)`
* `npm.publish.publication.<name>.readme (NPM_PUBLISH_PUBLICATION_<NAME>_README)`

##### Registry

* `npm.publish.registry.<name>.access (NPM_PUBLISH_REGISTRY_<NAME>_ACCESS)`
* `npm.publish.registry.<name>.uri (NPM_PUBLISH_REGISTRY_<NAME>_URI)`
* `npm.publish.registry.<name>.otp (NPM_PUBLISH_REGISTRY_<NAME>_OTP)`
* `npm.publish.registry.<name>.authToken (NPM_PUBLISH_REGISTRY_<NAME>_AUTHTOKEN)`

## Tasks

The plugin generates the following gradle tasks for various configuration elements:

* NpmPackageAssembleTask: generated for each publication and named
  as `assemble<UpperCammelCasePublicationName>NpmPublication`
* NpmPackTask: generated for each publication and named as `pack<UpperCammelCasePublicationName>NpmPublication`
* NpmPublishTask: generated for each publication + repository combination and named
  as `publish<UpperCammelCasePublicationName>NpmPublicationTo<UpperCammelCaseRepositoryName>`
  All created tasks are added as dependencies to grouping tasks to allow for group-invocation:
* NpmPackageAssembleTask: `assemble` task in `build` group
* NpmPackTask: `pack` task in `build` group
* NpmPublishTask: `publish` task in `publishing` group

## Dependency Resolution

Npm dependencies detected/declared for each publication are resolved into relevant package.json dependency block by this
priority order by their name (descending priority):

1. Optional
2. Peer
3. Dev
4. Normal

This ensures that any given dependency does not appear in multiple dependency scopes.

## Known Issues

* [#6435](https://github.com/npm/npm/issues/6435): (Only applies to legacy backend) npm and yarn tries to download
  bundled dependencies. Can be overcome for npm (sadly not yarn) with `shrinkwrapBundledDependencies` option. Note that
  it works fine for both package managers when installing from a tarball.
  Bug [#2143](https://github.com/npm/cli/issues/2143) on the new npm repo, so please vote for that to get it fixed.
  Bug [#8436](https://github.com/yarnpkg/yarn/issues/8436) on the yarn repo, so please vote for that to get it fixed.
