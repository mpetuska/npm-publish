package dev.petuska.npm.publish.config

import dev.petuska.npm.publish.extension.domain.*
import dev.petuska.npm.publish.util.*
import org.gradle.configurationcache.extensions.*

internal fun ProjectEnhancer.configure(pkg: NpmPackage) {
  val prefix = pkg.prefix
  pkg.readme.sysProjectEnvPropertyConvention(prefix + "readme", extension.readme) { layout.projectDirectory.file(it) }
  pkg.types.sysProjectEnvPropertyConvention(prefix + "types")
  pkg.main.sysProjectEnvPropertyConvention(prefix + "main")
  pkg.version.sysProjectEnvPropertyConvention(prefix + "version", extension.version)
  pkg.packageName.sysProjectEnvPropertyConvention(prefix + "packageName", provider { project.name })
  pkg.scope.sysProjectEnvPropertyConvention(prefix + "scope", extension.organization)
}

internal inline val NpmPackage.prefix get() = "package.$name."

internal fun assembleTaskName(packageName: String) = "assemble${packageName.capitalized()}NpmPackage"
internal fun packTaskName(packageName: String) = "pack${packageName.capitalized()}NpmPackage"
