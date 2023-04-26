repositories {
    maven {
        url = uri("https://kotlin.bintray.com/kotlinx")
        content {
            includeGroup("org.jetbrains.kotlinx")
        }
    }
}

plugins {
    java
    application
    id("scaffolding")
}

val main = "q2s.MainKt"

application {
    mainClass.set(main)
}

// tasks.named<ShadowJar>("shadowJar").configure {
//     archiveBaseName.set("dojo")
//     archiveVersion.set("latest")
//     archiveClassifier.set("server")
// }

val ktorVersion = "1.6.8"

dependencies {
    implementation(project(":logging"))
    implementation("org.jetbrains.kotlinx:kotlinx-cli:0.3.5")
    implementation("org.apache.commons:commons-csv:1.10.0")
    implementation("com.google.api-client:google-api-client:2.2.0")
    implementation("com.google.oauth-client:google-oauth-client-jetty:1.34.1")
    implementation("com.google.apis:google-api-services-sheets:v4-rev20230227-2.0.0")
    implementation("io.ktor:ktor-client-core:$ktorVersion")
    implementation("io.ktor:ktor-client-java:$ktorVersion")
    implementation("io.ktor:ktor-client-auth:$ktorVersion")
    // implementation("io.ktor:ktor-client-gson:$ktorVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:1.5.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.0")
}
