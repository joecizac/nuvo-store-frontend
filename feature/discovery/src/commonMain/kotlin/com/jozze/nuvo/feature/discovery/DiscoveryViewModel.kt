package com.jozze.nuvo.feature.discovery

import androidx.lifecycle.viewModelScope
import com.jozze.nuvo.core.mvi.BaseViewModel
import com.jozze.nuvo.domain.repository.StoreRepository
import kotlinx.coroutines.launch

class DiscoveryViewModel(
    private val storeRepository: StoreRepository
) : BaseViewModel<DiscoveryState, DiscoveryIntent, DiscoveryEffect>(DiscoveryState()) {

    override fun handleIntent(intent: DiscoveryIntent) {
        when (intent) {
            is DiscoveryIntent.LoadNearbyStores -> loadNearbyStores(
                intent.lat,
                intent.lng,
                intent.radius
            )
        }
    }

    private fun loadNearbyStores(lat: Double, lng: Double, radius: Int) {
        viewModelScope.launch {
            setState { copy(isLoading = true, error = null) }
            val result = storeRepository.getNearbyStores(lat, lng, radius)
            result.onSuccess { stores ->
                setState { copy(isLoading = false, stores = stores) }
            }.onFailure { error ->
                setState { copy(isLoading = false, error = error.message ?: "Unknown error") }
            }
        }
    }
}
