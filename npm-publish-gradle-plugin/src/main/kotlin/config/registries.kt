package dev.petuska.npm.publish.config

import dev.petuska.npm.publish.extension.NpmPublishExtension
import dev.petuska.npm.publish.extension.domain.NpmRegistries
import dev.petuska.npm.publish.util.PluginLogger
import org.gradle.api.Project
import org.gradle.configurationcache.extensions.capitalized

internal fun Project.configure(registries: NpmRegistries): Unit = with(PluginLogger.wrap(logger)) {
  val extension = extensions.getByType(NpmPublishExtension::class.java)
  registries.whenObjectAdded {
    configure(it)
    extension.packages.names.forEach { pkgName ->
      registerPublishTask(pkgName, it.name)
    }
  }
  registries.whenObjectRemoved {
    tasks.names.filter { name ->
      name.startsWith("publish") &&
        name.endsWith("PackageTo${it.name.capitalized()}Registry")
    }.mapNotNull(tasks::findByName).forEach { task ->
      tasks.remove(task)
      info { "Removed [${task.name}] NpmPublishTask due to removed [${it.name}] NpmRegistry" }
    }
  }
}
