package com.jozze.nuvo.domain.repository

import com.jozze.nuvo.domain.entity.Address
import com.jozze.nuvo.domain.entity.User

interface UserRepository {
    suspend fun getMyProfile(): Result<User>
    suspend fun getMyAddresses(): Result<List<Address>>
}
