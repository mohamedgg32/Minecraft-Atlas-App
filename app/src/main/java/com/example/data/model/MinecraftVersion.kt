package com.example.data.model

data class MinecraftVersion(
    val id: String,
    val displayLabel: String,
    val epoch: GenerationEpoch,
    val description: String,
    val keyFeatures: List<String>,
    val defaultSubversion: String
) {
    companion object {
        const val LATEST_ID = "26.2"

        val ALL_VERSIONS = listOf(
            MinecraftVersion(
                id = "26.2",
                displayLabel = "Minecraft Java 26.2 — Latest",
                epoch = GenerationEpoch.JAVA_26_2,
                description = "Latest Java 26.2 generation release with refined continental multi-noise blending, updated structure grid placements, and next-gen Fabric worldgen hooks.",
                keyFeatures = listOf("26.2 Multi-Noise", "Updated Structure Grids", "Trial Chambers & Vaults", "Cherry Groves", "Deep Dark", "Y: -64 to 320"),
                defaultSubversion = "26.2"
            ),
            MinecraftVersion(
                id = "1.21.x",
                displayLabel = "Minecraft 1.21.x",
                epoch = GenerationEpoch.MODERN_TRIALS,
                description = "Tricky Trials update. Introduces underground Trial Chambers, Breeze spawners, Crafters, and Ominous Vaults.",
                keyFeatures = listOf("Trial Chambers", "Ominous Vaults", "Cherry Groves", "Deep Dark", "Y: -64 to 320"),
                defaultSubversion = "1.21.1"
            ),
            MinecraftVersion(
                id = "1.20.x",
                displayLabel = "Minecraft 1.20.x",
                epoch = GenerationEpoch.MODERN_TRAILS,
                description = "Trails & Tales update. Adds Cherry Grove mountain biomes, Trail Ruins structures, and Archeology dig sites.",
                keyFeatures = listOf("Cherry Blossom Groves", "Trail Ruins", "Sniffer eggs", "Deep Dark", "Y: -64 to 320"),
                defaultSubversion = "1.20.1"
            ),
            MinecraftVersion(
                id = "1.19.x",
                displayLabel = "Minecraft 1.19.x",
                epoch = GenerationEpoch.MODERN_WILD,
                description = "The Wild Update. Adds subterranean Ancient Cities in Deep Dark biomes, Mangrove Swamps, and Mud plains.",
                keyFeatures = listOf("Ancient Cities", "Mangrove Swamps", "Deep Dark biome", "Y: -64 to 320"),
                defaultSubversion = "1.19.2"
            ),
            MinecraftVersion(
                id = "1.18.x",
                displayLabel = "Minecraft 1.18.x",
                epoch = GenerationEpoch.CAVES_AND_CLIFFS_OVERHAUL,
                description = "Caves & Cliffs Part II. Fundamental 3D noise generation overhaul. Massive mountains, lush caves, dripstone caves, and world height expansion to -64..320.",
                keyFeatures = listOf("3D Noise Terrain", "Massive Mountain Peaks", "Lush & Dripstone Caves", "Y: -64 to 320"),
                defaultSubversion = "1.18.2"
            ),
            MinecraftVersion(
                id = "1.17.x",
                displayLabel = "Minecraft 1.17.x",
                epoch = GenerationEpoch.LEGACY_HEIGHT,
                description = "Caves & Cliffs Part I. Pre-worldgen overhaul. World height 0..256. Amethyst geodes and copper ore present, but legacy terrain noise.",
                keyFeatures = listOf("Legacy terrain noise", "Amethyst Geodes", "World height 0 to 256"),
                defaultSubversion = "1.17.1"
            ),
            MinecraftVersion(
                id = "1.16.x",
                displayLabel = "Minecraft 1.16.x",
                epoch = GenerationEpoch.LEGACY_NETHER_UPDATE,
                description = "Nether Update. 4 new Nether biomes (Soul Sand Valley, Warped Forest, Crimson Forest, Basalt Deltas) and Bastion Remnants.",
                keyFeatures = listOf("Nether Biomes", "Bastions", "Legacy Overworld 0..256"),
                defaultSubversion = "1.16.5"
            ),
            MinecraftVersion(
                id = "1.12.2",
                displayLabel = "Minecraft 1.12.2",
                epoch = GenerationEpoch.LEGACY_ERA,
                description = "The classic modding golden age. Monolithic 2D biome generation with legacy temperature maps.",
                keyFeatures = listOf("Classic 2D biome generation", "World height 0..256", "Vast Fabric legacy mods"),
                defaultSubversion = "1.12.2"
            ),
            MinecraftVersion(
                id = "Older",
                displayLabel = "Older (Beta / Alpha)",
                epoch = GenerationEpoch.ANCIENT_BETA,
                description = "Historical Java world generators (Beta 1.7.3, Alpha, Infdev). Distinctive retro terrain algorithms.",
                keyFeatures = listOf("Beta 1.7.3 height maps", "Retro cave spaghetti", "Historical seeds"),
                defaultSubversion = "Beta 1.7.3"
            )
        )

        fun findById(id: String): MinecraftVersion {
            return ALL_VERSIONS.firstOrNull { it.id == id } ?: ALL_VERSIONS.first()
        }
    }
}

enum class GenerationEpoch {
    JAVA_26_2,                   // 26.2: Latest generation algorithms and structure grids
    MODERN_TRIALS,               // 1.21.x: Caveats for 1.18-1.20 (Chambers only in 1.21)
    MODERN_TRAILS,               // 1.20.x: Cherry Groves & Trail Ruins
    MODERN_WILD,                 // 1.19.x: Ancient Cities & Mangroves
    CAVES_AND_CLIFFS_OVERHAUL,   // 1.18.x: Shared base 3D noise terrain with 1.18-1.21
    LEGACY_HEIGHT,               // 1.17.x: Incompatible with 1.18+
    LEGACY_NETHER_UPDATE,        // 1.16.x: Incompatible with 1.18+
    LEGACY_ERA,                  // 1.12.2: Incompatible with modern
    ANCIENT_BETA                 // Beta/Alpha: Completely different math
}
