import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    kotlin("jvm") version "1.9.10" // Apply Kotlin JVM plugin
    id("com.github.johnrengelman.shadow") // Include Shadow plugin for building fat JARs
}

java {
    // Set the Java toolchain version (Java 21)
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21)) // Use Java 21
    }
}

repositories {
    // Specify Maven Central as the repository for dependencies
    mavenCentral()
}

dependencies {
    // Add runtime dependency for Logback (logging framework)
    implementation("ch.qos.logback:logback-classic")

    // Add dependencies for tests
    testImplementation("org.junit.jupiter:junit-jupiter-api") // JUnit 5 API
    testImplementation("org.junit.jupiter:junit-jupiter-engine") // JUnit 5 Test Engine
    testImplementation("org.assertj:assertj-core") // Assertion library
    testImplementation("org.mockito:mockito-core") // Mockito for mocking during testing
    testImplementation("org.mockito:mockito-junit-jupiter") // Mockito JUnit 5 integration
}

// Configure Kotlin compiler options
tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        jvmTarget = "20" // Kotlin supports up to JVM target 20
    }
}

tasks {
    // Configure the ShadowJar task for building a fat JAR
    named<ShadowJar>("shadowJar") {
        archiveBaseName.set("hw03-generics") // Set the base name of the JAR
        archiveVersion.set("0.1") // Set the version of the JAR
        archiveClassifier.set("") // No classifier (default empty string)
        manifest {
            // Add the Main-Class attribute pointing to the entry point of the application
            attributes(mapOf("Main-Class" to "ru.otus.Main")) // Replace 'ru.otus.Main' with your actual main class path
        }
    }

    // Make the 'build' task depend on the ShadowJar task
    build {
        dependsOn(shadowJar)
    }

    // Configure the task to run tests
    test {
        useJUnitPlatform() // Use JUnit 5 for testing
    }
}