package com.example.domain.worldmap

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Castle
import androidx.compose.material.icons.filled.Dangerous
import androidx.compose.material.icons.filled.DirectionsBoat
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Landscape
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Warning
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

enum class StructureCategory(val label: String) {
    VILLAGE("Villages"),
    STRONGHOLD("Strongholds"),
    ANCIENT_CITY("Ancient Cities"),
    TRIAL_CHAMBER("Trial Chambers"),
    MONUMENT("Ocean Monuments"),
    MANSION("Woodland Mansions"),
    OUTPOST("Pillager Outposts"),
    TEMPLE("Desert / Jungle Temples"),
    SHIPWRECK("Shipwrecks & Ruins"),
    MINESHAFT("Mineshafts"),
    RUINED_PORTAL("Ruined Portals"),
    WITCH_HUT("Witch Huts"),
    TRAIL_RUINS("Trail Ruins"),
    NETHER("Nether Structures"),
    MODDED("Modded Structures")
}

data class MapStructure(
    val id: String,
    val name: String,
    val x: Int,
    val y: Int,
    val z: Int,
    val category: StructureCategory,
    val dimension: String = "Overworld",
    val description: String = "",
    val isModded: Boolean = false,
    val modSource: String = ""
) {
    fun distanceFrom(otherX: Int, otherZ: Int): Int {
        val dx = (x - otherX).toDouble()
        val dz = (z - otherZ).toDouble()
        return kotlin.math.sqrt(dx * dx + dz * dz).toInt()
    }

    val badgeColor: Color
        get() = when (category) {
            StructureCategory.VILLAGE -> Color(0xFF66BB6A) // Emerald Green
            StructureCategory.STRONGHOLD -> Color(0xFF9C27B0) // Eye of Ender Purple
            StructureCategory.ANCIENT_CITY -> Color(0xFF00E5FF) // Skulk Cyan
            StructureCategory.TRIAL_CHAMBER -> Color(0xFFFF9800) // Copper Orange
            StructureCategory.MONUMENT -> Color(0xFF00B0FF) // Prismarine Aqua
            StructureCategory.MANSION -> Color(0xFF8D6E63) // Dark Oak Wood
            StructureCategory.OUTPOST -> Color(0xFFE53935) // Illager Crimson
            StructureCategory.TEMPLE -> Color(0xFFFFD54F) // Sandstone Gold
            StructureCategory.SHIPWRECK -> Color(0xFF4FC3F7) // Ocean Blue
            StructureCategory.MINESHAFT -> Color(0xFFB0BEC5) // Iron Rail Silver
            StructureCategory.RUINED_PORTAL -> Color(0xFF7E57C2) // Obsidian Purple
            StructureCategory.WITCH_HUT -> Color(0xFF2E7D32) // Swamp Murk
            StructureCategory.TRAIL_RUINS -> Color(0xFFD7CCC8) // Terracotta Gravel
            StructureCategory.NETHER -> Color(0xFFFF5722) // Nether Fire
            StructureCategory.MODDED -> Color(0xFFAB47BC) // Fabric Amethyst
        }

    val iconVector: ImageVector
        get() = when (category) {
            StructureCategory.VILLAGE -> Icons.Default.Home
            StructureCategory.STRONGHOLD -> Icons.Default.Security
            StructureCategory.ANCIENT_CITY -> Icons.Default.LocationCity
            StructureCategory.TRIAL_CHAMBER -> Icons.Default.Shield
            StructureCategory.MONUMENT -> Icons.Default.Castle
            StructureCategory.MANSION -> Icons.Default.Castle
            StructureCategory.OUTPOST -> Icons.Default.Dangerous
            StructureCategory.TEMPLE -> Icons.Default.Place
            StructureCategory.SHIPWRECK -> Icons.Default.DirectionsBoat
            StructureCategory.MINESHAFT -> Icons.Default.Build
            StructureCategory.RUINED_PORTAL -> Icons.Default.FlashOn
            StructureCategory.WITCH_HUT -> Icons.Default.Home
            StructureCategory.TRAIL_RUINS -> Icons.Default.Landscape
            StructureCategory.NETHER -> Icons.Default.Warning
            StructureCategory.MODDED -> Icons.Default.Castle
        }
}
