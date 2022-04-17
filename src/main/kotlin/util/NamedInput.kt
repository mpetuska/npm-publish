package dev.petuska.npm.publish.util

import org.gradle.api.Named
import org.gradle.api.tasks.Input

/**
 * An override of [Named] interface marking [Named.getName] bean as Gradle's @[Input]
 */
public interface NamedInput : Named {
  @Input
  override fun getName(): String
}
