package dev.petuska.npm.publish

import dev.petuska.npm.publish.config.configure
import dev.petuska.npm.publish.extension.NpmPublishExtension
import dev.petuska.npm.publish.task.NpmAssembleTask
import dev.petuska.npm.publish.task.NpmPackTask
import dev.petuska.npm.publish.task.NpmPublishTask
import dev.petuska.npm.publish.util.ProjectEnhancer
import dev.petuska.npm.publish.util.configure
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.publish.plugins.PublishingPlugin.PUBLISH_LIFECYCLE_TASK_NAME
import org.gradle.api.publish.plugins.PublishingPlugin.PUBLISH_TASK_GROUP
import org.jetbrains.kotlin.gradle.dsl.KotlinJsProjectExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.targets.js.dsl.KotlinJsTargetDsl
import org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsSetupTask

/**
 * Main entry point for npm-publish plugin
 */
@Suppress("unused")
public class NpmPublishPlugin : Plugin<Project> {
  override fun apply(project: Project) {
    ProjectEnhancer(
      project = project,
      extension = project.extensions.create(NpmPublishExtension.NAME, NpmPublishExtension::class.java)
    ).apply()
  }

  private fun ProjectEnhancer.apply() {
    configure(extension)
    pluginManager.withPlugin(KOTLIN_MPP_PLUGIN) {
      extensions.configure<KotlinMultiplatformExtension> {
        targets.filterIsInstance<KotlinJsTargetDsl>().forEach { configure(it) }
        targets.whenObjectAdded {
          if (it is KotlinJsTargetDsl) configure(it)
        }
        targets.whenObjectRemoved {
          if (it is KotlinJsTargetDsl) extension.packages.findByName(it.name)?.let(extension.packages::remove)
        }
      }
    }
    pluginManager.withPlugin(KOTLIN_JS_PLUGIN) {
      extensions.configure<KotlinJsProjectExtension> {
        configure(js())
      }
    }

    afterEvaluate {
      val nodeDest = rootProject.tasks.named("kotlinNodeJsSetup", NodeJsSetupTask::class.java).map { it.destination }
      extension.nodeHome.set(layout.dir(nodeDest))

      tasks.maybeCreate("assemble").apply {
        group = "build"
        dependsOn(tasks.withType(NpmAssembleTask::class.java))
      }
      tasks.maybeCreate("pack").apply {
        group = "build"
        dependsOn(tasks.withType(NpmPackTask::class.java))
      }
      tasks.maybeCreate(PUBLISH_LIFECYCLE_TASK_NAME).apply {
        group = PUBLISH_TASK_GROUP
        dependsOn(tasks.withType(NpmPublishTask::class.java))
      }
    }
  }

  private companion object {
    private const val KOTLIN_JS_PLUGIN = "org.jetbrains.kotlin.js"
    private const val KOTLIN_MPP_PLUGIN = "org.jetbrains.kotlin.multiplatform"
  }
}

internal val Project.npmPublish: NpmPublishExtension
  get() = extensions.findByType(NpmPublishExtension::class.java)
    ?: throw IllegalStateException("${NpmPublishExtension.NAME} is not registered or of incorrect type")
