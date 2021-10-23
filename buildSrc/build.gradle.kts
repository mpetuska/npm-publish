plugins {
  `kotlin-dsl`
}

repositories {
  mavenLocal()
  jcenter()
  mavenCentral()
}

dependencies {
  implementation("com.github.jakemarsden:git-hooks-gradle-plugin:_")
}
