import jekyll.JekyllBuildTask
import jekyll.JekyllServeTask
import org.jetbrains.dokka.gradle.DokkaTask

plugins {
  id("org.jetbrains.dokka")
}

tasks {
  val jekyllBuild = register("jekyllBuild", JekyllBuildTask::class.java) {
    sourceDir.set(layout.projectDirectory.dir("src/docs"))
  }
  val jekyllServe = register("jekyllServe", JekyllServeTask::class.java) {
    sourceDir.set(layout.projectDirectory.dir("src/docs"))
  }
  val dokkaHtml = named("dokkaHtml", DokkaTask::class.java)
  val docsBuild = register("docsBuild", Copy::class.java) {
    group = dokkaHtml.get().group.toString()
    dependsOn(jekyllBuild, dokkaHtml)
    from(jekyllBuild)
    into("api") {
      from(dokkaHtml)
    }
    destinationDir = buildDir.resolve("docs")
  }
  register("docsServe", JekyllServeTask::class.java) {
    group = docsBuild.get().group.toString()
    dependsOn(docsBuild)
    sourceDir.set(project.layout.dir(docsBuild.map(Copy::getDestinationDir)))
  }
}
