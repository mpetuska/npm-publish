package dev.petuska.npm.publish.config

import dev.petuska.npm.publish.extension.domain.*
import dev.petuska.npm.publish.util.*
import org.gradle.configurationcache.extensions.*
import java.net.*

internal fun ProjectEnhancer.configure(registry: NpmRegistry) {
  val prefix = registry.prefix
  registry.access.sysProjectEnvPropertyConvention(prefix + "access", extension.access, NpmAccess::fromString)
  registry.uri.sysProjectEnvPropertyConvention(prefix + "registry", converter = ::URI)
  registry.otp.sysProjectEnvPropertyConvention(prefix + "otp")
  registry.authToken.sysProjectEnvPropertyConvention(prefix + "authToken")
}

internal inline val NpmRegistry.prefix get() = "registry.$name."

internal fun publishTaskName(packageName: String, registryName: String) =
  "publish${packageName.capitalized()}PackageTo${registryName.capitalized()}Registry"
