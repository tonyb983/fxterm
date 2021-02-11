import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.Path
import java.nio.file.attribute.BasicFileAttributes
import kotlin.streams.toList
import com.expediagroup.graphql.plugin.gradle.config.GraphQLClientType
import com.expediagroup.graphql.plugin.gradle.config.GraphQLScalar
import com.expediagroup.graphql.plugin.gradle.config.TimeoutConfiguration
import com.expediagroup.graphql.plugin.gradle.tasks.GraphQLDownloadSDLTask
import com.expediagroup.graphql.plugin.gradle.tasks.GraphQLGenerateClientTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  id("org.asciidoctor.convert") version "1.5.8"
  id("kotlinx.benchmark") version "0.2.0-dev-20"

  kotlin("jvm") version "1.4.30"
  kotlin("plugin.serialization") version "1.4.30"
  kotlin("plugin.spring") version "1.4.30"

  id("org.openjfx.javafxplugin") version "0.0.9"

  id("com.expediagroup.graphql") version "4.0.0-alpha.12"
  id("io.spring.dependency-management") version "1.0.11.RELEASE"
  id("org.springframework.boot") version "2.4.2"

  id("io.gitlab.arturbosch.detekt") version "master-SNAPSHOT"
}

group = "io.imtony.vdrive"
version = "0.0.1"
java.sourceCompatibility = JavaVersion.VERSION_15

// Repos
// =====
repositories {
  mavenCentral()
  jcenter()
  google()
  maven(url = "https://jitpack.io/")
  maven(url = "https://kotlin.bintray.com/kotlinx/")
  maven(url = "https://github.com/javaterminal/terminalfx/raw/master/releases")
}

// Deps
// ====
dependencies {
  // detekt("io.gitlab.arturbosch.detekt:detekt-cli:1.14.2")
  detektPlugins("com.rickbusarow.dispatch:dispatch-detekt:1.0.0-beta08")
  detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:1.16.0-RC1")

  implementation("org.reflections:reflections:0.9.12")

  implementation(kotlin("reflect", "1.4.30"))
  implementation(kotlin("stdlib-jdk8", "1.4.30"))

  implementation("io.github.microutils:kotlin-logging-jvm:2.0.2")
  implementation("ch.qos.logback:logback-classic:1.2.3")

  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.2")
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-jdk9:1.4.2")
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-javafx:1.4.2")
  implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.0.1")
  implementation("org.jetbrains.kotlinx:kotlinx-serialization-cbor:1.0.1")
  implementation("com.github.jershell:kbson:0.4.4")
  implementation("com.ensarsarajcic.kotlinx:serialization-msgpack:0.1.0")
  implementation("org.jetbrains.kotlinx:kotlinx-collections-immutable:0.3.3")
  implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.1.1")

  implementation("org.springframework.boot:spring-boot-starter")
  implementation("org.springframework.boot:spring-boot-starter-actuator")
  implementation("org.springframework.boot:spring-boot-starter-validation")

  implementation("com.expediagroup:graphql-kotlin-spring-client:4.0.0-alpha.12")
  implementation("com.expediagroup:graphql-kotlin-hooks-provider:4.0.0-alpha.12")

  developmentOnly("org.springframework.boot:spring-boot-devtools")

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

  testImplementation("org.springframework.boot:spring-boot-starter-test")
  testImplementation("io.projectreactor:reactor-test")
  testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.4.2")
  testImplementation("io.kotest:kotest-runner-junit5:4.4.0.RC2")
  testImplementation("io.kotest:kotest-property:4.4.0.RC2")
}

// Kotlin Compile Setup for All Uses
// ====== ======= ===== === === ====
tasks.withType<KotlinCompile> {
  kotlinOptions {
    freeCompilerArgs = listOf(
      "-Xjsr305=strict",
      "-Xinline-classes",
      "-Xopt-in=kotlin.io.path.ExperimentalPathApi",
      "-Xopt-in=kotlin.contracts.ExperimentalContracts",
      "-Xopt-in=kotlin.ExperimentalStdlibApi",
      "-Xopt-in=kotlin.time.ExperimentalTime",
      "-Xopt-in=kotlinx.serialization.ExperimentalSerializationApi"
    )
    jvmTarget = "15"
    apiVersion = "1.4"
    languageVersion = "1.4"
  }
}

