import net.ornithemc.ploceus.api.PloceusGradleExtensionApi

plugins {
    id("dev.kikugie.loom-back-compat")
    id("net.fabricmc.fabric-loom-remap") version "1.17-SNAPSHOT" apply false
    id("ploceus") version "1.17.7" apply false
    id("dev.deftu.gradle.bloom") version "0.2.0"
    id("me.modmuss50.mod-publish-plugin") version "2.2.0"
}

val isOrnithe = stonecutter.current.version == "1.8.9"
val ploceus = if (isOrnithe) {
    pluginManager.apply("net.fabricmc.fabric-loom-remap")
    pluginManager.apply("ploceus")

    configurations.configureEach {
        exclude(group = "org.lwjgl.lwjgl")
    }

    extensions.getByType<PloceusGradleExtensionApi>().apply {
        setIntermediaryGeneration(2)
    }
} else {
    null
}

val modid: String = sc.properties["mod.id"]
val modname: String = sc.properties["mod.name"]
val modversion: String = sc.properties["mod.version"]
val mcversion: String = sc.current.version
val versionrange: String = sc.properties["mod.mc_compat"]
val loaderversion: String = sc.properties["deps.fabric_loader"]
val oneconfigversion: String = sc.properties["deps.oneconfig"]
val loader = if (isOrnithe) "ornithe" else "fabric"

val isUnobfuscated = sc.current.parsed >= "26"

val oneconfigModules = listOf(
    "commands", "config", "config-impl", "events", "internal", "ui", "utils", "hud"
)

sourceSets.named("main") {
    if (isOrnithe) java.exclude("org/polyfrost/fullbright/mixins/*.java")
    else java.exclude("org/polyfrost/fullbright/legacy/**", "org/polyfrost/fullbright/mixins/legacy/**")
}

version = "$modversion+$mcversion"
base.archivesName = modid

val requiredJava: JavaVersion = when {
    sc.current.parsed >= "26.1" -> JavaVersion.VERSION_25
    sc.current.parsed >= "1.20.5" -> JavaVersion.VERSION_21
    sc.current.parsed >= "1.18" -> JavaVersion.VERSION_17
    sc.current.parsed >= "1.17" -> JavaVersion.VERSION_16
    else -> JavaVersion.VERSION_25
}

val compatibleVersions: List<String> = sc.properties.rawOrNull("mod", "mc_releases")
    ?.asList().orEmpty().map { it.toString() }

repositories {
    fun strictMaven(url: String, alias: String, vararg groups: String) = exclusiveContent {
        forRepository { maven(url) { name = alias } }
        filter { groups.forEach(::includeGroup) }
    }

    mavenCentral()
    google()
    maven("https://maven.ornithemc.net/releases")
    maven("https://repo.polyfrost.org/releases") { name = "Polyfrost Releases" }
    maven("https://repo.polyfrost.org/snapshots") { name = "Polyfrost Snapshots" }
    maven("https://central.sonatype.com/repository/maven-snapshots") {
        name = "Sonatype Snapshots"
        content { includeGroup("net.kyori") }
    }
    maven("https://maven.cloverclient.com/releases") {
        content { includeGroup("pl.tomgirl") }
    }
    maven("https://maven.deftu.dev/releases") {
        name = "Deftu Releases"
        content { includeGroupAndSubgroups("dev.deftu") }
    }
    strictMaven("https://maven.parchmentmc.org", "ParchmentMC", "org.parchmentmc.data")
    strictMaven("https://maven.gegy.dev/releases", "Gegy", "dev.lambdaurora")
    strictMaven("https://maven.gnomecraft.net/releases", "GnomeCraft", "com.terraformersmc")
    strictMaven("https://maven.fabricmc.net/", "FabricMC", "net.fabricmc")
    strictMaven("https://www.cursemaven.com", "CurseForge", "curse.maven")
    strictMaven("https://api.modrinth.com/maven", "Modrinth", "maven.modrinth")
}

