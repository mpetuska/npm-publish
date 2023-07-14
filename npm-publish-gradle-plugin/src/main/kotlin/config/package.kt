package dev.petuska.npm.publish.config

import dev.petuska.npm.publish.extension.NpmPublishExtension
import dev.petuska.npm.publish.extension.domain.NpmPackage
import dev.petuska.npm.publish.extension.domain.json.PackageJson
import dev.petuska.npm.publish.util.sysProjectEnvPropertyConvention
import dev.petuska.npm.publish.util.toCamelCase
import org.gradle.api.Project
import java.io.File

internal fun Project.configure(pkg: NpmPackage) {
  val extension = extensions.getByType(NpmPublishExtension::class.java)
  val prefix = pkg.prefix
  pkg.main.convention(
    sysProjectEnvPropertyConvention(prefix + "main", pkg.packageJson.flatMap(PackageJson::main))
  )
  pkg.types.convention(
    sysProjectEnvPropertyConvention(prefix + "types", pkg.packageJson.flatMap(PackageJson::types))
  )
  pkg.readme.convention(
    sysProjectEnvPropertyConvention(prefix + "readme", extension.readme.asFile.map(File::getAbsolutePath))
      .map(layout.projectDirectory::file)
  )
  pkg.npmIgnore.convention(
    sysProjectEnvPropertyConvention(prefix + "npmIgnore", extension.npmIgnore.asFile.map(File::getAbsolutePath))
      .map(layout.projectDirectory::file)
  )
  pkg.version.convention(
    sysProjectEnvPropertyConvention(prefix + "version", extension.version)
  )
  pkg.packageName.convention(
    sysProjectEnvPropertyConvention(prefix + "packageName", provider { project.name })
  )
  pkg.scope.convention(
    sysProjectEnvPropertyConvention(prefix + "scope", extension.organization)
  )
}

internal inline val NpmPackage.prefix get() = "package.$name."

internal fun assembleTaskName(packageName: String) = "assemble${packageName.toCamelCase()}Package"
internal fun packTaskName(packageName: String) = "pack${packageName.toCamelCase()}Package"