// Test Setup
// ==== =====
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

// Detekt Plugin
// ====== ======
detekt {
  config = files(projectDir.resolve("./config/detekt/detekt.yml"))
  ignoreFailures = true
  basePath = projectDir.toString()
  baseline = projectDir.resolve("./config/detekt/baseline.xml")
}

// JavaFx Plugin
// ====== ======
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

// GraphQL Plugin And Tasks
// ======= ====== === =====
val gqlPath = Paths.get(projectDir.path, "src/main/resources/gql")
val gqlPathFile = projectDir.resolve("src/main/resources/gql")
val kotlinPath = Paths.get(projectDir.path, "src/main/kotlin")
val kotlinPathFile = projectDir.resolve("src/main/kotlin")
val generatedPackage = "io.imtony.vdrive.fxterm.generated"
val kotlinGeneratedPath = kotlinPath.resolve(generatedPackage)

val shouldIncludeGqlFile: (Path, BasicFileAttributes) -> Boolean = { path, _ ->
  path.toFile().let { file ->
    file.extension == "graphql" &&
    file.nameWithoutExtension.let { nameNoExt ->
      nameNoExt != "schema" && nameNoExt != "dl-schema"
    }
  }
}

val gqlQueryFiles = Files.find(
  gqlPath,
  10,
  shouldIncludeGqlFile
)

val gqlCustomScalars = listOf(
  GraphQLScalar("UUID", "java.util.UUID", "io.imtony.vdrive.fxterm.gql.scalars.UuidScalarConverter"),
  GraphQLScalar("LocalDateTime", "java.time.LocalDateTime", "io.imtony.vdrive.fxterm.gql.scalars.JavaTimeScalarConverter"),
  GraphQLScalar("Instant", "java.time.Instant", "io.imtony.vdrive.fxterm.gql.scalars.JavaInstantScalarConverter")
  //GraphQLScalar("LocalDateTime", "kotlinx.datetime.LocalDateTime", "io.imtony.vdrive.fxterm.gql.scalars.KotlinTimeScalarConverter"),
  //GraphQLScalar("Instant", "kotlinx.datetime.Instant", "io.imtony.vdrive.fxterm.gql.scalars.KotlinInstantScalarConverter")
)

graphql {
  client {
    allowDeprecatedFields = true
    clientType = GraphQLClientType.WEBCLIENT
    endpoint = "http://localhost:8080/graphql"
    sdlEndpoint = "http://localhost:8080/sdl"
    packageName = "io.imtony.vdrive.fxterm.generated"
    queryFiles = gqlQueryFiles.map { it.toFile() }.toList()
    customScalars = gqlCustomScalars
  }
}

val graphqlDownloadSDL by tasks.getting(GraphQLDownloadSDLTask::class) {
  endpoint.set("http://localhost:8080/sdl")
  outputFile.set(gqlPathFile.resolve("dl-schema.graphql"))
  headers.put("user-agent", "graphql-kotlin")
  timeoutConfig.set(TimeoutConfiguration(connect = 10_000, read = 30_000))
}

val graphqlGenerateClient by tasks.getting(GraphQLGenerateClientTask::class) {
  this.outputDirectory.set(kotlinPathFile)
  packageName.set("io.imtony.vdrive.fxterm.generated.gql")
  schemaFile.set(gqlPathFile.resolve("schema.graphql"))
  // schemaFile.set(graphqlDownloadSDL.outputFile)
  allowDeprecatedFields.set(true)
  clientType.set(GraphQLClientType.WEBCLIENT)
  customScalars.set(gqlCustomScalars)
  queryFiles.from(gqlQueryFiles)

  // dependsOn(graphqlDownloadSDL)
}

tasks.register<Delete>("cleanGraphQl") {
  this.group = "graphql"
  delete = setOf(kotlinGeneratedPath.resolve("gql"))
}