dependencies {
    minecraft("com.mojang:minecraft:$mcversion")
    if (isOrnithe) {
        mappings(ploceus!!.featherMappings(sc.properties["feather_build"]))
        ploceus.dependOsl(sc.properties.get<String>("deps.osl"))
    } else if (isUnobfuscated) {
        loomx.applyMojangMappings()
    } else {
        @Suppress("UnstableApiUsage")
        mappings(loom.layered {
            officialMojangMappings()
            sc.properties.getOrNull<String>("deps.parchment")?.let {
                parchment("org.parchmentmc.data:parchment-$mcversion:$it@zip")
            }
            sc.properties.getOrNull<String>("deps.yalmm")?.let {
                mappings("dev.lambdaurora:yalmm-mojbackward:$mcversion+build.$it")
            }
        })
    }

    modImplementation("net.fabricmc:fabric-loader:$loaderversion")
    modImplementation("org.polyfrost.oneconfig:$mcversion-$loader:$oneconfigversion")
    for (module in oneconfigModules) {
        modImplementation("org.polyfrost.oneconfig:$module:$oneconfigversion")
    }
}

loom {
    fabricModJsonPath = rootProject.file("src/main/resources/fabric.mod.json")

    decompilerOptions.named("vineflower") {
        options.put("mark-corresponding-synthetics", "1")
    }

    runConfigs.all {
        preferGradleTask = true
        generateRunConfig = stonecutter.current.isActive
        runDirectory = rootProject.file("run")
        jvmArguments.add("-Dmixin.debug.export=true")
    }

    runConfigs.remove(runConfigs["server"])
}

java {
    withSourcesJar()
    targetCompatibility = requiredJava
    sourceCompatibility = requiredJava

    toolchain {
        vendor = JvmVendorSpec.ADOPTIUM
        languageVersion = JavaLanguageVersion.of(requiredJava.majorVersion)
    }
}

tasks.withType<JavaCompile>().configureEach {
    options.release = requiredJava.majorVersion.toInt()
}

bloom {
    replacement("@MOD_ID@", modid)
    replacement("@MOD_NAME@", modname)
    replacement("@MOD_VERSION@", modversion)
}

val mixinConfig = if (isOrnithe) "mixins.$modid.legacy.json" else "mixins.$modid.json"
val unusedMixinConfig = if (isOrnithe) "mixins.$modid.json" else "mixins.$modid.legacy.json"

tasks {
    processResources {
        val props = mapOf(
            "mod_id" to modid,
            "mod_name" to modname,
            "mod_version" to modversion,
            "minecraft_version_range" to versionrange,
            "loader_version" to loaderversion,
            "mixin_config" to mixinConfig
        )

        inputs.properties(props)

        filesMatching("fabric.mod.json") { expand(props) }

        exclude(unusedMixinConfig)
    }

    jar {
        inputs.property("archivesName", base.archivesName)

        from(rootProject.file("LICENSE")) {
            rename { "${it}_${inputs.properties["archivesName"]}" }
        }
    }

    register<Copy>("buildAndCollect") {
        group = "build"
        description = "Builds mod jars and copies results to `build/libs/{mod version}/`"

        inputs.property("version", modversion)
        from(loomx.modJar.flatMap { it.archiveFile }, loomx.modSourcesJar.flatMap { it.archiveFile })
        into(rootProject.layout.buildDirectory.file("libs/$modversion"))
    }
}

val modrinthId = listOf("oneconfig.publish.modrinth", "publish.modrinth")
    .firstNotNullOfOrNull { findProperty(it) }?.toString()?.takeIf { it.isNotBlank() }
val modrinthToken = listOf("oneconfig.publish.modrinth.token", "publish.modrinth.token", "modrinth.token")
    .firstNotNullOfOrNull { findProperty(it) }?.toString()?.takeIf { it.isNotBlank() }

val changelogs = rootProject.file("CHANGELOG.md").takeIf { it.exists() }?.readText() ?: "No changelog provided."

val validateChangelog = tasks.register("validateChangelog") {
    description = "Validates that the changelog is written for the current version."
    if (!changelogs.contains(modversion)) {
        throw GradleException("Changelog for version $modversion not found.")
    }
}

tasks.publishMods.configure {
    dependsOn(validateChangelog)
}
tasks.matching { it.name == "publishModrinth" }.configureEach {
    dependsOn(validateChangelog)
}

publishMods {
    file = loomx.modJar.flatMap { it.archiveFile }

    displayName = modversion
    version = "v$modversion"
    changelog = changelogs
    type = STABLE

    modLoaders.add(loader)

    dryRun = modrinthId == null || modrinthToken == null

    if (modrinthId != null) {
        modrinth {
            projectId = modrinthId
            accessToken = modrinthToken.orEmpty()

            minecraftVersions.addAll(compatibleVersions.ifEmpty { listOf(mcversion) })

            requires("oneconfig")
            requires("fabric-language-kotlin")
        }
    }
}
