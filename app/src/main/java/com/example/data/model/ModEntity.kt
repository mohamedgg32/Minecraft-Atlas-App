package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "mods")
data class ModEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val minecraftVersion: String,
    val loader: String, // "Fabric" or "Forge" (NEVER NeoForge)
    val category: String, // from ModCategory displayName
    val description: String,
    val worldGenFeatures: String,
    val isPreinstalled: Boolean = false,
    val isEnabled: Boolean = true
)
