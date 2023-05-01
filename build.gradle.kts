import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.6.10"
    application
}

group = "com.goofy"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

object DependencyVersion {
    const val COROUTINES_CORE_VERSION = "1.6.4"
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${DependencyVersion.COROUTINES_CORE_VERSION}")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "11"
}

application {
    mainClass.set("MainKt")
}
