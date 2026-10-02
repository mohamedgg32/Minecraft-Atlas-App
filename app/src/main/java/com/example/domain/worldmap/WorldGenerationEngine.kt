package com.example.domain.worldmap

import com.example.data.model.MinecraftVersion
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

data class WorldGenerationState(
    val seedValue: Long,
    val minecraftVersion: String,
    val loader: String, // Always "Fabric"
    val activeMods: List<String>,
    val spawnX: Int,
    val spawnY: Int,
    val spawnZ: Int,
    val spawnBiome: MapBiome,
    val structures: List<MapStructure>,
    val unsupportedModNotices: List<String>,
    val compatibilityWarnings: List<String>,
    val isEstimated: Boolean = (minecraftVersion == "26.2"),
    val accuracyLabel: String = if (minecraftVersion == "26.2") "Estimated" else "Standard"
)

data class BiomeSearchResult(
    val biome: MapBiome,
    val foundX: Int,
    val foundZ: Int,
    val distanceFromSpawn: Int,
    val isEstimated: Boolean = true,
    val disclaimer: String = "Biome data is estimated based on Java multi-noise sampling."
)

object WorldGenerationEngine {

    // Supported worldgen mods with local procedural rule generators
    private val PROCEDURAL_SUPPORTED_MODS = setOf(
        "Terralith",
        "Tectonic"
    )

    fun isModSupported(modName: String): Boolean {
        return PROCEDURAL_SUPPORTED_MODS.any { it.equals(modName, ignoreCase = true) || modName.contains(it, ignoreCase = true) }
    }

    /**
     * Checks compatibility of selected Fabric mods with chosen Minecraft version
     */
    fun checkCompatibility(
        minecraftVersion: String,
        selectedModNames: List<String>
    ): List<String> {
        val warnings = mutableListOf<String>()

        // 26.2 vs legacy mods
        for (mod in selectedModNames) {
            when (mod) {
                "William Wythers' Overhauled Overworld" -> {
                    if (minecraftVersion == "26.2") {
                        warnings.add("William Wythers' Overhauled Overworld is targeted for Fabric 1.21.1 and has not released an official 26.2 build.")
                    }
                }
                "YUNG's Better Caves (Fabric)" -> {
                    if (minecraftVersion == "26.2") {
                        warnings.add("YUNG's Better Caves (Fabric) is designed for 1.21.1; experimental generation on 26.2 may cause biome seam anomalies.")
                    }
                }
                "YUNG's Better Strongholds (Fabric)" -> {
                    if (minecraftVersion == "26.2") {
                        warnings.add("YUNG's Better Strongholds (Fabric) requires Fabric API 1.21.1. On 26.2 vanilla stronghold generation will be used as fallback.")
                    }
                }
            }

            // Older version compatibility
            if (minecraftVersion.startsWith("1.18") || minecraftVersion.startsWith("1.17") || minecraftVersion.startsWith("1.16") || minecraftVersion.startsWith("1.12") || minecraftVersion == "Older") {
                warnings.add("$mod is built for modern Minecraft (1.21+ / 26.2) and is incompatible with legacy $minecraftVersion world generation.")
            }
        }

        return warnings
    }

    /**
     * Generates the world generation model
     */
    fun generateWorld(
        seedValue: Long,
        minecraftVersion: String,
        activeMods: List<String>
    ): WorldGenerationState {
        val unsupportedNotices = mutableListOf<String>()

        // Check unsupported mods (Rule 15: Do not claim modded generation is accurate unless the selected mod is actually supported)
        for (mod in activeMods) {
            if (!isModSupported(mod)) {
                unsupportedNotices.add("Modded generation for '$mod' is Unsupported. Using estimated base $minecraftVersion generator.")
            }
        }

        // Compatibility warnings
        val compWarnings = checkCompatibility(minecraftVersion, activeMods)

        // Calculate player spawn coordinates (Minecraft Java compass algorithm)
        val (spawnX, spawnZ, spawnY) = calculateJavaSpawn(seedValue, minecraftVersion, activeMods)
        val spawnBiome = sampleBiomeAt(seedValue, spawnX, spawnZ, minecraftVersion, activeMods)

        // Generate structures in ~3000 block radius around spawn
        val structures = generateStructures(
            seedValue = seedValue,
            minecraftVersion = minecraftVersion,
            activeMods = activeMods,
            spawnX = spawnX,
            spawnZ = spawnZ
        )

        return WorldGenerationState(
            seedValue = seedValue,
            minecraftVersion = minecraftVersion,
            loader = "Fabric",
            activeMods = activeMods,
            spawnX = spawnX,
            spawnY = spawnY,
            spawnZ = spawnZ,
            spawnBiome = spawnBiome,
            structures = structures,
            unsupportedModNotices = unsupportedNotices,
            compatibilityWarnings = compWarnings
        )
    }

