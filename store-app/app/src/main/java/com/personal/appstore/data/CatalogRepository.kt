package com.personal.appstore.data

import com.personal.appstore.data.model.AppCatalog
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.http.ContentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

class CatalogRepository {

    private val client = HttpClient(Android) {
        install(ContentNegotiation) {
            val json = Json { ignoreUnknownKeys = true }
            json(json)
            json(json, ContentType.Text.Plain)
        }
    }

    suspend fun fetchCatalog(url: String): Result<AppCatalog> = runCatching {
        client.get(url).body<AppCatalog>()
    }
}
