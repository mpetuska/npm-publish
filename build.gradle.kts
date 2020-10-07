import io.github.httpbuilderng.http.HttpTask
import kotlinx.validation.ApiValidationExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jlleitschuh.gradle.ktlint.KtlintCheckTask
import java.io.ByteArrayOutputStream

plugins {
    kotlin("jvm") version "1.3.72"
    `java-gradle-plugin`
    id("org.jetbrains.dokka") version "1.4.0"
    id("com.gradle.plugin-publish") version "0.12.0"
    id("org.jlleitschuh.gradle.ktlint") version "9.4.0"
    id("io.github.http-builder-ng.http-plugin") version "0.1.1"
    `maven-publish`
    idea
}

buildscript {
    dependencies {
        classpath("org.jetbrains.kotlinx:binary-compatibility-validator:0.2.3")
    }
}
apply(plugin = "binary-compatibility-validator")
configure<ApiValidationExtension> {}

group = "lt.petuska"
version = "0.0.2"

idea {
    module {
        isDownloadJavadoc = true
        isDownloadSources = true
    }
}

repositories {
    jcenter()
    mavenCentral()
    mavenLocal()
    maven("https://dl.bintray.com/mpetuska/lt.petuska")
    maven("https://dl.bintray.com/kotlin/kotlin-eap")
    maven("https://dl.bintray.com/kotlin/kotlin-dev")
    maven("https://kotlin.bintray.com/kotlinx")
    maven("https://maven.pkg.jetbrains.space/kotlin/p/dokka/dev")
}

dependencies {
    api(platform(kotlin("bom")))
    api(kotlin("gradle-plugin"))
    testImplementation("io.kotest:kotest-runner-junit5:4.1.0")
}

gradlePlugin {
    plugins {
        create(project.name) {
            id = "lt.petuska.kpm.publish"
            displayName = "Kotlin/JS publishing to NPM repositories"
            description =
                "Integrates with kotlin JS/MPP plugins to setup publishing to NPM repositories for all JS targets"
            implementationClass = "lt.petuska.kpm.publish.KpmPublishPlugin"
        }
    }
}

pluginBundle {
    website = "https://gitlab.com/lt.petuska/kpm-publish/-/wikis/home"
    vcsUrl = "https://gitlab.com/lt.petuska/kpm-publish"
    tags = listOf("npm", "publishing", "kotlin", "node")
}

tasks {
    withType<KotlinCompile> {
        kotlinOptions {
            jvmTarget = "1.8"
        }
    }
    withType<Jar> {
        manifest {
            attributes += sortedMapOf(
                "Built-By" to System.getProperty("user.name"),
                "Build-Jdk" to System.getProperty("java.version"),
                "Implementation-Version" to project.version,
                "Created-By" to org.gradle.util.GradleVersion.current()
            )
        }
    }
    withType<KtlintCheckTask> {
        dependsOn("ktlintFormat")
    }
    test {
        useJUnitPlatform()
    }
    val functionalTest by registering(Test::class) {
        testClassesDirs = functionalTestSourceSet.output.classesDirs
        classpath = functionalTestSourceSet.runtimeClasspath
        useJUnitPlatform()
        group = "verification"
    }
    val check by getting(Task::class) {
        dependsOn(functionalTest)
    }
}

val functionalTestSourceSet = sourceSets.create("functionalTest") {
}

gradlePlugin.testSourceSets(functionalTestSourceSet)
configurations.getByName("functionalTestImplementation").extendsFrom(configurations.getByName("testImplementation"))

fun getCommitHash() = ByteArrayOutputStream().use { os ->
    exec {
        commandLine("git", "rev-parse", "HEAD")
        standardOutput = os
    }
    os.toString().trim()
}

publishing {
    publications {
        repositories {
            maven {
                name = "bintray"
                url = uri(
                    "https://api.bintray.com/maven/${System.getenv("BINTRAY_USER")}/${project.group}/${project.name}/" +
                        ";publish=${if ("true".equals(project.properties["publish"] as? String?, true)) 1 else 0}" +
                        ";override=${if ("true".equals(project.properties["override"] as? String?, true)) 1 else 0}"
                )
                credentials {
                    username = System.getenv("BINTRAY_USER")
                    password = System.getenv("BINTRAY_KEY")
                }
            }
        }
    }
}

afterEvaluate {
    tasks {
        withType<Wrapper> {
            gradleVersion = "6.6.1"
            distributionType = Wrapper.DistributionType.ALL
        }
        val lib = rootProject
        val publish by getting
        create<HttpTask>("gitLabRelease") {
            dependsOn(publish)
            group = publish.group!!

            config {
                it.request.setUri("https://gitlab.com")
            }
            post {
                it.request.uri.setPath("/api/v4/projects/${System.getenv("CI_PROJECT_ID")}/releases")
                it.request.headers["Authorization"] = "Bearer ${System.getenv("PRIVATE_TOKEN")}"
                it.request.setContentType("application/json")
                fun buildPackageLink(prj: Project) =
                    """
        {
          "name": "${prj.name}",
          "url": "https://bintray.com/${System.getenv("BINTRAY_USER")}/${prj.group}/${prj.name}/${prj.version}",
          "link_type": "package"
        }
                    """.trimIndent()

                it.request.setBody(
                    """
        {
          "name": "Release v${lib.version}",
          "tag_name": "v${lib.version}",
          "ref": "${getCommitHash()}",
          "assets": {
              "links": [
                  ${setOf(lib).joinToString(",", transform = ::buildPackageLink)}
              ]
          },
          "description": "## Changelog\n### Breaking Changes\nN/A\n\n### New Features\nN/A\n\n### Fixes\nN/A"
        }
                    """.trimIndent()
                )
            }
        }
    }
}
