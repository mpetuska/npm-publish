package dev.petuska.npm.publish.config

import dev.petuska.npm.publish.extension.NpmPublishExtension
import dev.petuska.npm.publish.extension.domain.NpmAccess
import dev.petuska.npm.publish.util.notFalse
import dev.petuska.npm.publish.util.sysProjectEnvPropertyConvention
import org.gradle.api.Project

internal fun Project.configure(extension: NpmPublishExtension) {
  val layout = layout
  configure(extension.packages)
  configure(extension.registries)
  extension.nodeHome.convention(
    sysProjectEnvPropertyConvention(
      name = "nodeHome",
      default = providers.environmentVariable("NODE_HOME")
    ).map(layout.projectDirectory::dir)
  )
  extension.nodeBin.convention(
    sysProjectEnvPropertyConvention(
      "nodeBin",
      extension.nodeHome.map { it.file("bin/node").asFile.absolutePath }
    ).map(layout.projectDirectory::file)
  )
  extension.npmBin.convention(
    sysProjectEnvPropertyConvention(
      "npmBin",
      extension.nodeHome.map { it.file("bin/npm").asFile.absolutePath }
    ).map(layout.projectDirectory::file)
  )
  extension.readme.convention(
    sysProjectEnvPropertyConvention("readme").map(layout.projectDirectory::file)
  )
  extension.npmIgnore.convention(
    sysProjectEnvPropertyConvention(
      "npmIgnore",
      provider { layout.projectDirectory.file(".npmignore").asFile }
        .map { (if (it.exists()) it.absolutePath else null) }
    ).map(layout.projectDirectory::file)
  )
  extension.npmrc.convention(
    sysProjectEnvPropertyConvention(
      "npmrc",
      provider { layout.projectDirectory.file(".npmrc").asFile }
        .map { (if (it.exists()) it.absolutePath else null) }
    ).map(layout.projectDirectory::file)
  )
  extension.organization.convention(
    sysProjectEnvPropertyConvention("organization")
  )
  extension.version.convention(
    sysProjectEnvPropertyConvention("version", provider { project.version.toString() })
  )
  extension.access.convention(
    sysProjectEnvPropertyConvention("access", provider { NpmAccess.PUBLIC.toString() }).map(NpmAccess::fromString)
  )
  extension.dry.convention(
    sysProjectEnvPropertyConvention("dry", provider { "false" }).map { it.notFalse() }
  )
}
