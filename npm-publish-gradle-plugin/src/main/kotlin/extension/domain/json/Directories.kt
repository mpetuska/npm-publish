package dev.petuska.npm.publish.extension.domain.json

import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional

/** [directories](https://docs.npmjs.com/files/package.json#directories) */
public abstract class Directories : JsonObject<Any>() {
  /** [lib](https://docs.npmjs.com/files/package.json#directorieslib) */
  @get:Input
  @get:Optional
  public abstract val lib: Property<String>

  /** [bin](https://docs.npmjs.com/files/package.json#directoriesbin) */
  @get:Input
  @get:Optional
  public abstract val bin: Property<String>

  /** [man](https://docs.npmjs.com/files/package.json#directoriesman) */
  @get:Input
  @get:Optional
  public abstract val man: Property<String>

  /** [doc](https://docs.npmjs.com/files/package.json#directoriesdoc) */
  @get:Input
  @get:Optional
  public abstract val doc: Property<String>

  /** [example](https://docs.npmjs.com/files/package.json#directoriesexample) */
  @get:Input
  @get:Optional
  public abstract val example: Property<String>

  /** [test](https://docs.npmjs.com/files/package.json#directoriestest) */
  @get:Input
  @get:Optional
  public abstract val test: Property<String>

  override fun finalise(): MutableMap<String, Any> = super.finalise().apply {
    lib.finalOrNull?.let { put("lib", it) }
    bin.finalOrNull?.let { put("bin", it) }
    man.finalOrNull?.let { put("man", it) }
    doc.finalOrNull?.let { put("doc", it) }
    example.finalOrNull?.let { put("example", it) }
    test.finalOrNull?.let { put("test", it) }
  }
}
