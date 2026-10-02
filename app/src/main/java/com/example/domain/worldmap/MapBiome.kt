package com.example.domain.worldmap

import androidx.compose.ui.graphics.Color

enum class BiomeCategory {
    PLAINS,
    FOREST,
    DESERT,
    JUNGLE,
    SNOWY,
    CHERRY_GROVE,
    MOUNTAINS,
    OCEAN,
    DEEP_OCEAN,
    SWAMP,
    BADLANDS,
    MUSHROOM_ISLAND,
    CAVE_DEEP_DARK,
    MODDED
}

data class MapBiome(
    val id: String,
    val name: String,
    val color: Color,
    val category: BiomeCategory,
    val description: String,
    val isModded: Boolean = false
) {
    companion object {
        val PLAINS = MapBiome(
            id = "minecraft:plains",
            name = "Plains",
            color = Color(0xFF8DB360),
            category = BiomeCategory.PLAINS,
            description = "Grassy temperate plains ideal for settlements and horses."
        )

        val SUNFLOWER_PLAINS = MapBiome(
            id = "minecraft:sunflower_plains",
            name = "Sunflower Plains",
            color = Color(0xFFA5CD70),
            category = BiomeCategory.PLAINS,
            description = "Vibrant plains carpeted with sun-facing sunflowers."
        )

        val FOREST = MapBiome(
            id = "minecraft:forest",
            name = "Forest",
            color = Color(0xFF2C6B28),
            category = BiomeCategory.FOREST,
            description = "Standard oak and birch woodland."
        )

        val BIRCH_FOREST = MapBiome(
            id = "minecraft:birch_forest",
            name = "Birch Forest",
            color = Color(0xFF388540),
            category = BiomeCategory.FOREST,
            description = "Bright light woodland dominated by white birch trees."
        )

        val DARK_FOREST = MapBiome(
            id = "minecraft:dark_forest",
            name = "Dark Forest",
            color = Color(0xFF143B10),
            category = BiomeCategory.FOREST,
            description = "Dense canopy of dark oak trees where monsters spawn even by day; home to Woodland Mansions."
        )

        val FLOWER_FOREST = MapBiome(
            id = "minecraft:flower_forest",
            name = "Flower Forest",
            color = Color(0xFF3D944B),
            category = BiomeCategory.FOREST,
            description = "Lush hilly forest covered in rare allium, tulips, and roses."
        )

        val DESERT = MapBiome(
            id = "minecraft:desert",
            name = "Desert",
            color = Color(0xFFD6C275),
            category = BiomeCategory.DESERT,
            description = "Arid sandstone and dunes with cacti, desert pyramids, and desert wells."
        )

        val SAVANNA = MapBiome(
            id = "minecraft:savanna",
            name = "Savanna",
            color = Color(0xFFB5AC55),
            category = BiomeCategory.PLAINS,
            description = "Dry golden grass with acacia trees and wandering livestock."
        )

        val WINDSWEPT_SAVANNA = MapBiome(
            id = "minecraft:windswept_savanna",
            name = "Windswept Savanna",
            color = Color(0xFF9E9646),
            category = BiomeCategory.MOUNTAINS,
            description = "Shattered savanna with vertical floating plateaus and massive overhangs."
        )

        val JUNGLE = MapBiome(
            id = "minecraft:jungle",
            name = "Jungle",
            color = Color(0xFF4C7F0C),
            category = BiomeCategory.JUNGLE,
            description = "Thick tropical rainforest with giant 2x2 jungle trees, vines, parrots, and jungle temples."
        )

        val BAMBOO_JUNGLE = MapBiome(
            id = "minecraft:bamboo_jungle",
            name = "Bamboo Jungle",
            color = Color(0xFF6F9915),
            category = BiomeCategory.JUNGLE,
            description = "Dense bamboo groves home to pandas."
        )

        val CHERRY_GROVE = MapBiome(
            id = "minecraft:cherry_grove",
            name = "Cherry Grove",
            color = Color(0xFFFFB6C9),
            category = BiomeCategory.CHERRY_GROVE,
            description = "Serene mountain biome covered in pink blossoming cherry trees and falling petals."
        )

        val MEADOW = MapBiome(
            id = "minecraft:meadow",
            name = "Meadow",
            color = Color(0xFF679448),
            category = BiomeCategory.PLAINS,
            description = "Elevated grassy plateau speckled with turquoise and yellow flowers."
        )

        val SNOWY_PLAINS = MapBiome(
            id = "minecraft:snowy_plains",
            name = "Snowy Plains",
            color = Color(0xFFE4F0F2),
            category = BiomeCategory.SNOWY,
            description = "Frozen tundra covered in deep snow sheets and packed ice lakes."
        )

        val SNOWY_TAIGA = MapBiome(
            id = "minecraft:snowy_taiga",
            name = "Snowy Taiga",
            color = Color(0xFF385E54),
            category = BiomeCategory.SNOWY,
            description = "Cold coniferous evergreen forest blanketed in powdery snow."
        )

        val TAIGA = MapBiome(
            id = "minecraft:taiga",
            name = "Taiga",
            color = Color(0xFF1B6152),
            category = BiomeCategory.FOREST,
            description = "Temperate pine and spruce forest with sweet berry bushes and wolves."
        )

        val OLD_GROWTH_TAIGA = MapBiome(
            id = "minecraft:old_growth_pine_taiga",
            name = "Old Growth Taiga",
            color = Color(0xFF324F3F),
            category = BiomeCategory.FOREST,
            description = "Gigantic 2x2 spruce pines with podzol soil and mossy cobblestone boulders."
        )

        val JAGGED_PEAKS = MapBiome(
            id = "minecraft:jagged_peaks",
            name = "Jagged Peaks",
            color = Color(0xFFCFD9DF),
            category = BiomeCategory.MOUNTAINS,
            description = "Dramatic alpine mountain horns crowned with snow and glaciers up to Y=260."
        )

        val FROZEN_PEAKS = MapBiome(
            id = "minecraft:frozen_peaks",
            name = "Frozen Peaks",
            color = Color(0xFFB5CADB),
            category = BiomeCategory.MOUNTAINS,
            description = "Glaciated mountain peaks covered in ice and packed snow."
        )

        val STONY_PEAKS = MapBiome(
            id = "minecraft:stony_peaks",
            name = "Stony Peaks",
            color = Color(0xFF7D838A),
            category = BiomeCategory.MOUNTAINS,
            description = "Warm rugged mountain tops exposed to calcite strips and stone."
        )

        val SWAMP = MapBiome(
            id = "minecraft:swamp",
            name = "Swamp",
            color = Color(0xFF367554),
            category = BiomeCategory.SWAMP,
            description = "Murky teal wetlands with lily pads, witch huts, and slime spawns."
        )

        val MANGROVE_SWAMP = MapBiome(
            id = "minecraft:mangrove_swamp",
            name = "Mangrove Swamp",
            color = Color(0xFF5A6642),
            category = BiomeCategory.SWAMP,
            description = "Warm waterlogged mud flats with towering prop-rooted mangrove trees."
        )

        val BADLANDS = MapBiome(
            id = "minecraft:badlands",
            name = "Badlands",
            color = Color(0xFFD6572A),
            category = BiomeCategory.BADLANDS,
            description = "Stratified terracotta canyons with abundant gold deposits."
        )

        val ERODED_BADLANDS = MapBiome(
            id = "minecraft:eroded_badlands",
            name = "Eroded Badlands",
            color = Color(0xFFB03F1E),
            category = BiomeCategory.BADLANDS,
            description = "Striated terracotta hoodoos and spires resembling Bryce Canyon."
        )

        val OCEAN = MapBiome(
            id = "minecraft:ocean",
            name = "Ocean",
            color = Color(0xFF184E9E),
            category = BiomeCategory.OCEAN,
            description = "Expansive marine blue waters with sea grass, kelp, and dolphins."
        )

        val DEEP_OCEAN = MapBiome(
            id = "minecraft:deep_ocean",
            name = "Deep Ocean",
            color = Color(0xFF0D2A6B),
            category = BiomeCategory.DEEP_OCEAN,
            description = "Abyssal oceanic depths hiding Ocean Monuments and shipwrecks."
        )

        val WARM_OCEAN = MapBiome(
            id = "minecraft:warm_ocean",
            name = "Warm Ocean",
            color = Color(0xFF009696),
            category = BiomeCategory.OCEAN,
            description = "Vibrant turquoise shallows filled with colorful coral reefs."
        )

        val FROZEN_OCEAN = MapBiome(
            id = "minecraft:frozen_ocean",
            name = "Frozen Ocean",
            color = Color(0xFF5C72A8),
            category = BiomeCategory.OCEAN,
            description = "Icy ocean dotted with icebergs and polar bears."
        )

        val DEEP_FROZEN_OCEAN = MapBiome(
            id = "minecraft:deep_frozen_ocean",
            name = "Deep Frozen Ocean",
            color = Color(0xFF40558A),
            category = BiomeCategory.DEEP_OCEAN,
            description = "Deep icy ocean depths beneath sheets of packed ice."
        )

        val MUSHROOM_FIELDS = MapBiome(
            id = "minecraft:mushroom_fields",
            name = "Mushroom Fields",
            color = Color(0xFF9E659A),
            category = BiomeCategory.MUSHROOM_ISLAND,
            description = "Extremely rare isolated island of purple mycelium where hostile mobs cannot spawn."
        )

        val DEEP_DARK = MapBiome(
            id = "minecraft:deep_dark",
            name = "Deep Dark",
            color = Color(0xFF03222B),
            category = BiomeCategory.CAVE_DEEP_DARK,
            description = "Subterranean skulk depths beneath mountain ranges containing Ancient Cities."
        )

        // Modded Biomes (Terralith / Fabric)
        val TERRALITH_VOLCANIC_CRATER = MapBiome(
            id = "terralith:volcanic_crater",
            name = "Volcanic Caldera (Terralith)",
            color = Color(0xFF4A1B1B),
            category = BiomeCategory.MODDED,
            description = "Active geothermal crater with blackstone caldera rims and lava pools.",
            isModded = true
        )

        val TERRALITH_PAINTED_CANYON = MapBiome(
            id = "terralith:painted_canyon",
            name = "Painted Canyon (Terralith)",
            color = Color(0xFFBD6134),
            category = BiomeCategory.MODDED,
            description = "Multi-colored striped sandstone gorge with winding rivers.",
            isModded = true
        )

        val TERRALITH_YELLOWSTONE = MapBiome(
            id = "terralith:yellowstone",
            name = "Yellowstone Geothermal (Terralith)",
            color = Color(0xFFD49B28),
            category = BiomeCategory.MODDED,
            description = "Hydrothermal geysers, sulfur flats, and thermal mineral springs.",
            isModded = true
        )

        val TERRALITH_ALPINE_STEPPE = MapBiome(
            id = "terralith:alpine_steppe",
            name = "Alpine Steppe (Terralith)",
            color = Color(0xFF75855A),
            category = BiomeCategory.MODDED,
            description = "High altitude cold plateau surrounded by jagged precipices.",
            isModded = true
        )

        val ALL_BIOMES = listOf(
            PLAINS, SUNFLOWER_PLAINS, FOREST, BIRCH_FOREST, DARK_FOREST, FLOWER_FOREST,
            DESERT, SAVANNA, WINDSWEPT_SAVANNA, JUNGLE, BAMBOO_JUNGLE,
            CHERRY_GROVE, MEADOW, SNOWY_PLAINS, SNOWY_TAIGA, TAIGA, OLD_GROWTH_TAIGA,
            JAGGED_PEAKS, FROZEN_PEAKS, STONY_PEAKS, SWAMP, MANGROVE_SWAMP,
            BADLANDS, ERODED_BADLANDS, OCEAN, DEEP_OCEAN, WARM_OCEAN, FROZEN_OCEAN,
            MUSHROOM_FIELDS, DEEP_DARK,
            TERRALITH_VOLCANIC_CRATER, TERRALITH_PAINTED_CANYON, TERRALITH_YELLOWSTONE, TERRALITH_ALPINE_STEPPE
        )
    }
}
