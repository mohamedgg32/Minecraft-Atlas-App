package com.example.data.model

enum class ModCategory(val displayName: String, val iconName: String) {
    BIOME_MODS("Biome mods", "forest"),
    TERRAIN_GENERATION("Terrain generation", "landscape"),
    MOUNTAINS("Mountains", "terrain"),
    CAVES("Caves", "cave"),
    OCEANS("Oceans", "water"),
    RIVERS("Rivers", "waves"),
    STRUCTURES("Structures", "castle"),
    VILLAGES("Villages", "holiday_village"),
    DUNGEONS("Dungeons", "fort"),
    FANTASY_TERRAIN("Fantasy terrain", "auto_awesome"),
    OVERWORLD_GENERATION("Overworld generation", "public"),
    NETHER_GENERATION("Nether generation", "local_fire_department"),
    END_GENERATION("End generation", "diamond"),
    NEW_DIMENSIONS("New dimensions", "portal");

    companion object {
        fun fromString(value: String): ModCategory {
            return entries.firstOrNull {
                it.name.equals(value, ignoreCase = true) ||
                it.displayName.equals(value, ignoreCase = true)
            } ?: BIOME_MODS
        }
    }
}
