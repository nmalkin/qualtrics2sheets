val versions = mapOf(
    "kotlin" to "1.5.0"
)

plugins {
    `kotlin-dsl`
    kotlin("jvm") version "1.5.0"
}

repositories {
    gradlePluginPortal() // so that external plugins can be resolved in dependencies section
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:${versions["kotlin"]}")
    implementation("org.jlleitschuh.gradle:ktlint-gradle:10.0.0")
    implementation("io.gitlab.arturbosch.detekt:detekt-gradle-plugin:1.17.1")
    implementation("com.github.ben-manes:gradle-versions-plugin:0.38.0")
}

