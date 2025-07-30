import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.plugin.serialization)
    application
    id("com.gradleup.shadow") version "8.3.0"
}

val appPackageVersion = "1.0.0"
group = "batage"
version = appPackageVersion

repositories {
    mavenCentral()
    gradlePluginPortal()
    google()
}

dependencies {
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.swing)
}

application{
    mainClass = "BatteryAgingSimu"
}

tasks.withType<ShadowJar>{
    archiveBaseName.set("BatteryAgingSimu")
    archiveVersion.set(appPackageVersion)
}