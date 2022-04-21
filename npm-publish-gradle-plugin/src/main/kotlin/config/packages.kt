package dev.petuska.npm.publish.config

import dev.petuska.npm.publish.extension.domain.NpmPackages
import dev.petuska.npm.publish.task.NpmAssembleTask
import dev.petuska.npm.publish.task.NpmPackTask
import dev.petuska.npm.publish.util.ProjectEnhancer
import org.gradle.configurationcache.extensions.capitalized

internal fun ProjectEnhancer.configure(packages: NpmPackages) {
  packages.whenObjectAdded {
    configure(it)
    val assTask = tasks.register(assembleTaskName(it.name), NpmAssembleTask::class.java) { task ->
      task.description = "Assembles ${it.name} package."
      task.`package`.set(it)
    }.also { task ->
      info { "Registered [${task.name}] NpmAssembleTask for [${it.name}] NpmPackage" }
    }
    tasks.register(packTaskName(it.name), NpmPackTask::class.java) { task ->
      task.description = "Packs ${it.name} package."
      task.dry.set(extension.dry)
      task.packageDir.set(assTask.flatMap(NpmAssembleTask::destinationDir))
      task.nodeHome.set(extension.nodeHome)
    }.also { task ->
      info { "Registered [${task.name}] NpmPackTask for [${it.name}] NpmPackage" }
    }
    extension.registries.names.forEach { registryName ->
      registerPublishTask(it.name, registryName)
    }
  }
  packages.whenObjectRemoved {
    tasks.findByName(assembleTaskName(it.name))?.let { task ->
      tasks.remove(task)
      info { "Removed [${task.name}] NpmAssembleTask due to removed [${it.name}] NpmPackage" }
    }
    tasks.findByName(packTaskName(it.name))?.let { task ->
      tasks.remove(task)
      info { "Removed [${task.name}] NpmPackTask due to removed [${it.name}] NpmPackage" }
    }
    tasks.names.filter { name ->
      name.startsWith("publish${it.name.capitalized()}PackageTo")
    }.mapNotNull(tasks::findByName).forEach { task ->
      tasks.remove(task)
      info { "Removed [${task.name}] NpmPublishTask due to removed [${it.name}] NpmPackage" }
    }
  }
}
