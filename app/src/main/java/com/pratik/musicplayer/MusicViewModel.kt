package com.pratik.musicplayer

import android.media.MediaPlayer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pratik.musicplayer.data.JamendoResponse
import com.pratik.musicplayer.data.KtorClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.io.IOException

data class Track(
    val id: String,
    val name: String,
    val artist: String,
    val duration: Int, // duration in seconds
    val url: String
)

sealed class MusicUiState {
    object Loading : MusicUiState()
    object Success : MusicUiState()
    data class Error(val message: String) : MusicUiState()
}

class MusicViewModel : ViewModel() {

    private val _tracks = MutableStateFlow<List<Track>>(emptyList())
    val tracks: StateFlow<List<Track>> = _tracks.asStateFlow()

    private val _uiState = MutableStateFlow<MusicUiState>(MusicUiState.Loading)
    val uiState: StateFlow<MusicUiState> = _uiState.asStateFlow()

    private val _currentTrack = MutableStateFlow<Track?>(null)
    val currentTrack: StateFlow<Track?> = _currentTrack.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    // Seek Bar States
    private val _currentPosition = MutableStateFlow(0L)
    val currentPosition: StateFlow<Long> = _currentPosition.asStateFlow()

    private val _totalDuration = MutableStateFlow(0L)
    val totalDuration: StateFlow<Long> = _totalDuration.asStateFlow()

    private var mediaPlayer: MediaPlayer? = null
    private var playbackJob: Job? = null
    private val apiClient = KtorClient.client
    private val CLIENT_ID = "f6eec65a"

    init {
        fetchTracks()
    }

    private fun fetchTracks() {
        viewModelScope.launch {
            _uiState.value = MusicUiState.Loading
            try {
                val response: JamendoResponse = apiClient
                    .get("https://api.jamendo.com/v3.0/tracks/?client_id=$CLIENT_ID&format=json&limit=20")
                    .body()
                
                val mappedTracks = response.results.map { dto ->
                    Track(
                        id = dto.id,
                        name = dto.name,
                        artist = dto.artist_name,
                        duration = dto.duration,
                        url = dto.audio
                    )
                }
                _tracks.value = mappedTracks
                _uiState.value = MusicUiState.Success
            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.value = MusicUiState.Error("Failed to load music: ${e.message}")
            }
        }
    }

    fun sortByName() {
        _tracks.value = _tracks.value.sortedBy { it.name }
    }

    fun sortByDuration() {
        _tracks.value = _tracks.value.sortedBy { it.duration }
    }

    fun togglePlayDir(track: Track) {
        if (_currentTrack.value?.id == track.id) {
            if (mediaPlayer?.isPlaying == true) {
                pause()
            } else {
                resume()
            }
        } else {
            playTrack(track)
        }
    }

    fun seekTo(position: Float) {
        mediaPlayer?.let {
            it.seekTo(position.toInt())
            _currentPosition.value = position.toLong()
        }
    }

    private fun playTrack(track: Track) {
        resetPlayer()
        _currentTrack.value = track
        
        mediaPlayer = MediaPlayer().apply {
            try {
                setDataSource(track.url)
                prepareAsync()
                setOnPreparedListener { mp ->
                    mp.start()
                    _isPlaying.value = true
                    _totalDuration.value = mp.duration.toLong()
                    startPositionUpdater()
                }
                setOnCompletionListener {
                    _isPlaying.value = false
                    _currentPosition.value = 0
                    stopPositionUpdater()
                }
                setOnErrorListener { _, _, _ ->
                    _isPlaying.value = false
                    stopPositionUpdater()
                    true
                }
            } catch (e: IOException) {
                e.printStackTrace()
                _isPlaying.value = false
            }
        }
    }

    private fun pause() {
        mediaPlayer?.let {
            if (it.isPlaying) {
                it.pause()
                _isPlaying.value = false
                stopPositionUpdater()
            }
        }
    }

    private fun resume() {
        mediaPlayer?.let {
            if (!it.isPlaying) {
                it.start()
                _isPlaying.value = true
                startPositionUpdater()
            }
        }
    }

    private fun startPositionUpdater() {
        stopPositionUpdater()
        playbackJob = viewModelScope.launch {
            while (isActive) {
                mediaPlayer?.let { mp ->
                    if (mp.isPlaying) {
                        _currentPosition.value = mp.currentPosition.toLong()
                    }
                }
                delay(500)
            }
        }
    }

    private fun stopPositionUpdater() {
        playbackJob?.cancel()
        playbackJob = null
    }

    private fun resetPlayer() {
        stopPositionUpdater()
        mediaPlayer?.release()
        mediaPlayer = null
        _isPlaying.value = false
        _currentPosition.value = 0
        _totalDuration.value = 0
    }

    override fun onCleared() {
        resetPlayer()
        super.onCleared()
    }
}
