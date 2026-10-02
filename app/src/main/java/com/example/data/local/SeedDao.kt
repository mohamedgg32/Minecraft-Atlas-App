package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.SeedEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SeedDao {
    @Query("SELECT * FROM seeds ORDER BY createdAt DESC")
    fun getAllSeeds(): Flow<List<SeedEntity>>

    @Query("SELECT * FROM seeds WHERE isFavorite = 1 ORDER BY createdAt DESC")
    fun getFavoriteSeeds(): Flow<List<SeedEntity>>

    @Query("SELECT * FROM seeds WHERE targetVersion LIKE :versionPattern ORDER BY createdAt DESC")
    fun getSeedsByVersion(versionPattern: String): Flow<List<SeedEntity>>

    @Query("SELECT * FROM seeds WHERE loader = :loader ORDER BY createdAt DESC")
    fun getSeedsByLoader(loader: String): Flow<List<SeedEntity>>

    @Query("SELECT * FROM seeds WHERE id = :id")
    suspend fun getSeedById(id: Long): SeedEntity?

    @Query("SELECT * FROM seeds WHERE seedValue = :seedValue LIMIT 1")
    suspend fun findBySeedValue(seedValue: Long): SeedEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSeed(seed: SeedEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSeeds(seeds: List<SeedEntity>)

    @Update
    suspend fun updateSeed(seed: SeedEntity)

    @Delete
    suspend fun deleteSeed(seed: SeedEntity)

    @Query("UPDATE seeds SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun updateFavorite(id: Long, isFavorite: Boolean)

    @Query("SELECT COUNT(*) FROM seeds")
    suspend fun getSeedCount(): Int
}
