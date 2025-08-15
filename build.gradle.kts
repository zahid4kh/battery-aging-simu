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
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    google()
}

application{
    mainClass = "KruppAgingModel"
}

dependencies {
    implementation(libs.kotlinx.serialization.json)
}