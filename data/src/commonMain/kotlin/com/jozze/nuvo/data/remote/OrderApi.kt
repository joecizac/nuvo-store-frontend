package com.jozze.nuvo.data.remote

import com.jozze.nuvo.domain.entity.Order
import com.jozze.nuvo.domain.entity.OrderStatus
import io.ktor.client.*

class OrderApi(private val client: HttpClient) {
    suspend fun placeOrder(addressId: String): Order {
        // Mock order placement
        return Order(
            id = "order_123",
            items = emptyList(),
            totalCents = 0L,
            addressId = addressId,
            status = OrderStatus.PLACED,
            createdAt = 123456789L
        )
    }

    suspend fun getOrders(): List<Order> {
        return emptyList()
    }
}
