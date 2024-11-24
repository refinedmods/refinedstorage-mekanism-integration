plugins {
    id("refinedarchitect.root")
    id("refinedarchitect.neoforge")
}

repositories {
    maven {
        url = uri("https://maven.pkg.github.com/refinedmods/refinedstorage2")
        credentials {
            username = "anything"
            password = "\u0067hp_oGjcDFCn8jeTzIj4Ke9pLoEVtpnZMP4VQgaX"
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
}

refinedarchitect {
    modId = "refinedstorage_mekanism_integration"
    neoForge()
    sonarQube("refinedmods_refinedstorage-mekanism-integration", "refinedmods")
    publishing {
        maven = true
    }
}

group = "com.refinedmods.refinedstorage"

base {
    archivesName.set("refinedstorage-mekanism-integration")
}

val refinedstorageVersion: String by project
val minecraftVersion: String by project
val mekanismVersion: String by project
val jeiVersion: String by project

dependencies {
    api("com.refinedmods.refinedstorage:refinedstorage-neoforge:${refinedstorageVersion}")
    compileOnlyApi("mekanism:Mekanism:${minecraftVersion}-${mekanismVersion}:api")
    runtimeOnly("mekanism:Mekanism:${minecraftVersion}-${mekanismVersion}:all") {
        exclude(group = "com.blamejared.crafttweaker")
    }
    runtimeOnly("mezz.jei:jei-${minecraftVersion}-neoforge:${jeiVersion}")
    compileOnlyApi("mezz.jei:jei-${minecraftVersion}-common-api:${jeiVersion}")
    testCompileOnly("mezz.jei:jei-${minecraftVersion}-common:${jeiVersion}")
    compileOnlyApi("mezz.jei:jei-${minecraftVersion}-neoforge-api:${jeiVersion}")
}

