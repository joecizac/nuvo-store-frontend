package com.jozze.nuvo.data.remote

import com.jozze.nuvo.core.network.BaseResponse
import com.jozze.nuvo.domain.entity.Store
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter

class StoreApi(private val client: HttpClient) {
    suspend fun getNearbyStores(lat: Double, lng: Double, radius: Int): BaseResponse<List<Store>> {
        return client.get("api/v1/stores") {
            parameter("lat", lat)
            parameter("lng", lng)
            parameter("radius", radius)
        }.body()
    }

    suspend fun getStoreById(id: String): BaseResponse<Store> {
        return client.get("api/v1/stores/$id").body()
    }
}
