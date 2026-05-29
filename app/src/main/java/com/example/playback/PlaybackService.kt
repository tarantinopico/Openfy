package com.example.playback

import android.app.PendingIntent
import android.content.Intent
import androidx.annotation.OptIn
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaLibraryService
import androidx.media3.session.MediaSession
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class PlaybackService : MediaLibraryService() {

    private var player: ExoPlayer? = null
    private var mediaLibrarySession: MediaLibrarySession? = null
    
    // In a real app we would inject a repository to handle library items
    // @Inject lateinit var audioRepository: AudioRepository

    override fun onCreate() {
        super.onCreate()
        
        player = ExoPlayer.Builder(this)
            .setAudioAttributes(AudioAttributes.Builder()
                .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
                .setUsage(C.USAGE_MEDIA)
                .build(), true)
            .setHandleAudioBecomingNoisy(true)
            .build()
            
        val sessionActivityPendingIntent = packageManager?.getLaunchIntentForPackage(packageName)?.let {
            PendingIntent.getActivity(
                this, 0, it,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )
        }

        mediaLibrarySession = MediaLibrarySession.Builder(this, player!!, LibrarySessionCallback())
            .setSessionActivity(sessionActivityPendingIntent!!)
            .build()
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaLibrarySession? {
        return mediaLibrarySession
    }

    override fun onDestroy() {
        mediaLibrarySession?.run {
            player.release()
            release()
            mediaLibrarySession = null
        }
        player = null
        super.onDestroy()
    }

    private inner class LibrarySessionCallback : MediaLibrarySession.Callback {
        @OptIn(UnstableApi::class)
        override fun onGetLibraryRoot(
            session: MediaLibrarySession,
            browser: MediaSession.ControllerInfo,
            params: LibraryParams?
        ): ListenableFuture<LibraryResult<MediaItem>> {
            // Simplified return root
            return Futures.immediateFuture(LibraryResult.ofItem(
                MediaItem.Builder().setMediaId("root").build(),
                params
            ))
        }

        @OptIn(UnstableApi::class)
        override fun onGetChildren(
            session: MediaLibrarySession,
            browser: MediaSession.ControllerInfo,
            parentId: String,
            page: Int,
            pageSize: Int,
            params: LibraryParams?
        ): ListenableFuture<LibraryResult<List<MediaItem>>> {
            return Futures.immediateFuture(LibraryResult.ofItemList(emptyList(), params))
        }
        
        override fun onAddMediaItems(
            mediaSession: MediaSession,
            controller: MediaSession.ControllerInfo,
            mediaItems: MutableList<MediaItem>
        ): ListenableFuture<MutableList<MediaItem>> {
            // Can process incoming items to attach local URIs if needed.
            // For now, allow passthrough.
            val updated = mediaItems.map { it.buildUpon().setUri(it.mediaId).build() }.toMutableList()
            // In a real app, you would resolve mediaId into actual playable stream URL here.
            // But since the PlayerController passes the stream URL directly in MediaItem, we just return the items.
            return Futures.immediateFuture(mediaItems)
        }
    }
}
