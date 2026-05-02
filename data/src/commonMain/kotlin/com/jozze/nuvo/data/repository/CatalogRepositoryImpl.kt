package com.jozze.nuvo.data.repository

import com.jozze.nuvo.core.logging.NuvoLogger
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
                NuvoLogger.d(TAG) { "Loaded categories. store=$storeId, count=${data.size}" }
                Result.success(data)
            } else {
                val exception = Exception(response.message ?: "Failed to get categories")
                NuvoLogger.e(TAG, exception) { "Failed to load categories. store=$storeId" }
                Result.failure(exception)
            }
        } catch (e: Exception) {
            NuvoLogger.e(TAG, e) { "Exception loading categories. store=$storeId" }
            Result.failure(e)
        }
    }

    override suspend fun getProducts(storeId: String, categoryId: String?): Result<List<Product>> {
        return try {
            val response = api.getProducts(storeId, categoryId)
            val data = response.data
            if (response.success && data != null) {
                NuvoLogger.d(TAG) {
                    "Loaded products. store=$storeId, category=$categoryId, count=${data.size}"
                }
                Result.success(data)
            } else {
                val exception = Exception(response.message ?: "Failed to get products")
                NuvoLogger.e(TAG, exception) {
                    "Failed to load products. store=$storeId, category=$categoryId"
                }
                Result.failure(exception)
            }
        } catch (e: Exception) {
            NuvoLogger.e(TAG, e) {
                "Exception loading products. store=$storeId, category=$categoryId"
            }
            Result.failure(e)
        }
    }

    override suspend fun getProductById(productId: String): Result<Product> {
        return try {
            val response = api.getProductById(productId)
            val data = response.data
            if (response.success && data != null) {
                NuvoLogger.d(TAG) { "Loaded product. product=$productId" }
                Result.success(data)
            } else {
                val exception = Exception(response.message ?: "Product not found")
                NuvoLogger.e(TAG, exception) { "Failed to load product. product=$productId" }
                Result.failure(exception)
            }
        } catch (e: Exception) {
            NuvoLogger.e(TAG, e) { "Exception loading product. product=$productId" }
            Result.failure(e)
        }
    }

    private companion object {
        const val TAG = "CatalogRepository"
    }
}
