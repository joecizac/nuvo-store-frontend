package com.jozze.nuvo.data.repository

import com.jozze.nuvo.core.network.BaseResponse
import com.jozze.nuvo.data.remote.CatalogApi
import com.jozze.nuvo.domain.entity.Category
import com.jozze.nuvo.domain.entity.Product
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

class CatalogRepositoryImplTest {

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
    fun `getCategories returns list on success`() = runTest {
        val mockCategories = listOf(Category("1", "Cat 1", null))
        val response = BaseResponse(success = true, data = mockCategories)
        val client = createMockClient { json.encodeToString(response) }
        val repository = CatalogRepositoryImpl(CatalogApi(client))

        val result = repository.getCategories("1")

        assertTrue(result.isSuccess)
        assertEquals(mockCategories, result.getOrNull())
    }

    @Test
    fun `getProducts returns list on success`() = runTest {
        val mockProducts = listOf(Product("1", "Prod 1", "", 100, null, true, "1"))
        val response = BaseResponse(success = true, data = mockProducts)
        val client = createMockClient { json.encodeToString(response) }
        val repository = CatalogRepositoryImpl(CatalogApi(client))

        val result = repository.getProducts("1")

        assertTrue(result.isSuccess)
        assertEquals(mockProducts, result.getOrNull())
    }

    @Test
    fun `getProductById returns product on success`() = runTest {
        val mockProduct = Product("1", "Prod 1", "", 100, null, true, "1")
        val response = BaseResponse(success = true, data = mockProduct)
        val client = createMockClient { json.encodeToString(response) }
        val repository = CatalogRepositoryImpl(CatalogApi(client))

        val result = repository.getProductById("1")

        assertTrue(result.isSuccess)
        assertEquals(mockProduct, result.getOrNull())
    }
}
