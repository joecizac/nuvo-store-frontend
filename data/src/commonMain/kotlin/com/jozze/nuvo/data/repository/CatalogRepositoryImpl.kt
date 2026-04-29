package com.jozze.nuvo.data.repository

import com.jozze.nuvo.data.remote.CatalogApi
import com.jozze.nuvo.domain.entity.Category
import com.jozze.nuvo.domain.entity.Product
import com.jozze.nuvo.domain.repository.CatalogRepository

class CatalogRepositoryImpl(private val api: CatalogApi) : CatalogRepository {
    override suspend fun getCategories(storeId: String): Result<List<Category>> {
        // Return mock data for testing
        val mockCategories = listOf(
            Category("1", "Coffee", null),
            Category("2", "Pastries", null),
            Category("3", "Breakfast", null)
        )
        return Result.success(mockCategories)
    }

    override suspend fun getProducts(storeId: String, categoryId: String?): Result<List<Product>> {
        // Return mock data for testing
        val mockProducts = listOf(
            Product("1", "Latte", "Smooth espresso with steamed milk", 450, null, true, "1"),
            Product("2", "Cappuccino", "Classic espresso with foamed milk", 400, null, true, "1"),
            Product("3", "Croissant", "Buttery and flaky", 350, null, true, "2"),
            Product("4", "Pain au Chocolat", "Croissant with chocolate", 400, null, true, "2")
        ).filter { categoryId == null || it.categoryId == categoryId }

        return Result.success(mockProducts)
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
