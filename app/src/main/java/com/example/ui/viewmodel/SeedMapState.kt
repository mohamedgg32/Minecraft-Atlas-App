package com.example.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.domain.worldmap.MapBiome
import com.example.domain.worldmap.MapStructure
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.math.sqrt

/**
 * Representation of the customizable layers on the interactive Minecraft seed map.
 */
enum class MapLayer(val displayName: String, val description: String) {
    BIOMES("Biomes", "Color-coded multi-noise biome distribution"),
    VILLAGES("Villages", "Plains, Desert, Savanna, Taiga, Snowy villages"),
    STRUCTURES("Structures", "Ancient Cities, Strongholds, Monuments, Temples, etc."),
    SPAWN("Spawn", "Player compass spawn point beacon & radius"),
    TERRAIN("Terrain", "Subtle height & shading elevation relief"),
    OCEANS("Oceans", "Ocean depth highlighting and coastlines"),
    COORDINATES("Coordinates", "Grid lines and coordinate target HUD")
}

/**
 * Details shown when a user inspects any area or biome on the map.
 */
data class MapInspection(
    val biome: MapBiome,
    val x: Int,
    val z: Int,
    val distanceFromSpawn: Int
)

/**
 * ViewModel managing the interactive World Map state for 'Seed Generate'.
 *
 * Persists zoom level, center coordinates, and active map layers across
 * Android configuration changes (screen rotations, font changes, etc.)
 * using SavedStateHandle, and provides smooth, responsive pan & zoom mechanics.
 */
