package com.jozze.nuvo.data.repository

import com.jozze.nuvo.core.logging.NuvoLogger
import com.jozze.nuvo.data.remote.OrderApi
import com.jozze.nuvo.domain.entity.Order
import com.jozze.nuvo.domain.repository.OrderRepository

class OrderRepositoryImpl(
    private val orderApi: OrderApi
) : OrderRepository {
    override suspend fun placeOrder(addressId: String): Result<Order> {
        NuvoLogger.d(TAG) { "Placing order. address=$addressId" }
        return runCatching { orderApi.placeOrder(addressId) }
            .onSuccess { order ->
                NuvoLogger.i(TAG) { "Order placed. order=${order.id}, status=${order.status}" }
            }
            .onFailure { error ->
                NuvoLogger.e(TAG, error) { "Failed to place order. address=$addressId" }
            }
    }

    override suspend fun getOrders(): Result<List<Order>> {
        return runCatching { orderApi.getOrders() }
            .onSuccess { orders ->
                NuvoLogger.d(TAG) { "Loaded orders. count=${orders.size}" }
            }
            .onFailure { error ->
                NuvoLogger.e(TAG, error) { "Failed to load orders" }
            }
    }

    private companion object {
        const val TAG = "OrderRepository"
    }
}
