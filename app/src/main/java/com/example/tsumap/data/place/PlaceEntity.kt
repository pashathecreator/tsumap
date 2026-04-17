package com.example.tsumap.data.place

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "places")
data class PlaceEntity(
    @PrimaryKey val key: String,
    val name: String,
    val emoji: String,
    val imageResName: String,
    val gridRow: Int,
    val gridCol: Int,
    val type: String
)