package com.example.tsumap.data.repository

import com.example.tsumap.data.rating.RatingDao
import com.example.tsumap.data.rating.RatingEntity
import kotlinx.coroutines.flow.Flow

class RatingRepository(private val dao: RatingDao) {

    suspend fun saveRating(placeName: String, rating: Int) {
        dao.insert(RatingEntity(placeName = placeName, rating = rating, timestamp = System.currentTimeMillis()))
    }

    fun getAverageRating(place: String): Flow<Float?> = dao.getAverageRating(place)
}