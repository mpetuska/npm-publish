package dev.petuska.npm.publish.util

import org.gradle.api.Project
import org.gradle.api.provider.Provider
import java.util.*

private const val GLOBAL_PREFIX: String = "npm.publish."

/**
 * Convention resolution order by descending priority
 * 1. CLI arguments (`--arg=value`)
 * 2. System properties (`-Dprop=value`)
 * 3. Gradle properties (`-Pprop=value`,
 *    `ORG_GRADLE_PROJECT_prop=value` env variable,
 *    `-Dorg.gradle.project.prop=value` system property
 *    or `prop=value` stored in `gradle.properties` file)
 * 4. Env variables (`PROP=value`)
 * 5. [default] value provider
 *
 * Additionally, prop names replace spaces with dots.
 * Env variable names are uppercase prop names with dots replaced with underscores.
 */
internal fun Project.sysProjectEnvPropertyConvention(
  name: String,
  default: Provider<String> = providers.provider { null },
): Provider<String> {
  val propName = GLOBAL_PREFIX + name
  val envName = name.uppercase(Locale.getDefault()).replace(".", "_")

  return providers.systemProperty(propName)
    .orElse(providers.provider { extensions.extraProperties.properties[propName]?.toString() })
    .orElse(providers.gradleProperty(propName))
    .orElse(providers.environmentVariable(envName))
    .orElse(default)
}
