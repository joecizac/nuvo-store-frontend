package com.jozze.nuvo.data.remote

import com.jozze.nuvo.core.logging.NuvoLogger
import com.jozze.nuvo.core.network.BaseResponse
import com.jozze.nuvo.domain.entity.Category
import com.jozze.nuvo.domain.entity.Product
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter

class CatalogApi(private val client: HttpClient) {
    suspend fun getCategories(storeId: String): BaseResponse<List<Category>> {
        return try {
            client.get("api/v1/stores/$storeId/categories").body()
        } catch (e: Exception) {
            NuvoLogger.e(TAG, e) {
                "Categories request failed. Using fallback data. store=$storeId"
            }
            BaseResponse(
                success = true,
                data = listOf(
                    Category("1", "Error | Coffee", null),
                    Category("2", "Error | Pastries", null),
                    Category("3", "Error | Sandwiches", null)
                )
            )
        }
    }

    suspend fun getProducts(storeId: String, categoryId: String?): BaseResponse<List<Product>> {
        return try {
            client.get("api/v1/stores/$storeId/products") {
                categoryId?.let { parameter("categoryId", it) }
            }.body()
        } catch (e: Exception) {
            NuvoLogger.e(TAG, e) {
                "Products request failed. Using fallback data. store=$storeId, category=$categoryId"
            }
            val products = listOf(
                Product(
                    "1",
                    "Error | Latte",
                    "Smooth espresso with steamed milk",
                    450,
                    null,
                    true,
                    "1",
                    4.7
                ),
                Product(
                    "2",
                    "Error | Cappuccino",
                    "Classic espresso with foamed milk",
                    400,
                    null,
                    true,
                    "1",
                    4.5
                ),
                Product("3", "Error | Croissant", "Buttery and flaky", 350, null, true, "2", 4.8),
                Product(
                    "4",
                    "Error | Pain au Chocolat",
                    "Croissant with chocolate",
                    400,
                    null,
                    true,
                    "2",
                    4.6
                )
            ).filter { categoryId == null || it.categoryId == categoryId }
            
            BaseResponse(
                success = true,
                data = products
            )
        }
    }

    suspend fun getProductById(productId: String): BaseResponse<Product> {
        return try {
            client.get("api/v1/products/$productId").body()
        } catch (e: Exception) {
            NuvoLogger.e(TAG, e) {
                "Product request failed. Using fallback data. product=$productId"
            }
            BaseResponse(
                success = true,
                data = Product(
                    "1",
                    "Error | Latte",
                    "Smooth espresso with steamed milk",
                    450,
                    null,
                    true,
                    "1",
                    4.7
                )
            )
        }
    }

    private companion object {
        const val TAG = "CatalogApi"
    }
}
