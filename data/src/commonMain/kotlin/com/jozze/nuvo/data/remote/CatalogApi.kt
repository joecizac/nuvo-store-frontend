package com.jozze.nuvo.data.remote

import com.jozze.nuvo.core.network.BaseResponse
import com.jozze.nuvo.domain.entity.Category
import com.jozze.nuvo.domain.entity.Product
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter

class CatalogApi(private val client: HttpClient) {
    suspend fun getCategories(storeId: String): BaseResponse<List<Category>> {
        return client.get("api/v1/stores/$storeId/categories").body()
    }

    suspend fun getProducts(
        storeId: String,
        categoryId: String? = null
    ): BaseResponse<List<Product>> {
        return client.get("api/v1/stores/$storeId/products") {
            categoryId?.let { parameter("categoryId", it) }
        }.body()
    }

    suspend fun getProductById(productId: String): BaseResponse<Product> {
        return client.get("api/v1/products/$productId").body()
    }
}
