import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("jvm")
    id("org.jetbrains.compose")
    kotlin("plugin.serialization") version "1.9.22"
}

group = "com.apu"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    google()
}

dependencies {
    implementation(compose.desktop.currentOs)
    // ktor
    implementation("io.ktor:ktor-client-core:1.6.3")
    // ktor okhttp
    implementation("io.ktor:ktor-client-okhttp:1.6.3")
    // ktor serialization
    implementation("io.ktor:ktor-client-serialization:1.6.3")

    implementation("org.jsoup:jsoup:1.15.3")
    //Klaxon
    implementation("com.beust:klaxon:5.5")
}

compose.desktop {
    application {
        mainClass = "MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg)
            packageName = "esdownloader"
            packageVersion = "1.0.0"
        }
    }
}
