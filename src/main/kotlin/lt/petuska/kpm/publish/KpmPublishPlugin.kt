package lt.petuska.kpm.publish

import lt.petuska.kpm.publish.dsl.KpmPublication
import lt.petuska.kpm.publish.dsl.KpmPublishExtension
import lt.petuska.kpm.publish.dsl.KpmPublishExtension.Companion.EXTENSION_NAME
import lt.petuska.kpm.publish.task.KpmPackagePrepareTask
import lt.petuska.kpm.publish.task.KpmPublishTask
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.util.GUtil
import org.jetbrains.kotlin.gradle.dsl.KotlinJsProjectExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinJsCompilation
import org.jetbrains.kotlin.gradle.targets.js.KotlinJsTarget

class KpmPublishPlugin : Plugin<Project> {
  override fun apply(project: Project) {
    project.pluginManager.withPlugin(KOTLIN_MPP_PLUGIN) {
      project.createExtension()
      project.afterEvaluate {
        project.extensions.configure(KotlinMultiplatformExtension::class.java) {
          it.targets.filterIsInstance<KotlinJsTarget>().forEach { t ->
            project.configureExtension(t.name, t.compilations, true)
          }
        }
      }
    }
    project.pluginManager.withPlugin(KOTLIN_JS_PLUGIN) {
      project.createExtension()
      project.afterEvaluate {
        project.extensions.configure(KotlinJsProjectExtension::class.java) {
          val target = it.js()
          project.configureExtension(target.name, target.compilations, false)
        }
      }
    }
  }

  companion object {
    private const val KOTLIN_JS_PLUGIN = "org.jetbrains.kotlin.js"
    private const val KOTLIN_MPP_PLUGIN = "org.jetbrains.kotlin.multiplatform"

    private fun Project.createExtension() = extensions.findByType(KpmPublishExtension::class.java) ?: extensions.create(
      EXTENSION_NAME,
      KpmPublishExtension::class.java,
      this@createExtension
    )

    private fun Project.configureExtension(targetName: String, compilations: NamedDomainObjectContainer<out KotlinJsCompilation>, mpp: Boolean) {
      val publications = mutableListOf<KpmPublication>()
      kpmPublish {
        publications {
          val comp = compilations.first { comp -> comp.name.contains("main", true) }
          val pub = publication(targetName) {
            compilation = comp
          }
          publications.add(pub)
        }
      }

      val pubTasks = publications.mapNotNull { pub ->
        project.tasks.findByName("kotlinNodeJsSetup")?.let { nodeJsSetupTask ->
          val upperName = if (mpp) GUtil.toCamelCase(pub.name) else ""
          val packagePrepareTask =
            tasks.register("assemble${upperName}KpmPublication", KpmPackagePrepareTask::class.java, pub)
          packagePrepareTask.configure {
            it.dependsOn(pub.compilation!!.processResourcesTaskName, pub.compilation!!.compileKotlinTaskName)
          }
          val kpmPublishTask = tasks.register("publish${upperName}KpmPublication", KpmPublishTask::class.java, pub)
          kpmPublishTask.configure {
            it.dependsOn(packagePrepareTask, nodeJsSetupTask)
          }
          kpmPublishTask
        }
      }

      tasks.findByName("publish")?.dependsOn(*pubTasks.toTypedArray())
    }
  }
}

internal val Project.kpmPublish: KpmPublishExtension
  get() = extensions.getByName(EXTENSION_NAME) as? KpmPublishExtension
    ?: throw IllegalStateException("$EXTENSION_NAME is not of the correct type")

internal fun Project.kpmPublish(config: KpmPublishExtension.() -> Unit = {}): KpmPublishExtension =
  kpmPublish.apply(config)
