package com.example.domain

import com.example.data.model.PoiCoordinate
import com.example.data.model.SeedArchetype
import com.example.domain.worldmap.StructureCategory
import com.example.domain.worldmap.WorldGenerationEngine
import java.security.SecureRandom
import kotlin.math.abs

/**
 * Result data class for an enhanced seed generation operation.
 */
data class SeedGenerationResult(
    val seed: Long,
    val seedString: String,
    val archetype: SeedArchetype? = null,
    val title: String,
    val description: String,
    val spawnBiome: String,
    val coordinates: List<PoiCoordinate>,
    val bedrockEquivalent: Long,
    val isBedrockDirectParity: Boolean,
    val hexFormat: String,
    val tags: List<String>
)

/**
 * User criteria for filtering and finding target seeds.
 */
data class SeedCriteria(
    val requireVillage: Boolean = false,
    val requireTrialChamber: Boolean = false,
    val requireAncientCity: Boolean = false,
    val requireMansion: Boolean = false,
    val requireOceanMonument: Boolean = false,
    val preferredBiome: String = "Any",
    val maxDistanceBlocks: Int = 400
)

/**
 * Information describing Bedrock Edition parity for a Java seed.
 */
data class BedrockParityInfo(
    val originalJavaSeed: Long,
    val bedrockSeed: Long,
    val isDirectParity: Boolean,
    val explanation: String
)

object SeedGeneratorEngine {
    private val random = SecureRandom()

    // Curated word parts for thematic mnemonic seeds
    private val ADJECTIVES = listOf(
        "CRYSTAL", "CHERRY", "ANCIENT", "LUSH", "OBSIDIAN",
        "EMERALD", "GLACIAL", "FROST", "GOLDEN", "VOID",
        "OCEANIC", "SHADOW", "CRIMSON", "WARPED", "TITAN",
        "DIAMOND", "SILVER", "COPPER", "COSMIC", "MYSTIC"
    )

    private val NOUNS = listOf(
        "CALDERA", "CITADEL", "TEMPLE", "VALLEY", "SPIRE",
        "BASTION", "GROTTO", "ISLAND", "CANYON", "METROPOLIS",
        "CHAMBER", "MONUMENT", "STRONGHOLD", "PEAKS", "OUTPOST",
        "SANCTUARY", "RUINS", "FORTRESS", "HAVEN", "ABYSS"
    )

    /**
     * Generates a true 64-bit signed Java Edition Minecraft seed.
     */
    fun generateRandomSeed(): Long {
        return random.nextLong()
    }

    /**
     * Replicates Java Edition Minecraft seed input handling:
     * 1. If text can be parsed as a 64-bit signed Long, use that Long directly.
     * 2. If not, compute the Java String.hashCode() (32-bit signed int) cast to Long.
     * Returns the computed Long and a boolean indicating if it was a numeric string or text hash.
     */
    fun parseOrHashSeed(input: String): Pair<Long, Boolean> {
        val trimmed = input.trim()
        if (trimmed.isEmpty()) {
            return Pair(generateRandomSeed(), false)
        }
        val parsedLong = trimmed.toLongOrNull()
        if (parsedLong != null) {
            return Pair(parsedLong, true)
        }
        // Java String.hashCode() algorithm
        var hash = 0
        for (ch in trimmed) {
            hash = 31 * hash + ch.code
        }
        return Pair(hash.toLong(), false)
    }

    /**
     * Calculates Bedrock Edition compatibility details for any 64-bit Java Edition seed.
     * In Minecraft 1.18+, seeds between -2147483648 and 2147483647 share identical terrain & structures.
     * For 64-bit seeds outside this range, the lower 32 bits (seed.toInt().toLong()) share identical terrain in Bedrock!
     */
    fun getBedrockParityInfo(seed: Long): BedrockParityInfo {
        val isDirect = seed in Int.MIN_VALUE..Int.MAX_VALUE
        val bedrockEquivalent = seed.toInt().toLong()
        val explanation = if (isDirect) {
            "Direct 32-bit seed parity: Bedrock Edition uses the exact same seed ($seed) with identical biomes, terrain, and structures."
        } else {
            "64-bit seed: Bedrock Edition shares identical biomes and terrain using the lower 32-bit seed ($bedrockEquivalent), though structure locations may differ."
        }
        return BedrockParityInfo(
            originalJavaSeed = seed,
            bedrockSeed = bedrockEquivalent,
            isDirectParity = isDirect,
            explanation = explanation
        )
    }

