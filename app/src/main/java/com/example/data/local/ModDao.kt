package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.ModEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ModDao {
    @Query("SELECT * FROM mods ORDER BY name ASC")
    fun getAllMods(): Flow<List<ModEntity>>

    @Query("SELECT * FROM mods WHERE loader = :loader ORDER BY name ASC")
    fun getModsByLoader(loader: String): Flow<List<ModEntity>>

    @Query("SELECT * FROM mods WHERE category = :category ORDER BY name ASC")
    fun getModsByCategory(category: String): Flow<List<ModEntity>>

    @Query("SELECT * FROM mods WHERE id = :id")
    suspend fun getModById(id: Long): ModEntity?

    @Query("SELECT * FROM mods WHERE id IN (:ids)")
    suspend fun getModsByIds(ids: List<Long>): List<ModEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMod(mod: ModEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMods(mods: List<ModEntity>)

    @Update
    suspend fun updateMod(mod: ModEntity)

    @Delete
    suspend fun deleteMod(mod: ModEntity)

    @Query("SELECT COUNT(*) FROM mods")
    suspend fun getModCount(): Int
}