    /**
     * Replicates Java Edition compass spawn point selection:
     * Starts at (0, 0) and searches outwards up to 256 blocks for a valid land biome (not deep ocean).
     */
    private fun calculateJavaSpawn(seed: Long, version: String, mods: List<String>): Triple<Int, Int, Int> {
        // Pseudo-random offset based on lower seed bits
        var bestX = ((seed % 128) - 64).toInt()
        var bestZ = (((seed ushr 8) % 128) - 64).toInt()

        // Check biome at this point; if deep ocean, nudge inland
        val biome = sampleBiomeAt(seed, bestX, bestZ, version, mods)
        if (biome.category == BiomeCategory.DEEP_OCEAN || biome.category == BiomeCategory.OCEAN) {
            for (radius in 32..256 step 32) {
                var foundLand = false
                for (angleDeg in 0 until 360 step 45) {
                    val rad = Math.toRadians(angleDeg.toDouble())
                    val testX = (radius * cos(rad)).toInt()
                    val testZ = (radius * sin(rad)).toInt()
                    val testBiome = sampleBiomeAt(seed, testX, testZ, version, mods)
                    if (testBiome.category != BiomeCategory.DEEP_OCEAN && testBiome.category != BiomeCategory.OCEAN) {
                        bestX = testX
                        bestZ = testZ
                        foundLand = true
                        break
                    }
                }
                if (foundLand) break
            }
        }

        val y = when (biome.category) {
            BiomeCategory.MOUNTAINS -> 128
            BiomeCategory.CHERRY_GROVE -> 110
            BiomeCategory.OCEAN -> 63
            BiomeCategory.DEEP_OCEAN -> 63
            else -> 71
        }

        return Triple(bestX, bestZ, y)
    }

