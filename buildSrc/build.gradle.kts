plugins {
    `kotlin-dsl`
    kotlin("jvm") version "1.8.21"
}

repositories {
    gradlePluginPortal() // so that external plugins can be resolved in dependencies section
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.8.21")
    implementation("org.jlleitschuh.gradle:ktlint-gradle:11.3.2")
    implementation("io.gitlab.arturbosch.detekt:detekt-gradle-plugin:1.22.0")
    implementation("com.github.ben-manes:gradle-versions-plugin:0.46.0")
    implementation("com.github.johnrengelman.shadow:com.github.johnrengelman.shadow.gradle.plugin:7.1.2")
}

