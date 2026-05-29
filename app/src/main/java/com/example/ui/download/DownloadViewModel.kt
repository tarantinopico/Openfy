package com.example.ui.download

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.model.Song
import com.example.playback.AudioDownloadManager
import com.example.playback.DownloadItemState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class DownloadViewModel @Inject constructor(
    private val downloadManager: AudioDownloadManager
) : ViewModel() {

    val activeDownloads: StateFlow<List<DownloadItemState>> = downloadManager.downloads
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun startDownload(song: Song) {
        downloadManager.enqueueDownload(song)
    }

    fun removeDownload(songId: String) {
        downloadManager.removeDownload(songId)
    }
}
