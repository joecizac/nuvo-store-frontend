package com.jozze.nuvo.feature.discovery

import com.jozze.nuvo.core.mvi.*
import com.jozze.nuvo.domain.entity.Store

data class DiscoveryState(
    val isLoading: Boolean = false,
    val stores: List<Store> = emptyList(),
    val error: String? = null
) : MviState

sealed interface DiscoveryIntent : MviIntent {
    data class LoadNearbyStores(val lat: Double, val lng: Double, val radius: Int) : DiscoveryIntent
}

sealed interface DiscoveryEffect : MviEffect {
    data class ShowError(val message: String) : DiscoveryEffect
}
