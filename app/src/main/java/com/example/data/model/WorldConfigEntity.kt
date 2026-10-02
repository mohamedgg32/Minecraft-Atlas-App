package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "world_configs")
data class WorldConfigEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val minecraftVersion: String,
    val loader: String, // "Fabric", "Forge", or "Vanilla"
    val modIds: String, // Comma separated mod IDs
    val description: String,
    val isDefault: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
