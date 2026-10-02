package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.WorldConfigEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WorldConfigDao {
    @Query("SELECT * FROM world_configs ORDER BY createdAt DESC")
    fun getAllConfigs(): Flow<List<WorldConfigEntity>>

    @Query("SELECT * FROM world_configs WHERE id = :id")
    suspend fun getConfigById(id: Long): WorldConfigEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConfig(config: WorldConfigEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConfigs(configs: List<WorldConfigEntity>)

    @Update
    suspend fun updateConfig(config: WorldConfigEntity)

    @Delete
    suspend fun deleteConfig(config: WorldConfigEntity)

    @Query("SELECT COUNT(*) FROM world_configs")
    suspend fun getConfigCount(): Int
}
