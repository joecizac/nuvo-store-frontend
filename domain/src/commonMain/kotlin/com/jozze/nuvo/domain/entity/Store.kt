package com.jozze.nuvo.domain.entity

import kotlinx.serialization.Serializable

@Serializable
data class Store(
    val id: String,
    val name: String,
    val description: String?,
    val imageUrl: String?,
    val latitude: Double,
    val longitude: Double,
    val rating: Double,
    val distance: Double? = null
)
