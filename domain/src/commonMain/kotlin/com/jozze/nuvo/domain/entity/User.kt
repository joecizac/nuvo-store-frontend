package com.jozze.nuvo.domain.entity

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val uid: String,
    val email: String,
    val name: String? = null,
    val phoneNumber: String? = null,
    val profileImageUrl: String? = null
)
