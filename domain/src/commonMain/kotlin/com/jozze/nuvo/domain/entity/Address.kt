package com.jozze.nuvo.domain.entity

import kotlinx.serialization.Serializable

@Serializable
data class Address(
    val id: String,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val addressLine: String,
    val city: String?,
    val state: String?,
    val country: String?,
    val isDefault: Boolean
)
