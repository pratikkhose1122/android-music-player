package com.pratik.musicplayer

import kotlinx.coroutines.flow.StateFlow

interface AudioPlayer {
    val isPlaying: StateFlow<Boolean>
    val currentPosition: StateFlow<Long>
    val totalDuration: StateFlow<Long>

    fun play(url: String)
    fun pause()
    fun resume()
    fun seekTo(position: Long)
    fun stop()
    fun release()
}

expect fun createAudioPlayer(): AudioPlayer
