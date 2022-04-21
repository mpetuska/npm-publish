package dev.petuska.npm.publish.config

import dev.petuska.npm.publish.extension.domain.NpmAccess
import dev.petuska.npm.publish.extension.domain.NpmRegistry
import dev.petuska.npm.publish.task.NpmAssembleTask
import dev.petuska.npm.publish.task.NpmPublishTask
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

internal fun ProjectEnhancer.registerPublishTask(packageName: String, registryName: String) {
  tasks.register(publishTaskName(packageName, registryName), NpmPublishTask::class.java) { task ->
    description = "Publishes $packageName package to $registryName registry"
    task.registry.set(extension.registries.named(registryName))
    task.nodeHome.set(extension.nodeHome)
    task.packageDir.set(
      tasks.named(assembleTaskName(packageName), NpmAssembleTask::class.java).flatMap(NpmAssembleTask::destinationDir)
    )
  }.also { task ->
    info { "Registered [${task.name}] NpmPublishTask for [$packageName] NpmPackage and [$registryName] NpmRegistry" }
  }
}

internal inline val NpmRegistry.prefix get() = "registry.$name."

internal fun publishTaskName(packageName: String, registryName: String) =
  "publish${packageName.toCamelCase()}PackageTo${registryName.toCamelCase()}Registry"
