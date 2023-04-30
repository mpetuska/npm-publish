package dev.petuska.npm.publish.task

import dev.petuska.npm.publish.extension.domain.NpmDependency
import dev.petuska.npm.publish.extension.domain.NpmPackage
import dev.petuska.npm.publish.util.*
import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import org.gradle.api.Action
import org.gradle.api.DefaultTask
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Nested
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.options.Option
import java.io.File

/**
 * A task to assemble all required files for a given [NpmPackage].
 */
@CacheableTask
@Suppress("LeakingThis")
public abstract class NpmAssembleTask : DefaultTask(), PluginLogger {

  @get:Nested
  internal abstract val extraDependencies: ListProperty<NpmDependency>

  /**
   * The configuration of the package to assemble.
   * @see [NpmPackage]
   */
  @get:Nested
  @Suppress("VariableNaming")
  internal abstract val `package`: Property<NpmPackage>

  /** Output directory to assemble the package to. */
  @get:OutputDirectory
  public abstract val destinationDir: DirectoryProperty

  /**
   * Sets [destinationDir]
   * @param path to the output directory
   */
  @Option(option = "nodeNome", description = "Output directory to assemble the package to")
  public fun destinationDir(path: String) {
    destinationDir.set(File(path))
  }

  /**
   * Configuration DSL allowing to modify a given package config.
   */
  @Suppress("FunctionNaming")
  public fun `package`(action: Action<NpmPackage>) {
    `package`.configure(action)
  }

  init {
    group = "build"
    description = "Assembles NPM package"
    destinationDir.convention(
      `package`.flatMap {
        project.layout.buildDirectory.dir("packages/${it.name}")
      }
    )
    `package`.convention(
      project.provider {
        project.objects.newInstance(
          NpmPackage::class.java,
          name
        )
      }
    )
  }

  @TaskAction
  @Suppress("unused")
  private fun action() {
    val pkg = `package`.get()
    val dest = destinationDir.get()
    debug { "Assembling ${pkg.name} package in ${dest.asFile.path}" }
    val files = pkg.files.apply(ConfigurableFileCollection::finalizeValue).files

    project.copy { cp ->
      cp.from(files)
      pkg.readme.orNull?.let { md ->
        cp.from(md) {
          it.rename(md.asFile.name, "README.md")
        }
      }
      cp.from(pkg.npmIgnore.orNull)
      cp.into(dest)
    }
    val pJsonFile = dest.file("package.json").asFile
    debug { "Resolving package.json for ${pkg.name} package to ${pJsonFile.path}" }
    val pJson = pkg.resolvePackageJson()
    pJsonFile.writeText(JsonOutput.prettyPrint(JsonOutput.toJson(pJson)))
    info { "Resolved package.json for ${pkg.name} package to ${pJsonFile.path}" }
    info { "Assembled ${pkg.name} package in ${dest.asFile.path}" }
  }

  private fun NpmPackage.resolvePackageJson(): Map<String, Any> {
    packageJsonFile.orNull?.let {
      info { "package.json file set and found for $name package. Not resolving further..." }
      return JsonSlurper().parse(it.asFile).unsafeCast()
    }
    val pJson = packageJsonTemplateFile.orNull?.let {
      info { "package.json template file set and found for $name package. Using it as a baseline..." }
      JsonSlurper().parse(it.asFile).unsafeCast<MutableMap<String, Any>>()
    } ?: mutableMapOf()

    packageJson.orNull?.finalise()?.let(pJson::overrideFrom)
    main.orNull?.let { pJson.putIfAbsent("main", it) }
    types.orNull?.let { pJson.putIfAbsent("types", it) }
    version.orNull?.let { pJson.putIfAbsent("version", it) }
    packageName.orNull?.let { pName ->
      pJson.putIfAbsent("name", scope.orNull?.let { s -> npmFullName(pName, s) } ?: pName)
    }

    resolveDependencies(pJson)

    return pJson
  }

  private fun NpmPackage.resolveDependencies(pJson: MutableMap<String, Any>) {
    val direct =
      (dependencies.toList() + extraDependencies.get()).distinct().groupBy { it.type.get() }
    val dOptional = pJson.mergeDependencies(
      "optionalDependencies",
      direct.getOrDefault(NpmDependency.Type.OPTIONAL, listOf())
    )
    val dPeer = pJson.mergeDependencies(
      "peerDependencies",
      direct.getOrDefault(NpmDependency.Type.PEER, listOf())
    ) { d ->
      dOptional.keys.none { d == it }.also {
        if (!it) {
          warn {
            "Registered peer dependency $d for $name package already present in higher priority scope. Skipping..."
          }
        }
      }
    }
    val dDev = pJson.mergeDependencies(
      "devDependencies",
      direct.getOrDefault(NpmDependency.Type.DEV, listOf())
    ) { d ->
      (dOptional.keys.none { d == it } && dPeer.keys.none { d == it }).also {
        if (!it) {
          warn {
            "Registered dev dependency $d for $name package already present in higher priority scope. Skipping..."
          }
        }
      }
    }
    pJson.mergeDependencies(
      "dependencies",
      direct.getOrDefault(NpmDependency.Type.NORMAL, listOf())
    ) { d ->
      (dOptional.keys.none { d == it } && dPeer.keys.none { d == it } && dDev.keys.none { d == it }).also {
        if (!it) {
          warn {
            "Registered normal dependency $d for $name package already present in higher priority scope. Skipping..."
          }
        }
      }
    }
  }

  private fun MutableMap<String, Any>.mergeDependencies(
    key: String,
    direct: List<NpmDependency>,
    filter: (String) -> Boolean = { true },
  ): Map<String, String> {
    val dDeps = direct.groupBy(NpmDependency::getName).filterKeys(filter)
      .mapValues { (_, v) -> v.first().version.get() }
    if (dDeps.isNotEmpty()) {
      putIfAbsent(key, dDeps)
      get(key).unsafeCast<MutableMap<String, Any>>().apply {
        dDeps.forEach { (name, version) ->
          putIfAbsent(name, version)
        }
      }
    }
    return dDeps
  }
}
