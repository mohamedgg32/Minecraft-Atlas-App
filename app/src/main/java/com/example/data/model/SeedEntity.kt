package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "seeds")
data class SeedEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val seedValue: Long,
    val seedText: String,
    val title: String,
    val targetVersion: String,
    val loader: String, // "Vanilla", "Fabric", "Forge"
    val configName: String,
    val category: String, // Archetype or user category
    val primaryBiome: String,
    val spawnCoordinates: String,
    val poiCoordinatesJson: String, // JSON formatted POIs
    val verifiedVersions: String, // Comma separated, e.g., "1.21.x"
    val notes: String,
    val selectedMods: String = "", // Comma-separated list of active worldgen mods (e.g. "Terralith, Tectonic")
    val isFavorite: Boolean = false,
    val tags: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
