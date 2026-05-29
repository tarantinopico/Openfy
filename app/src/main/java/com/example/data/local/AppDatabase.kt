package com.example.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        FavoriteSongEntity::class,
        RecentSearchEntity::class,
        DownloadMetadataEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun audioDao(): AudioDao
}
