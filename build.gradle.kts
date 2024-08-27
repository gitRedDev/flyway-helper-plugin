plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm") version "1.8.21"
    id("org.jetbrains.intellij") version "1.13.3"
}

group = "com.helpers"
version = "1.3.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
}


// Configure Gradle IntelliJ Plugin
// Read more: https://plugins.jetbrains.com/docs/intellij/tools-gradle-intellij-plugin.html
intellij {
    version.set("2022.2.5")
    type.set("IC") // Target IDE Platform

    plugins.set(listOf(/* Plugin Dependencies */))
}

tasks {
    // Set the JVM compatibility versions
    withType<JavaCompile> {
        sourceCompatibility = "17"
        targetCompatibility = "17"
    }
    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions.jvmTarget = "17"
    }

    patchPluginXml {
        sinceBuild.set("222")
        untilBuild.set("242.*")
    }

    runPluginVerifier {
        ideVersions.set(listOf("IC-2022.1.2", "IC-2022.2.4", "IC-2022.3.2", "IU-2023.1.7", "IU-2023.2.7", "IU-2024.1.1"))
        localPaths.set(listOf())
    }


    signPlugin {
        certificateChainFile.set(file(System.getenv("CERTIFICATE_CHAIN_FILE")))
        privateKeyFile.set(file(System.getenv("PRIVATE_KEY_FILE")))
        password.set(System.getenv("PRIVATE_KEY_PASSWORD"))
    }

//    publishPlugin {
//        token.set(System.getenv("PUBLISH_TOKEN"))
//    }
}
