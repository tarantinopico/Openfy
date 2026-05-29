package com.example.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.playback.PlaybackStateValue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerScreen(
    playbackState: PlaybackStateValue,
    onPlayPauseClick: () -> Unit,
    onNextClick: () -> Unit,
    onPreviousClick: () -> Unit,
    onSeekTo: (Long) -> Unit,
    onCloseClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Now Playing", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)) },
                navigationIcon = {
                    IconButton(onClick = onCloseClick) {
                        Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Close", modifier = Modifier.size(32.dp))
                    }
                },
                actions = {
                    IconButton(onClick = { /* TODO More Menu */ }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "More options")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        modifier = modifier
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.weight(1f))

            Card(
                modifier = Modifier
                    .aspectRatio(1f)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 16.dp)
            ) {
                AsyncImage(
                    model = playbackState.artworkUri,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.surfaceVariant)
                )
            }

            Spacer(modifier = Modifier.height(36.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = playbackState.title ?: "Unknown Title",
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onBackground,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = playbackState.artist ?: "Unknown Artist",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                IconButton(onClick = { /* TODO favorite */ }) {
                    Icon(Icons.Default.FavoriteBorder, contentDescription = "Favorite", tint = MaterialTheme.colorScheme.primary)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            val progress = if (playbackState.duration > 0) {
                (playbackState.currentPosition.toFloat() / playbackState.duration.toFloat()).coerceIn(0f, 1f)
            } else 0f

            Slider(
                value = progress,
                onValueChange = { newProgress ->
                    onSeekTo((newProgress * playbackState.duration).toLong())
                },
                modifier = Modifier.fillMaxWidth(),
                colors = SliderDefaults.colors(
                    thumbColor = MaterialTheme.colorScheme.primary,
                    activeTrackColor = MaterialTheme.colorScheme.primary,
                    inactiveTrackColor = MaterialTheme.colorScheme.primaryContainer
                )
            )

            Row(
                modifier = Modifier.fillMaxWidth().offset(y = (-12).dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(formatTime(playbackState.currentPosition), style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f))
                Text(formatTime(playbackState.duration), style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f))
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { /* TODO Shuffle */ }) {
                    Icon(Icons.Default.Shuffle, contentDescription = "Shuffle", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                }

                IconButton(onClick = onPreviousClick, modifier = Modifier.size(56.dp)) {
                    Icon(Icons.Default.SkipPrevious, contentDescription = "Previous", modifier = Modifier.size(36.dp), tint = MaterialTheme.colorScheme.onBackground)
                }

                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary)
                        .clickable { onPlayPauseClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (playbackState.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (playbackState.isPlaying) "Pause" else "Play",
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }

                IconButton(onClick = onNextClick, modifier = Modifier.size(56.dp)) {
                    Icon(Icons.Default.SkipNext, contentDescription = "Next", modifier = Modifier.size(36.dp), tint = MaterialTheme.colorScheme.onBackground)
                }
                
                IconButton(onClick = { /* TODO Repeat */ }) {
                    Icon(Icons.Default.Repeat, contentDescription = "Repeat", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            Spacer(modifier = Modifier.weight(1.5f))
        }
    }
}

private fun formatTime(ms: Long): String {
    val totalSeconds = ms / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format("%02d:%02d", minutes, seconds)
}
