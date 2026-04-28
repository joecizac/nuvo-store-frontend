package com.jozze.nuvo.domain.repository

import com.jozze.nuvo.domain.entity.Category
import com.jozze.nuvo.domain.entity.Product

interface CatalogRepository {
    suspend fun getCategories(storeId: String): Result<List<Category>>
    suspend fun getProducts(storeId: String, categoryId: String? = null): Result<List<Product>>
    suspend fun getProductById(productId: String): Result<Product>
}
