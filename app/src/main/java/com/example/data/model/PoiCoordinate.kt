package com.example.data.model

data class PoiCoordinate(
    val name: String,
    val x: Int,
    val y: Int,
    val z: Int,
    val dimension: String = "Overworld",
    val description: String = ""
) {
    fun toTpCommand(): String {
        val dimStr = when (dimension.lowercase()) {
            "nether" -> "minecraft:the_nether"
            "end" -> "minecraft:the_end"
            else -> "minecraft:overworld"
        }
        return "/execute in $dimStr run tp @s $x $y $z"
    }

    fun toCoordinatesString(): String = "X: $x, Y: $y, Z: $z"
}
