plugins {
    kotlin("jvm") version "2.2.0"
}

group = "battery.cli"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {

}

kotlin {
    jvmToolchain(17)
}