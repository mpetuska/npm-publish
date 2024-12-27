package dev.petuska.npm.publish.config

import dev.petuska.npm.publish.extension.NpmPublishExtension
import dev.petuska.npm.publish.extension.domain.NpmAccess
import dev.petuska.npm.publish.util.notFalse
import dev.petuska.npm.publish.util.sysProjectEnvPropertyConvention
import dev.petuska.npm.publish.util.unsafeCast
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
  extension.nodeBin.convention(extension.nodeHome.map { it.file("bin/node") })
  extension.npmBin.convention(extension.nodeHome.map { it.file("bin/npm") })
  extension.readme.convention(
    sysProjectEnvPropertyConvention("readme").map { layout.projectDirectory.file(it) }
  )
  extension.npmIgnore.convention(
    sysProjectEnvPropertyConvention(
      "npmIgnore",
      provider { layout.projectDirectory.file(".npmignore") }
        .map { (if (it.asFile.exists()) it else null).unsafeCast() }
    ).map { layout.projectDirectory.file(it) }
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