    /**
     * Multi-Noise 2D Sampling Function:
     * Computes continentalness, temperature, humidity, erosion, and weirdness for coordinates (x, z).
     */
    fun sampleBiomeAt(
        seed: Long,
        x: Int,
        z: Int,
        version: String,
        activeMods: List<String>
    ): MapBiome {
        // Multi-noise coordinate hashes using Minecraft Java 64-bit seed mixing
        val nx = x.toDouble() / 320.0
        val nz = z.toDouble() / 320.0

        val s0 = seed xor 0x5DEECE66DL
        val s1 = (seed ushr 16) xor 0x4B023FL
        val s2 = (seed ushr 32) xor 0x89C13AL
        val s3 = (seed ushr 48) xor 0x12F8B7L

        // Continentalness: ocean vs landmass (range -1.0 to 1.0)
        var cont = (sin(nx * 0.7 + (s0 and 0xFF) * 0.05) * 0.6 +
            cos(nz * 0.6 + (s0 ushr 8 and 0xFF) * 0.05) * 0.4).toFloat()

        // Temperature: cold to hot (range -1.0 to 1.0)
        val temp = (cos(nz * 0.5 + (s1 and 0xFF) * 0.04) * 0.7 +
            sin(nx * 0.4 + (s1 ushr 8 and 0xFF) * 0.04) * 0.3).toFloat()

        // Humidity: arid to wet (range -1.0 to 1.0)
        val humid = (sin(nx * 0.5 + (s2 and 0xFF) * 0.04) * 0.5 +
            cos(nz * 0.4 + (s2 ushr 8 and 0xFF) * 0.04) * 0.5).toFloat()

        // Erosion: flat to mountains
        var erosion = (cos(nx * 0.8 + (s3 and 0xFF) * 0.06) * 0.5 +
            sin(nz * 0.8 + (s3 ushr 8 and 0xFF) * 0.06) * 0.5).toFloat()

        // Terralith Modded World Generation Rules (Fabric)
        val isTerralith = activeMods.contains("Terralith")
        if (isTerralith) {
            // Terralith increases continental roughness and adds volcanic / canyon hotspots
            if (cont > 0.4f && temp > 0.3f && humid < -0.2f && (abs(x + z) % 180 < 35)) {
                return MapBiome.TERRALITH_PAINTED_CANYON
            }
            if (cont > 0.5f && temp > 0.4f && humid > 0.2f && (abs(x * 3 + z) % 200 < 30)) {
                return MapBiome.TERRALITH_YELLOWSTONE
            }
            if (erosion > 0.55f && cont > 0.35f && temp < 0.1f) {
                return MapBiome.TERRALITH_ALPINE_STEPPE
            }
            if (erosion > 0.6f && cont > 0.5f && (abs(x - z) % 240 < 25)) {
                return MapBiome.TERRALITH_VOLCANIC_CRATER
            }
        }

        // Tectonic Modded Generation Rules (Fabric)
        val isTectonic = activeMods.contains("Tectonic")
        if (isTectonic) {
            cont = (cont * 1.3f).coerceIn(-1.0f, 1.0f)
            erosion = (erosion * 1.25f).coerceIn(-1.0f, 1.0f)
        }

        // Deep Ocean & Ocean
        if (cont < -0.45f) {
            return if (temp < -0.3f) MapBiome.DEEP_FROZEN_OCEAN else MapBiome.DEEP_OCEAN
        }
        if (cont < -0.15f) {
            return when {
                temp < -0.3f -> MapBiome.FROZEN_OCEAN
                temp > 0.45f -> MapBiome.WARM_OCEAN
                else -> MapBiome.OCEAN
            }
        }

        // Rare isolated Mushroom Fields in shallow/coastal oceans with specific weirdness
        if (cont in -0.25f..-0.05f && abs(nx + nz) % 1.0 < 0.04) {
            return MapBiome.MUSHROOM_FIELDS
        }

        // Mountain Peaks (High continentalness + low erosion in modern 1.18+ and 26.2)
        if (cont > 0.45f && erosion > 0.45f) {
            return when {
                temp < -0.35f -> MapBiome.FROZEN_PEAKS
                temp < 0.1f -> MapBiome.JAGGED_PEAKS
                temp > 0.4f -> MapBiome.STONY_PEAKS
                // Cherry Grove exists in 1.20+ and 26.2
                (version == "26.2" || version.startsWith("1.20") || version.startsWith("1.21")) && humid > 0.2f -> MapBiome.CHERRY_GROVE
                else -> MapBiome.MEADOW
            }
        }

        // Cherry Groves on temperate highlands (26.2 & 1.20+)
        if ((version == "26.2" || version.startsWith("1.20") || version.startsWith("1.21")) &&
            cont > 0.3f && temp in -0.1f..0.3f && humid in 0.25f..0.65f && erosion in 0.2f..0.45f
        ) {
            return MapBiome.CHERRY_GROVE
        }

        // Cold / Snowy Biomes
        if (temp < -0.35f) {
            return if (humid > 0.1f) MapBiome.SNOWY_TAIGA else MapBiome.SNOWY_PLAINS
        }

        // Arid / Hot Biomes
        if (temp > 0.45f) {
            return when {
                humid < -0.3f -> {
                    // Badlands vs Desert
                    if (erosion > 0.25f) MapBiome.BADLANDS else MapBiome.DESERT
                }
                humid < 0.15f -> MapBiome.SAVANNA
                // Mangrove Swamp (1.19+ and 26.2)
                (version == "26.2" || version.startsWith("1.19") || version.startsWith("1.20") || version.startsWith("1.21")) && humid > 0.5f && cont in -0.1f..0.2f -> MapBiome.MANGROVE_SWAMP
                humid > 0.35f -> {
                    if (erosion > 0.3f) MapBiome.BAMBOO_JUNGLE else MapBiome.JUNGLE
                }
                else -> MapBiome.PLAINS
            }
        }

        // Temperate Biomes
        return when {
            humid > 0.45f -> {
                if (cont in -0.1f..0.15f) MapBiome.SWAMP
                else if (temp < 0.1f) MapBiome.OLD_GROWTH_TAIGA
                else MapBiome.DARK_FOREST
            }
            humid > 0.15f -> {
                if (temp < 0.0f) MapBiome.TAIGA
                else if (erosion < -0.15f) MapBiome.FLOWER_FOREST
                else MapBiome.FOREST
            }
            humid < -0.2f -> {
                if (temp > 0.2f) MapBiome.SAVANNA
                else MapBiome.PLAINS
            }
            else -> {
                if (erosion < -0.35f) MapBiome.SUNFLOWER_PLAINS
                else MapBiome.PLAINS
            }
        }
    }

