package com.pratik.musicplayer.data

import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

object KtorClient {
    // No specific engine, Ktor will pick one available (Android: OkHttp, iOS: Darwin) if dependencies are set correctly
    // or use CIO if added to common.
    // Since we added OkHttp to androidMain and Darwin to iosMain, basic HttpClient() is sufficient.
    val client = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                prettyPrint = true
                isLenient = true
            })
        }
    }
}
