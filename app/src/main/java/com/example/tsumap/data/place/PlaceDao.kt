package com.example.tsumap.data.place

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaceDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(places: List<PlaceEntity>)

    @Query("SELECT * FROM places WHERE gridRow = :gridRow AND gridCol = :gridCol LIMIT 1")
    suspend fun getByCoords(gridRow: Int, gridCol: Int): PlaceEntity?

    @Query("SELECT * FROM places WHERE type = :type")
    fun getByType(type: String): Flow<List<PlaceEntity>>

    @Query("SELECT * FROM places")
    fun getAll(): Flow<List<PlaceEntity>>
}