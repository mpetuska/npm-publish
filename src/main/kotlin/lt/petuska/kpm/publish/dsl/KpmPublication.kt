package lt.petuska.kpm.publish.dsl

import lt.petuska.kpm.publish.util.fallbackDelegate
import lt.petuska.kpm.publish.util.gradleNullableProperty
import lt.petuska.kpm.publish.util.gradleProperty
import org.gradle.api.Project
import org.gradle.api.file.CopySpec
import org.gradle.util.GUtil
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinJsCompilation
import org.jetbrains.kotlin.gradle.targets.js.npm.NpmDependency
import java.io.File

class KpmPublication internal constructor(
  name: String,
  private val project: Project,
  extension: KpmPublishExtension,
  val npmDependencies: MutableList<NpmDependency> = mutableListOf()
) {
  val name: String = GUtil.toLowerCamelCase(name)
  var moduleName: String by project.gradleProperty(project.name)
  var scope by extension.fallbackDelegate(KpmPublishExtension::organization)
  var readme by extension.fallbackDelegate(KpmPublishExtension::readme)
  var destinationDir by project.gradleProperty(File("${project.buildDir}/publications/kpm/${this.name}"))
  var main by project.gradleNullableProperty<String>()
  var nodeJsDir by project.gradleNullableProperty(System.getenv("NODE_HOME")?.let(::File))
  internal var compilation by project.gradleNullableProperty<KotlinJsCompilation>()

  internal var fileSpecs = mutableListOf<CopySpec.(File) -> Unit>()

  fun files(config: CopySpec.(destinationDir: File) -> Unit) {
    fileSpecs.add(config)
  }

  fun dependencies(config: MutableList<NpmDependency>.() -> Unit) = npmDependencies.config()
  fun MutableList<NpmDependency>.dependency(name: String, version: String, scope: NpmDependency.Scope) = NpmDependency(project, name, version, scope, false).also {
    add(it)
  }
  fun MutableList<NpmDependency>.npm(name: String, version: String) = dependency(name, version, NpmDependency.Scope.NORMAL)
  fun MutableList<NpmDependency>.npmDev(name: String, version: String) = dependency(name, version, NpmDependency.Scope.DEV)
  fun MutableList<NpmDependency>.npmOptional(name: String, version: String) = dependency(name, version, NpmDependency.Scope.OPTIONAL)
  fun MutableList<NpmDependency>.npmPeer(name: String, version: String) = dependency(name, version, NpmDependency.Scope.PEER)

  internal fun validate(alternativeNodeJsDir: File?): KpmPublication? {
    nodeJsDir = nodeJsDir ?: alternativeNodeJsDir
    return takeIf { nodeJsDir != null }
  }
}
