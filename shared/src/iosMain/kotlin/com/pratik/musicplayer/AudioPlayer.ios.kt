package com.pratik.musicplayer

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class IosAudioPlayer : AudioPlayer {
    private val _isPlaying = MutableStateFlow(false)
    override val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _currentPosition = MutableStateFlow(0L)
    override val currentPosition: StateFlow<Long> = _currentPosition.asStateFlow()

    private val _totalDuration = MutableStateFlow(0L)
    override val totalDuration: StateFlow<Long> = _totalDuration.asStateFlow()

    override fun play(url: String) {
        // Not implemented for this task
        println("Playing on iOS (stub): $url")
    }

    override fun pause() {
    }

    override fun resume() {
    }

    override fun seekTo(position: Long) {
    }

    override fun stop() {
    }

    override fun release() {
    }
}

actual fun createAudioPlayer(): AudioPlayer = IosAudioPlayer()
