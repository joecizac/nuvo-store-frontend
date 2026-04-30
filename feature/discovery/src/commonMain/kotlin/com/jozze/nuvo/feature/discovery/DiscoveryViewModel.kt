package com.jozze.nuvo.feature.discovery

import androidx.lifecycle.viewModelScope
import com.jozze.nuvo.core.mvi.BaseViewModel
import com.jozze.nuvo.domain.repository.FavouriteRepository
import com.jozze.nuvo.domain.repository.StoreRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class DiscoveryViewModel(
    private val storeRepository: StoreRepository,
    private val favouriteRepository: FavouriteRepository
) : BaseViewModel<DiscoveryState, DiscoveryIntent, DiscoveryEffect>(DiscoveryState()) {

    init {
        observeFavourites()
    }

    override fun handleIntent(intent: DiscoveryIntent) {
        when (intent) {
            is DiscoveryIntent.LoadNearbyStores -> loadNearbyStores(
                intent.lat,
                intent.lng,
                intent.radius
            )
            is DiscoveryIntent.ToggleFavourite -> toggleFavourite(intent.storeId)
        }
    }

    private fun observeFavourites() {
        favouriteRepository.getFavouriteStores()
            .onEach { favouriteIds ->
                setState {
                    copy(stores = stores.map { store ->
                        store.copy(isFavourite = favouriteIds.contains(store.id))
                    })
                }
            }
            .launchIn(viewModelScope)
    }

    private fun loadNearbyStores(lat: Double, lng: Double, radius: Int) {
        viewModelScope.launch {
            setState { copy(isLoading = true, error = null) }
            val result = storeRepository.getNearbyStores(lat, lng, radius)
            val favouriteIds = try {
                favouriteRepository.getFavouriteStores().first()
            } catch (e: Exception) {
                emptyList()
            }

            result.onSuccess { stores ->
                setState {
                    copy(
                        isLoading = false,
                        stores = stores.map { it.copy(isFavourite = favouriteIds.contains(it.id)) }
                    )
                }
            }.onFailure { error ->
                setState { copy(isLoading = false, error = error.message ?: "Unknown error") }
            }
        }
    }

    private fun toggleFavourite(storeId: String) {
        viewModelScope.launch {
            favouriteRepository.toggleStoreFavourite(storeId)
        }
    }
}
