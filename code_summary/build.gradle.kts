import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    antlr
    java
    application
    kotlin("jvm") version "1.4.10"
}

group = "me.vancho"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.antlr:antlr4:4.7.2")
    implementation("org.antlr:antlr4-runtime:4.7.2")
    testImplementation(kotlin("test-junit"))
}

tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "1.8"
}

application {
    mainClassName = "MainKt"
}
