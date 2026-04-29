package com.jozze.nuvo.data.remote

import com.jozze.nuvo.domain.entity.CartItem
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.http.*

class CartApi(private val client: HttpClient) {
    suspend fun getCart(): List<CartItem> {
        // Mock API call: return empty by default for clean test state
        // In a real app, this would fetch from the server
        return emptyList()
    }

    suspend fun syncCart(items: List<CartItem>) {
        // Mock sync
    }
}
