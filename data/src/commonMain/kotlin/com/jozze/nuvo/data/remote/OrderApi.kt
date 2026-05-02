package com.jozze.nuvo.data.remote

import com.jozze.nuvo.core.logging.NuvoLogger
import com.jozze.nuvo.domain.entity.Order
import com.jozze.nuvo.domain.entity.OrderStatus
import io.ktor.client.HttpClient

class OrderApi(private val client: HttpClient) {
    suspend fun placeOrder(addressId: String): Order {
        // Mock order placement
        NuvoLogger.d(TAG) { "Mock place order. address=$addressId" }
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
        NuvoLogger.d(TAG) { "Mock get orders returned empty list" }
        return emptyList()
    }

    private companion object {
        const val TAG = "OrderApi"
    }
}
