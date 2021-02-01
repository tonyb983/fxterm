import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  id("org.asciidoctor.convert") version "1.5.8"
  kotlin("jvm") version "1.4.21"
  kotlin("plugin.serialization") version "1.4.21"
  id("org.openjfx.javafxplugin") version "0.0.9"
  id("io.gitlab.arturbosch.detekt") version "1.16.0-RC1"
}

group = "io.imtony.vdrive"
version = "0.0.1"
java.sourceCompatibility = JavaVersion.VERSION_15

repositories {
  mavenCentral()
  jcenter()
  google()
  maven("https://jitpack.io/")
  maven("https://github.com/javaterminal/terminalfx/raw/master/releases")
}

javafx {
  this.version = "15.0.1"
  this.modules(
    "javafx.controls",
    "javafx.graphics",
    "javafx.fxml",
    "javafx.media",
    "javafx.swing"
  )
}

detekt {
  config = files(projectDir.resolve("./config/detekt/detekt.yml"))
  ignoreFailures = true
  basePath = projectDir.toString()
  baseline = projectDir.resolve("config/detekt/baseline.xml")
}

dependencies {
  // detekt("io.gitlab.arturbosch.detekt:detekt-cli:1.14.2")
  detektPlugins("com.rickbusarow.dispatch:dispatch-detekt:1.0.0-beta08")
  detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:1.16.0-RC1")

  implementation(kotlin("reflect", "1.4.21"))
  implementation(kotlin("stdlib-jdk8", "1.4.21"))

  implementation("io.github.microutils:kotlin-logging-jvm:2.0.2")
  implementation("ch.qos.logback:logback-classic:1.3.0-alpha5")

  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.2")
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-jdk9:1.4.2")
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-javafx:1.4.2")
  implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.0.1")
  implementation("org.jetbrains.kotlinx:kotlinx-collections-immutable:0.3.3")

  implementation("org.apache.httpcomponents:httpclient:4.5.13")
  implementation("org.apache.httpcomponents:httpcore-nio:4.4.14")
  implementation("org.apache.httpcomponents:httpasyncclient:4.1.4")
  implementation("org.apache.httpcomponents:fluent-hc:4.5.13")

  implementation("com.rickbusarow.dispatch:dispatch-core:1.0.0-beta08")

  implementation("com.google.api-client:google-api-client:1.31.2")
  implementation("com.google.api-client:google-api-client-jackson2:1.31.2")
  implementation("com.google.oauth-client:google-oauth-client-jetty:1.31.4")
  implementation("com.google.apis:google-api-services-docs:v1-rev61-1.25.0")
  implementation("com.google.apis:google-api-services-sheets:v4-rev612-1.25.0")
  implementation("com.google.apis:google-api-services-drive:v3-rev197-1.25.0")
  implementation("com.google.apis:google-api-services-calendar:v3-rev411-1.25.0")
  implementation("com.google.jimfs:jimfs:1.2")

  implementation("com.kodedu.terminalfx:terminalfx:1.1.0")

  implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.12.+")

  implementation("at.favre.lib:bcrypt:0.9.0")
  implementation("io.github.serpro69:kotlin-faker:1.6.0")

  implementation("no.tornado:tornadofx:1.7.20")
  implementation("no.tornado:tornadofx-controlsfx:0.1.1")
  implementation("com.jfoenix:jfoenix:9.0.10")
  implementation("eu.hansolo:Medusa:11.5")
  implementation("com.dlsc.gemsfx:gemsfx:1.23.0")
  implementation("eu.hansolo:tilesfx:11.45")
  implementation("org.controlsfx:controlsfx:11.0.3")
  implementation("net.raumzeitfalle.fx:scenic-view:11.0.2")
  implementation("de.jensd:fontawesomefx-commons:11.0")
  implementation("de.jensd:fontawesomefx-controls:11.0")
  implementation("de.jensd:fontawesomefx-fontawesome:4.7.0-11")
  implementation("de.jensd:fontawesomefx-materialicons:2.2.0-11")
  implementation("org.jfxtras:jfxtras-agenda:15-r1")
  implementation("org.jfxtras:jfxtras-common:15-r1")
  implementation("org.jfxtras:jfxtras-controls:15-r1")
  implementation("org.jfxtras:jfxtras-font-roboto:15-r1")
  implementation("org.jfxtras:jfxtras-fxml:15-r1")
  implementation("org.jfxtras:jfxtras-gauge-linear:15-r1")
  implementation("org.jfxtras:jfxtras-icalendarfx:15-r1")
  implementation("org.jfxtras:jfxtras-menu:15-r1")
  implementation("org.jfxtras:jfxtras-parent:15-r1")
  implementation("org.jfxtras:jfxtras-test-support:15-r1")
  implementation("org.jfxtras:jfxtras-window:15-r1")
  implementation("org.jfxtras:jmetro:11.6.12")

  implementation("org.apache.commons:commons-text:1.9")

  testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.4.2")
  testImplementation("io.kotest:kotest-runner-junit5:4.4.0.RC2")
  testImplementation("io.kotest:kotest-property:4.4.0.RC2")
}

tasks.withType<KotlinCompile> {
  kotlinOptions {
    freeCompilerArgs = listOf(
      "-Xjsr305=strict",
      "-Xinline-classes",
      "-Xopt-in=kotlin.io.path.ExperimentalPathApi",
      "-Xopt-in=kotlin.contracts.ExperimentalContracts",
      "-Xopt-in=kotlin.ExperimentalStdlibApi",
      "-Xopt-in=kotlin.time.ExperimentalTime"
    )
    jvmTarget = "15"
    apiVersion = "1.4"
    languageVersion = "1.4"
  }
}

tasks.withType<Test> {
  useJUnitPlatform()
  failFast = false
  testLogging {
    showStandardStreams = true
    showExceptions = false
    showCauses = false
    showStackTraces = false
  }
}
//val compileKotlin: KotlinCompile by tasks
//compileKotlin.kotlinOptions {
//  freeCompilerArgs = listOf(
//    "-Xjsr305=strict",
//    "-Xinline-classes",
//    "-Xopt-in=kotlin.io.path.ExperimentalPathApi"
//  )
//  jvmTarget = "15"
//  apiVersion = "1.4"
//  languageVersion = "1.4"
//}