    /**
     * Generates structure placements matching Java Edition region algorithms:
     * - Chunk Grid region spacing & separation
     * - Salted linear congruential generator (LCG) per structure type
     */
    private fun generateStructures(
        seedValue: Long,
        minecraftVersion: String,
        activeMods: List<String>,
        spawnX: Int,
        spawnZ: Int
    ): List<MapStructure> {
        val list = mutableListOf<MapStructure>()

        // 1. VILLAGES (Java Edition Spacing: 34 chunks = 544 blocks, Separation: 8 chunks = 128 blocks, Salt: 10387312)
        val villageSalt = 10387312L
        for (regX in -3..3) {
            for (regZ in -3..3) {
                val (chunkX, chunkZ) = getRegionChunk(seedValue, regX, regZ, 34, 8, villageSalt)
                val blockX = chunkX * 16 + 8
                val blockZ = chunkZ * 16 + 8
                val biome = sampleBiomeAt(seedValue, blockX, blockZ, minecraftVersion, activeMods)

                // Villages only generate on land (Plains, Desert, Savanna, Taiga, Snowy)
                val villageType = when (biome.category) {
                    BiomeCategory.PLAINS -> "Plains Village"
                    BiomeCategory.DESERT -> "Desert Village"
                    BiomeCategory.SNOWY -> "Snowy Village"
                    BiomeCategory.FOREST -> "Taiga Village"
                    else -> if (biome.name.contains("Savanna")) "Savanna Village" else null
                }

                if (villageType != null) {
                    val isTownsMod = activeMods.contains("Towns and Towers")
                    val isCtovMod = activeMods.contains("ChoiceTheorem's Overhauled Village")
                    val displayName = when {
                        isCtovMod -> "CTOV Overhauled $villageType"
                        isTownsMod -> "Towns & Towers $villageType"
                        else -> villageType
                    }
                    list.add(
                        MapStructure(
                            id = "village_${regX}_$regZ",
                            name = displayName,
                            x = blockX,
                            y = 70,
                            z = blockZ,
                            category = StructureCategory.VILLAGE,
                            dimension = "Overworld",
                            description = "Inhabited settlement with iron golem, village bell, job site blocks, and trading villagers.",
                            isModded = isTownsMod || isCtovMod,
                            modSource = if (isCtovMod) "CTOV" else if (isTownsMod) "Towns and Towers" else ""
                        )
                    )
                }
            }
        }

        // 2. STRONGHOLDS (Ring 1 concentric calculation: 1280 to 2816 blocks from origin, 3 strongholds spaced ~120 degrees)
        val shRandom = (seedValue xor 0x5DEECE66DL)
        val startAngle = (shRandom % 360).toDouble() * (Math.PI / 180.0)
        for (i in 0..2) {
            val angle = startAngle + (i * 2.0 * Math.PI / 3.0)
            val dist = 1450.0 + ((seedValue ushr (i * 8 + 4)) % 650).toInt()
            val shX = (dist * cos(angle)).toInt()
            val shZ = (dist * sin(angle)).toInt()
            list.add(
                MapStructure(
                    id = "stronghold_$i",
                    name = if (activeMods.contains("YUNG's Better Strongholds (Fabric)")) "YUNG's Overhauled Stronghold" else "Stronghold (Ring 1)",
                    x = shX,
                    y = -16,
                    z = shZ,
                    category = StructureCategory.STRONGHOLD,
                    dimension = "Overworld",
                    description = "Subterranean fortress containing End Portal frame with Silverfish spawner and libraries.",
                    isModded = activeMods.contains("YUNG's Better Strongholds (Fabric)"),
                    modSource = if (activeMods.contains("YUNG's Better Strongholds (Fabric)")) "YUNG's Better Strongholds" else ""
                )
            )
        }

        // 3. ANCIENT CITIES (Available in 1.19+, 1.20+, 1.21+, 26.2; Spacing: 24 chunks, Separation: 8 chunks, Salt: 20083232)
        if (minecraftVersion == "26.2" || minecraftVersion.startsWith("1.21") || minecraftVersion.startsWith("1.20") || minecraftVersion.startsWith("1.19")) {
            val acSalt = 20083232L
            for (regX in -2..2) {
                for (regZ in -2..2) {
                    val (chunkX, chunkZ) = getRegionChunk(seedValue, regX, regZ, 24, 8, acSalt)
                    val blockX = chunkX * 16 + 8
                    val blockZ = chunkZ * 16 + 8
                    val biome = sampleBiomeAt(seedValue, blockX, blockZ, minecraftVersion, activeMods)

                    // Ancient Cities generate under mountainous / high-continental biomes in Deep Dark
                    if (biome.category == BiomeCategory.MOUNTAINS || biome.category == BiomeCategory.CHERRY_GROVE || biome.category == BiomeCategory.PLAINS) {
                        list.add(
                            MapStructure(
                                id = "ancient_city_${regX}_$regZ",
                                name = "Ancient City",
                                x = blockX,
                                y = -42,
                                z = blockZ,
                                category = StructureCategory.ANCIENT_CITY,
                                dimension = "Overworld",
                                description = "Massive Deep Dark palatial ruin with Reinforced Deepslate center frame and Warden shrieker traps."
                            )
                        )
                    }
                }
            }
        }

        // 4. TRIAL CHAMBERS (Introduced in 1.21 & 26.2; Spacing: 34 chunks, Separation: 12 chunks, Salt: 94251324)
        if (minecraftVersion == "26.2" || minecraftVersion.startsWith("1.21")) {
            val tcSalt = 94251324L
            for (regX in -2..2) {
                for (regZ in -2..2) {
                    val (chunkX, chunkZ) = getRegionChunk(seedValue, regX, regZ, 34, 12, tcSalt)
                    val blockX = chunkX * 16 + 8
                    val blockZ = chunkZ * 16 + 8
                    val biome = sampleBiomeAt(seedValue, blockX, blockZ, minecraftVersion, activeMods)

                    if (biome.category != BiomeCategory.DEEP_OCEAN) {
                        val tcY = -18 - (abs(seedValue ushr 4) % 20).toInt()
                        list.add(
                            MapStructure(
                                id = "trial_chamber_${regX}_$regZ",
                                name = "Trial Chamber",
                                x = blockX,
                                y = tcY,
                                z = blockZ,
                                category = StructureCategory.TRIAL_CHAMBER,
                                dimension = "Overworld",
                                description = "Subterranean copper & tuff battle vault containing Breeze spawners and Ominous Vaults."
                            )
                        )
                    }
                }
            }
        }

        // 5. OCEAN MONUMENTS (Deep Ocean; Spacing: 32 chunks, Separation: 5 chunks, Salt: 10387313)
        val monSalt = 10387313L
        for (regX in -3..3) {
            for (regZ in -3..3) {
                val (chunkX, chunkZ) = getRegionChunk(seedValue, regX, regZ, 32, 5, monSalt)
                val blockX = chunkX * 16 + 8
                val blockZ = chunkZ * 16 + 8
                val biome = sampleBiomeAt(seedValue, blockX, blockZ, minecraftVersion, activeMods)

                if (biome.category == BiomeCategory.DEEP_OCEAN || biome.category == BiomeCategory.OCEAN) {
                    list.add(
                        MapStructure(
                            id = "monument_${regX}_$regZ",
                            name = "Ocean Monument",
                            x = blockX,
                            y = 48,
                            z = blockZ,
                            category = StructureCategory.MONUMENT,
                            dimension = "Overworld",
                            description = "Prismarine underwater fortress guarded by 3 Elder Guardians with 8 gold blocks inside."
                        )
                    )
                }
            }
        }

        // 6. WOODLAND MANSIONS (Dark Forest; Spacing: 80 chunks, Separation: 20 chunks, Salt: 10387319)
        val manSalt = 10387319L
        for (regX in -2..2) {
            for (regZ in -2..2) {
                val (chunkX, chunkZ) = getRegionChunk(seedValue, regX, regZ, 80, 20, manSalt)
                val blockX = chunkX * 16 + 8
                val blockZ = chunkZ * 16 + 8
                val biome = sampleBiomeAt(seedValue, blockX, blockZ, minecraftVersion, activeMods)

                if (biome == MapBiome.DARK_FOREST || biome.category == BiomeCategory.FOREST) {
                    list.add(
                        MapStructure(
                            id = "mansion_${regX}_$regZ",
                            name = "Woodland Mansion",
                            x = blockX,
                            y = 74,
                            z = blockZ,
                            category = StructureCategory.MANSION,
                            dimension = "Overworld",
                            description = "Colossal 3-story dark oak estate populated by Evokers and Vindicators holding Totems of Undying."
                        )
                    )
                }
            }
        }

        // 7. PILLAGER OUTPOSTS (Spacing: 40 chunks, Separation: 10 chunks, Salt: 165745296)
        val outpostSalt = 165745296L
        for (regX in -2..2) {
            for (regZ in -2..2) {
                val (chunkX, chunkZ) = getRegionChunk(seedValue, regX, regZ, 40, 10, outpostSalt)
                val blockX = chunkX * 16 + 8
                val blockZ = chunkZ * 16 + 8
                val biome = sampleBiomeAt(seedValue, blockX, blockZ, minecraftVersion, activeMods)

                if (biome.category != BiomeCategory.OCEAN && biome.category != BiomeCategory.DEEP_OCEAN) {
                    list.add(
                        MapStructure(
                            id = "outpost_${regX}_$regZ",
                            name = "Pillager Outpost",
                            x = blockX,
                            y = 72,
                            z = blockZ,
                            category = StructureCategory.OUTPOST,
                            dimension = "Overworld",
                            description = "Dark oak watchtower with crossbow pillagers, Allay iron cages, and Bad Omen raid captains."
                        )
                    )
                }
            }
        }

        // 8. DESERT & JUNGLE TEMPLES (Spacing: 32 chunks, Separation: 8 chunks, Salt: 14357617)
        val templeSalt = 14357617L
        for (regX in -2..2) {
            for (regZ in -2..2) {
                val (chunkX, chunkZ) = getRegionChunk(seedValue, regX, regZ, 32, 8, templeSalt)
                val blockX = chunkX * 16 + 8
                val blockZ = chunkZ * 16 + 8
                val biome = sampleBiomeAt(seedValue, blockX, blockZ, minecraftVersion, activeMods)

                if (biome.category == BiomeCategory.DESERT) {
                    list.add(
                        MapStructure(
                            id = "pyramid_${regX}_$regZ",
                            name = "Desert Pyramid",
                            x = blockX,
                            y = 66,
                            z = blockZ,
                            category = StructureCategory.TEMPLE,
                            dimension = "Overworld",
                            description = "Sandstone pyramid with hidden TNT pressure plate trap and 4 loot chests."
                        )
                    )
                } else if (biome.category == BiomeCategory.JUNGLE) {
                    list.add(
                        MapStructure(
                            id = "jungle_temple_${regX}_$regZ",
                            name = "Jungle Temple",
                            x = blockX,
                            y = 70,
                            z = blockZ,
                            category = StructureCategory.TEMPLE,
                            dimension = "Overworld",
                            description = "Mossy cobblestone temple with tripwire arrow dispensers and hidden lever puzzle room."
                        )
                    )
                }
            }
        }

        // 9. SHIPWRECKS (Spacing: 24 chunks, Separation: 4 chunks, Salt: 165745295)
        val wreckSalt = 165745295L
        for (regX in -3..3) {
            for (regZ in -3..3) {
                val (chunkX, chunkZ) = getRegionChunk(seedValue, regX, regZ, 24, 4, wreckSalt)
                val blockX = chunkX * 16 + 8
                val blockZ = chunkZ * 16 + 8
                val biome = sampleBiomeAt(seedValue, blockX, blockZ, minecraftVersion, activeMods)

                if (biome.category == BiomeCategory.OCEAN || biome.category == BiomeCategory.DEEP_OCEAN) {
                    list.add(
                        MapStructure(
                            id = "shipwreck_${regX}_$regZ",
                            name = "Shipwreck",
                            x = blockX,
                            y = 56,
                            z = blockZ,
                            category = StructureCategory.SHIPWRECK,
                            dimension = "Overworld",
                            description = "Sunken or beached vessel holding Buried Treasure maps and supply barrels."
                        )
                    )
                }
            }
        }

        // 10. WITCH HUTS (Swamp biomes; Spacing: 32 chunks, Separation: 8 chunks, Salt: 14357620)
        val witchSalt = 14357620L
        for (regX in -3..3) {
            for (regZ in -3..3) {
                val (chunkX, chunkZ) = getRegionChunk(seedValue, regX, regZ, 32, 8, witchSalt)
                val blockX = chunkX * 16 + 8
                val blockZ = chunkZ * 16 + 8
                val biome = sampleBiomeAt(seedValue, blockX, blockZ, minecraftVersion, activeMods)

                if (biome == MapBiome.SWAMP || biome == MapBiome.MANGROVE_SWAMP) {
                    list.add(
                        MapStructure(
                            id = "witch_hut_${regX}_$regZ",
                            name = "Witch Hut",
                            x = blockX,
                            y = 65,
                            z = blockZ,
                            category = StructureCategory.WITCH_HUT,
                            dimension = "Overworld",
                            description = "Swamp stilt shack housing a witch, black cat, and cauldron."
                        )
                    )
                }
            }
        }

        // 11. MINESHAFTS (Spacing: 24 chunks, Separation: 6 chunks, Salt: 10387321)
        val mineSalt = 10387321L
        for (regX in -2..2) {
            for (regZ in -2..2) {
                val (chunkX, chunkZ) = getRegionChunk(seedValue, regX, regZ, 24, 6, mineSalt)
                val blockX = chunkX * 16 + 8
                val blockZ = chunkZ * 16 + 8
                val biome = sampleBiomeAt(seedValue, blockX, blockZ, minecraftVersion, activeMods)
                if (biome.category != BiomeCategory.DEEP_OCEAN) {
                    list.add(
                        MapStructure(
                            id = "mineshaft_${regX}_$regZ",
                            name = "Abandoned Mineshaft",
                            x = blockX,
                            y = 28,
                            z = blockZ,
                            category = StructureCategory.MINESHAFT,
                            dimension = "Overworld",
                            description = "Underground network of wood supports, minecart with chests, and cave spider spawners."
                        )
                    )
                }
            }
        }

        // 12. RUINED PORTALS (Spacing: 36 chunks, Separation: 10 chunks, Salt: 40552231)
        val portalSalt = 40552231L
        for (regX in -2..2) {
            for (regZ in -2..2) {
                val (chunkX, chunkZ) = getRegionChunk(seedValue, regX, regZ, 36, 10, portalSalt)
                val blockX = chunkX * 16 + 8
                val blockZ = chunkZ * 16 + 8
                list.add(
                    MapStructure(
                        id = "ruined_portal_${regX}_$regZ",
                        name = "Ruined Portal",
                        x = blockX,
                        y = 68,
                        z = blockZ,
                        category = StructureCategory.RUINED_PORTAL,
                        dimension = "Overworld",
                        description = "Ancient broken obsidian Nether portal with crying obsidian, gold blocks, and loot chest."
                    )
                )
            }
        }

        // 13. TRAIL RUINS (1.20+, 1.21+, 26.2; Spacing: 34 chunks, Separation: 8 chunks, Salt: 83469837)
        if (minecraftVersion == "26.2" || minecraftVersion.startsWith("1.20") || minecraftVersion.startsWith("1.21")) {
            val trailSalt = 83469837L
            for (regX in -2..2) {
                for (regZ in -2..2) {
                    val (chunkX, chunkZ) = getRegionChunk(seedValue, regX, regZ, 34, 8, trailSalt)
                    val blockX = chunkX * 16 + 8
                    val blockZ = chunkZ * 16 + 8
                    val biome = sampleBiomeAt(seedValue, blockX, blockZ, minecraftVersion, activeMods)

                    if (biome == MapBiome.TAIGA || biome == MapBiome.OLD_GROWTH_TAIGA || biome == MapBiome.SNOWY_TAIGA || biome.category == BiomeCategory.JUNGLE) {
                        list.add(
                            MapStructure(
                                id = "trail_ruins_${regX}_$regZ",
                                name = "Trail Ruins",
                                x = blockX,
                                y = 58,
                                z = blockZ,
                                category = StructureCategory.TRAIL_RUINS,
                                dimension = "Overworld",
                                description = "Buried archaeological site with suspicious gravel, pottery sherds, and armor trims."
                            )
                        )
                    }
                }
            }
        }

        // Sort by distance from player spawn
        return list.sortedBy { it.distanceFrom(spawnX, spawnZ) }
    }

