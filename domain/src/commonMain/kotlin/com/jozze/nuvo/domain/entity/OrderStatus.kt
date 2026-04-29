package com.jozze.nuvo.domain.entity

import kotlinx.serialization.Serializable

@Serializable
enum class OrderStatus {
    PENDING,
    PLACED,
    PREPARING,
    OUT_FOR_DELIVERY,
    DELIVERED,
    CANCELLED
}
