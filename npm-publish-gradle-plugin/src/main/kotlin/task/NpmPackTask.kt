package dev.petuska.npm.publish.task

import dev.petuska.npm.publish.extension.NpmPublishExtension
import dev.petuska.npm.publish.util.unsafeCast
import groovy.json.JsonSlurper
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.ProjectLayout
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.ProviderFactory
import org.gradle.api.provider.ValueSource
import org.gradle.api.provider.ValueSourceParameters
import org.gradle.api.tasks.*
import org.gradle.api.tasks.options.Option
import java.io.File
import javax.inject.Inject

/**
 * A task to pack a `.tgz` archive for the given package
 */
@CacheableTask
@Suppress("LeakingThis")
public abstract class NpmPackTask : NpmExecTask() {
  @get:Inject
  internal abstract val layout: ProjectLayout

  @get:Inject
  internal abstract val providers: ProviderFactory

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
   * Defaults to `build/packages/<scope>-<name>-<version>.tgz`
   */
  @get:OutputFile
  public abstract val outputFile: RegularFileProperty

  /**
   * Sets [outputFile]
   * @param path to the output file
   */
  @Option(option = "outputFile", description = "Path to the output file")
  public fun outputFile(path: String) {
    outputFile.set(File(path))
  }

  internal abstract class PackageJsonParserValueSource : ValueSource<String, PackageJsonParserValueSource.Params> {
    internal interface Params : ValueSourceParameters {
      var packageDir: DirectoryProperty
    }

    override fun obtain(): String? {
      return parameters.packageDir.get().file("package.json").asFile.takeIf(File::exists)
        ?.let { JsonSlurper().parse(it).unsafeCast<MutableMap<String, Any>>() }
        ?.let {
          val name = it["name"]?.toString()?.replace("@", "")?.replace("/", "-")
          val version = it["version"]?.toString()
          name?.let {
            var fileName = name
            version?.let { fileName += "-$version" }
            "$fileName.tgz"
          }
        }
    }
  }

  init {
    group = "build"
    description = "Packs NPM package"
    dry.convention(false)
    outputFile.convention(
      layout.buildDirectory.zip(
        providers.of(PackageJsonParserValueSource::class.java) {
          it.parameters.packageDir = packageDir
        }
      ) { buildDir, fileName ->
        buildDir.file("packages/$fileName")
      }
    )
  }

  @Suppress("unused")
  @TaskAction
  internal fun doAction() {
    val pDir = packageDir.asFile.get()
    val oDir = outputFile.asFile.get()
    val d = dry.get()
    debug {
      "Packing package at ${pDir.path} to ${oDir.parentFile.path} ${if (d) "with" else "without"} --dry-run flag"
    }
    val tmpDir = temporaryDir
    val args: List<String> = buildList {
      add("pack")
      add("$pDir")
      if (d) add("--dry-run")
    }
    npmExec(args) { it.workingDir(tmpDir) }.rethrowFailure()
    if (!d) {
      val outFile =
        tmpDir.listFiles()?.firstOrNull() ?: error("[npm-publish] Internal error. Temporary packed file not found.")
      outFile.copyTo(oDir, true)
      info { "Packed package at ${pDir.path} to ${oDir.path}" }
    }
  }
}
