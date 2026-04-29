package com.jozze.nuvo.data.repository

import com.jozze.nuvo.data.remote.CatalogApi
import com.jozze.nuvo.domain.entity.Category
import com.jozze.nuvo.domain.entity.Product
import com.jozze.nuvo.domain.repository.CatalogRepository

class CatalogRepositoryImpl(private val api: CatalogApi) : CatalogRepository {
    override suspend fun getCategories(storeId: String): Result<List<Category>> {
        return try {
            val response = api.getCategories(storeId)
            val data = response.data
            if (response.success && data != null) {
                Result.success(data)
            } else {
                Result.failure(Exception(response.message ?: "Failed to get categories"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getProducts(storeId: String, categoryId: String?): Result<List<Product>> {
        return try {
            val response = api.getProducts(storeId, categoryId)
            val data = response.data
            if (response.success && data != null) {
                Result.success(data)
            } else {
                Result.failure(Exception(response.message ?: "Failed to get products"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getProductById(productId: String): Result<Product> {
        return try {
            val response = api.getProductById(productId)
            val data = response.data
            if (response.success && data != null) {
                Result.success(data)
            } else {
                Result.failure(Exception(response.message ?: "Product not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
