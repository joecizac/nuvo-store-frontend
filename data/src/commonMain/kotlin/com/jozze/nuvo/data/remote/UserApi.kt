package com.jozze.nuvo.data.remote

import com.jozze.nuvo.core.network.BaseResponse
import com.jozze.nuvo.domain.entity.Address
import com.jozze.nuvo.domain.entity.User
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

class UserApi(private val client: HttpClient) {
    suspend fun getMyProfile(): BaseResponse<User> {
        return client.get("api/v1/users/me").body()
    }

    suspend fun getMyAddresses(): BaseResponse<List<Address>> {
        return client.get("api/v1/users/me/addresses").body()
    }
}
