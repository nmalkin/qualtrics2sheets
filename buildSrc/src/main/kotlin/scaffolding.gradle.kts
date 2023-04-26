import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    id("org.jlleitschuh.gradle.ktlint")
    id("io.gitlab.arturbosch.detekt")
    id("com.github.ben-manes.versions")
    id("com.github.johnrengelman.shadow")
}

repositories {
    mavenCentral()
}


dependencies {
    // JUnit
    testImplementation(kotlin("test-junit5"))
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.2")
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "11"
}

tasks.test {
    useJUnitPlatform()
}


ktlint {
    version.set("0.49.0")

    filter {
        exclude("build/**")
        exclude("**/generated/**")
        /*
        From https://github.com/jlleitschuh/ktlint-gradle#faq:
        Gradle based filtering are only working for files located inside project (subproject) folder, see https://github.com/gradle/gradle/issues/3417 To filter files outside project dir, use:
         */
        exclude { element -> element.file.path.contains("generated/") }
    }
}

detekt {
    toolVersion = "1.18.1"
    config = files("${rootDir}/detekt.yml")
    buildUponDefaultConfig = true
}

tasks.getByPath("detekt").onlyIf { gradle.startParameter.taskNames.contains("detekt") }

// documented at https://github.com/ben-manes/gradle-versions-plugin
tasks.named<DependencyUpdatesTask>("dependencyUpdates").configure {
    gradleReleaseChannel = "current" // rather than nightly or release-candidate (the default)
    rejectVersionIf {
        listOf("-M", "-RC").any { it in candidate.version }
    }
}
