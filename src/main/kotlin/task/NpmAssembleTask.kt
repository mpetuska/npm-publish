package dev.petuska.npm.publish.task

import com.google.gson.*
import dev.petuska.npm.publish.extension.domain.*
import dev.petuska.npm.publish.extension.domain.json.*
import dev.petuska.npm.publish.util.*
import org.gradle.api.*
import org.gradle.api.file.*
import org.gradle.api.provider.*
import org.gradle.api.tasks.*

/**
 * A task to assemble all required files for a given [NpmPublication].
 *
 * @constructor publication to assemble
 */
@Suppress("LeakingThis")
abstract class NpmAssembleTask : DefaultTask() {
  companion object {
    /** Gson instance to be reused across multiple functions of the task. */
    private val gson = GsonBuilder().setPrettyPrinting().create()
  }

  /**
   * Main configuration of the publication to assemble. If no publication is passed to a
   * constructor, a default one will be constructed with basic project properties.
   */
  @get:Nested
  internal abstract val `package`: Property<NpmPackage>

  /** Output directory to assemble the package to. */
  @get:OutputDirectory
  abstract val destinationDir: DirectoryProperty

  /** Configuration DSL allowing to modify a given publication config. */
  fun `package`(action: Action<NpmPackage>) {
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
    `package`.convention(project.provider { project.objects.newInstance(NpmPackage::class.java, name) })
  }

  @TaskAction
  @Suppress("unused")
  private fun action() {
    val pkg = `package`.final
    val dest = destinationDir.final
    val files = pkg.files.apply(ConfigurableFileCollection::finalizeValue).files

    project.copy { cp ->
      cp.from(files)
      cp.from(pkg.readme.finalOrNull)
      cp.into(dest)
    }
    val pJson = pkg.resolvePackageJson()
    dest.file("package.json").asFile.writeText(gson.toJson(pJson))
  }

  private fun NpmPackage.resolvePackageJson(): Map<String, Any> {
    packageJsonFile.finalOrNull?.let { return gson.fromJson(it.asFile.reader(), Map::class.java).unsafeCast() }
    val pJson = packageJsonTemplateFile.finalOrNull?.let {
      gson.fromJson(it.asFile.reader(), Map::class.java).unsafeCast<MutableMap<String, Any>>()
    } ?: mutableMapOf()

    packageJson.finalOrNull?.finalise()?.let(pJson::overrideFrom)
    main.finalOrNull?.let { pJson.putIfAbsent("main", it) }
    types.finalOrNull?.let { pJson.putIfAbsent("types", it) }
    version.finalOrNull?.let { pJson.putIfAbsent("version", it) }
    packageName.finalOrNull?.let { pName ->
      pJson.putIfAbsent("name", scope.finalOrNull?.let { s -> npmFullName(pName, s) } ?: pName)
    }

    resolveDependencies(pJson)

    return pJson
  }

  private fun NpmPackage.resolveDependencies(pJson: MutableMap<String, Any>) {
    val direct = dependencies.toList().groupBy { it.scope.final }
    val dOptional =
      pJson.mergeDependencies("optionalDependencies", direct.getOrDefault(NpmDependency.Scope.OPTIONAL, listOf()))
    val dPeer =
      pJson.mergeDependencies("peerDependencies", direct.getOrDefault(NpmDependency.Scope.PEER, listOf())) { d ->
        dOptional.keys.none { d == it }
      }
    val dDev = pJson.mergeDependencies("devDependencies", direct.getOrDefault(NpmDependency.Scope.DEV, listOf())) { d ->
      dOptional.keys.none { d == it } && dPeer.keys.none { d == it }
    }
    pJson.mergeDependencies("dependencies", direct.getOrDefault(NpmDependency.Scope.NORMAL, listOf())) { d ->
      dOptional.keys.none { d == it } && dPeer.keys.none { d == it } && dDev.keys.none { d == it }
    }
  }

  private fun MutableMap<String, Any>.mergeDependencies(
    key: String,
    direct: List<NpmDependency>,
    filter: (String) -> Boolean = { true },
  ): Map<String, String> {
    val dDeps =
      direct.groupBy(NpmDependency::getName).filterKeys(filter).mapValues { (_, v) -> v.first().version.final }
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
