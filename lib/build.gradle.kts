plugins {
  alias(libs.plugins.kotlinMultiplatform)
  alias(libs.plugins.kotestMultiplatform)
}

kotlin {
  jvmToolchain(17)

  jvm()
  iosX64()
  iosArm64()
  iosSimulatorArm64()
  linuxX64()

  sourceSets {

    commonMain {
      dependencies {
        implementation(libs.acinqSecp256k1)
        implementation(libs.kotlinxCoroutines)
        implementation(libs.okIo)
      }
    }

    jvmMain {
      dependencies {
        api(libs.okHttp)
        implementation(libs.acinqSecp256k1Jvm)
        implementation(libs.acinqSecp256k1JniJvm)
        implementation(libs.moshi)
        implementation(libs.kotlinLoggingJvm)
        implementation(libs.guava)
        apply(plugin = libs.plugins.dokka.get().pluginId)
      }
    }
  }
}

plugins.withId("com.vanniktech.maven.publish.base") {
  configure<com.vanniktech.maven.publish.MavenPublishBaseExtension> {
    coordinates("app.cash.nostrino", "nostr-sdk-kmm", "0.0.7-SNAPSHOT")
    // pomFromGradleProperties() // TODO use pom
    // publishToMavenCentral(com.vanniktech.maven.publish.SonatypeHost.DEFAULT, true)
    signAllPublications()
  }
}