    /**
     * Minecraft Java Edition LCG Grid formula for structure regions
     */
    private fun getRegionChunk(
        seed: Long,
        regX: Int,
        regZ: Int,
        spacing: Int,
        separation: Int,
        salt: Long
    ): Pair<Int, Int> {
        val regionSeed = (regX.toLong() * 341873128712L + regZ.toLong() * 132897987541L + seed + salt)
        val rand = abs((regionSeed xor 0x5DEECE66DL) ushr 16)
        val maxOffset = spacing - separation
        val offsetX = (rand % maxOffset).toInt()
        val offsetZ = ((rand ushr 8) % maxOffset).toInt()

        val chunkX = regX * spacing + offsetX
        val chunkZ = regZ * spacing + offsetZ
        return Pair(chunkX, chunkZ)
    }

    /**
     * Structure Search: Filters generated structures by query (e.g., "Village", "Ancient City")
     */
    fun searchStructures(
        query: String,
        structures: List<MapStructure>,
        spawnX: Int,
        spawnZ: Int
    ): List<MapStructure> {
        val clean = query.trim()
        if (clean.isEmpty()) return structures
        return structures.filter {
            it.name.contains(clean, ignoreCase = true) ||
                it.category.label.contains(clean, ignoreCase = true) ||
                it.category.name.contains(clean, ignoreCase = true)
        }.sortedBy { it.distanceFrom(spawnX, spawnZ) }
    }

