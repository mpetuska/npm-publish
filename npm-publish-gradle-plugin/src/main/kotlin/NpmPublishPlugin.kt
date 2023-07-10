package dev.petuska.npm.publish

import com.moowork.gradle.node.task.SetupTask
import dev.petuska.npm.publish.config.configure
import dev.petuska.npm.publish.extension.NpmPublishExtension
import dev.petuska.npm.publish.task.NodeExecTask
import dev.petuska.npm.publish.task.NpmAssembleTask
import dev.petuska.npm.publish.task.NpmPackTask
import dev.petuska.npm.publish.task.NpmPublishTask
import dev.petuska.npm.publish.util.ProjectEnhancer
import dev.petuska.npm.publish.util.configure
import dev.petuska.npm.publish.util.unsafeCast
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.publish.plugins.PublishingPlugin.PUBLISH_LIFECYCLE_TASK_NAME
import org.gradle.api.publish.plugins.PublishingPlugin.PUBLISH_TASK_GROUP
import org.jetbrains.kotlin.gradle.dsl.KotlinJsProjectExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.targets.js.dsl.KotlinJsTargetDsl
import org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsSetupTask
import org.jetbrains.kotlin.gradle.utils.named

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
    pluginManager.withPlugin(NEBULA_NODE_PLUGIN) {
      val nebulaNodeHome = project.tasks.named<SetupTask>(SetupTask.NAME)
        .map { it.takeIf { it.enabled }.unsafeCast<SetupTask>() }
        .map(SetupTask::getNodeDir)
        .let(layout::dir)
      extension.nodeHome.sysProjectEnvPropertyConvention(
        name = "nodeHome",
        default = nebulaNodeHome.orElse(
          providers.environmentVariable("NODE_HOME").map(layout.projectDirectory::dir)
        ),
        converter = layout.projectDirectory::dir
      )
      tasks.withType(NodeExecTask::class.java) {
        it.dependsOn(nebulaNodeHome)
      }
    }
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
      logger.warn("Kotlin/JS plugin integration is deprecated. Please migrate to Kotlin/Multiplatform plugin")
      extensions.configure<KotlinJsProjectExtension> {
        @Suppress("DEPRECATION")
        configure(target)
      }
    }

    afterEvaluate {
      if (rootProject.tasks.names.contains("kotlinNodeJsSetup")) {
        rootProject.tasks.named<NodeJsSetupTask>("kotlinNodeJsSetup").map(NodeJsSetupTask::destination)
          .let(layout::dir)
          .let(extension.nodeHome::convention)
      }
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
    private const val NEBULA_NODE_PLUGIN = "com.netflix.nebula.node"
  }
}
