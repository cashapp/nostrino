import org.jetbrains.dokka.gradle.DokkaTask
import java.net.URL

dependencies {
  implementation(project(":lib"))
  implementation(libs.kotestProperty)

  // For primitive types
  implementation(libs.okIo)

  // Basic test libraries:
  testImplementation(libs.kotestAssertions)
  testImplementation(libs.kotestJunitRunnerJvm)
  testRuntimeOnly(libs.junitEngine)

  apply(plugin = libs.plugins.dokka.get().pluginId)
}

tasks.withType<DokkaTask>().configureEach {
  dokkaSourceSets {
    named("main") {
      moduleName.set("Nostrino Nostr SDK Testing Lib")

      // Includes custom documentation
      includes.from("module.md")

      // Points source links to GitHub
      sourceLink {
        localDirectory.set(file("src/main/kotlin"))
        remoteUrl.set(URL("https://github.com/cashapp/nostrino/tree/master/lib-test/src/main/kotlin"))
        remoteLineSuffix.set("#L")
      }
    }
  }
}
