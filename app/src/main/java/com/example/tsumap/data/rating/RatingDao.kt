package com.example.tsumap.data.rating

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface RatingDao {
    @Insert
    suspend fun insert(rating: RatingEntity)

    @Query("SELECT AVG(rating) FROM ratings WHERE placeName = :place")
    fun getAverageRating(place: String): Flow<Float?>

    @Query("SELECT * FROM ratings ORDER BY timestamp DESC")
    fun getAllRatings(): Flow<List<RatingEntity>>
}
