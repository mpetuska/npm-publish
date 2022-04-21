package dev.petuska.npm.publish.extension.domain

import dev.petuska.npm.publish.util.NamedInput
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import kotlin.math.abs

/**
 * A simple representation of a npm dependency
 */
@Suppress("unused")
public interface NpmDependency : NamedInput {
  /**
   * Dependency version specification.
   * [More info](https://docs.npmjs.com/about-semantic-versioning#using-semantic-versioning-to-specify-update-types-your-package-can-accept)
   */
  @get:Input
  public val version: Property<String>

  /**
   * Dependency type
   * @see [Type]
   */
  @get:Input
  @get:Optional
  public val type: Property<Type>

  public companion object {
    /**
     * @see [Type.OPTIONAL]
     */
    public val OPTIONAL: Type = Type.OPTIONAL

    /**
     * @see [Type.PEER]
     */
    public val PEER: Type = Type.PEER

    /**
     * @see [Type.DEV]
     */
    public val DEV: Type = Type.DEV

    /**
     * @see [Type.NORMAL]
     */
    public val NORMAL: Type = Type.NORMAL
  }

  /**
   * Npm dependency type.
   * [More info](https://docs.npmjs.com/cli/v6/configuring-npm/package-json#dependencies)
   */
  public enum class Type {
    OPTIONAL,
    PEER,
    DEV,
    NORMAL;

    /**
     * Type priority in descending order
     */
    public inline val priority: Int get() = abs(0 - ordinal)
  }
}

public typealias NpmDependencies = NamedDomainObjectContainer<NpmDependency>
