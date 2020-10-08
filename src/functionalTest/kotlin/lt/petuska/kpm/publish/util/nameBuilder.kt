package lt.petuska.kpm.publish.util

import org.gradle.util.GUtil

const val defaultRepoName = "npmjs"
fun assembleTaskName(pubName: String) = "assemble${GUtil.toCamelCase(pubName)}KpmPublication"
fun publishTaskName(pubName: String, repoName: String = defaultRepoName) = "publish${GUtil.toCamelCase(pubName)}KpmPublicationTo${GUtil.toCamelCase(repoName)}"
