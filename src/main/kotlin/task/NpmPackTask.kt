package dev.petuska.npm.publish.task

import com.google.gson.Gson
import dev.petuska.npm.publish.extension.NpmPublishExtension
import dev.petuska.npm.publish.util.final
import dev.petuska.npm.publish.util.unsafeCast
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.options.Option
import java.io.File

/**
 * A task to pack a tgs archive for the given package
 */
@CacheableTask
@Suppress("LeakingThis")
public abstract class NpmPackTask : NpmExecTask() {

  /**
   * The directory where the assembled and ready-to-pack package is.
   * @see [NpmAssembleTask]
   */
  @get:InputDirectory
  @get:PathSensitive(PathSensitivity.NAME_ONLY)
  public abstract val packageDir: DirectoryProperty

  /**
   * Controls dry-tun mode for the execution.
   * @see [NpmPublishExtension.dry]
   */
  @get:Input
  @get:Option(option = "dry", description = "Execute in dry-run mode")
  public abstract val dry: Property<Boolean>

  /**
   * Output file to pack the publication to.
   *
   * Defaults to `build/packages/<name>.tgz`
   */
  @get:OutputFile
  public abstract val outputFile: RegularFileProperty

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
    val pDir = packageDir.final.asFile
    val oDir = outputFile.final.asFile
    val d = dry.final
    debug {
      "Packing package at ${pDir.path} to ${oDir.parentFile.path} ${if (d) "with" else "without"} --dry-run flag"
    }
    val tmpDir = temporaryDir
    npmExec(listOf("pack", pDir, if (d) "--dry-run" else null)) {
      it.workingDir(tmpDir)
    }
    val outFile = tmpDir.listFiles().firstOrNull() ?: error("Internal error. Temporary packed file not found.")
    outFile.copyTo(oDir, true)
    if (!d) info { "Packed package at ${pDir.path} to ${oDir.path}" }
  }
}
