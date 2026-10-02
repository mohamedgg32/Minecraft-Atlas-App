package com.example.data.model

enum class ModLoader(val displayName: String) {
    FABRIC("Fabric");

    companion object {
        fun fromString(value: String): ModLoader {
            return FABRIC
        }
    }
}
