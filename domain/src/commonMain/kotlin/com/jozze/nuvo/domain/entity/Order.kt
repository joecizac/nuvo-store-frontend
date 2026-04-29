package com.jozze.nuvo.domain.entity

import kotlinx.serialization.Serializable

@Serializable
data class Order(
    val id: String,
    val items: List<CartItem>,
    val totalCents: Long,
    val addressId: String,
    val status: OrderStatus,
    val createdAt: Long
) {
    val totalAmount: Double get() = totalCents / 100.0
}
