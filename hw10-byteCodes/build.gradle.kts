import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("com.github.johnrengelman.shadow") // Plugin for creating fat jar (ShadowJar).
}

dependencies {
    implementation("ch.qos.logback:logback-classic") // Logging dependency: SLF4J with Logback.
}

tasks {
    // Task to create a fat jar for ProxyDemo.
    register<ShadowJar>("proxyDemoJar") {
        archiveBaseName.set("proxyDemo") // Set the jar's base name.
        archiveVersion.set("") // No version in the jar name.
        archiveClassifier.set("") // No classifier in the jar name.

        // Defining the jar's manifest attributes.
        manifest {
            attributes(mapOf(
                "Main-Class" to "ru.otus.aop.proxy.ProxyDemo" // Entry point class for the application.
            ))
        }

        // Includes all compiled classes and resources from the main source set.
        from(sourceSets.main.get().output)

        // Includes runtime dependencies in the jar.
        configurations = listOf(project.configurations.runtimeClasspath.get())
    }

    // Ensures the ShadowJar task is run as part of the build task.
    build {
        dependsOn("proxyDemoJar")
    }
}