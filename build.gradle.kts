import net.fabricmc.loom.api.LoomGradleExtensionAPI

plugins {
    java
    id("dev.architectury.loom") version "1.10-SNAPSHOT" apply false
    id("architectury-plugin") version "3.4-SNAPSHOT"
    id("com.gradleup.shadow") version "8.3.6" apply false
}

architectury {
    minecraft = property("minecraft_version") as String
}

allprojects {
    group = property("maven_group")!!
    version = property("mod_version")!!
}

subprojects {
    apply(plugin = "dev.architectury.loom")
    apply(plugin = "architectury-plugin")
    apply(plugin = "maven-publish")

    // Set up a suffixed format for the mod jar names, e.g. `example-fabric`.
    base.archivesName = "${property("archives_name")}-${property("name")}"

    repositories {
        maven("https://maven.isxander.dev/releases") {
            name = "Xander Maven"
        }

        maven("https://maven.parchmentmc.org") {
            name = "ParchmentMC"
        }
    }

    val loom = project.extensions.getByName("loom") as LoomGradleExtensionAPI

    dependencies {
        "minecraft"("net.minecraft:minecraft:${property("minecraft_version")}")
        @Suppress("UnstableApiUsage")
        "mappings"(loom.layered {
            officialMojangMappings()
            @Suppress("UnstableApiUsage")
            parchment("org.parchmentmc.data:parchment-${property("parchment_version")}@zip")
        })
    }

    java {
        withSourcesJar()

        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    tasks.withType<JavaCompile>().configureEach {
        options.release = 21
    }

    // Configure Maven publishing.
    configure<PublishingExtension> {
        publications {
            create<MavenPublication>("mavenJava") {
                artifactId = base.archivesName.get()
                from(components["java"])
            }
        }

        // See https://docs.gradle.org/current/userguide/publishing_maven.html for information on how to set up publishing.
        repositories {
            // Add repositories to publish to here.
            // Notice: This block does NOT have the same function as the block in the top level.
            // The repositories here will be used for publishing your artifact, not for
            // retrieving dependencies.
        }
    }
}
