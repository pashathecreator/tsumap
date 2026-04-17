package com.example.tsumap.data.rating

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ratings")
data class RatingEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val placeName: String,
    val rating: Int,
    val timestamp: Long
)