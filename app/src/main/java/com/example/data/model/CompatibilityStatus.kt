package com.example.data.model

enum class CompatibilityLevel(val label: String, val badgeColorHex: Long) {
    VERIFIED_COMPATIBLE("Verified Compatible", 0xFF10B981), // Emerald Green
    COMPATIBLE_WITH_CAVEATS("Compatible with Caveats", 0xFFF59E0B), // Amber / Gold
    TERRAIN_INCOMPATIBLE("Terrain Incompatible", 0xFFEF4444), // Red
    UNVERIFIED_UNKNOWN("Unverified / Unknown", 0xFF6B7280) // Muted Gray
}

data class VersionCompatibilityReport(
    val targetVersion: String,
    val evaluatedVersion: String,
    val level: CompatibilityLevel,
    val explanation: String,
    val biomeSimilarityPercent: Int,
    val structureConsistency: String
)
