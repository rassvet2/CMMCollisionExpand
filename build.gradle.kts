@file:Suppress("UnstableApiUsage")

import net.fabricmc.loom.util.ModPlatform
import java.util.*

plugins {
    id("dev.architectury.loom")
    id("architectury-plugin")
    id("me.modmuss50.mod-publish-plugin")
    id("com.github.johnrengelman.shadow")
}

val minecraft = stonecutter.current.version
val loader = loom.platform.get().name.lowercase()

version = "${mod.version}+$minecraft"
group = mod.group
base {
    archivesName = "${mod.id}-$loader"
}

architectury {
    if (stonecutter.current.isActive) platformSetupLoomIde()

    common(stonecutter.tree.branches.mapNotNull {
        if (stonecutter.current.project !in it) null
        else it.project.prop("loom.platform")
    })
}

repositories {
    maven("https://maven.neoforged.net/releases")

    // modmenu
    maven("https://maven.terraformersmc.com")
    maven("https://maven.nucleoid.xyz")

    // yacl & parchment
    maven("https://maven.isxander.dev/releases")

    // kotlinforforge
    maven("https://thedarkcolour.github.io/KotlinForForge")
}

dependencies {
    minecraft("com.mojang:minecraft:$minecraft")

    mappings(loom.layered {
        officialMojangMappings()
        parchment("org.parchmentmc.data:parchment-$minecraft:${mod.dep("parchment")}@zip")
    })

    // Architectury API. This is optional, and you can comment it out if you don't need it.
    modImplementation("dev.architectury:architectury-$loader:${mod.dep("architectury_api")}")
    modImplementation("dev.isxander:yet-another-config-lib:${mod.dep("yacl")}+$minecraft-$loader") {
//        exclude(group = "net.neoforged.fancymodloader", module = "loader")
        // fixme ^^^ to fix KotlinForForge #106, temporary exclude fml manually
        // https://github.com/thedarkcolour/KotlinForForge/issues/106
    }

    if (loader == "fabric") {
        modImplementation("net.fabricmc:fabric-loader:${mod.dep("fabric_loader")}")
        modImplementation("com.terraformersmc:modmenu:${mod.dep("modmenu_version")}")

        // some features (like automatic resource loading from non vanilla namespaces) work only with fabric API installed
        // for example translations from assets/modid/lang/en_us.json won't be working, same stuff with textures
        // but we keep runtime only to not accidentally depend on fabric's api, because it doesn't exist in neo/forge
        modRuntimeOnly("net.fabricmc.fabric-api:fabric-api:${mod.dep("fabric_version")}")
    }

    if (loader == "forge") {
        "forge"("net.minecraftforge:forge:${minecraft}-${mod.dep("forge_loader")}")
        annotationProcessor("io.github.llamalad7:mixinextras-common:${mod.dep("mixin_extras")}")

        compileOnly(annotationProcessor("io.github.llamalad7:mixinextras-common:${mod.dep("mixin_extras")}").toString())
        implementation(include("io.github.llamalad7:mixinextras-forge:${mod.dep("mixin_extras")}").toString())
//        implementation("io.github.llamalad7:mixinextras-forge:${mod.dep("mixin_extras")}")
//        include("io.github.llamalad7:mixinextras-forge:${mod.dep("mixin_extras")}")
    }

    if (loader == "neoforge") {
        "neoForge"("net.neoforged:neoforge:${mod.dep("neoforge_loader")}")

//        implementation("thedarkcolour:kotlinforforge-neoforge:5.6.0")
//        implementation("org.quiltmc.parsers:gson:0.2.1")
        // fixme ^^^ to fix KotlinForForge #106, temporary include yacl deps
        // https://github.com/thedarkcolour/KotlinForForge/issues/106
        // https://github.com/isXander/YetAnotherConfigLib/blob/multiversion/dev/build.gradle.kts
    }
}

loom {
    accessWidenerPath = rootProject.file("src/main/resources/cmmce.accesswidener")

    decompilers {
        get("vineflower").apply { // Adds names to lambdas - useful for mixins
            options.put("mark-corresponding-synthetics", "1")
        }
    }

    if (loader == "forge") {
        forge.mixinConfigs(
            "cmmce-common.mixins.json",
            "cmmce-forge.mixins.json",
        )
    }
}


