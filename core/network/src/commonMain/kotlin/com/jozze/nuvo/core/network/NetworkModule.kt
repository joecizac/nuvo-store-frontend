package com.jozze.nuvo.core.network

import com.jozze.nuvo.core.logging.NuvoLogger
import io.ktor.client.HttpClient
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.HttpRequestPipeline
import io.ktor.client.request.header
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.dsl.module

val networkModule = module {
    single {
        val tokenProvider: TokenProvider? = getOrNull()
        val baseUrl = getProperty("SERVER_URL", "http://localhost:8080/")

        HttpClient {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    prettyPrint = true
                    isLenient = true
                })
            }
            if (NuvoLogger.isEnabled) {
                install(Logging) {
                    level = LogLevel.ALL
                    logger = object : Logger {
                        override fun log(message: String) {
                            NuvoLogger.d("Network") { message }
                        }
                    }
                    sanitizeHeader { header -> header == HttpHeaders.Authorization }
                }
            }

            defaultRequest {
                url(baseUrl)
            }

            install(DefaultRequest) {
                header(HttpHeaders.ContentType, ContentType.Application.Json)
            }

            // JWT Interceptor
            if (tokenProvider != null) {
                install("AuthInterceptor") {
                    requestPipeline.intercept(HttpRequestPipeline.State) {
                        val token = tokenProvider.getToken()
                        if (token != null) {
                            context.header(HttpHeaders.Authorization, "Bearer $token")
                            NuvoLogger.d("Network") {
                                "Attached Authorization header to ${context.url.buildString()}"
                            }
                        }
                    }
                }
            }
        }
    }
}
