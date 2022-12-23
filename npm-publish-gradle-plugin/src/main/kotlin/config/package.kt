package dev.petuska.npm.publish.config

import dev.petuska.npm.publish.extension.domain.NpmPackage
import dev.petuska.npm.publish.extension.domain.json.PackageJson
import dev.petuska.npm.publish.util.ProjectEnhancer
import dev.petuska.npm.publish.util.toCamelCase

internal fun ProjectEnhancer.configure(pkg: NpmPackage) {
  val prefix = pkg.prefix
  pkg.project = project
  pkg.main.sysProjectEnvPropertyConvention(prefix + "main", pkg.packageJson.flatMap(PackageJson::main))
  pkg.types.sysProjectEnvPropertyConvention(prefix + "types", pkg.packageJson.flatMap(PackageJson::types))
  pkg.readme.sysProjectEnvPropertyConvention(prefix + "readme", extension.readme) { layout.projectDirectory.file(it) }
  pkg.npmIgnore.sysProjectEnvPropertyConvention(
    prefix + "npmIgnore",
    extension.npmIgnore
  ) { layout.projectDirectory.file(it) }
  pkg.version.sysProjectEnvPropertyConvention(prefix + "version", extension.version)
  pkg.packageName.sysProjectEnvPropertyConvention(prefix + "packageName", provider { project.name })
  pkg.scope.sysProjectEnvPropertyConvention(prefix + "scope", extension.organization)
}

internal inline val NpmPackage.prefix get() = "package.$name."

internal fun assembleTaskName(packageName: String) = "assemble${packageName.toCamelCase()}Package"
internal fun packTaskName(packageName: String) = "pack${packageName.toCamelCase()}Package"
