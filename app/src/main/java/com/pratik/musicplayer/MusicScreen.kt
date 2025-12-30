package com.pratik.musicplayer

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MusicScreen(viewModel: MusicViewModel) {

    val tracks by viewModel.tracks.collectAsState()
    val currentTrack by viewModel.currentTrack.collectAsState()
    val isPlaying by viewModel.isPlaying.collectAsState()
    val uiState by viewModel.uiState.collectAsState()
    
    // Seek Bar States
    val currentPos by viewModel.currentPosition.collectAsState()
    val totalDur by viewModel.totalDuration.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Music Player") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        bottomBar = {
            currentTrack?.let { track ->
                BottomPlayerBar(
                    track = track,
                    isPlaying = isPlaying,
                    currentPosition = currentPos,
                    totalDuration = totalDur,
                    onTogglePlay = { viewModel.togglePlayDir(track) },
                    onSeek = { viewModel.seekTo(it) }
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (val state = uiState) {
                is MusicUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is MusicUiState.Error -> {
                    Text(
                        text = state.message,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                is MusicUiState.Success -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        // Sorting Controls
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = { viewModel.sortByName() },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Sort Name")
                            }
                            Button(
                                onClick = { viewModel.sortByDuration() },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Sort Time")
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Track List
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            contentPadding = PaddingValues(bottom = 80.dp) // Space for bottom bar
                        ) {
                            items(tracks) { track ->
                                val isCurrent = currentTrack?.id == track.id
                                TrackItem(
                                    track = track,
                                    isPlaying = isCurrent && isPlaying,
                                    isCurrent = isCurrent,
                                    onTrackClick = { viewModel.togglePlayDir(track) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TrackItem(
    track: Track,
    isPlaying: Boolean,
    isCurrent: Boolean,
    onTrackClick: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (isCurrent) MaterialTheme.colorScheme.secondaryContainer 
                             else MaterialTheme.colorScheme.surfaceVariant
        ),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onTrackClick() }
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = track.name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "${track.artist} • ${formatDuration((track.duration))}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            if (isCurrent && isPlaying) {
                 Icon(
                    imageVector = Icons.Default.PlayArrow, // Just a static indicator for list
                    contentDescription = "Playing",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun BottomPlayerBar(
    track: Track,
    isPlaying: Boolean,
    currentPosition: Long,
    totalDuration: Long,
    onTogglePlay: () -> Unit,
    onSeek: (Float) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceContainerHighest)
            .padding(16.dp)
    ) {
        // Track Info & Play Button
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = track.name,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1
                )
                Text(
                    text = track.artist,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1
                )
            }
            
            IconButton(onClick = onTogglePlay) {
                Icon(
                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = if (isPlaying) "Pause" else "Play",
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Slider & Time
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = formatDuration((currentPosition / 1000).toInt()),
                style = MaterialTheme.typography.labelSmall
            )
            
            Slider(
                value = currentPosition.toFloat(),
                onValueChange = { onSeek(it) },
                valueRange = 0f..(if (totalDuration > 0) totalDuration.toFloat() else 1f),
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp)
            )
            
            Text(
                text = formatDuration((totalDuration / 1000).toInt()),
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}


fun formatDuration(seconds: Int): String {
    val minutes = seconds / 60
    val remainingSeconds = seconds % 60
    return "%02d:%02d".format(minutes, remainingSeconds)
}
