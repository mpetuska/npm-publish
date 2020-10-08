package lt.petuska.npm.publish.util

import org.gradle.util.GUtil

const val defaultRepoName = "npmjs"
fun assembleTaskName(pubName: String) = "assemble${GUtil.toCamelCase(pubName)}NpmPublication"
fun publishTaskName(pubName: String, repoName: String = defaultRepoName) = "publish${GUtil.toCamelCase(pubName)}NpmPublicationTo${GUtil.toCamelCase(repoName)}"