val localProperties = Properties().also {
    val localPropertiesFile = rootProject.file("local.properties")
    if (localPropertiesFile.exists()) {
        it.load(localPropertiesFile.inputStream())
    }
}

publishMods {
    val modrinthToken = localProperties.getProperty("publish.modrinthToken", "")
    val curseforgeToken = localProperties.getProperty("publish.curseforgeToken", "")

    file = project.tasks.remapJar.get().archiveFile
    dryRun = modrinthToken == null || curseforgeToken == null

    displayName = "${mod.name} ${loader.replaceFirstChar { it.uppercase() }} ${property("mod.mc_title")}-${mod.version}"
    version = mod.version
    changelog = rootProject.file("CHANGELOG.md").readText()
    type = BETA

    modLoaders.add(loader)

    val targets = property("mod.mc_targets").toString().split(' ')
    modrinth {
        projectId = property("publish.modrinth").toString()
        accessToken = modrinthToken
        targets.forEach(minecraftVersions::add)
        if (loader == "fabric") {
            requires("fabric-api")
            optional("modmenu")
        }
    }

    curseforge {
        projectId = property("publish.curseforge").toString()
        accessToken = curseforgeToken.toString()
        targets.forEach(minecraftVersions::add)
        if (loader == "fabric") {
            requires("fabric-api")
            optional("modmenu")
        }
    }
}

java {
    withSourcesJar()
    if (stonecutter.eval(minecraft, ">=1.20.5")) {
        targetCompatibility = JavaVersion.VERSION_21
        sourceCompatibility = JavaVersion.VERSION_21
    } else {
        targetCompatibility = JavaVersion.VERSION_17
        sourceCompatibility = JavaVersion.VERSION_17
    }
}

val shadowBundle: Configuration by configurations.creating {
    isCanBeConsumed = false
    isCanBeResolved = true
}

tasks.shadowJar {
    configurations = listOf(shadowBundle)
    archiveClassifier = "dev-shadow"
}

tasks.remapJar {
    injectAccessWidener = true
    inputFile = tasks.shadowJar.get().archiveFile
    archiveClassifier = null
    dependsOn(tasks.shadowJar)
}

tasks.jar {
    archiveClassifier = "dev"
}

val buildAndCollect = tasks.register<Copy>("buildAndCollect") {
    group = "versioned"
    description = "Must run through 'chiseledBuild'"
    from(tasks.remapJar.get().archiveFile, tasks.remapSourcesJar.get().archiveFile)
    into(rootProject.layout.buildDirectory.file("libs/${mod.version}/$loader"))
    dependsOn("build")
}

if (stonecutter.current.isActive) {
    rootProject.tasks.register("buildActive") {
        group = "project"
        dependsOn(buildAndCollect)
    }

    rootProject.tasks.register("runActive") {
        group = "project"
        dependsOn(tasks.named("runClient"))
    }
}

tasks.processResources {
    val commonProperties = arrayOf(
        "id" to mod.id,
        "name" to mod.name,
        "version" to mod.version,
        "description" to mod.prop("description"),

        "architectury" to mod.dep("architectury_api"),
        "yacl" to mod.dep("yacl"),
    )
    properties(
        listOf("fabric.mod.json"),
        *commonProperties,
        "minecraft" to mod.prop("mc_dep_fabric"),
        "fabric_loader" to mod.dep("fabric_loader"),
        "modmenu" to mod.dep("modmenu_version")
    )
    properties(
        listOf("META-INF/mods.toml", "pack.mcmeta"),
        *commonProperties,
        "minecraft" to mod.prop("mc_dep_forgelike"),
        "forge" to mod.dep("forge_loader")
    )
    properties(
        listOf("META-INF/neoforge.mods.toml", "pack.mcmeta"),
        *commonProperties,
        "minecraft" to mod.prop("mc_dep_forgelike"),
        "neoforge" to mod.dep("neoforge_loader")
    )
}

tasks.build {
    group = "versioned"
    description = "Must run through 'chiseledBuild'"
}

stonecutter {
    constants {
        put("fabric", loom.platform.get() == ModPlatform.FABRIC)
        put("forge", loom.platform.get() == ModPlatform.FORGE)
        put("neoforge", loom.platform.get() == ModPlatform.NEOFORGE)
    }
}
