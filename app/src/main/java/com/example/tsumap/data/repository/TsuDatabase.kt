package com.example.tsumap.data.repository

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.tsumap.data.place.PlaceEntity
import com.example.tsumap.data.place.PlaceDao
import com.example.tsumap.data.place.PlaceSeeder
import com.example.tsumap.data.rating.RatingEntity
import com.example.tsumap.data.rating.RatingDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [RatingEntity::class, PlaceEntity::class], version = 2)
abstract class TsuDatabase : RoomDatabase() {

    abstract fun ratingDao(): RatingDao
    abstract fun placeDao(): PlaceDao

    companion object {
        @Volatile private var INSTANCE: TsuDatabase? = null

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS places (
                        key TEXT PRIMARY KEY NOT NULL,
                        name TEXT NOT NULL,
                        emoji TEXT NOT NULL,
                        imageResName TEXT NOT NULL,
                        gridRow INTEGER NOT NULL,
                        gridCol INTEGER NOT NULL,
                        type TEXT NOT NULL
                    )
                """)
            }
        }

        fun getInstance(context: Context): TsuDatabase =
            INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(context, TsuDatabase::class.java, "tsu_db")
                    .addMigrations(MIGRATION_1_2)
                    .addCallback(object : RoomDatabase.Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            CoroutineScope(Dispatchers.IO).launch {
                                INSTANCE?.placeDao()?.insertAll(PlaceSeeder.getInitialPlaces())
                            }
                        }
                        override fun onOpen(db: SupportSQLiteDatabase) {
                            CoroutineScope(Dispatchers.IO).launch {
                                INSTANCE?.placeDao()?.insertAll(PlaceSeeder.getInitialPlaces())
                            }
                        }
                    })
                    .build()
                    .also { INSTANCE = it }
            }
    }
}