package com.example.data.model

enum class SeedArchetype(
    val title: String,
    val description: String,
    val targetBiome: String,
    val iconName: String,
    val recommendedEpoch: String
) {
    RANDOM_FORGE(
        title = "Random Forge Seed",
        description = "Unfiltered 64-bit Java Edition seed generated using pseudo-random cryptographic entropy.",
        targetBiome = "Varied / Random",
        iconName = "shuffle",
        recommendedEpoch = "All Versions"
    ),
    SURVIVAL_ISLAND(
        title = "Survival Island",
        description = "Spawns player on an isolated landmass surrounded by deep cold ocean with limited wood and lone shipwreck.",
        targetBiome = "Deep Ocean / Small Island",
        iconName = "sailing",
        recommendedEpoch = "1.18.x - 1.21.x"
    ),
    ALPINE_PEAKS(
        title = "Alpine Peaks & Glaciers",
        description = "Towering jagged peaks and frozen slopes reaching Y=250+ enclosing a sheltered valley lake.",
        targetBiome = "Jagged Peaks / Frozen Peaks",
        iconName = "terrain",
        recommendedEpoch = "1.18.x - 1.21.x"
    ),
    CHERRY_CALDERA(
        title = "Cherry Blossom Caldera",
        description = "Lush pink petal forest sitting atop a crater ring surrounded by snowy summits and sunken cave openings.",
        targetBiome = "Cherry Grove",
        iconName = "spa",
        recommendedEpoch = "1.20.x - 1.21.x"
    ),
    TRIAL_CHAMBER_SPAWN(
        title = "Trial Chamber Spawn",
        description = "1.21 Tricky Trials feature: sprawling copper and tuff brick Trial Chamber buried directly beneath spawn.",
        targetBiome = "Plains / Trial Underground",
        iconName = "key",
        recommendedEpoch = "1.21.x Only"
    ),
    ANCIENT_CITY_CALDERA(
        title = "Ancient City Under Spawn",
        description = "Deep Dark biome with Warden shrieker city ruins directly beneath spawn at Y=-40.",
        targetBiome = "Deep Dark / Mountain Hollow",
        iconName = "visibility_off",
        recommendedEpoch = "1.19.x - 1.21.x"
    ),
    LUSH_CAVE_MEGA(
        title = "Lush Cave Mega-Opening",
        description = "Massive yawning cavern filled with azalea trees, moss carpets, glowberries, and underground waterfalls.",
        targetBiome = "Lush Caves",
        iconName = "nature",
        recommendedEpoch = "1.18.x - 1.21.x"
    ),
    BADLANDS_CANYON(
        title = "Eroded Badlands Canyons",
        description = "Striated terracotta hoodoos, mesa plateaus, and gold-rich abandoned mineshafts exposed to open sky.",
        targetBiome = "Eroded Badlands",
        iconName = "landscape",
        recommendedEpoch = "1.18.x - 1.21.x"
    ),
    VILLAGE_CROSSROADS(
        title = "Spawn Village Metropolis",
        description = "Large multibiome village (plains, meadow or desert) with blacksmiths and iron golems right at player spawn.",
        targetBiome = "Plains / Meadow",
        iconName = "location_city",
        recommendedEpoch = "All Versions"
    ),
    WOODLAND_MANSION(
        title = "Woodland Mansion Spawn",
        description = "Extremely rare 3-story Dark Oak Woodland Mansion within 300 blocks of player world spawn.",
        targetBiome = "Dark Forest",
        iconName = "home_work",
        recommendedEpoch = "All Versions"
    ),
    MONUMENT_ARCHIPELAGO(
        title = "Ocean Monument Bay",
        description = "Prismarine Elder Guardian ocean monument clearly visible in shallow crystal waters off a tropical coast.",
        targetBiome = "Warm Ocean / Beach",
        iconName = "water",
        recommendedEpoch = "1.18.x - 1.21.x"
    ),
    MUSHROOM_ISLAND(
        title = "Mushroom Fields Island",
        description = "Rare safe haven biome completely isolated by deep ocean where no hostile monsters can spawn naturally.",
        targetBiome = "Mushroom Fields",
        iconName = "shield",
        recommendedEpoch = "All Versions"
    ),
    ICE_SPIKES_VALLEY(
        title = "Ice Spikes Wonderland",
        description = "Vast frozen valley packed with colossal spires of blue and packed ice towering into the sky.",
        targetBiome = "Ice Spikes",
        iconName = "ac_unit",
        recommendedEpoch = "1.18.x - 1.21.x"
    ),
    MANSION_VILLAGE_COMBO(
        title = "Mansion & Village Combo",
        description = "A thriving plains village situated directly across the river from an imposing Woodland Mansion.",
        targetBiome = "Dark Forest / Plains",
        iconName = "domain",
        recommendedEpoch = "1.18.x - 1.21.x"
    ),
    PILLAGER_OUTPOST_SIEGE(
        title = "Pillager Outpost at Spawn",
        description = "Cobblestone and dark oak watchtower towering over spawn, offering immediate challenge and ominous banner.",
        targetBiome = "Meadow / Plains",
        iconName = "fort",
        recommendedEpoch = "All Versions"
    ),
    JUNGLE_TEMPLE_COAST(
        title = "Jungle Temple Coast",
        description = "Ancient mossy cobblestone temple with redstone tripwires and loot chest on a lush tropical jungle shore.",
        targetBiome = "Jungle",
        iconName = "park",
        recommendedEpoch = "All Versions"
    ),
    DESERT_PYRAMID_OASIS(
        title = "Desert Pyramid & Oasis",
        description = "Sandstone pyramid with hidden TNT treasure vault nestled between rolling sand dunes and a desert well.",
        targetBiome = "Desert",
        iconName = "wb_sunny",
        recommendedEpoch = "All Versions"
    )
}
