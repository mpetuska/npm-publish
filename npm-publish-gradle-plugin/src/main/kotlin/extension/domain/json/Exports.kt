package dev.petuska.npm.publish.extension.domain.json

import org.gradle.api.provider.MapProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional

/**
 * [exports field](https://nodejs.org/api/packages.html#main-entry-point-export)
 */
public abstract class Exports : GenericJsonObject() {

  internal abstract val paths: MapProperty<String, ExportedPath>

  override fun finalise(): MutableMap<String, Any> = super.finalise().apply {
    paths.get().forEach { (path, info) ->
      put(path, info.finalise())
    }
  }
}

/**
 * A path that will be exported in [Exports]
 */
public abstract class ExportedPath : GenericJsonObject() {

  /**
   * If set, this path will be set in "default"
   */
  @get:Input
  @get:Optional
  public abstract val default: Property<String>

  /**
   * If set, this path will be set in "types"
   */
  @get:Input
  @get:Optional
  public abstract val types: Property<String>

  /**
   * If set, this path will be set in "import"
   */
  @get:Input
  @get:Optional
  public abstract val import: Property<String>

  /**
   * If set, this path will be set in "require"
   */
  @get:Input
  @get:Optional
  public abstract val require: Property<String>

  override fun finalise(): MutableMap<String, Any> = super.finalise().apply {
    types.finalOrNull?.let {
      put("types", it)
    }
    default.finalOrNull?.let {
      put("default", it)
    }
    require.finalOrNull?.let {
      put("require", it)
    }
    import.finalOrNull?.let {
      put("import", it)
    }
  }
}
