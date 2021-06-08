import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.5.10"
    application
    id("com.github.johnrengelman.shadow") version "7.0.0"
}

group = "me.duncte123"
version = "1.0-SNAPSHOT"

application {
    mainClassName = "${project.group}.antihonde.MainKt"
}

repositories {
    maven("https://m2.dv8tion.net/releases")
    mavenCentral()
}

dependencies {
    implementation("net.dv8tion:JDA:4.2.1_273")
    implementation(group = "ch.qos.logback", name = "logback-classic", version = "1.2.3")
}

tasks {
    wrapper {
        gradleVersion = "7.0.2"
    }
}

tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "16"
}