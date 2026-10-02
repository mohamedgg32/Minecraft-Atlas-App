package com.example.data.local

import com.example.data.model.ModCategory
import com.example.data.model.ModEntity
import com.example.data.model.SeedEntity
import com.example.data.model.WorldConfigEntity

object DatabasePrepopulate {

    suspend fun populateDatabase(
        modDao: ModDao,
        configDao: WorldConfigDao,
        seedDao: SeedDao
    ) {
        if (modDao.getModCount() > 0) return

        val defaultMods = listOf(
            ModEntity(
                name = "Terralith",
                minecraftVersion = "26.2",
                loader = "Fabric",
                category = ModCategory.BIOME_MODS.displayName,
                description = "Massive vanilla-compatible overworld overhaul introducing 85+ new biomes, volcanic calderas, canyons, and alpine ranges without adding new blocks.",
                worldGenFeatures = "85+ procedural biomes, yellowstone thermal springs, painted canyons, elevated mountain plateaus, sunken caldera lakes",
                isPreinstalled = true
            ),
            ModEntity(
                name = "Tectonic",
                minecraftVersion = "26.2",
                loader = "Fabric",
                category = ModCategory.TERRAIN_GENERATION.displayName,
                description = "Overhauls terrain shape with colossal continental mountain ranges, smooth cliff gradients, deep ocean trenches, and realistic plate tectonics.",
                worldGenFeatures = "Continental tectonic plates, immense ridge lines reaching Y=310, sheer ocean cliffs, inland seas",
                isPreinstalled = true
            ),
            ModEntity(
                name = "Continents",
                minecraftVersion = "26.2",
                loader = "Fabric",
                category = ModCategory.TERRAIN_GENERATION.displayName,
                description = "Changes world generation to create vast, distinct continents separated by sprawling navigable oceans.",
                worldGenFeatures = "True continental landmasses, oceanic expanses, natural straits and island chains",
                isPreinstalled = true
            ),
            ModEntity(
                name = "Towns and Towers",
                minecraftVersion = "26.2",
                loader = "Fabric",
                category = ModCategory.STRUCTURES.displayName,
                description = "Expands village variety with dozens of biome-specific settlements, ocean pillager watchtowers, and mountain fortresses.",
                worldGenFeatures = "Biome-themed villages, defensive fortifications, harbor docks, coastal watchtowers",
                isPreinstalled = true
            ),
            ModEntity(
                name = "ChoiceTheorem's Overhauled Village",
                minecraftVersion = "26.2",
                loader = "Fabric",
                category = ModCategory.VILLAGES.displayName,
                description = "Completely overhauls vanilla villages with multi-level street architecture, windmills, town halls, and stone guard walls.",
                worldGenFeatures = "Multi-tiered hillside villages, custom blacksmiths, village bell plazas, perimeter palisades",
                isPreinstalled = true
            ),
            ModEntity(
                name = "Incendium",
                minecraftVersion = "26.2",
                loader = "Fabric",
                category = ModCategory.NETHER_GENERATION.displayName,
                description = "Transforms the Nether into an epic, perilous volcanic realm with 8 new fiery biomes, jagged obsidian towers, and the Sanctum structure.",
                worldGenFeatures = "8 volcanic Nether biomes, ash barrens, nether reactor ruins, inverted spire fortresses",
                isPreinstalled = true
            ),
            ModEntity(
                name = "BetterNether",
                minecraftVersion = "26.2",
                loader = "Fabric",
                category = ModCategory.NETHER_GENERATION.displayName,
                description = "Breathes organic life into the Nether with glowing mushroom canopies, volumetric flora, gravestones, and sprawling cities.",
                worldGenFeatures = "Bioluminescent nether canopies, nether cities, bone reefs, magmatic geysers",
                isPreinstalled = true
            ),
            ModEntity(
                name = "Nullscape",
                minecraftVersion = "26.2",
                loader = "Fabric",
                category = ModCategory.END_GENERATION.displayName,
                description = "Reimagines The End dimension with soaring void spires, crystalline ridges, and 384-block amplified floating islands.",
                worldGenFeatures = "Amplified End terrain, floating crystal spikes, void chasms, chorus tree valleys",
                isPreinstalled = true
            ),
            ModEntity(
                name = "Geophilic",
                minecraftVersion = "26.2",
                loader = "Fabric",
                category = ModCategory.BIOME_MODS.displayName,
                description = "Subtle vanilla enhancement that adds fallen logs, boulders, moss, and micro-vegetation to standard biomes.",
                worldGenFeatures = "Vanilla-style micro terrain variations, fallen tree logs, mossy stone clusters",
                isPreinstalled = true
            ),
            ModEntity(
                name = "William Wythers' Overhauled Overworld",
                minecraftVersion = "1.21.1",
                loader = "Fabric",
                category = ModCategory.BIOME_MODS.displayName,
                description = "Realistic climate and biome overhaul generating vast forests, steppe, and realistic temperate zones.",
                worldGenFeatures = "Realistic ecosphere distribution, temperate rainforests, boreal plateaus",
                isPreinstalled = true
            ),
            ModEntity(
                name = "YUNG's Better Caves (Fabric)",
                minecraftVersion = "1.21.1",
                loader = "Fabric",
                category = ModCategory.CAVES.displayName,
                description = "Redesigns subterranean Minecraft with deep winding ravines, flooded cave systems, and surface entrances.",
                worldGenFeatures = "Winding cavern corridors, lava catacombs, subterranean waterfalls, surface sinkholes",
                isPreinstalled = true
            ),
            ModEntity(
                name = "YUNG's Better Strongholds (Fabric)",
                minecraftVersion = "1.21.1",
                loader = "Fabric",
                category = ModCategory.STRUCTURES.displayName,
                description = "Complete reimagination of the End Stronghold into a sprawling subterranean citadel with grand libraries and crypts.",
                worldGenFeatures = "Subterranean fortress halls, armories, grand portal sanctum",
                isPreinstalled = true
            )
        )

        modDao.insertMods(defaultMods)

        val defaultConfigs = listOf(
            WorldConfigEntity(
                name = "Vanilla Java 26.2 (Latest)",
                minecraftVersion = "26.2",
                loader = "Fabric",
                modIds = "",
                description = "Pure Minecraft Java Edition 26.2 generation. Includes latest multi-noise distributions, Trial Chambers, Cherry Groves, and Deep Dark.",
                isDefault = true
            ),
            WorldConfigEntity(
                name = "Terralith World Overhaul (Fabric 26.2)",
                minecraftVersion = "26.2",
                loader = "Fabric",
                modIds = "1,3", // Terralith, Continents
                description = "Rich procedural world with 85+ biomes, volcanic calderas, and natural continents on Fabric 26.2.",
                isDefault = false
            ),
            WorldConfigEntity(
                name = "Tectonic Highlands (Fabric 26.2)",
                minecraftVersion = "26.2",
                loader = "Fabric",
                modIds = "2,4", // Tectonic, Towns and Towers
                description = "Immense continental ridges up to Y=310, deep trenches, and overhauled settlements on Fabric 26.2.",
                isDefault = false
            ),
            WorldConfigEntity(
                name = "Dimensions Trilogy (Fabric 26.2)",
                minecraftVersion = "26.2",
                loader = "Fabric",
                modIds = "1,6,8", // Terralith, Incendium, Nullscape
                description = "Full trilogy overhaul: Overworld (Terralith), Nether (Incendium), and End (Nullscape) on Fabric 26.2.",
                isDefault = false
            )
        )

        configDao.insertConfigs(defaultConfigs)

        // Initial seeds with Minecraft Java 26.2 and Fabric configurations
        val initialSeeds = listOf(
            SeedEntity(
                seedValue = -4270425838048259167L,
                seedText = "-4270425838048259167",
                title = "Trial Chamber & Cherry Basin",
                targetVersion = "26.2",
                loader = "Fabric",
                configName = "Vanilla Java 26.2 (Latest)",
                category = "Trial Chamber Spawn",
                primaryBiome = "Cherry Grove & Meadow",
                spawnCoordinates = "X: 48, Y: 122, Z: -16",
                poiCoordinatesJson = "[{\"name\":\"Trial Chamber\",\"x\":64,\"y\":12,\"z\":-32,\"dimension\":\"Overworld\"},{\"name\":\"Plains Village\",\"x\":-120,\"y\":110,\"z\":80,\"dimension\":\"Overworld\"},{\"name\":\"Ancient City\",\"x\":112,\"y\":-42,\"z\":240,\"dimension\":\"Overworld\"},{\"name\":\"Stronghold\",\"x\":1440,\"y\":-16,\"z\":-880,\"dimension\":\"Overworld\"}]",
                verifiedVersions = "26.2, 1.21.x",
                notes = "Spawn right beside a scenic pink petal cherry crater rim with immediate access to a subterranean Trial Chamber.",
                selectedMods = "",
                isFavorite = true,
                tags = "26.2,Cherry,TrialChamber,Village,Spawn"
            ),
            SeedEntity(
                seedValue = 8624896L,
                seedText = "8624896",
                title = "Warden's Abyss Caldera",
                targetVersion = "26.2",
                loader = "Fabric",
                configName = "Vanilla Java 26.2 (Latest)",
                category = "Ancient City Under Spawn",
                primaryBiome = "Jagged Peaks & Deep Dark",
                spawnCoordinates = "X: 0, Y: 184, Z: 0",
                poiCoordinatesJson = "[{\"name\":\"Ancient City Center\",\"x\":16,\"y\":-40,\"z\":32,\"dimension\":\"Overworld\"},{\"name\":\"Caldera Lake\",\"x\":-48,\"y\":160,\"z\":-24,\"dimension\":\"Overworld\"},{\"name\":\"Stronghold\",\"x\":1440,\"y\":-16,\"z\":-880,\"dimension\":\"Overworld\"}]",
                verifiedVersions = "26.2, 1.21.x, 1.20.x",
                notes = "Spawn right at the edge of a snow-crested mountain ring encircling a hollow crater. Deep Dark city sits directly beneath.",
                selectedMods = "",
                isFavorite = true,
                tags = "AncientCity,Caldera,Mountains,26.2"
            ),
            SeedEntity(
                seedValue = -7281928471928471928L,
                seedText = "FabricExpedition2026",
                title = "Terralith Alpine Sanctuary",
                targetVersion = "26.2",
                loader = "Fabric",
                configName = "Terralith World Overhaul (Fabric 26.2)",
                category = "Fantasy terrain",
                primaryBiome = "Terralith Painted Mountain",
                spawnCoordinates = "X: 112, Y: 144, Z: -64",
                poiCoordinatesJson = "[{\"name\":\"Thermal Basin\",\"x\":160,\"y\":112,\"z\":-110,\"dimension\":\"Overworld\"},{\"name\":\"Overhauled Mountain Town\",\"x\":32,\"y\":130,\"z\":180,\"dimension\":\"Overworld\"},{\"name\":\"Trial Chamber\",\"x\":96,\"y\":16,\"z\":-128,\"dimension\":\"Overworld\"}]",
                verifiedVersions = "26.2",
                notes = "Active Fabric mod: Terralith on Minecraft 26.2. Spectacular geothermal vents, custom calderas, and terraced waterfalls.",
                selectedMods = "Terralith",
                isFavorite = true,
                tags = "Modded,Terralith,Fabric,26.2"
            ),
            SeedEntity(
                seedValue = 5485490214872951L,
                seedText = "5485490214872951",
                title = "Lonely Sentinel Island",
                targetVersion = "26.2",
                loader = "Fabric",
                configName = "Vanilla Java 26.2 (Latest)",
                category = "Survival Island",
                primaryBiome = "Deep Ocean Island",
                spawnCoordinates = "X: -32, Y: 68, Z: 110",
                poiCoordinatesJson = "[{\"name\":\"Lone Oak & Shipwreck\",\"x\":-24,\"y\":64,\"z\":128,\"dimension\":\"Overworld\"},{\"name\":\"Ocean Monument\",\"x\":176,\"y\":48,\"z\":-144,\"dimension\":\"Overworld\"},{\"name\":\"Buried Treasure\",\"x\":-8,\"y\":58,\"z\":96,\"dimension\":\"Overworld\"}]",
                verifiedVersions = "26.2, 1.21.x, 1.20.x, 1.18.x",
                notes = "Classic survival island with a lone oak tree, beach shipwreck with treasure map, and nearby Ocean Monument.",
                selectedMods = "",
                isFavorite = false,
                tags = "SurvivalIsland,Hardcore,Ocean,26.2"
            ),
            SeedEntity(
                seedValue = 407038371475L,
                seedText = "407038371475",
                title = "Golden Badlands Citadel",
                targetVersion = "26.2",
                loader = "Fabric",
                configName = "Vanilla Java 26.2 (Latest)",
                category = "Eroded Badlands Canyons",
                primaryBiome = "Eroded Badlands & Desert",
                spawnCoordinates = "X: 64, Y: 82, Z: -128",
                poiCoordinatesJson = "[{\"name\":\"Desert Village\",\"x\":120,\"y\":70,\"z\":-200,\"dimension\":\"Overworld\"},{\"name\":\"Desert Pyramid\",\"x\":-180,\"y\":68,\"z\":-320,\"dimension\":\"Overworld\"},{\"name\":\"Exposed Gold Mineshaft\",\"x\":32,\"y\":64,\"z\":-96,\"dimension\":\"Overworld\"}]",
                verifiedVersions = "26.2, 1.21.x, 1.20.x",
                notes = "Vast multi-colored terracotta badlands with exposed gold-vein mineshafts running across open canyon chasms.",
                selectedMods = "",
                isFavorite = false,
                tags = "Badlands,Mineshaft,Desert,26.2"
            )
        )

        seedDao.insertSeeds(initialSeeds)
    }
}