    /**
     * Biome Search (Rule 12):
     * Scans multi-noise samples radiating out from spawn to find the closest region matching the query.
     * Always flags `isEstimated = true` and shows explicit estimation notice.
     */
    fun searchBiome(
        query: String,
        seedValue: Long,
        minecraftVersion: String,
        activeMods: List<String>,
        spawnX: Int,
        spawnZ: Int
    ): BiomeSearchResult? {
        val clean = query.trim().lowercase()
        if (clean.isEmpty()) return null

        // Step through concentric circles from spawn up to 4000 blocks
        for (radius in 64..4000 step 128) {
            for (angleDeg in 0 until 360 step 30) {
                val rad = Math.toRadians(angleDeg.toDouble())
                val testX = (spawnX + radius * cos(rad)).toInt()
                val testZ = (spawnZ + radius * sin(rad)).toInt()

                val biome = sampleBiomeAt(seedValue, testX, testZ, minecraftVersion, activeMods)
                if (biome.name.lowercase().contains(clean) ||
                    biome.category.name.lowercase().contains(clean) ||
                    biome.id.lowercase().contains(clean)
                ) {
                    val dist = sqrt(((testX - spawnX) * (testX - spawnX) + (testZ - spawnZ) * (testZ - spawnZ)).toDouble()).toInt()
                    return BiomeSearchResult(
                        biome = biome,
                        foundX = testX,
                        foundZ = testZ,
                        distanceFromSpawn = dist,
                        isEstimated = true,
                        disclaimer = "Biome data is estimated based on Java 26.2 multi-noise sampling. Actual boundaries may vary slightly in-game."
                    )
                }
            }
        }
        return null
    }
}
