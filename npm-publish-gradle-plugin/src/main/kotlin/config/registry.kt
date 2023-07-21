package dev.petuska.npm.publish.config

import dev.petuska.npm.publish.extension.NpmPublishExtension
import dev.petuska.npm.publish.extension.domain.NpmAccess
import dev.petuska.npm.publish.extension.domain.NpmRegistry
import dev.petuska.npm.publish.task.NpmAssembleTask
import dev.petuska.npm.publish.task.NpmPublishTask
import dev.petuska.npm.publish.util.PluginLogger
import dev.petuska.npm.publish.util.notFalse
import dev.petuska.npm.publish.util.sysProjectEnvPropertyConvention
import dev.petuska.npm.publish.util.toCamelCase
import org.gradle.api.Project
import java.net.URI

internal fun Project.configure(registry: NpmRegistry) {
  val extension = extensions.getByType(NpmPublishExtension::class.java)
  val prefix = registry.prefix
  registry.access.convention(
    sysProjectEnvPropertyConvention(prefix + "access", extension.access.map(NpmAccess::toString))
      .map(NpmAccess::fromString)
  )
  registry.uri.convention(sysProjectEnvPropertyConvention(prefix + "uri").map(::URI))
  registry.otp.convention(sysProjectEnvPropertyConvention(prefix + "otp"))
  registry.authToken.convention(sysProjectEnvPropertyConvention(prefix + "authToken"))
  registry.dry.convention(
    sysProjectEnvPropertyConvention(prefix + "dry", extension.dry.map(Boolean::toString)).map { it.notFalse() }
  )
}

internal fun Project.registerPublishTask(packageName: String, registryName: String): Unit =
  with(PluginLogger.wrap(logger)) {
    val extension = extensions.getByType(NpmPublishExtension::class.java)
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
