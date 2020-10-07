package lt.petuska.kpm.publish.task

import lt.petuska.kpm.publish.dsl.*
import lt.petuska.kpm.publish.util.*
import org.apache.tools.ant.taskdefs.condition.*
import org.gradle.api.*
import org.gradle.api.tasks.*
import org.jetbrains.kotlin.gradle.targets.js.nodejs.*
import javax.inject.*

open class KpmPublishTask @Inject constructor(
  kpmPublication: KpmPublication
) : DefaultTask() {
  var registry by kpmPublication.fallbackDelegate(KpmPublication::registry)
  var access by kpmPublication.fallbackDelegate(KpmPublication::access)
  var authToken by kpmPublication.fallbackDelegate(KpmPublication::authToken)
  var packageDir by kpmPublication.fallbackDelegate(KpmPublication::destinationDir)
  var otp by kpmPublication.fallbackDelegate(KpmPublication::otp)
  
  var dry by project.gradleProperty((project.properties[KpmPublishExtension.DRY_RUN_PROP] as String?)?.toBoolean()
    ?: false)
  
  private val kotlinNodeJsSetupTask by lazy {
    project.tasks.getByName("kotlinNodeJsSetup") as NodeJsSetupTask
  }
  
  init {
    group = "publish"
    description = "Publishes ${kpmPublication.name} NPM module"
  }
  
  private val npm by lazy {
    if (Os.isFamily(Os.FAMILY_WINDOWS)) {
      kotlinNodeJsSetupTask.destination
        .resolve("node_modules")
        .resolve("npm")
        .resolve("bin")
        .resolve("npm-cli.js")
    } else {
      kotlinNodeJsSetupTask.destination
        .resolve("lib")
        .resolve("node_modules")
        .resolve("npm")
        .resolve("bin")
        .resolve("npm-cli.js")
    }
  }
  
  private val node by lazy {
    // For some unknown reason, the node distribution's structure is different on Windows and UNIX.
    if (Os.isFamily(Os.FAMILY_WINDOWS)) {
      kotlinNodeJsSetupTask.destination
        .resolve("node.exe")
    } else {
      kotlinNodeJsSetupTask.destination
        .resolve("bin")
        .resolve("node")
    }
  }
  
  @TaskAction
  fun doAction() {
    project.exec {
      val cmd = listOfNotNull(node,
        npm,
        "publish",
        packageDir,
        "--access $access",
        "--registry=${registry.removeSuffix("/")}/",
        "--//${registry.substringAfter("//").removeSuffix("/")}/:_authToken=$authToken",
        if (otp != null) "--otp $otp" else null,
        if (dry) "--dry-run" else ""
      ).toTypedArray()
      it.commandLine(*cmd)
    }
  }
}