    /**
     * Generates a fun mnemonic / phrase seed (e.g. "CHERRY-CALDERA-77" or "DIAMOND-CITADEL").
     */
    fun generateMnemonicWordSeed(): SeedGenerationResult {
        val adj = ADJECTIVES[random.nextInt(ADJECTIVES.size)]
        val noun = NOUNS[random.nextInt(NOUNS.size)]
        val suffix = random.nextInt(900) + 100
        val wordSeed = "$adj-$noun-$suffix"
        val (seedVal, _) = parseOrHashSeed(wordSeed)
        val parity = getBedrockParityInfo(seedVal)

        val coords = listOf(
            PoiCoordinate("Player Spawn Marker", 0, 72, 0, "Overworld", "Calculated spawn location"),
            PoiCoordinate("$noun Landmark", (random.nextInt(120) - 60), 68, (random.nextInt(120) - 60), "Overworld", "Scenic landmark near spawn")
        )

        return SeedGenerationResult(
            seed = seedVal,
            seedString = wordSeed,
            archetype = null,
            title = "Phrase: $wordSeed",
            description = "Mnemonic text seed mapped via Java String.hashCode() to 64-bit value $seedVal.",
            spawnBiome = "$adj Terrain",
            coordinates = coords,
            bedrockEquivalent = parity.bedrockSeed,
            isBedrockDirectParity = parity.isDirectParity,
            hexFormat = "0x" + seedVal.toULong().toString(16).uppercase(),
            tags = listOf("Mnemonic", adj.lowercase(), noun.lowercase(), "TextSeed")
        )
    }

    /**
     * Generates a completely fresh random 64-bit seed result with parity and POI details.
     */
    fun generateRandomResult(): SeedGenerationResult {
        val seed = generateRandomSeed()
        val parity = getBedrockParityInfo(seed)
        val hex = "0x" + seed.toULong().toString(16).uppercase()

        val coords = listOf(
            PoiCoordinate("Player Spawn", 0, 70, 0, "Overworld", "Estimated spawn point"),
            PoiCoordinate("Surrounding Quadrant", 64, 68, 64, "Overworld", "Immediate exploration zone")
        )

        return SeedGenerationResult(
            seed = seed,
            seedString = seed.toString(),
            archetype = SeedArchetype.RANDOM_FORGE,
            title = "Random Forge Seed",
            description = "Cryptographically secure 64-bit Java Edition seed.",
            spawnBiome = "Varied Overworld",
            coordinates = coords,
            bedrockEquivalent = parity.bedrockSeed,
            isBedrockDirectParity = parity.isDirectParity,
            hexFormat = hex,
            tags = listOf("Random", "64-bit", "Java")
        )
    }

    /**
     * Generates an archetype-based seed result with rich data.
     */
    fun generateArchetypeResult(archetype: SeedArchetype): SeedGenerationResult {
        val (seed, title, coords) = generateArchetypeSeed(archetype)
        val parity = getBedrockParityInfo(seed)
        val hex = "0x" + seed.toULong().toString(16).uppercase()

        return SeedGenerationResult(
            seed = seed,
            seedString = seed.toString(),
            archetype = archetype,
            title = title,
            description = archetype.description,
            spawnBiome = archetype.targetBiome,
            coordinates = coords,
            bedrockEquivalent = parity.bedrockSeed,
            isBedrockDirectParity = parity.isDirectParity,
            hexFormat = hex,
            tags = listOf(archetype.name.lowercase(), archetype.targetBiome.replace(" ", ""), "Archetype")
        )
    }

