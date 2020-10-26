package lt.petuska.npm.publish

import lt.petuska.npm.publish.dsl.NpmPublishExtension
import lt.petuska.npm.publish.dsl.NpmPublishExtension.Companion.EXTENSION_NAME
import lt.petuska.npm.publish.task.NpmPackTask
import lt.petuska.npm.publish.task.NpmPackageAssembleTask
import lt.petuska.npm.publish.task.NpmPublishTask
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.Copy
import org.gradle.util.GUtil
import org.jetbrains.kotlin.gradle.dsl.KotlinJsProjectExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinJsCompilation
import org.jetbrains.kotlin.gradle.targets.js.dsl.KotlinJsTargetDsl
import org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsSetupTask
import org.jetbrains.kotlin.gradle.targets.js.npm.NpmDependency

/**
 * Main entry point for npm-publish plugin
 */
class NpmPublishPlugin : Plugin<Project> {
  override fun apply(project: Project) {
    val extension = project.createExtension()
    project.afterEvaluate { prj ->
      prj.pluginManager.withPlugin(KOTLIN_MPP_PLUGIN) {
        prj.extensions.configure(KotlinMultiplatformExtension::class.java) {
          it.targets.filterIsInstance<KotlinJsTargetDsl>().forEach { target ->
            prj.configureExtension(extension, target.name, target.compilations)
          }
        }
      }
      prj.pluginManager.withPlugin(KOTLIN_JS_PLUGIN) {
        prj.extensions.configure(KotlinJsProjectExtension::class.java) {
          val target = it.js()
          prj.configureExtension(extension, target.name, target.compilations)
        }
      }
      prj.configureTasks(extension)
    }
  }

  companion object {
    private const val KOTLIN_JS_PLUGIN = "org.jetbrains.kotlin.js"
    private const val KOTLIN_MPP_PLUGIN = "org.jetbrains.kotlin.multiplatform"
    private const val MAVEN_PUBLISH_PLUGIN = "org.gradle.maven-publish"

    private fun Project.createExtension() = extensions.findByType(NpmPublishExtension::class.java) ?: extensions.create(
      EXTENSION_NAME,
      NpmPublishExtension::class.java,
      this@createExtension
    )

    private fun Project.configureExtension(extension: NpmPublishExtension, targetName: String, compilations: NamedDomainObjectContainer<out KotlinJsCompilation>) {
      val compilation = compilations.first { comp -> comp.name.contains("main", true) }
      val deps = compilation.relatedConfigurationNames.flatMap { conf ->
        val mainName = "${targetName}Main${conf.substringAfter(targetName)}"
        val normDeps = configurations.findByName(conf)?.dependencies?.toSet() ?: setOf()
        val mainDeps = configurations.findByName(mainName)?.dependencies?.toSet() ?: setOf()
        (normDeps + mainDeps).filterIsInstance<NpmDependency>()
      }

      extension.apply {
        publications(0) {
          publication(targetName) {
            this.compilation = compilation
            this.main = compileKotlinTask?.outputFile?.name
            dependencies {
              addAll(deps)
            }
            packageJson {
              bundledDependencies {
                -"kotlin-test.*".toRegex()
              }
            }
          }
        }
      }
    }

    private fun Project.configureTasks(extension: NpmPublishExtension) {
      val nodeJsSetupTask = project.rootProject.tasks.findByName("kotlinNodeJsSetup") as NodeJsSetupTask?

      val publishTask = tasks.findByName("publish") ?: tasks.create("publish") {
        it.group = "publishing"
        it.enabled = false
      }
      val assembleTask = tasks.findByName("assemble")
      val packTask = tasks.findByName("pack") ?: tasks.create("pack") {
        it.group = "build"
        it.enabled = false
      }

      val publications = with(extension) {
        pubConfigs.forEach {
          publications.configure(it)
        }
        publications.mapNotNull { pub ->
          val needsNode = pub.nodeJsDir == null
          pub.validate(nodeJsSetupTask?.destination)?.let {
            it to nodeJsSetupTask?.takeIf { needsNode }
          } ?: null.also { logger.warn("NPM Publication [${pub.name}] is invalid. Skipping...") }
        }
      }

      val repositories = with(extension) {
        repoConfigs.forEach {
          repositories.configure(it)
        }
        repositories.mapNotNull { repo ->
          repo.validate() ?: null.also { logger.warn("NPM Repository [${repo.name}] is invalid. Skipping...") }
        }
      }

      val pubTasks = publications.flatMap { (pub, nodeJsTask) ->
        val (compileKotlinTask, processResourcesTask) = pub.compilation?.let {
          val processResourcesTask = project.tasks.findByName(it.processResourcesTaskName) as Copy
          pub.compileKotlinTask?.also { compileKotlinTask ->
            pub.files {
              from(compileKotlinTask.outputFile.parentFile)
              from(processResourcesTask.destinationDir)
            }
          } to processResourcesTask
        } ?: null to null
        val upperName = GUtil.toCamelCase(pub.name)

        val assembleTaskName = "assemble${upperName}NpmPublication"
        val packTaskName = "pack${upperName}NpmPublication"
        val assemblePackageTask = tasks.findByName(assembleTaskName) as NpmPackageAssembleTask?
          ?: tasks.create(assembleTaskName, NpmPackageAssembleTask::class.java, pub).also { task ->
            task.onlyIf { pub.compileKotlinTask?.outputFile?.exists() ?: true }
            task.dependsOn(
              *listOfNotNull(
                processResourcesTask,
                compileKotlinTask,
                nodeJsTask
              ).toTypedArray()
            )
            assembleTask?.dependsOn(task)
          }
        val npmPackTask = tasks.findByName(packTaskName) as NpmPackTask?
          ?: tasks.create(packTaskName, NpmPackTask::class.java, pub).also {
            it.onlyIf { assemblePackageTask.didWork }
            it.dependsOn(assemblePackageTask)
          }
        packTask.dependsOn(npmPackTask)
        packTask.enabled = true
        repositories.map { repo ->
          val upperRepoName = GUtil.toCamelCase(repo.name)
          val publishTaskName = "publish${upperName}NpmPublicationTo$upperRepoName"
          tasks.findByName(publishTaskName)
            ?: tasks.create(publishTaskName, NpmPublishTask::class.java, pub, repo).also { task ->
              task.onlyIf { assemblePackageTask.didWork }
              task.dependsOn(assemblePackageTask)
              publishTask?.dependsOn(task)
            }
        }
      }
      if (pubTasks.isNotEmpty()) {
        publishTask.enabled = true
      }
    }
  }
}

internal val Project.npmPublishing: NpmPublishExtension
  get() = extensions.getByName(EXTENSION_NAME) as? NpmPublishExtension
    ?: throw IllegalStateException("$EXTENSION_NAME is not of the correct type")

internal fun Project.npmPublishing(config: NpmPublishExtension.() -> Unit = {}): NpmPublishExtension =
  npmPublishing.apply(config)
