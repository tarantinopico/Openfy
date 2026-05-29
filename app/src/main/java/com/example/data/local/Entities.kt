package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_songs")
data class FavoriteSongEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val artistName: String,
    val coverArtUrl: String?,
    val durationMs: Long,
    val streamUrl: String,
    val addedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "recent_searches")
data class RecentSearchEntity(
    @PrimaryKey
    val query: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "download_metadata")
data class DownloadMetadataEntity(
    @PrimaryKey
    val songId: String,
    val localFilePath: String,
    val downloadedAt: Long = System.currentTimeMillis()
)
