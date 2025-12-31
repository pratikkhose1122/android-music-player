package com.pratik.musicplayer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pratik.musicplayer.data.JamendoResponse
import com.pratik.musicplayer.data.KtorClient
import com.pratik.musicplayer.data.TrackDto
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

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

    private val audioPlayer = createAudioPlayer()

    val isPlaying: StateFlow<Boolean> = audioPlayer.isPlaying
    val currentPosition: StateFlow<Long> = audioPlayer.currentPosition
    val totalDuration: StateFlow<Long> = audioPlayer.totalDuration

    private val apiClient = KtorClient.client
    private val CLIENT_ID = "f6eec65a"

    init {
        fetchTracks()
    }

    private fun fetchTracks() {
        viewModelScope.launch {
            _uiState.value = MusicUiState.Loading
            try {
                // Using Jamendo API with client_id
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
                // Fallback to mock data if API fails or for demo
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
            if (audioPlayer.isPlaying.value) {
                audioPlayer.pause()
            } else {
                audioPlayer.resume()
            }
        } else {
            playTrack(track)
        }
    }

    fun seekTo(position: Float) {
        audioPlayer.seekTo(position.toLong())
    }

    private fun playTrack(track: Track) {
        _currentTrack.value = track
        audioPlayer.play(track.url)
    }

    override fun onCleared() {
        audioPlayer.release()
        super.onCleared()
    }
}
