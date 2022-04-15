package dev.petuska.npm.publish.config

import dev.petuska.npm.publish.extension.domain.NpmAccess
import dev.petuska.npm.publish.extension.domain.NpmRegistry
import dev.petuska.npm.publish.util.ProjectEnhancer
import dev.petuska.npm.publish.util.toCamelCase
import java.net.URI

internal fun ProjectEnhancer.configure(registry: NpmRegistry) {
  val prefix = registry.prefix
  registry.access.sysProjectEnvPropertyConvention(prefix + "access", extension.access, NpmAccess::fromString)
  registry.uri.sysProjectEnvPropertyConvention(prefix + "uri", converter = ::URI)
  registry.otp.sysProjectEnvPropertyConvention(prefix + "otp")
  registry.authToken.sysProjectEnvPropertyConvention(prefix + "authToken")
}

internal inline val NpmRegistry.prefix get() = "registry.$name."

internal fun publishTaskName(packageName: String, registryName: String) =
  "publish${packageName.toCamelCase()}PackageTo${registryName.toCamelCase()}Registry"
