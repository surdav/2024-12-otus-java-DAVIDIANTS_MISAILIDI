import io.spring.gradle.dependencymanagement.dsl.DependencyManagementExtension
import org.springframework.boot.gradle.plugin.SpringBootPlugin.BOM_COORDINATES

plugins {
    idea
    id("io.spring.dependency-management")
    id("org.springframework.boot") apply false
    id("com.github.johnrengelman.shadow") version "8.1.1" apply false
}

idea {
    module {
        isDownloadJavadoc = true
        isDownloadSources = true
    }
}

allprojects {
    group = "ru.otus"

    repositories {
        mavenLocal()
        mavenCentral()
    }

        val jmh: String by project
    val asm: String by project
    val testcontainersBom: String by project
    val protobufBom: String by project
    val guava: String by project
    val glassfishJson: String by project


    // Dependency management for consistent versions
    apply(plugin = "io.spring.dependency-management")
    dependencyManagement {
        dependencies {
            imports {
                mavenBom(BOM_COORDINATES)
                mavenBom("org.testcontainers:testcontainers-bom:$testcontainersBom")
                mavenBom("com.google.protobuf:protobuf-bom:$protobufBom")
            }
            dependency("com.google.guava:guava:$guava")
            dependency("org.openjdk.jmh:jmh-generator-annprocess:$jmh")
            dependency("org.glassfish:jakarta.json:$glassfishJson")
            dependency("org.ow2.asm:asm-commons:$asm")
            dependency("com.google.guava:guava:$guava")
            dependency("org.glassfish:jakarta.json:$glassfishJson")
        }
    }
}

subprojects {
    // Apply common plugins
    apply(plugin = "java")
    apply(plugin = "com.github.johnrengelman.shadow")

    repositories {
        mavenLocal()
        mavenCentral()
    }

    dependencies {
        val logbackVersion: String by project
        val junitVersion: String by project
        val mockitoVersion: String by project
        val assertjVersion: String by project
        val guava: String by project

        // Main dependencies
        add("implementation", "ch.qos.logback:logback-classic:$logbackVersion")
        add("implementation", "com.google.guava:guava:$guava")

        add("implementation", "com.fasterxml.jackson.core:jackson-databind")
        add("implementation", "org.glassfish:jakarta.json")
        add("implementation", "com.fasterxml.jackson.datatype:jackson-datatype-jsr310")

        // Test dependencies (explicitly declare test framework + runtime engine)
        add("testImplementation", platform("org.junit:junit-bom:$junitVersion"))
        add("testImplementation", "org.junit.jupiter:junit-jupiter-api")
        add("testImplementation", "org.assertj:assertj-core:$assertjVersion")
        add("testImplementation", "org.mockito:mockito-core:$mockitoVersion")
        add("testImplementation", "org.mockito:mockito-junit-jupiter:$mockitoVersion")
        add("testRuntimeOnly", "org.junit.jupiter:junit-jupiter-engine") // Engine for running tests
    }

    extensions.configure<JavaPluginExtension> {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    tasks.withType<JavaCompile> {
        options.encoding = "UTF-8"
        options.compilerArgs.addAll(listOf("-Xlint:all,-serial,-processing"))
    }

    tasks.withType<Test> {
        useJUnitPlatform() // Ensure JUnit5 Platform is used

        // Optional: Enable logs for passed, skipped, and failed tests
        testLogging {
            events("passed", "skipped", "failed")
        }
    }

    // Consistent resolution for JUnit versions
    configurations.all {
        resolutionStrategy {
            val junitVersion: String by project
            force("org.junit.jupiter:junit-jupiter-api:$junitVersion")
            force("org.junit.jupiter:junit-jupiter-engine:$junitVersion")
            force("org.junit.platform:junit-platform-commons:$junitVersion")
            force("org.junit.platform:junit-platform-launcher:$junitVersion")
        }
    }
}

tasks {
    val managedVersions by registering {
        doLast {
            project.extensions.getByType<DependencyManagementExtension>()
                .managedVersions
                .toSortedMap()
                .map { "${it.key}:${it.value}" }
                .forEach(::println)
        }
    }
}