package dev.petuska.npm.publish.extension.domain

/**
 * Provides implicit accessors to [NpmAccess] values
 */
@Suppress("PropertyName", "unused", "VariableNaming")
public interface NpmAccessScope {
  /**
   * @see [NpmAccess.PUBLIC]
   */
  public val PUBLIC: NpmAccess
    get() = NpmAccess.PUBLIC

  /**
   * @see [NpmAccess.RESTRICTED]
   */
  public val RESTRICTED: NpmAccess
    get() = NpmAccess.RESTRICTED
}
