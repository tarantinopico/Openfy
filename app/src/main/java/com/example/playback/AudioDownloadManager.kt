package com.example.playback

import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.offline.Download
import androidx.media3.exoplayer.offline.DownloadManager
import androidx.media3.exoplayer.offline.DownloadRequest
import androidx.media3.exoplayer.offline.DownloadService
import com.example.domain.model.Song
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AudioDownloadManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    // Basic placeholder for download manager logic
    // True implementation requires Exoplayer's DownloadService, StandaloneDownloadManager, database, etc.
    
    private val _downloads = MutableStateFlow<List<DownloadItemState>>(emptyList())
    val downloads = _downloads.asStateFlow()

    fun enqueueDownload(song: Song) {
        // Enqueue download in Media3
        val current = _downloads.value.toMutableList()
        current.add(DownloadItemState(song.id, 0f, com.example.domain.model.DownloadStatus.DOWNLOADING))
        _downloads.value = current
    }

    fun removeDownload(songId: String) {
        val current = _downloads.value.toMutableList()
        current.removeAll { it.songId == songId }
        _downloads.value = current
    }
}

data class DownloadItemState(
    val songId: String,
    val progress: Float,
    val status: com.example.domain.model.DownloadStatus
)
