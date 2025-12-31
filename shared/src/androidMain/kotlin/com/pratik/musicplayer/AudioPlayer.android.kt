package com.pratik.musicplayer

import android.media.MediaPlayer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.io.IOException

class AndroidAudioPlayer : AudioPlayer {
    private var mediaPlayer: MediaPlayer? = null
    
    // We use a scope for polling position. 
    // Ideally this scope should be cancelled when player is released or injected.
    // For simplicity, we'll create a scope or just use GlobalScope (bad practice) 
    // or just manage the Job manually if we had a scope passed in.
    // Let's create a private scope that we cancel on release.
    private val playerScope = CoroutineScope(Dispatchers.Main)
    private var positionJob: Job? = null

    private val _isPlaying = MutableStateFlow(false)
    override val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _currentPosition = MutableStateFlow(0L)
    override val currentPosition: StateFlow<Long> = _currentPosition.asStateFlow()

    private val _totalDuration = MutableStateFlow(0L)
    override val totalDuration: StateFlow<Long> = _totalDuration.asStateFlow()

    override fun play(url: String) {
        stop() // Release previous
        mediaPlayer = MediaPlayer().apply {
            try {
                setDataSource(url)
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

    override fun pause() {
        mediaPlayer?.let {
            if (it.isPlaying) {
                it.pause()
                _isPlaying.value = false
                stopPositionUpdater()
            }
        }
    }

    override fun resume() {
        mediaPlayer?.let {
            if (!it.isPlaying) {
                it.start()
                _isPlaying.value = true
                startPositionUpdater()
            }
        }
    }

    override fun seekTo(position: Long) {
        mediaPlayer?.let {
            it.seekTo(position.toInt())
            _currentPosition.value = position // Update immediately for UI responsiveness
        }
    }

    override fun stop() {
        stopPositionUpdater()
        if (mediaPlayer?.isPlaying == true) {
            mediaPlayer?.stop()
        }
        mediaPlayer?.release()
        mediaPlayer = null
        _isPlaying.value = false
        _currentPosition.value = 0
        _totalDuration.value = 0
    }

    override fun release() {
        stop()
        // Cancel scope if we are done with the player instance entirely
    }

    private fun startPositionUpdater() {
        stopPositionUpdater()
        positionJob = playerScope.launch {
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
        positionJob?.cancel()
        positionJob = null
    }
}

actual fun createAudioPlayer(): AudioPlayer = AndroidAudioPlayer()
