// import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

repositories {
    // jcenter()
    maven {
        url = uri("https://kotlin.bintray.com/kotlinx")
        content {
            includeGroup("org.jetbrains.kotlinx")
        }
    }
    maven {
        url = uri("https://maven.pkg.jetbrains.space/public/p/kotlinx-html/maven")
        content {
            includeGroup("org.jetbrains.kotlinx")
        }
    } // for kotlinx-cli
}

plugins {
    java
    application
    id("scaffolding")
    // kotlin("plugin.serialization") version "1.4.31"
    id("com.github.johnrengelman.shadow") version "7.0.0"
    // kotlin("plugin.serialization") version "1.5.0"
}

val main = "q2s.MainKt"

application {
    mainClassName = main // needed for shadow plugin
    mainClass.set(main)
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=true")
}

// tasks.named<ShadowJar>("shadowJar").configure {
//     archiveBaseName.set("dojo")
//     archiveVersion.set("latest")
//     archiveClassifier.set("server")
// }

val ktorVersion = "1.5.4"

dependencies {
    implementation(project(":logging"))
    implementation("org.jetbrains.kotlinx:kotlinx-cli:0.3.2")
    implementation("org.apache.commons:commons-csv:1.8")
    implementation("com.google.api-client:google-api-client:1.31.5")
    implementation("com.google.oauth-client:google-oauth-client-jetty:1.31.5")
    implementation("com.google.apis:google-api-services-sheets:v4-rev20210504-1.31.0")
    implementation("io.ktor:ktor-client-core:$ktorVersion")
    implementation("io.ktor:ktor-client-java:$ktorVersion")
    implementation("io.ktor:ktor-client-auth:$ktorVersion")
    // implementation("io.ktor:ktor-client-gson:$ktorVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:1.2.1")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.2.1")
}

// tasks.withType<JavaExec> {
//     jvmArgs?.add("-Dio.ktor.development=true")
// }
