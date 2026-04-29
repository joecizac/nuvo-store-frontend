package com.jozze.nuvo.data.repository

import com.jozze.nuvo.core.network.BaseResponse
import com.jozze.nuvo.data.remote.StoreApi
import com.jozze.nuvo.domain.entity.Store
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class StoreRepositoryImplTest {

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    private fun createMockClient(handler: suspend () -> String): HttpClient {
        val mockEngine = MockEngine { request ->
            respond(
                content = handler(),
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            )
        }
        return HttpClient(mockEngine) {
            install(ContentNegotiation) {
                json(json)
            }
        }
    }

    @Test
    fun `getNearbyStores returns list of stores on success`() = runTest {
        val mockStores = listOf(
            Store("1", "Store 1", "", null, 0.0, 0.0, 4.5, 1.0)
        )
        val response = BaseResponse(success = true, data = mockStores)
        val client = createMockClient { json.encodeToString(response) }
        val repository = StoreRepositoryImpl(StoreApi(client))

        val result = repository.getNearbyStores(0.0, 0.0, 10)

        assertTrue(result.isSuccess)
        assertEquals(mockStores, result.getOrNull())
    }

    @Test
    fun `getStoreById returns store on success`() = runTest {
        val mockStore = Store("1", "Store 1", "", null, 0.0, 0.0, 4.5, 1.0)
        val response = BaseResponse(success = true, data = mockStore)
        val client = createMockClient { json.encodeToString(response) }
        val repository = StoreRepositoryImpl(StoreApi(client))

        val result = repository.getStoreById("1")

        assertTrue(result.isSuccess)
        assertEquals(mockStore, result.getOrNull())
    }

    @Test
    fun `getStoreById returns failure on error response`() = runTest {
        val response: BaseResponse<Store> = BaseResponse(success = false, message = "Error")
        val client = createMockClient { json.encodeToString(response) }
        val repository = StoreRepositoryImpl(StoreApi(client))

        val result = repository.getStoreById("1")

        assertTrue(result.isFailure)
        assertEquals("Error", result.exceptionOrNull()?.message)
    }
}
