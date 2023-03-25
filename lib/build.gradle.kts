import com.bmuschko.gradle.docker.tasks.container.DockerCreateContainer
import com.bmuschko.gradle.docker.tasks.container.DockerStartContainer
import com.bmuschko.gradle.docker.tasks.image.DockerBuildImage
import com.bmuschko.gradle.docker.tasks.image.Dockerfile
import org.jetbrains.dokka.gradle.DokkaTask
import java.net.URL
import java.net.ServerSocket

plugins {
  `java-library`
  id("com.bmuschko.docker-remote-api") version "9.3.0"
}

dependencies {
  implementation(libs.kotlinxCoroutines)

  // Comms/IO
  implementation(libs.okHttp)

  // JSON
  implementation(libs.moshi)

  // Cache
  implementation(libs.guava)

  // Curves
  implementation(libs.acinqSecp256k1JniJvm)
  implementation(libs.acinqSecp256k1Jvm)
  implementation(files("lib/secp256k1-kmp-jni-jvm-darwin-0.7.1.jar"))

  // Logging
  implementation(libs.kotlinLoggingJvm)
  testRuntimeOnly(libs.slf4jSimple)

  // Basic test libraries:
  testImplementation(libs.kotestAssertions)
  testImplementation(libs.kotestJunitRunnerJvm)
  testImplementation(libs.kotestProperty)
  testRuntimeOnly(libs.junitEngine)

  apply(plugin = libs.plugins.dokka.get().pluginId)
}

tasks.withType<DokkaTask>().configureEach {
  dependsOn("copyDocumentationImages")
  dokkaSourceSets {
    named("main") {
      moduleName.set("Nostrino Nostr SDK")

      // Includes custom documentation
      includes.from("module.md")

      // Points source links to GitHub
      sourceLink {
        localDirectory.set(file("src/main/kotlin"))
        remoteUrl.set(URL("https://github.com/cashapp/nostrino/tree/master/lib/src/main/kotlin"))
        remoteLineSuffix.set("#L")
      }
    }
  }
}

val createDockerfile by tasks.creating(Dockerfile::class) {
  from("scsibug/nostr-rs-relay:latest")
}

val buildImage by tasks.creating(DockerBuildImage::class) {
  dependsOn(createDockerfile)
  images.add("scsibug/nostr-rs-relay:latest")
}

val createContainer by tasks.creating(DockerCreateContainer::class) {
  onlyIf { !relayIsRunning() }
  dependsOn(buildImage)
  targetImageId(buildImage.imageId)
  containerName.set("nostr-relay")
  hostConfig.portBindings.set(listOf("7707:8080"))
  hostConfig.autoRemove.set(true)
}

val startContainer by tasks.creating(DockerStartContainer::class) {
  onlyIf { !relayIsRunning() }
  dependsOn(createContainer)
  targetContainerId(createContainer.containerId)
}

tasks.withType<Test>().configureEach {
  dependsOn(startContainer)
}

fun relayIsRunning() =
  try {
    val s = ServerSocket(7707)
    s.close()
    false
  } catch (_: java.io.IOException) {
    true
  }
