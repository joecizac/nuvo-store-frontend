package com.jozze.nuvo.domain.entity

import kotlinx.serialization.Serializable

@Serializable
data class CartItem(
    val id: String,
    val productId: String,
    val name: String,
    val priceCents: Long,
    val quantity: Int,
    val storeId: String,
    val imageUrl: String? = null
) {
    val priceAmount: Double get() = priceCents / 100.0
}
