plugins {
    id("com.refinedmods.refinedarchitect.root")
    id("com.refinedmods.refinedarchitect.neoforge")
}

repositories {
    maven {
        name = "Refined Storage"
        url = uri("https://maven.creeperhost.net")
        content {
            includeGroup("com.refinedmods.refinedstorage")
        }
    }
    maven {
        name = "Modmaven"
        url = uri("https://modmaven.dev/")
        content {
            includeGroup("mekanism")
        }
    }
    maven {
        name = "JEI"
        url = uri("https://maven.blamejared.com/")
    }
    maven {
        name = "EMI"
        url = uri("https://maven.terraformersmc.com/")
    }
}

refinedarchitect {
    modId = "refinedstorage_mekanism_integration"
    neoForge()
    sonarQube("refinedmods_refinedstorage-mekanism-integration", "refinedmods")
    publishing {
        maven = true
        curseForge = "1230504"
        curseForgeRequiredDependencies = listOf("refined-storage", "mekanism")
        modrinth = "QHMDf0Sd"
        modrinthRequiredDependencies = listOf("refined-storage", "mekanism")
    }
}

group = "com.refinedmods.refinedstorage"

base {
    archivesName.set("refinedstorage-mekanism-integration")
}

val refinedstorageVersion: String by project
val refinedstorageJeiIntegrationVersion: String by project
val minecraftVersion: String by project
val mekanismVersion: String by project
val jeiVersion: String by project
val emiVersion: String by project

dependencies {
    api("com.refinedmods.refinedstorage:refinedstorage-neoforge:${refinedstorageVersion}")
    // runtimeOnly("com.refinedmods.refinedstorage:refinedstorage-jei-integration-neoforge:${refinedstorageJeiIntegrationVersion}")
    // runtimeOnly("com.refinedmods.refinedstorage:refinedstorage-emi-integration-neoforge:0.5.0")
    compileOnlyApi("mekanism:Mekanism:${minecraftVersion}-${mekanismVersion}:api")
    runtimeOnly("mekanism:Mekanism:${minecraftVersion}-${mekanismVersion}:all") {
        exclude(group = "com.blamejared.crafttweaker")
    }
    runtimeOnly("mezz.jei:jei-${minecraftVersion}-neoforge:${jeiVersion}")
    compileOnlyApi("mezz.jei:jei-${minecraftVersion}-common-api:${jeiVersion}")
    testCompileOnly("mezz.jei:jei-${minecraftVersion}-common:${jeiVersion}")
    compileOnlyApi("mezz.jei:jei-${minecraftVersion}-neoforge-api:${jeiVersion}")
    // runtimeOnly("dev.emi:emi-neoforge:${emiVersion}")
    compileOnlyApi("dev.emi:emi-neoforge:${emiVersion}")
}