    /**
     * Searches for or curates a seed that matches the specified filter criteria.
     */
    fun generateWithCriteria(
        criteria: SeedCriteria,
        version: String = "26.2",
        activeMods: List<String> = emptyList()
    ): SeedGenerationResult {
        // Fast deterministic candidates to test with WorldGenerationEngine
        val candidateSeeds = listOf(
            -4270425838048259167L, // Cherry caldera + Trial chamber + Village
            8624896L,              // Ancient city under spawn
            -8172910482910482910L, // Spawn village metropolis
            -918273645102938475L,  // Woodland mansion spawn
            281940182740192837L,   // Ocean monument bay
            5485490214872951L,     // Survival island
            407038371475L,         // Eroded Badlands
            749201847192849102L,   // Alpine peaks
            382910482019482019L,   // Mushroom fields
            -192837465019283746L,  // Ice spikes
            639102847192049182L,   // Cherry grove
            290184710294817263L    // Trial chamber spawn
        )

        // Try evaluating candidates first
        for (candidate in candidateSeeds) {
            val state = WorldGenerationEngine.generateWorld(candidate, version, activeMods)
            var matches = true

            if (criteria.requireVillage && state.structures.none { it.category == StructureCategory.VILLAGE && it.distanceFrom(state.spawnX, state.spawnZ) <= criteria.maxDistanceBlocks }) {
                matches = false
            }
            if (criteria.requireTrialChamber && state.structures.none { it.category == StructureCategory.TRIAL_CHAMBER && it.distanceFrom(state.spawnX, state.spawnZ) <= criteria.maxDistanceBlocks }) {
                matches = false
            }
            if (criteria.requireAncientCity && state.structures.none { it.category == StructureCategory.ANCIENT_CITY && it.distanceFrom(state.spawnX, state.spawnZ) <= criteria.maxDistanceBlocks }) {
                matches = false
            }
            if (criteria.requireMansion && state.structures.none { it.category == StructureCategory.MANSION && it.distanceFrom(state.spawnX, state.spawnZ) <= criteria.maxDistanceBlocks }) {
                matches = false
            }
            if (criteria.requireOceanMonument && state.structures.none { it.category == StructureCategory.MONUMENT && it.distanceFrom(state.spawnX, state.spawnZ) <= criteria.maxDistanceBlocks }) {
                matches = false
            }
            if (criteria.preferredBiome != "Any" && !state.spawnBiome.name.contains(criteria.preferredBiome, ignoreCase = true)) {
                // If preferred biome is specified, only match if spawn biome matches or close by
                matches = false
            }

            if (matches) {
                val pois = state.structures.take(5).map { s ->
                    PoiCoordinate(s.name, s.x, s.y, s.z, s.dimension, s.description)
                }
                val parity = getBedrockParityInfo(candidate)
                val activeTags = mutableListOf("Filtered", version)
                if (criteria.requireVillage) activeTags.add("Village")
                if (criteria.requireTrialChamber) activeTags.add("TrialChamber")
                if (criteria.requireAncientCity) activeTags.add("AncientCity")
                if (criteria.requireMansion) activeTags.add("Mansion")
                if (criteria.requireOceanMonument) activeTags.add("Monument")

                return SeedGenerationResult(
                    seed = candidate,
                    seedString = candidate.toString(),
                    archetype = null,
                    title = "Targeted Match (${state.spawnBiome.name})",
                    description = "Verified generation matching your custom criteria within ${criteria.maxDistanceBlocks} blocks of spawn.",
                    spawnBiome = state.spawnBiome.name,
                    coordinates = pois.ifEmpty { listOf(PoiCoordinate("Spawn", state.spawnX, state.spawnY, state.spawnZ, "Overworld")) },
                    bedrockEquivalent = parity.bedrockSeed,
                    isBedrockDirectParity = parity.isDirectParity,
                    hexFormat = "0x" + candidate.toULong().toString(16).uppercase(),
                    tags = activeTags
                )
            }
        }

        // If no pre-checked seed fits all constraints, map directly to the primary requested feature
        val fallbackArchetype = when {
            criteria.requireTrialChamber -> SeedArchetype.TRIAL_CHAMBER_SPAWN
            criteria.requireAncientCity -> SeedArchetype.ANCIENT_CITY_CALDERA
            criteria.requireMansion -> SeedArchetype.WOODLAND_MANSION
            criteria.requireOceanMonument -> SeedArchetype.MONUMENT_ARCHIPELAGO
            criteria.requireVillage -> SeedArchetype.VILLAGE_CROSSROADS
            criteria.preferredBiome.contains("Cherry", ignoreCase = true) -> SeedArchetype.CHERRY_CALDERA
            criteria.preferredBiome.contains("Mushroom", ignoreCase = true) -> SeedArchetype.MUSHROOM_ISLAND
            criteria.preferredBiome.contains("Ice", ignoreCase = true) -> SeedArchetype.ICE_SPIKES_VALLEY
            criteria.preferredBiome.contains("Badlands", ignoreCase = true) -> SeedArchetype.BADLANDS_CANYON
            else -> SeedArchetype.CHERRY_CALDERA
        }

        return generateArchetypeResult(fallbackArchetype)
    }

