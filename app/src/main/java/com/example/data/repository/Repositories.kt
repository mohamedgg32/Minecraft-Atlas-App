package com.example.data.repository

import com.example.data.local.ModDao
import com.example.data.local.SeedDao
import com.example.data.local.WorldConfigDao
import com.example.data.model.ModEntity
import com.example.data.model.SeedEntity
import com.example.data.model.WorldConfigEntity
import kotlinx.coroutines.flow.Flow

class ModRepository(private val modDao: ModDao) {
    val allMods: Flow<List<ModEntity>> = modDao.getAllMods()

    fun getModsByLoader(loader: String): Flow<List<ModEntity>> = modDao.getModsByLoader(loader)

    fun getModsByCategory(category: String): Flow<List<ModEntity>> = modDao.getModsByCategory(category)

    suspend fun getModById(id: Long): ModEntity? = modDao.getModById(id)

    suspend fun getModsByIds(ids: List<Long>): List<ModEntity> = modDao.getModsByIds(ids)

    suspend fun insert(mod: ModEntity): Long = modDao.insertMod(mod)

    suspend fun update(mod: ModEntity) = modDao.updateMod(mod)

    suspend fun delete(mod: ModEntity) = modDao.deleteMod(mod)
}

class WorldConfigRepository(private val worldConfigDao: WorldConfigDao) {
    val allConfigs: Flow<List<WorldConfigEntity>> = worldConfigDao.getAllConfigs()

    suspend fun getConfigById(id: Long): WorldConfigEntity? = worldConfigDao.getConfigById(id)

    suspend fun insert(config: WorldConfigEntity): Long = worldConfigDao.insertConfig(config)

    suspend fun update(config: WorldConfigEntity) = worldConfigDao.updateConfig(config)

    suspend fun delete(config: WorldConfigEntity) = worldConfigDao.deleteConfig(config)
}

class SeedRepository(private val seedDao: SeedDao) {
    val allSeeds: Flow<List<SeedEntity>> = seedDao.getAllSeeds()

    val favoriteSeeds: Flow<List<SeedEntity>> = seedDao.getFavoriteSeeds()

    fun getSeedsByVersion(version: String): Flow<List<SeedEntity>> = seedDao.getSeedsByVersion("%$version%")

    fun getSeedsByLoader(loader: String): Flow<List<SeedEntity>> = seedDao.getSeedsByLoader(loader)

    suspend fun getSeedById(id: Long): SeedEntity? = seedDao.getSeedById(id)

    suspend fun findBySeedValue(seedValue: Long): SeedEntity? = seedDao.findBySeedValue(seedValue)

    suspend fun insert(seed: SeedEntity): Long = seedDao.insertSeed(seed)

    suspend fun update(seed: SeedEntity) = seedDao.updateSeed(seed)

    suspend fun delete(seed: SeedEntity) = seedDao.deleteSeed(seed)

    suspend fun toggleFavorite(id: Long, currentStatus: Boolean) = seedDao.updateFavorite(id, !currentStatus)
}
