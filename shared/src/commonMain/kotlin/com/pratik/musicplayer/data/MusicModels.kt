package com.pratik.musicplayer.data

import kotlinx.serialization.Serializable

@Serializable
data class JamendoResponse(
    val results: List<TrackDto>
)

@Serializable
data class TrackDto(
    val id: String, // Keep as string for safety in JSON
    val name: String,
    val duration: Int,
    val artist_name: String,
    val audio: String,
    val image: String? = null // Optional thumbnail
)
