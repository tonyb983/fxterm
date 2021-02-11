pluginManagement {
  resolutionStrategy {
    eachPlugin {
      if (requested.id.id == "io.gitlab.arturbosch.detekt") {
        useModule("io.gitlab.arturbosch.detekt:detekt-gradle-plugin:${requested.version}")
      }
    }
  }
  repositories {
    gradlePluginPortal()
    maven(url = "https://dl.bintray.com/kotlin/kotlinx")
    maven {
      url = uri("https://oss.sonatype.org/content/repositories/snapshots/")
    }
  }
}

rootProject.name = "fxterm"
