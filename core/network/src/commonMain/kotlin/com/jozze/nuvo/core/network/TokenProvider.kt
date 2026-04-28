package com.jozze.nuvo.core.network

/**
 * Interface to provide authentication token for API calls.
 */
interface TokenProvider {
    suspend fun getToken(): String?
}
