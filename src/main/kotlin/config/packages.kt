package dev.petuska.npm.publish.config

import dev.petuska.npm.publish.extension.domain.*
import dev.petuska.npm.publish.task.*
import dev.petuska.npm.publish.util.*
import org.gradle.configurationcache.extensions.*

internal fun ProjectEnhancer.configure(packages: NpmPackages) {
  packages.whenObjectAdded {
    configure(it)
    val assTask = tasks.register(assembleTaskName(it.name), NpmAssembleTask::class.java) { task ->
      task.description = "Assembles ${it.name} NPM package."
      task.`package`.set(it)
    }
    tasks.register(packTaskName(it.name), NpmPackTask::class.java) { task ->
      task.description = "Packs ${it.name} NPM package."
      task.dry.set(extension.dry)
      task.packageDir.set(assTask.flatMap(NpmAssembleTask::destinationDir))
      task.nodeHome.set(extension.nodeHome)
    }
    extension.registries.names.forEach { registryName ->
      tasks.register(publishTaskName(it.name, registryName), NpmPublishTask::class.java) { task ->
        description = "Publishes ${it.name} NPM package to $registryName NPM registry"
        task.registry.set(extension.registries.named(registryName))
        task.nodeHome.set(extension.nodeHome)
        task.packageDir.set(
          tasks.named(assembleTaskName(it.name), NpmAssembleTask::class.java).flatMap(NpmAssembleTask::destinationDir)
        )
      }
    }
  }
  packages.whenObjectRemoved {
    tasks.findByName(assembleTaskName(it.name))?.let(tasks::remove)
    tasks.findByName(packTaskName(it.name))?.let(tasks::remove)
    tasks.names.filter { name ->
      name.startsWith("publish${it.name.capitalized()}PackageTo")
    }.map(tasks::findByName).forEach(tasks::remove)
  }
}
