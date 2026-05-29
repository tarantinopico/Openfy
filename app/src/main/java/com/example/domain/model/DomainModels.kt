package com.example.domain.model

data class Song(
    val id: String,
    val title: String,
    val durationMs: Long,
    val artist: Artist,
    val album: Album?,
    val streamUrl: String,
    val coverArtUrl: String?,
    val isAvailableOffline: Boolean = false
)

data class Artist(
    val id: String,
    val name: String,
    val imageUrl: String?
)

data class Album(
    val id: String,
    val title: String,
    val releaseYear: Int?,
    val coverArtUrl: String?
)

data class Playlist(
    val id: String,
    val name: String,
    val description: String?,
    val coverArtUrl: String?,
    val songs: List<Song>
)

data class SearchQuery(
    val query: String,
    val limit: Int = 20,
    val offset: Int = 0
)

data class DownloadItem(
    val songId: String,
    val progress: Float,
    val status: DownloadStatus
)

enum class DownloadStatus {
    PENDING, DOWNLOADING, COMPLETED, FAILED, PAUSED
}

data class PlaybackState(
    val currentSong: Song?,
    val isPlaying: Boolean,
    val currentPositionMs: Long
)

enum class CachePolicy {
    NETWORK_ONLY, CACHE_ONLY, CACHE_FIRST, NETWORK_FIRST
}
