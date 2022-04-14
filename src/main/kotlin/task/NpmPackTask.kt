package dev.petuska.npm.publish.task

import com.google.gson.*
import dev.petuska.npm.publish.util.*
import org.gradle.api.file.*
import org.gradle.api.provider.*
import org.gradle.api.tasks.*
import org.gradle.api.tasks.options.*
import java.io.*

/**
 * A publishing task that publishes a given publication to a given repository.
 */
@CacheableTask
@Suppress("LeakingThis")
abstract class NpmPackTask : NpmExecTask() {

  /**
   * The directory where the assembled and ready-to-pack package is.
   * @see [NpmPublication.destinationDir]
   */
  @get:InputDirectory
  @get:PathSensitive(PathSensitivity.NAME_ONLY)
  abstract val packageDir: DirectoryProperty

  /** See Also: [dev.petuska.npm.publish.dsl.NpmPublishExtension.dry] */
  @get:Input
  @get:Option(option = "dry", description = "Execute in dry-run mode")
  abstract val dry: Property<Boolean>

  /**
   * Output file to pack the publication to.
   * @see [NpmPackTask.packageDir]
   */
  @get:OutputFile
  abstract val outputFile: RegularFileProperty

  init {
    group = "build"
    description = "Packs NPM package"
    dry.convention(false)
    outputFile.convention(
      packageDir.map { dir ->
        dir.file("package.json").asFile.takeIf(File::exists)?.let(File::reader)?.let {
          Gson().fromJson(it, MutableMap::class.java)
        }.unsafeCast<MutableMap<String, Any>>()
      }.flatMap {
        val name = it["name"]?.toString()?.replace("@", "")?.replace("/", "-")
        val version = it["version"]?.toString()
        name?.let {
          var fileName = name
          version?.let { fileName += "-$version" }
          project.layout.buildDirectory.file("packages/$fileName.tgz")
        }.unsafeCast()
      }
    )
  }

  @Suppress("unused")
  @TaskAction
  private fun doAction() {
    val tmpDir = temporaryDir
    npmExec(listOf("pack", packageDir.final, if (dry.final) "--dry-run" else null)) {
      it.workingDir(tmpDir)
    }
    val outFile = tmpDir.listFiles().firstOrNull()
    outFile?.copyTo(outputFile.final.asFile, true)
  }
}
