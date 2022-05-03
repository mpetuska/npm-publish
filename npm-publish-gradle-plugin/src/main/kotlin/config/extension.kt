package dev.petuska.npm.publish.config

import dev.petuska.npm.publish.extension.NpmPublishExtension
import dev.petuska.npm.publish.extension.domain.NpmAccess
import dev.petuska.npm.publish.util.ProjectEnhancer
import dev.petuska.npm.publish.util.notFalse
import dev.petuska.npm.publish.util.unsafeCast

internal fun ProjectEnhancer.configure(extension: NpmPublishExtension) {
  configure(extension.packages)
  configure(extension.registries)
  extension.nodeHome.sysProjectEnvPropertyConvention(
    name = "nodeHome",
    default = providers.environmentVariable("NODE_HOME").map { layout.projectDirectory.dir(it) },
    converter = { layout.projectDirectory.dir(it) }
  )
  extension.readme.sysProjectEnvPropertyConvention("readme") { layout.projectDirectory.file(it) }
  extension.npmIgnore.sysProjectEnvPropertyConvention(
    "npmIgnore",
    provider { layout.projectDirectory.file(".npmignore") }.map { (if (it.asFile.exists()) it else null).unsafeCast() }
  ) { layout.projectDirectory.file(it) }
  extension.organization.sysProjectEnvPropertyConvention("organization")
  extension.version.sysProjectEnvPropertyConvention("version", provider { project.version.toString() })
  extension.access.sysProjectEnvPropertyConvention("access", provider { NpmAccess.PUBLIC }, NpmAccess::fromString)
  extension.dry.sysProjectEnvPropertyConvention("dry", provider { false }) { it.notFalse() }
}
