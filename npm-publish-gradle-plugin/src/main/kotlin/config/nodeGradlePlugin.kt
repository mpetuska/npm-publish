package dev.petuska.npm.publish.config

import com.github.gradle.node.task.NodeSetupTask
import dev.petuska.npm.publish.extension.NpmPublishExtension
import dev.petuska.npm.publish.task.NodeExecTask
import dev.petuska.npm.publish.task.NpmExecTask
import dev.petuska.npm.publish.util.sysProjectEnvPropertyConvention
import dev.petuska.npm.publish.util.unsafeCast
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.utils.named

private const val NODE_GRADLE_PLUGIN = "com.github.node-gradle.node"

internal fun Project.configureNodeGradlePlugin(extension: NpmPublishExtension) {
  pluginManager.withPlugin(NODE_GRADLE_PLUGIN) {
    val nodeGradleHome = project.tasks.named<NodeSetupTask>(NodeSetupTask.NAME)
      .map { it.takeIf { it.enabled }.unsafeCast<NodeSetupTask>() }
      .flatMap(NodeSetupTask::nodeDir)
    extension.nodeHome.convention(
      sysProjectEnvPropertyConvention(
        name = "nodeHome",
        default = nodeGradleHome.map { it.asFile.absolutePath }
          .orElse(providers.environmentVariable("NODE_HOME")),
      ).map(layout.projectDirectory::dir)
    )
    tasks.withType(NodeExecTask::class.java) {
      it.dependsOn(nodeGradleHome)
      it.nodeHome.convention(extension.nodeHome)
      it.node.convention(extension.nodeBin)
    }
    tasks.withType(NpmExecTask::class.java) {
      it.dependsOn(nodeGradleHome)
      it.npm.convention(extension.npmBin)
    }
  }
}
