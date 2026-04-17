package com.example.tsumap.data.repository

import com.example.tsumap.data.place.PlaceDao
import com.example.tsumap.data.place.PlaceEntity
import kotlinx.coroutines.flow.Flow

class PlaceRepository(private val dao: PlaceDao) {

    suspend fun getByCoords(gridRow: Int, gridCol: Int): PlaceEntity? =
        dao.getByCoords(gridRow, gridCol)

    fun getCafes(): Flow<List<PlaceEntity>> = dao.getByType("CAFE")

    fun getAll(): Flow<List<PlaceEntity>> = dao.getAll()
}