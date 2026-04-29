package com.jozze.nuvo.feature.checkout

import androidx.lifecycle.viewModelScope
import com.jozze.nuvo.core.mvi.BaseViewModel
import com.jozze.nuvo.domain.repository.CartRepository
import com.jozze.nuvo.domain.repository.OrderRepository
import com.jozze.nuvo.domain.repository.UserRepository
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class CheckoutViewModel(
    private val cartRepository: CartRepository,
    private val userRepository: UserRepository,
    private val orderRepository: OrderRepository
) : BaseViewModel<CheckoutContract.State, CheckoutContract.Intent, CheckoutContract.Effect>(CheckoutContract.State()) {

    init {
        observeCart()
        handleIntent(CheckoutContract.Intent.LoadData)
    }

    override fun handleIntent(intent: CheckoutContract.Intent) {
        when (intent) {
            is CheckoutContract.Intent.LoadData -> loadData()
            is CheckoutContract.Intent.SelectAddress -> setState { copy(selectedAddressId = intent.addressId) }
            is CheckoutContract.Intent.PlaceOrder -> placeOrder()
        }
    }

    private fun observeCart() {
        cartRepository.getCartItems()
            .onEach { items ->
                setState { copy(cartItems = items) }
            }
            .launchIn(viewModelScope)
    }

    private fun loadData() {
        viewModelScope.launch {
            setState { copy(isLoading = true) }
            val addressesResult = userRepository.getMyAddresses()

            if (addressesResult.isFailure) {
                emitEffect(CheckoutContract.Effect.ShowError(addressesResult.exceptionOrNull()?.message ?: "Failed to load addresses"))
            }

            val addresses = addressesResult.getOrDefault(emptyList())

            setState {
                copy(
                    isLoading = false,
                    addresses = addresses,
                    selectedAddressId = state.selectedAddressId ?: addresses.firstOrNull { it.isDefault }?.id ?: addresses.firstOrNull()?.id
                )
            }
        }
    }

    private fun placeOrder() {
        val addressId = state.selectedAddressId ?: return
        viewModelScope.launch {
            setState { copy(isPlacingOrder = true) }
            val result = orderRepository.placeOrder(addressId)
            setState { copy(isPlacingOrder = false) }

            result.onSuccess {
                cartRepository.clearCart()
                emitEffect(CheckoutContract.Effect.NavigateToOrderConfirmation)
            }.onFailure { error ->
                emitEffect(CheckoutContract.Effect.ShowError(error.message ?: "Failed to place order"))
            }
        }
    }
}
