package com.jozze.nuvo.data.repository

import com.jozze.nuvo.data.remote.OrderApi
import com.jozze.nuvo.domain.entity.Order
import com.jozze.nuvo.domain.repository.OrderRepository

class OrderRepositoryImpl(
    private val orderApi: OrderApi
) : OrderRepository {
    override suspend fun placeOrder(addressId: String): Result<Order> {
        return runCatching { orderApi.placeOrder(addressId) }
    }

    override suspend fun getOrders(): Result<List<Order>> {
        return runCatching { orderApi.getOrders() }
    }
}