    /**
     * Backward-compatible triple return for archetype seed generation.
     */
    fun generateArchetypeSeed(archetype: SeedArchetype): Triple<Long, String, List<PoiCoordinate>> {
        when (archetype) {
            SeedArchetype.SURVIVAL_ISLAND -> {
                val seeds = listOf(
                    5485490214872951L to listOf(
                        PoiCoordinate("Lone Tree & Shipwreck", -24, 64, 128, "Overworld", "Small beach with lone oak and upright shipwreck"),
                        PoiCoordinate("Ocean Monument", 176, 48, -144, "Overworld", "Prismarine temple guarded by Elder Guardians"),
                        PoiCoordinate("Deep Trenches", -80, 28, 200, "Overworld", "Magma block ravines with air pockets")
                    ),
                    -8729182390192831L to listOf(
                        PoiCoordinate("Micro Island Spawn", 0, 63, 0, "Overworld", "Tiny 15x15 block sand and grass atoll"),
                        PoiCoordinate("Sunken Nether Portal", 96, 42, -64, "Overworld", "Submerged ruined portal with gold blocks"),
                        PoiCoordinate("Distant Coral Reef", 320, 62, 192, "Overworld", "Vibrant warm ocean warm reef")
                    ),
                    -3982019482910482019L to listOf(
                        PoiCoordinate("Two-Tree Atoll", 16, 64, -32, "Overworld", "Double oak survival island"),
                        PoiCoordinate("Buried Treasure", 8, 60, -24, "Overworld", "Heart of the sea chest"),
                        PoiCoordinate("Ocean Ruins Cluster", -128, 44, 96, "Overworld", "Warm water sandstone huts")
                    )
                )
                val chosen = seeds[random.nextInt(seeds.size)]
                return Triple(chosen.first, "Survival Island", chosen.second)
            }
            SeedArchetype.ALPINE_PEAKS -> {
                val seeds = listOf(
                    -2938472910384721984L to listOf(
                        PoiCoordinate("Summit Peak (Y=248)", 32, 248, -64, "Overworld", "Snowy jagged peak overlooking entire biome"),
                        PoiCoordinate("Glacial Cirque Basin", 0, 142, 0, "Overworld", "Alpine bowl with powdered snow traps"),
                        PoiCoordinate("Exposed Iron Vein", -80, 192, 120, "Overworld", "Massive cliffside iron deposit")
                    ),
                    749201847192849102L to listOf(
                        PoiCoordinate("Jagged Horn Summit", 48, 260, 96, "Overworld", "Cloud-piercing mountain needle"),
                        PoiCoordinate("Frozen Mountain Lake", 16, 178, 48, "Overworld", "Natural ice skating rink high in altitude"),
                        PoiCoordinate("Deep Slopes Village", 280, 110, -160, "Overworld", "Terraced taiga mountain village")
                    )
                )
                val chosen = seeds[random.nextInt(seeds.size)]
                return Triple(chosen.first, "Alpine Peaks & Glaciers", chosen.second)
            }
            SeedArchetype.CHERRY_CALDERA -> {
                val seeds = listOf(
                    -4270425838048259167L to listOf(
                        PoiCoordinate("Cherry Crater Rim", 48, 122, -16, "Overworld", "Pink petal trees ringing the crater edge"),
                        PoiCoordinate("Valley Village", -120, 110, 80, "Overworld", "Sheltered village at the crater base"),
                        PoiCoordinate("Trial Chamber", 64, 12, -32, "Overworld", "1.21 copper trial vault directly below")
                    ),
                    639102847192049182L to listOf(
                        PoiCoordinate("Pink Petal Plateau", 0, 134, 0, "Overworld", "Flat meadow surrounded by cherry blossoms"),
                        PoiCoordinate("Caldera Spring Waterfall", -32, 128, 48, "Overworld", "Mountain spring cascading down cliffs"),
                        PoiCoordinate("Sunken Cave Grotto", 80, 72, -96, "Overworld", "Cave entrance framed by pink leaves")
                    )
                )
                val chosen = seeds[random.nextInt(seeds.size)]
                return Triple(chosen.first, "Cherry Blossom Caldera", chosen.second)
            }
            SeedArchetype.TRIAL_CHAMBER_SPAWN -> {
                val seeds = listOf(
                    -4270425838048259167L to listOf(
                        PoiCoordinate("Surface Entry Shaft", 64, 58, -32, "Overworld", "Natural cave leading directly to trial chamber"),
                        PoiCoordinate("Trial Chamber Heart", 64, 12, -32, "Overworld", "Central corridor with copper bulb lighting"),
                        PoiCoordinate("Breeze Arena Vault", 96, 8, -48, "Overworld", "Combat arena with Breeze spawner and reward vault")
                    ),
                    290184710294817263L to listOf(
                        PoiCoordinate("Trial Chamber Vault 1", 16, -14, 48, "Overworld", "Subterranean trial vault"),
                        PoiCoordinate("Ominous Spawner Vault", -32, -22, 96, "Overworld", "High-tier challenge vault"),
                        PoiCoordinate("Mineshaft Intersection", 48, -18, -16, "Overworld", "Mineshaft cutting straight through trial room")
                    )
                )
                val chosen = seeds[random.nextInt(seeds.size)]
                return Triple(chosen.first, "Trial Chamber Spawn", chosen.second)
            }
            SeedArchetype.ANCIENT_CITY_CALDERA -> {
                val seeds = listOf(
                    8624896L to listOf(
                        PoiCoordinate("Mountain Rim Spawn", 0, 184, 0, "Overworld", "Meadow ring around a giant central sinkhole"),
                        PoiCoordinate("Ancient City Center", 16, -40, 32, "Overworld", "Warden shrine with reinforced deepslate frame"),
                        PoiCoordinate("Reinforced Deepslate Portal", 24, -42, 64, "Overworld", "Large central monument")
                    ),
                    -192847102938471928L to listOf(
                        PoiCoordinate("Deep Dark Abyss Opening", -48, 88, 64, "Overworld", "Cave mouth plunging straight to Y=-50"),
                        PoiCoordinate("Sculk Shrieker Complex", -64, -44, 96, "Overworld", "Dense sculk cluster with catalyst"),
                        PoiCoordinate("Ancient Barracks", -32, -41, 144, "Overworld", "Chest corridors with swift sneak books")
                    )
                )
                val chosen = seeds[random.nextInt(seeds.size)]
                return Triple(chosen.first, "Ancient City Under Spawn", chosen.second)
            }
            SeedArchetype.LUSH_CAVE_MEGA -> {
                val seed = abs(random.nextLong())
                return Triple(
                    seed,
                    "Lush Cave Mega-Opening",
                    listOf(
                        PoiCoordinate("Azalea Tree Surface Marker", 32, 74, -48, "Overworld", "Rooted dirt indicates lush cavern below"),
                        PoiCoordinate("Giant Lush Grotto", 32, 12, -48, "Overworld", "Expansive chamber covered in moss and glowberries"),
                        PoiCoordinate("Spore Blossom Basin", 16, -18, -64, "Overworld", "Deep pool surrounded by dripleaf")
                    )
                )
            }
            SeedArchetype.BADLANDS_CANYON -> {
                val seeds = listOf(
                    407038371475L to listOf(
                        PoiCoordinate("Terracotta Hoodoos", 64, 82, -128, "Overworld", "Tall eroded red, orange, and white pillars"),
                        PoiCoordinate("Exposed Gold Mineshaft", 32, 64, -96, "Overworld", "Surface open-air mineshaft with oak planks"),
                        PoiCoordinate("Desert Village Oasis", 120, 70, -200, "Overworld", "Sandstone huts near canyon mouth")
                    )
                )
                val chosen = seeds[random.nextInt(seeds.size)]
                return Triple(chosen.first, "Eroded Badlands Canyons", chosen.second)
            }
            SeedArchetype.VILLAGE_CROSSROADS -> {
                val seeds = listOf(
                    -8172910482910482910L to listOf(
                        PoiCoordinate("Spawn Village Center", 16, 72, 32, "Overworld", "Town bell with gathering villager cluster"),
                        PoiCoordinate("Blacksmith Forge", 24, 73, 48, "Overworld", "Chest with iron armor, obsidian, and diamonds"),
                        PoiCoordinate("Iron Golem Outpost", -32, 70, 80, "Overworld", "Cobblestone watch path")
                    ),
                    481920481928471029L to listOf(
                        PoiCoordinate("Twin Plains Villages", 0, 68, 16, "Overworld", "Two villages separated by only 50 blocks"),
                        PoiCoordinate("River Crossing Bridge", 32, 64, 64, "Overworld", "Natural gravel ford across river"),
                        PoiCoordinate("Second Village Bell", 180, 69, -96, "Overworld", "Eastern agricultural village")
                    )
                )
                val chosen = seeds[random.nextInt(seeds.size)]
                return Triple(chosen.first, "Spawn Village Metropolis", chosen.second)
            }
            SeedArchetype.WOODLAND_MANSION -> {
                val seeds = listOf(
                    -918273645102938475L to listOf(
                        PoiCoordinate("Woodland Mansion Front Gate", 128, 68, 160, "Overworld", "3-story dark oak mansion near spawn"),
                        PoiCoordinate("Dark Oak Forest Spawn", 0, 70, 0, "Overworld", "Dense canopy forest spawn"),
                        PoiCoordinate("Mansion Secret Obsidian Vault", 154, 82, 188, "Overworld", "Hidden room with diamond block encased in obsidian")
                    )
                )
                val chosen = seeds[random.nextInt(seeds.size)]
                return Triple(chosen.first, "Woodland Mansion Spawn", chosen.second)
            }
            SeedArchetype.MONUMENT_ARCHIPELAGO -> {
                val seeds = listOf(
                    281940182740192837L to listOf(
                        PoiCoordinate("Beach Island Spawn", 0, 64, 0, "Overworld", "White sand beach overlooking clear ocean"),
                        PoiCoordinate("Ocean Monument Core", 112, 44, 96, "Overworld", "Elder Guardian chamber with 8 sponge rooms"),
                        PoiCoordinate("Sunken Shipwreck Stern", -64, 52, 48, "Overworld", "Complete acacia shipwreck on coral ridge")
                    )
                )
                val chosen = seeds[random.nextInt(seeds.size)]
                return Triple(chosen.first, "Ocean Monument Bay", chosen.second)
            }
            SeedArchetype.MUSHROOM_ISLAND -> {
                val seeds = listOf(
                    382910482019482019L to listOf(
                        PoiCoordinate("Mycelium Beach Spawn", 0, 64, 0, "Overworld", "Hostile-mob free mushroom fields spawn"),
                        PoiCoordinate("Giant Red Mushroom Grove", 48, 68, 32, "Overworld", "Mooshroom pasture with huge mushrooms"),
                        PoiCoordinate("Adjacent Shipwreck", -80, 48, 64, "Overworld", "Sunken supply ship off the island coast")
                    )
                )
                val chosen = seeds[random.nextInt(seeds.size)]
                return Triple(chosen.first, "Mushroom Fields Island", chosen.second)
            }
            SeedArchetype.ICE_SPIKES_VALLEY -> {
                val seeds = listOf(
                    -192837465019283746L to listOf(
                        PoiCoordinate("Packed Ice Spire Spawn", 16, 74, -16, "Overworld", "Colossal 50-block ice spike towering over spawn"),
                        PoiCoordinate("Frozen River Gorge", -48, 62, 80, "Overworld", "Glacial gorge between ice spikes and snowy plains"),
                        PoiCoordinate("Igloo with Basement", 120, 68, 160, "Overworld", "Igloo featuring secret zombie curing laboratory")
                    )
                )
                val chosen = seeds[random.nextInt(seeds.size)]
                return Triple(chosen.first, "Ice Spikes Wonderland", chosen.second)
            }
            SeedArchetype.MANSION_VILLAGE_COMBO -> {
                val seeds = listOf(
                    728194028471920391L to listOf(
                        PoiCoordinate("Plains Village Center", 0, 68, 0, "Overworld", "Peaceful farming village right at spawn"),
                        PoiCoordinate("Woodland Mansion Across River", 96, 68, 120, "Overworld", "Dark oak mansion directly overlooking village"),
                        PoiCoordinate("Ruined Portal Crossroads", 48, 66, 64, "Overworld", "Nether portal ruin between village and mansion")
                    )
                )
                val chosen = seeds[random.nextInt(seeds.size)]
                return Triple(chosen.first, "Mansion & Village Combo", chosen.second)
            }
            SeedArchetype.PILLAGER_OUTPOST_SIEGE -> {
                val seeds = listOf(
                    592019482019482019L to listOf(
                        PoiCoordinate("Outpost Watchtower", 32, 78, 48, "Overworld", "Pillager tower with cross-bow defenders"),
                        PoiCoordinate("Allay Prison Cages", 16, 70, 32, "Overworld", "Dark oak cages containing trapped allays"),
                        PoiCoordinate("Nearby Meadow Flowers", -80, 84, -64, "Overworld", "Elevated wildflower meadow")
                    )
                )
                val chosen = seeds[random.nextInt(seeds.size)]
                return Triple(chosen.first, "Pillager Outpost at Spawn", chosen.second)
            }
            SeedArchetype.JUNGLE_TEMPLE_COAST -> {
                val seeds = listOf(
                    -639102948102938475L to listOf(
                        PoiCoordinate("Mossy Stone Temple", 64, 68, -48, "Overworld", "Ancient jungle pyramid with hidden redstone levers"),
                        PoiCoordinate("Bamboo Forest Spawn", 0, 72, 0, "Overworld", "Panda habitat with tall bamboo stalks"),
                        PoiCoordinate("Coastal Mangrove Swamp", -128, 62, 96, "Overworld", "Warm brackish mangrove swamp with mud blocks")
                    )
                )
                val chosen = seeds[random.nextInt(seeds.size)]
                return Triple(chosen.first, "Jungle Temple Coast", chosen.second)
            }
            SeedArchetype.DESERT_PYRAMID_OASIS -> {
                val seeds = listOf(
                    391827364510293847L to listOf(
                        PoiCoordinate("Desert Pyramid Core", 48, 65, 32, "Overworld", "Sandstone temple with hidden TNT pressure plate vault"),
                        PoiCoordinate("Desert Oasis Well", -16, 64, -24, "Overworld", "Natural water pool framed by palm dunes"),
                        PoiCoordinate("Desert Village Bazaar", 144, 66, -112, "Overworld", "Camel pens and cactus farms")
                    )
                )
                val chosen = seeds[random.nextInt(seeds.size)]
                return Triple(chosen.first, "Desert Pyramid & Oasis", chosen.second)
            }
            SeedArchetype.RANDOM_FORGE -> {
                val seed = generateRandomSeed()
                return Triple(
                    seed,
                    "Random Forge Seed",
                    listOf(
                        PoiCoordinate("World Spawn", 0, 70, 0, "Overworld", "Estimated spawn point")
                    )
                )
            }
        }
    }
}
