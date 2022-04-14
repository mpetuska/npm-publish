package dev.petuska.npm.publish.config

import dev.petuska.npm.publish.extension.domain.*
import dev.petuska.npm.publish.task.*
import dev.petuska.npm.publish.util.*
import org.gradle.configurationcache.extensions.*

internal fun ProjectEnhancer.configure(registries: NpmRegistries) {
  registries.whenObjectAdded {
    configure(it)
    extension.packages.names.forEach { pkgName ->
      tasks.register(publishTaskName(pkgName, it.name), NpmPublishTask::class.java) { task ->
        description = "Publishes $pkgName NPM package to ${it.name} NPM registry"
        task.registry.set(it)
        task.nodeHome.set(extension.nodeHome)
        task.packageDir.set(
          tasks.named(assembleTaskName(pkgName), NpmAssembleTask::class.java).flatMap(NpmAssembleTask::destinationDir)
        )
      }
    }
  }
  registries.whenObjectRemoved {
    tasks.names.filter { name ->
      name.startsWith("publish") &&
        name.endsWith("PackageTo${it.name.capitalized()}Registry")
    }.map(tasks::findByName).forEach(tasks::remove)
  }
}