class SeedMapState(
    private val savedStateHandle: SavedStateHandle = SavedStateHandle()
) : ViewModel() {

    companion object {
        const val KEY_CENTER_X = "seed_map_center_x"
        const val KEY_CENTER_Z = "seed_map_center_z"
        const val KEY_ZOOM = "seed_map_zoom"
        const val KEY_LAYER_BIOMES = "seed_map_layer_biomes"
        const val KEY_LAYER_VILLAGES = "seed_map_layer_villages"
        const val KEY_LAYER_STRUCTURES = "seed_map_layer_structures"
        const val KEY_LAYER_SPAWN = "seed_map_layer_spawn"
        const val KEY_LAYER_TERRAIN = "seed_map_layer_terrain"
        const val KEY_LAYER_OCEANS = "seed_map_layer_oceans"
        const val KEY_LAYER_COORDINATES = "seed_map_layer_coordinates"
        const val KEY_STRUCTURE_SEARCH = "seed_map_structure_search"
        const val KEY_BIOME_SEARCH = "seed_map_biome_search"

        const val MIN_ZOOM = 0.25f
        const val MAX_ZOOM = 6.0f
        const val DEFAULT_ZOOM = 1.0f
    }

    // --- Center World Coordinates ---
    val centerX: StateFlow<Float> = savedStateHandle.getStateFlow(KEY_CENTER_X, 0f)
    val centerZ: StateFlow<Float> = savedStateHandle.getStateFlow(KEY_CENTER_Z, 0f)

    // --- Zoom Level ---
    val zoom: StateFlow<Float> = savedStateHandle.getStateFlow(KEY_ZOOM, DEFAULT_ZOOM)

    // --- Map Layers (Persisted across configuration changes) ---
    val layerBiomes: StateFlow<Boolean> = savedStateHandle.getStateFlow(KEY_LAYER_BIOMES, true)
    val layerVillages: StateFlow<Boolean> = savedStateHandle.getStateFlow(KEY_LAYER_VILLAGES, true)
    val layerStructures: StateFlow<Boolean> = savedStateHandle.getStateFlow(KEY_LAYER_STRUCTURES, true)
    val layerSpawn: StateFlow<Boolean> = savedStateHandle.getStateFlow(KEY_LAYER_SPAWN, true)
    val layerTerrain: StateFlow<Boolean> = savedStateHandle.getStateFlow(KEY_LAYER_TERRAIN, true)
    val layerOceans: StateFlow<Boolean> = savedStateHandle.getStateFlow(KEY_LAYER_OCEANS, true)
    val layerCoordinates: StateFlow<Boolean> = savedStateHandle.getStateFlow(KEY_LAYER_COORDINATES, true)

    // --- Search Queries ---
    val structureSearchQuery: StateFlow<String> = savedStateHandle.getStateFlow(KEY_STRUCTURE_SEARCH, "")
    val biomeSearchQuery: StateFlow<String> = savedStateHandle.getStateFlow(KEY_BIOME_SEARCH, "")

    // --- Selected / Inspected Items (Runtime State) ---
    private val _selectedStructure = MutableStateFlow<MapStructure?>(null)
    val selectedStructure: StateFlow<MapStructure?> = _selectedStructure.asStateFlow()

    private val _inspectedLocation = MutableStateFlow<MapInspection?>(null)
    val inspectedLocation: StateFlow<MapInspection?> = _inspectedLocation.asStateFlow()

    // ==========================================
    // SMOOTH PANNING & CENTERING INTERACTIONS
    // ==========================================

    /**
     * Pan the map by a delta offset in world blocks.
     */
    fun pan(deltaX: Float, deltaZ: Float) {
        val currentX = centerX.value
        val currentZ = centerZ.value
        savedStateHandle[KEY_CENTER_X] = currentX + deltaX
        savedStateHandle[KEY_CENTER_Z] = currentZ + deltaZ
    }

    /**
     * Center the map at the given world coordinates.
     */
    fun centerOn(x: Float, z: Float) {
        savedStateHandle[KEY_CENTER_X] = x
        savedStateHandle[KEY_CENTER_Z] = z
    }

    /**
     * Jump directly to integer coordinates.
     */
    fun jumpTo(x: Int, z: Int) {
        centerOn(x.toFloat(), z.toFloat())
    }

    /**
     * Center smoothly on player spawn.
     */
    fun centerOnSpawn(spawnX: Int, spawnZ: Int) {
        savedStateHandle[KEY_CENTER_X] = spawnX.toFloat()
        savedStateHandle[KEY_CENTER_Z] = spawnZ.toFloat()
    }

    // ==========================================
    // SMOOTH ZOOM INTERACTIONS
    // ==========================================

    /**
     * Set explicit zoom clamped to safe limits.
     */
    fun setZoom(newZoom: Float) {
        savedStateHandle[KEY_ZOOM] = newZoom.coerceIn(MIN_ZOOM, MAX_ZOOM)
    }

    /**
     * Multiply current zoom by a gesture or step factor.
     */
    fun zoomBy(factor: Float) {
        setZoom(zoom.value * factor)
    }

    /**
     * Step zoom in (+35%).
     */
    fun zoomIn() {
        zoomBy(1.35f)
    }

    /**
     * Step zoom out (-26%).
     */
    fun zoomOut() {
        zoomBy(1.0f / 1.35f)
    }

    /**
     * Reset zoom to default 1.0f.
     */
    fun resetZoom() {
        savedStateHandle[KEY_ZOOM] = DEFAULT_ZOOM
    }

    // ==========================================
    // LAYER TOGGLES
    // ==========================================

    fun toggleLayerBiomes() {
        savedStateHandle[KEY_LAYER_BIOMES] = !layerBiomes.value
    }

    fun toggleLayerVillages() {
        savedStateHandle[KEY_LAYER_VILLAGES] = !layerVillages.value
    }

    fun toggleLayerStructures() {
        savedStateHandle[KEY_LAYER_STRUCTURES] = !layerStructures.value
    }

    fun toggleLayerSpawn() {
        savedStateHandle[KEY_LAYER_SPAWN] = !layerSpawn.value
    }

    fun toggleLayerTerrain() {
        savedStateHandle[KEY_LAYER_TERRAIN] = !layerTerrain.value
    }

    fun toggleLayerOceans() {
        savedStateHandle[KEY_LAYER_OCEANS] = !layerOceans.value
    }

    fun toggleLayerCoordinates() {
        savedStateHandle[KEY_LAYER_COORDINATES] = !layerCoordinates.value
    }

    fun toggleLayer(layer: MapLayer) {
        when (layer) {
            MapLayer.BIOMES -> toggleLayerBiomes()
            MapLayer.VILLAGES -> toggleLayerVillages()
            MapLayer.STRUCTURES -> toggleLayerStructures()
            MapLayer.SPAWN -> toggleLayerSpawn()
            MapLayer.TERRAIN -> toggleLayerTerrain()
            MapLayer.OCEANS -> toggleLayerOceans()
            MapLayer.COORDINATES -> toggleLayerCoordinates()
        }
    }

    fun setLayer(layer: MapLayer, enabled: Boolean) {
        when (layer) {
            MapLayer.BIOMES -> savedStateHandle[KEY_LAYER_BIOMES] = enabled
            MapLayer.VILLAGES -> savedStateHandle[KEY_LAYER_VILLAGES] = enabled
            MapLayer.STRUCTURES -> savedStateHandle[KEY_LAYER_STRUCTURES] = enabled
            MapLayer.SPAWN -> savedStateHandle[KEY_LAYER_SPAWN] = enabled
            MapLayer.TERRAIN -> savedStateHandle[KEY_LAYER_TERRAIN] = enabled
            MapLayer.OCEANS -> savedStateHandle[KEY_LAYER_OCEANS] = enabled
            MapLayer.COORDINATES -> savedStateHandle[KEY_LAYER_COORDINATES] = enabled
        }
    }

    fun resetLayers() {
        savedStateHandle[KEY_LAYER_BIOMES] = true
        savedStateHandle[KEY_LAYER_VILLAGES] = true
        savedStateHandle[KEY_LAYER_STRUCTURES] = true
        savedStateHandle[KEY_LAYER_SPAWN] = true
        savedStateHandle[KEY_LAYER_TERRAIN] = true
        savedStateHandle[KEY_LAYER_OCEANS] = true
        savedStateHandle[KEY_LAYER_COORDINATES] = true
    }

    // ==========================================
    // SELECTION & INSPECTION
    // ==========================================

    fun selectStructure(structure: MapStructure?) {
        _selectedStructure.value = structure
        if (structure != null) {
            _inspectedLocation.value = null
            centerOn(structure.x.toFloat(), structure.z.toFloat())
        }
    }

    fun clearSelectedStructure() {
        _selectedStructure.value = null
    }

    fun inspectLocation(biome: MapBiome, x: Int, z: Int, spawnX: Int, spawnZ: Int) {
        _selectedStructure.value = null
        val dx = (x - spawnX).toDouble()
        val dz = (z - spawnZ).toDouble()
        val dist = sqrt(dx * dx + dz * dz).toInt()
        _inspectedLocation.value = MapInspection(
            biome = biome,
            x = x,
            z = z,
            distanceFromSpawn = dist
        )
    }

    fun clearInspection() {
        _inspectedLocation.value = null
    }

    // ==========================================
    // SEARCH
    // ==========================================

    fun setStructureSearchQuery(query: String) {
        savedStateHandle[KEY_STRUCTURE_SEARCH] = query
    }

    fun setBiomeSearchQuery(query: String) {
        savedStateHandle[KEY_BIOME_SEARCH] = query
    }
}
