package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.MinecraftVersion
import com.example.data.model.ModCategory
import com.example.data.model.ModEntity
import com.example.data.model.ModLoader
import com.example.data.model.PoiCoordinate
import com.example.data.model.SeedArchetype
import com.example.data.model.SeedEntity
import com.example.data.model.WorldConfigEntity
import com.example.data.repository.ModRepository
import com.example.data.repository.SeedRepository
import com.example.data.repository.WorldConfigRepository
import com.example.domain.BedrockParityInfo
import com.example.domain.SeedAnalysisResult
import com.example.domain.SeedAnalyzerEngine
import com.example.domain.SeedCriteria
import com.example.domain.SeedGenerationResult
import com.example.domain.SeedGeneratorEngine
import com.example.domain.worldmap.BiomeSearchResult
import com.example.domain.worldmap.MapStructure
import com.example.domain.worldmap.WorldGenerationEngine
import com.example.domain.worldmap.WorldGenerationState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject

class ForgeViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application, viewModelScope)
    private val seedRepo = SeedRepository(database.seedDao())
    private val modRepo = ModRepository(database.modDao())
    private val configRepo = WorldConfigRepository(database.worldConfigDao())

    // --- Seed Input & World Configuration State ---
    private val _currentSeed = MutableStateFlow<Long>(-4270425838048259167L)
    val currentSeed: StateFlow<Long> = _currentSeed.asStateFlow()

    private val _seedInputText = MutableStateFlow("-4270425838048259167")
    val seedInputText: StateFlow<String> = _seedInputText.asStateFlow()

    // Default to latest: Minecraft Java 26.2
    private val _selectedVersion = MutableStateFlow(MinecraftVersion.LATEST_ID)
    val selectedVersion: StateFlow<String> = _selectedVersion.asStateFlow()

    // Fabric ONLY (Rule 2)
    val selectedLoader: String = "Fabric"

    private val _selectedWorldGenMods = MutableStateFlow<Set<String>>(emptySet())
    val selectedWorldGenMods: StateFlow<Set<String>> = _selectedWorldGenMods.asStateFlow()

    private val _selectedArchetype = MutableStateFlow(SeedArchetype.CHERRY_CALDERA)
    val selectedArchetype: StateFlow<SeedArchetype> = _selectedArchetype.asStateFlow()

    // 0: Archetypes, 1: Custom Criteria/Filter, 2: Random 64-bit, 3: Phrase / Word Seed
    private val _selectedGeneratorMode = MutableStateFlow(0)
    val selectedGeneratorMode: StateFlow<Int> = _selectedGeneratorMode.asStateFlow()

    private val _latestGeneratedResult = MutableStateFlow<SeedGenerationResult?>(null)
    val latestGeneratedResult: StateFlow<SeedGenerationResult?> = _latestGeneratedResult.asStateFlow()

    private val _seedCriteria = MutableStateFlow(SeedCriteria())
    val seedCriteria: StateFlow<SeedCriteria> = _seedCriteria.asStateFlow()

    private val _isGenerating = MutableStateFlow(false)
    val isGenerating: StateFlow<Boolean> = _isGenerating.asStateFlow()

    private val _selectedConfig = MutableStateFlow<WorldConfigEntity?>(null)
    val selectedConfig: StateFlow<WorldConfigEntity?> = _selectedConfig.asStateFlow()

    private val _isForgingAnimation = MutableStateFlow(false)
    val isForgingAnimation: StateFlow<Boolean> = _isForgingAnimation.asStateFlow()

    // Compatibility check state
    private val _compatibilityWarnings = MutableStateFlow<List<String>>(emptyList())
    val compatibilityWarnings: StateFlow<List<String>> = _compatibilityWarnings.asStateFlow()

    // --- World Map State (Primary Feature) ---
    private val _worldGenState = MutableStateFlow<WorldGenerationState?>(null)
    val worldGenState: StateFlow<WorldGenerationState?> = _worldGenState.asStateFlow()

    private val _isShowingWorldMap = MutableStateFlow(false)
    val isShowingWorldMap: StateFlow<Boolean> = _isShowingWorldMap.asStateFlow()

    private val _mapCenterX = MutableStateFlow(0f)
    val mapCenterX: StateFlow<Float> = _mapCenterX.asStateFlow()

    private val _mapCenterZ = MutableStateFlow(0f)
    val mapCenterZ: StateFlow<Float> = _mapCenterZ.asStateFlow()

    private val _mapZoom = MutableStateFlow(1.0f)
    val mapZoom: StateFlow<Float> = _mapZoom.asStateFlow()

    private val _selectedStructure = MutableStateFlow<MapStructure?>(null)
    val selectedStructure: StateFlow<MapStructure?> = _selectedStructure.asStateFlow()

    // Map Search state
    private val _structureSearchQuery = MutableStateFlow("")
    val structureSearchQuery: StateFlow<String> = _structureSearchQuery.asStateFlow()

    private val _biomeSearchQuery = MutableStateFlow("")
    val biomeSearchQuery: StateFlow<String> = _biomeSearchQuery.asStateFlow()

    private val _biomeSearchResult = MutableStateFlow<BiomeSearchResult?>(null)
    val biomeSearchResult: StateFlow<BiomeSearchResult?> = _biomeSearchResult.asStateFlow()

    // Map Layer Toggles
    private val _layerBiomes = MutableStateFlow(true)
    val layerBiomes: StateFlow<Boolean> = _layerBiomes.asStateFlow()

    private val _layerStructures = MutableStateFlow(true)
    val layerStructures: StateFlow<Boolean> = _layerStructures.asStateFlow()

    private val _layerVillages = MutableStateFlow(true)
    val layerVillages: StateFlow<Boolean> = _layerVillages.asStateFlow()

    private val _layerTerrain = MutableStateFlow(true)
    val layerTerrain: StateFlow<Boolean> = _layerTerrain.asStateFlow()

    private val _layerSpawn = MutableStateFlow(true)
    val layerSpawn: StateFlow<Boolean> = _layerSpawn.asStateFlow()

    // --- Analyzer & Technical Stats State ---
    private val _currentAnalysis = MutableStateFlow<SeedAnalysisResult?>(null)
    val currentAnalysis: StateFlow<SeedAnalysisResult?> = _currentAnalysis.asStateFlow()

    private val _analyzerSeedInput = MutableStateFlow("-4270425838048259167")
    val analyzerSeedInput: StateFlow<String> = _analyzerSeedInput.asStateFlow()

    private val _analyzerVersion = MutableStateFlow(MinecraftVersion.LATEST_ID)
    val analyzerVersion: StateFlow<String> = _analyzerVersion.asStateFlow()

    private val _analyzerLoader = MutableStateFlow("Fabric")
    val analyzerLoader: StateFlow<String> = _analyzerLoader.asStateFlow()

    private val _analyzerResult = MutableStateFlow<SeedAnalysisResult?>(null)
    val analyzerResult: StateFlow<SeedAnalysisResult?> = _analyzerResult.asStateFlow()

    // --- Notification message ---
    private val _userMessage = MutableStateFlow<String?>(null)
    val userMessage: StateFlow<String?> = _userMessage.asStateFlow()

    // --- Navigation Tab State ---
    // 0: Seed Forge / Map, 1: Library, 2: Fabric Mods, 3: Presets, 4: Analyzer
    private val _currentTab = MutableStateFlow(0)
    val currentTab: StateFlow<Int> = _currentTab.asStateFlow()

    // --- Database Flows ---
    val allModsRaw = modRepo.allMods.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    val allConfigs = configRepo.allConfigs.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    val allSeedsRaw = seedRepo.allSeeds.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    // Library Filter States
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _versionFilter = MutableStateFlow("All")
    val versionFilter: StateFlow<String> = _versionFilter.asStateFlow()

    private val _onlyFavorites = MutableStateFlow(false)
    val onlyFavorites: StateFlow<Boolean> = _onlyFavorites.asStateFlow()

    val filteredSeeds: StateFlow<List<SeedEntity>> = combine(
        allSeedsRaw,
        _searchQuery,
        _versionFilter,
        _onlyFavorites
    ) { seeds, query, vFilter, favs ->
        seeds.filter { seed ->
            val matchesQuery = query.isEmpty() ||
                seed.title.contains(query, ignoreCase = true) ||
                seed.seedValue.toString().contains(query) ||
                seed.tags.contains(query, ignoreCase = true) ||
                seed.primaryBiome.contains(query, ignoreCase = true) ||
                seed.selectedMods.contains(query, ignoreCase = true)

            val matchesVersion = vFilter == "All" || seed.targetVersion.contains(vFilter.replace(".x", ""))
            val matchesFav = !favs || seed.isFavorite

            matchesQuery && matchesVersion && matchesFav
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Mod Catalog Filters
    private val _modCategoryFilter = MutableStateFlow("All")
    val modCategoryFilter: StateFlow<String> = _modCategoryFilter.asStateFlow()

    private val _modSearchQuery = MutableStateFlow("")
    val modSearchQuery: StateFlow<String> = _modSearchQuery.asStateFlow()

    val filteredMods: StateFlow<List<ModEntity>> = combine(
        allModsRaw,
        _modCategoryFilter,
        _modSearchQuery
    ) { mods, category, query ->
        mods.filter { mod ->
            val matchesCategory = category == "All" || mod.category.equals(category, ignoreCase = true)
            val matchesQuery = query.isEmpty() ||
                mod.name.contains(query, ignoreCase = true) ||
                mod.description.contains(query, ignoreCase = true) ||
                mod.worldGenFeatures.contains(query, ignoreCase = true)

            matchesCategory && matchesQuery
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        updateCompatibilityCheck()
        generateArchetype(SeedArchetype.CHERRY_CALDERA)
        runAnalysisForCurrentSeed()
        runAnalyzer()
    }

    // --- Tab Navigation ---
    fun setTab(index: Int) {
        _currentTab.value = index
    }

    // --- Enhanced Seed Generator Actions ---
    fun setGeneratorMode(mode: Int) {
        _selectedGeneratorMode.value = mode
    }

    fun setSelectedArchetype(archetype: SeedArchetype) {
        _selectedArchetype.value = archetype
        generateArchetype(archetype)
    }

    fun generateArchetype(archetype: SeedArchetype = _selectedArchetype.value) {
        val result = SeedGeneratorEngine.generateArchetypeResult(archetype)
        _selectedArchetype.value = archetype
        applyGeneratedResult(result)
    }

    fun generateMnemonicWordSeed() {
        val result = SeedGeneratorEngine.generateMnemonicWordSeed()
        applyGeneratedResult(result)
    }

    fun generateRandomSeed() {
        val result = SeedGeneratorEngine.generateRandomResult()
        applyGeneratedResult(result)
    }

    fun updateCriteria(newCriteria: SeedCriteria) {
        _seedCriteria.value = newCriteria
    }

    fun generateWithCriteria() {
        viewModelScope.launch(Dispatchers.Default) {
            _isGenerating.value = true
            try {
                val result = SeedGeneratorEngine.generateWithCriteria(
                    criteria = _seedCriteria.value,
                    version = _selectedVersion.value,
                    activeMods = _selectedWorldGenMods.value.toList()
                )
                applyGeneratedResult(result)
            } finally {
                _isGenerating.value = false
            }
        }
    }

    fun applyGeneratedResult(result: SeedGenerationResult) {
        _latestGeneratedResult.value = result
        _currentSeed.value = result.seed
        _seedInputText.value = result.seedString
        runAnalysisForCurrentSeed()
        _userMessage.value = "Generated seed: ${result.title} (${result.seed})"
    }

    // --- Seed Input & Configuration ---
    fun setSeedInputText(text: String) {
        _seedInputText.value = text
        val (seedVal, _) = SeedGeneratorEngine.parseOrHashSeed(text)
        _currentSeed.value = seedVal
        // Also update latest result preview
        val parity = SeedGeneratorEngine.getBedrockParityInfo(seedVal)
        _latestGeneratedResult.value = SeedGenerationResult(
            seed = seedVal,
            seedString = text,
            archetype = null,
            title = if (text.toLongOrNull() != null) "Numeric Seed" else "Hashed Seed: $text",
            description = "Custom input parsed for Minecraft Java ${_selectedVersion.value}.",
            spawnBiome = "Custom World",
            coordinates = listOf(PoiCoordinate("Spawn Point", 0, 70, 0, "Overworld")),
            bedrockEquivalent = parity.bedrockSeed,
            isBedrockDirectParity = parity.isDirectParity,
            hexFormat = "0x" + seedVal.toULong().toString(16).uppercase(),
            tags = listOf("Custom", _selectedVersion.value)
        )
        runAnalysisForCurrentSeed()
    }

    fun setSelectedVersion(version: String) {
        _selectedVersion.value = version
        updateCompatibilityCheck()
        runAnalysisForCurrentSeed()
    }

    fun toggleModSelection(modName: String) {
        val current = _selectedWorldGenMods.value.toMutableSet()
        if (current.contains(modName)) {
            current.remove(modName)
        } else {
            current.add(modName)
        }
        _selectedWorldGenMods.value = current
        updateCompatibilityCheck()
        runAnalysisForCurrentSeed()
    }

    fun selectConfig(config: WorldConfigEntity?) {
        _selectedConfig.value = config
        if (config != null) {
            _selectedVersion.value = config.minecraftVersion
            val modIdsList = config.modIds.split(",").mapNotNull { it.trim().toLongOrNull() }
            val activeNames = allModsRaw.value.filter { it.id in modIdsList }.map { it.name }.toSet()
            _selectedWorldGenMods.value = activeNames
        }
        updateCompatibilityCheck()
        runAnalysisForCurrentSeed()
    }

    private fun updateCompatibilityCheck() {
        _compatibilityWarnings.value = WorldGenerationEngine.checkCompatibility(
            minecraftVersion = _selectedVersion.value,
            selectedModNames = _selectedWorldGenMods.value.toList()
        )
    }

    // --- Primary Action: SHOW SEED -> Launches World Map ---
    fun showSeed() {
        viewModelScope.launch(Dispatchers.Default) {
            val (seedValue, _) = if (_seedInputText.value.isNotBlank()) {
                SeedGeneratorEngine.parseOrHashSeed(_seedInputText.value)
            } else {
                Pair(_currentSeed.value, true)
            }

            _currentSeed.value = seedValue

            // Execute modular World Generation Engine
            val worldState = WorldGenerationEngine.generateWorld(
                seedValue = seedValue,
                minecraftVersion = _selectedVersion.value,
                activeMods = _selectedWorldGenMods.value.toList()
            )

            _worldGenState.value = worldState
            _mapCenterX.value = worldState.spawnX.toFloat()
            _mapCenterZ.value = worldState.spawnZ.toFloat()
            _mapZoom.value = 1.0f
            _selectedStructure.value = null
            _structureSearchQuery.value = ""
            _biomeSearchQuery.value = ""
            _biomeSearchResult.value = null

            // Open the World Map screen
            _isShowingWorldMap.value = true
        }
    }

    fun closeWorldMap() {
        _isShowingWorldMap.value = false
    }

    // --- World Map Interactive Controls ---
    fun centerOnSpawn() {
        val state = _worldGenState.value ?: return
        _mapCenterX.value = state.spawnX.toFloat()
        _mapCenterZ.value = state.spawnZ.toFloat()
    }

    fun jumpToCoordinates(x: Int, z: Int) {
        _mapCenterX.value = x.toFloat()
        _mapCenterZ.value = z.toFloat()
    }

    fun panMap(deltaX: Float, deltaZ: Float) {
        _mapCenterX.value += deltaX
        _mapCenterZ.value += deltaZ
    }

    fun setZoom(newZoom: Float) {
        _mapZoom.value = newZoom.coerceIn(0.25f, 4.0f)
    }

    fun zoomIn() {
        _mapZoom.value = (_mapZoom.value * 1.35f).coerceAtMost(4.0f)
    }

    fun zoomOut() {
        _mapZoom.value = (_mapZoom.value / 1.35f).coerceAtLeast(0.25f)
    }

    fun resetZoom() {
        _mapZoom.value = 1.0f
    }

    fun selectStructure(structure: MapStructure?) {
        _selectedStructure.value = structure
        if (structure != null) {
            _mapCenterX.value = structure.x.toFloat()
            _mapCenterZ.value = structure.z.toFloat()
        }
    }

    // Layer toggles
    fun toggleLayerBiomes() { _layerBiomes.value = !_layerBiomes.value }
    fun toggleLayerStructures() { _layerStructures.value = !_layerStructures.value }
    fun toggleLayerVillages() { _layerVillages.value = !_layerVillages.value }
    fun toggleLayerTerrain() { _layerTerrain.value = !_layerTerrain.value }
    fun toggleLayerSpawn() { _layerSpawn.value = !_layerSpawn.value }

    // Map Search (Structures & Biomes)
    fun setStructureSearchQuery(q: String) {
        _structureSearchQuery.value = q
    }

    fun searchBiome(query: String) {
        _biomeSearchQuery.value = query
        val state = _worldGenState.value ?: return
        if (query.isBlank()) {
            _biomeSearchResult.value = null
            return
        }

        viewModelScope.launch(Dispatchers.Default) {
            val result = WorldGenerationEngine.searchBiome(
                query = query,
                seedValue = state.seedValue,
                minecraftVersion = state.minecraftVersion,
                activeMods = state.activeMods,
                spawnX = state.spawnX,
                spawnZ = state.spawnZ
            )
            _biomeSearchResult.value = result
            if (result != null) {
                _mapCenterX.value = result.foundX.toFloat()
                _mapCenterZ.value = result.foundZ.toFloat()
            }
        }
    }

    // --- Save & Reopen Seed Configurations (Rule 16) ---
    fun saveCurrentSeed(title: String, customNotes: String = "") {
        viewModelScope.launch(Dispatchers.IO) {
            val seedVal = _currentSeed.value
            val state = _worldGenState.value

            val spawnCoordsStr = if (state != null) {
                "X: ${state.spawnX}, Y: ${state.spawnY}, Z: ${state.spawnZ}"
            } else {
                "X: 0, Y: 70, Z: 0"
            }

            val poisJsonArray = JSONArray()
            if (state != null) {
                for (s in state.structures.take(8)) {
                    val obj = JSONObject()
                    obj.put("name", s.name)
                    obj.put("x", s.x)
                    obj.put("y", s.y)
                    obj.put("z", s.z)
                    obj.put("dimension", s.dimension)
                    obj.put("description", s.description)
                    poisJsonArray.put(obj)
                }
            }

            val finalTitle = title.ifBlank {
                "${state?.spawnBiome?.name ?: "World"} (${_selectedVersion.value})"
            }

            val activeModsStr = _selectedWorldGenMods.value.joinToString(", ")
            val configDescriptor = "Seed: $seedVal + Minecraft ${_selectedVersion.value} + Fabric + Selected Mods: ${if (activeModsStr.isNotBlank()) activeModsStr else "None"}"

            val seedEntity = SeedEntity(
                seedValue = seedVal,
                seedText = if (_seedInputText.value.isNotBlank()) _seedInputText.value else seedVal.toString(),
                title = finalTitle,
                targetVersion = _selectedVersion.value,
                loader = "Fabric",
                configName = _selectedConfig.value?.name ?: configDescriptor,
                category = state?.spawnBiome?.name ?: "Overworld",
                primaryBiome = state?.spawnBiome?.name ?: "Plains",
                spawnCoordinates = spawnCoordsStr,
                poiCoordinatesJson = poisJsonArray.toString(),
                verifiedVersions = _selectedVersion.value,
                notes = customNotes.ifBlank { configDescriptor },
                selectedMods = activeModsStr,
                isFavorite = false,
                tags = "${_selectedVersion.value},Fabric,${state?.spawnBiome?.name?.replace(" ", "") ?: "World"}"
            )

            seedRepo.insert(seedEntity)
            _userMessage.value = "Saved configuration: $configDescriptor"
        }
    }

    /**
     * Restores exact configuration when reopening a saved seed (Rule 16)
     */
    fun loadSeedConfiguration(seed: SeedEntity) {
        _currentSeed.value = seed.seedValue
        _seedInputText.value = seed.seedText
        _selectedVersion.value = seed.targetVersion
        val modsList = seed.selectedMods.split(",").map { it.trim() }.filter { it.isNotEmpty() }.toSet()
        _selectedWorldGenMods.value = modsList
        updateCompatibilityCheck()
        runAnalysisForCurrentSeed()
        // Switch to Map tab and trigger showSeed
        _currentTab.value = 0
        showSeed()
        _userMessage.value = "Loaded seed: ${seed.title}"
    }

    fun toggleFavorite(seed: SeedEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            seedRepo.toggleFavorite(seed.id, seed.isFavorite)
        }
    }

    fun deleteSeed(seed: SeedEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            seedRepo.delete(seed)
            _userMessage.value = "Deleted seed: ${seed.title}"
        }
    }

    // --- Fabric Mod Catalog Actions ---
    fun addMod(
        name: String,
        minecraftVersion: String,
        category: ModCategory,
        description: String,
        worldGenFeatures: String
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val mod = ModEntity(
                name = name.trim(),
                minecraftVersion = minecraftVersion.trim(),
                loader = "Fabric",
                category = category.displayName,
                description = description.trim(),
                worldGenFeatures = worldGenFeatures.trim(),
                isPreinstalled = false,
                isEnabled = true
            )
            modRepo.insert(mod)
            _userMessage.value = "Added Fabric mod: ${mod.name}"
        }
    }

    fun deleteMod(mod: ModEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            modRepo.delete(mod)
            _userMessage.value = "Removed mod: ${mod.name}"
        }
    }

    // --- Preset World Configurations ---
    fun createWorldConfig(
        name: String,
        minecraftVersion: String,
        selectedModIds: List<Long>,
        description: String
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val config = WorldConfigEntity(
                name = name.trim(),
                minecraftVersion = minecraftVersion.trim(),
                loader = "Fabric",
                modIds = selectedModIds.joinToString(","),
                description = description.trim(),
                isDefault = false
            )
            val newId = configRepo.insert(config)
            _userMessage.value = "Created Fabric preset: ${config.name}"
            selectConfig(config.copy(id = newId))
        }
    }

    fun deleteWorldConfig(config: WorldConfigEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            configRepo.delete(config)
            if (_selectedConfig.value?.id == config.id) {
                _selectedConfig.value = null
            }
            _userMessage.value = "Deleted preset: ${config.name}"
        }
    }

    // --- Analyzer Screen Support ---
    fun setAnalyzerSeedInput(s: String) {
        _analyzerSeedInput.value = s
        runAnalyzer()
    }

    fun setAnalyzerVersion(v: String) {
        _analyzerVersion.value = v
        runAnalyzer()
    }

    fun runAnalyzer() {
        val (seed, _) = SeedGeneratorEngine.parseOrHashSeed(_analyzerSeedInput.value)
        _analyzerResult.value = SeedAnalyzerEngine.analyzeSeed(
            seedValue = seed,
            targetVersion = _analyzerVersion.value,
            loader = "Fabric",
            activeMods = emptyList()
        )
    }

    fun inspectInAnalyzer(seedValue: Long, targetVersion: String) {
        _analyzerSeedInput.value = seedValue.toString()
        _analyzerVersion.value = targetVersion
        runAnalyzer()
        _currentTab.value = 4
    }

    private fun runAnalysisForCurrentSeed() {
        val seed = _currentSeed.value
        val version = _selectedVersion.value
        val activeMods = _selectedWorldGenMods.value.toList()

        _currentAnalysis.value = SeedAnalyzerEngine.analyzeSeed(
            seedValue = seed,
            targetVersion = version,
            loader = "Fabric",
            activeMods = activeMods
        )
    }

    fun setSearchQuery(q: String) { _searchQuery.value = q }
    fun setVersionFilter(v: String) { _versionFilter.value = v }
    fun toggleFavoritesFilter() { _onlyFavorites.value = !_onlyFavorites.value }

    fun setModCategoryFilter(cat: String) { _modCategoryFilter.value = cat }
    fun setModSearchQuery(q: String) { _modSearchQuery.value = q }

    fun clearUserMessage() {
        _userMessage.value = null
    }

    fun addBookmarkToSeed(seed: SeedEntity, name: String, x: Int, y: Int, z: Int, dimension: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val currentPois = try {
                val array = JSONArray(seed.poiCoordinatesJson)
                val list = mutableListOf<PoiCoordinate>()
                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    list.add(
                        PoiCoordinate(
                            name = obj.getString("name"),
                            x = obj.getInt("x"),
                            y = obj.getInt("y"),
                            z = obj.getInt("z"),
                            dimension = obj.optString("dimension", "Overworld"),
                            description = obj.optString("description", "")
                        )
                    )
                }
                list
            } catch (e: Exception) {
                mutableListOf<PoiCoordinate>()
            }

            currentPois.add(PoiCoordinate(name, x, y, z, dimension, "Custom coordinate bookmark"))

            val newArray = JSONArray()
            for (p in currentPois) {
                val obj = JSONObject()
                obj.put("name", p.name)
                obj.put("x", p.x)
                obj.put("y", p.y)
                obj.put("z", p.z)
                obj.put("dimension", p.dimension)
                obj.put("description", p.description)
                newArray.put(obj)
            }

            val updated = seed.copy(poiCoordinatesJson = newArray.toString())
            seedRepo.update(updated)
            _userMessage.value = "Added coordinate '$name' ($x, $y, $z)"
        }
    }
}
