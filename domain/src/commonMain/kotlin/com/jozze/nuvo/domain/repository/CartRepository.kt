package com.jozze.nuvo.domain.repository

import com.jozze.nuvo.domain.entity.CartItem
import kotlinx.coroutines.flow.Flow

interface CartRepository {
    fun getCartItems(): Flow<List<CartItem>>
    suspend fun addItem(item: CartItem)
    suspend fun updateQuantity(itemId: String, quantity: Int)
    suspend fun removeItem(itemId: String)
    suspend fun clearCart()
}
