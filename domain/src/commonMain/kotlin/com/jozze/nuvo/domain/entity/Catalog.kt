package com.jozze.nuvo.domain.entity

import kotlinx.serialization.Serializable

@Serializable
data class Category(
    val id: String,
    val name: String,
    val imageUrl: String?
)

@Serializable
data class Product(
    val id: String,
    val name: String,
    val description: String?,
    val price: Double,
    val imageUrl: String?,
    val isAvailable: Boolean,
    val categoryId: String?
)
