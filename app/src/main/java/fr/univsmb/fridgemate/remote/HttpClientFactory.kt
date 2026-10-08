package fr.univsmb.fridgemate.remote

import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.UserAgent
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

object HttpClientFactory {

    fun create(): HttpClient {
        return HttpClient(engineFactory = OkHttp) {
            install(ContentNegotiation) {
                json(
                    Json {
                        ignoreUnknownKeys=true
                    }
                )
            }

            install(plugin = Logging) {
                logger = object: Logger {
                    override fun log(message: String) {
                        Log.d("HttpClient", message)
                    }
                }
                level = LogLevel.BODY
            }

            install(UserAgent) {
                agent = "FridgeMate/1.0 nanajounchan@gmail.com"
            }

            install(HttpTimeout) {
                requestTimeoutMillis = 10_000
            }

            defaultRequest {
                url("https://world.openfoodfacts.org/")
            }
        }
    }
}