package com.jozze.nuvo.data.remote

import com.jozze.nuvo.core.logging.NuvoLogger
import com.jozze.nuvo.domain.entity.CartItem
import io.ktor.client.HttpClient

class CartApi(private val client: HttpClient) {
    suspend fun getCart(): List<CartItem> {
        // Mock API call: return empty by default for clean test state
        // In a real app, this would fetch from the server
        NuvoLogger.d(TAG) { "Mock get cart returned empty list" }
        return emptyList()
    }

    suspend fun syncCart(items: List<CartItem>) {
        // Mock sync
        NuvoLogger.d(TAG) { "Mock cart sync accepted itemCount=${items.size}" }
    }

    private companion object {
        const val TAG = "CartApi"
    }
}
