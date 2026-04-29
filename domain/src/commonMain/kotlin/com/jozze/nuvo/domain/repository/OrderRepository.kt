package com.jozze.nuvo.domain.repository

import com.jozze.nuvo.domain.entity.Order

interface OrderRepository {
    suspend fun placeOrder(addressId: String): Result<Order>
    suspend fun getOrders(): Result<List<Order>>
}
