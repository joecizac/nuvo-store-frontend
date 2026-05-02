package com.jozze.nuvo.data.remote

import com.jozze.nuvo.core.logging.NuvoLogger
import com.jozze.nuvo.core.network.BaseResponse
import com.jozze.nuvo.domain.entity.Store
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter

class StoreApi(private val client: HttpClient) {
    suspend fun getNearbyStores(lat: Double, lng: Double, radius: Int): BaseResponse<List<Store>> {
        return try {
            client.get("api/v1/stores") {
                parameter("lat", lat)
                parameter("lng", lng)
                parameter("radius", radius)
            }.body()
        } catch (e: Exception) {
            NuvoLogger.e(TAG, e) {
                "Nearby stores request failed. Using fallback data. lat=$lat, lng=$lng, radius=$radius"
            }
            // Mock data fallback for UI testing without backend
            BaseResponse(
                success = true,
                data = listOf(
                    Store(
                        "1",
                        "Error | The Coffee House",
                        "Best coffee in town",
                        "https://images.unsplash.com/photo-1501339847302-ac426a4a7cbb",
                        -33.9249,
                        18.4241,
                        4.8,
                        1.2
                    ),
                    Store(
                        "2",
                        "Error | Pizza Palace",
                        "Authentic Italian pizza",
                        "https://images.unsplash.com/photo-1513104890138-7c749659a591",
                        -33.9230,
                        18.4230,
                        4.5,
                        2.5
                    ),
                    Store(
                        "3",
                        "Error | Burger Bistro",
                        "Juicy burgers and fries",
                        "https://images.unsplash.com/photo-1550547660-d9450f859349",
                        -33.9260,
                        18.4250,
                        4.2,
                        3.1
                    )
                )
            )
        }
    }

    suspend fun getStoreById(id: String): BaseResponse<Store> {
        return try {
            client.get("api/v1/stores/$id").body()
        } catch (e: Exception) {
            NuvoLogger.e(TAG, e) { "Store request failed. Using fallback data. store=$id" }
            BaseResponse(
                success = true,
                data = Store(
                    "1",
                    "Error | The Coffee House",
                    "Best coffee in town",
                    "https://images.unsplash.com/photo-1501339847302-ac426a4a7cbb",
                    -33.9249,
                    18.4241,
                    4.8,
                    1.2
                )
            )
        }
    }

    private companion object {
        const val TAG = "StoreApi"
    }
}
