package com.example.ui.player

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import com.example.domain.model.Song
import com.example.playback.PlaybackStateValue
import com.example.playback.PlayerController
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class PlayerUiState(
    val currentSong: Song? = null,
    val isPlaying: Boolean = false,
    val currentPosition: Long = 0L,
    val duration: Long = 0L,
    val artworkUri: String? = null
)

@HiltViewModel
class PlayerViewModel @Inject constructor(
    private val playerController: PlayerController
) : ViewModel() {

    val playbackState: StateFlow<PlaybackStateValue> = playerController.playbackState
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = PlaybackStateValue()
        )

    fun playSong(song: Song) {
        val mediaItem = MediaItem.Builder()
            .setMediaId(song.id)
            .setUri(song.streamUrl)
            .setMediaMetadata(
                MediaMetadata.Builder()
                    .setTitle(song.title)
                    .setArtist(song.artist.name)
                    .setArtworkUri(android.net.Uri.parse(song.coverArtUrl ?: ""))
                    .build()
            )
            .build()
        playerController.play(mediaItem)
    }

    fun playQueue(songs: List<Song>, startIndex: Int) {
        val mediaItems = songs.map { song ->
            MediaItem.Builder()
                .setMediaId(song.id)
                .setUri(song.streamUrl)
                .setMediaMetadata(
                    MediaMetadata.Builder()
                        .setTitle(song.title)
                        .setArtist(song.artist.name)
                        .setArtworkUri(android.net.Uri.parse(song.coverArtUrl ?: ""))
                        .build()
                )
                .build()
        }
        playerController.playQueue(mediaItems, startIndex)
    }

    fun pause() = playerController.pause()
    fun resume() = playerController.resume()
    fun seekTo(positionMs: Long) = playerController.seekTo(positionMs)
    fun skipToNext() = playerController.skipToNext()
    fun skipToPrevious() = playerController.skipToPrevious()
    
    override fun onCleared() {
        super.onCleared()
        // PlayerController is a singleton, do not release here unless needed.
    }
}
