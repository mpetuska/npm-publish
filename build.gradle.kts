plugins {
    kotlin("jvm") version "1.3.72"
    `java-gradle-plugin`
    id("com.gradle.plugin-publish") version "0.12.0"
}

group = "lt.petuska"
version = "0.0.1"

repositories {
    jcenter()
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
