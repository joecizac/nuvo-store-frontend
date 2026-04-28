package com.jozze.nuvo.core.network

import kotlinx.serialization.Serializable

/**
 * Base response structure for all API calls.
 */
@Serializable
data class BaseResponse<T>(
    val success: Boolean,
    val data: T? = null,
    val message: String? = null,
    val errorCode: String? = null
)
