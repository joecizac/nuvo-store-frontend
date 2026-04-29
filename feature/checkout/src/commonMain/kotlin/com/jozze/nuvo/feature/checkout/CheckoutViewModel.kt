package com.jozze.nuvo.feature.checkout

import androidx.lifecycle.viewModelScope
import com.jozze.nuvo.core.mvi.BaseViewModel
import com.jozze.nuvo.domain.repository.CartRepository
import com.jozze.nuvo.domain.repository.OrderRepository
import com.jozze.nuvo.domain.repository.UserRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class CheckoutViewModel(
    private val cartRepository: CartRepository,
    private val userRepository: UserRepository,
    private val orderRepository: OrderRepository
) : BaseViewModel<CheckoutContract.State, CheckoutContract.Intent, CheckoutContract.Effect>(CheckoutContract.State()) {

    init {
        handleIntent(CheckoutContract.Intent.LoadData)
    }

    override fun handleIntent(intent: CheckoutContract.Intent) {
        when (intent) {
            is CheckoutContract.Intent.LoadData -> loadData()
            is CheckoutContract.Intent.SelectAddress -> setState { copy(selectedAddressId = intent.addressId) }
            is CheckoutContract.Intent.PlaceOrder -> placeOrder()
        }
    }

    private fun loadData() {
        viewModelScope.launch {
            setState { copy(isLoading = true) }
            val cartItemsDeferred = async { cartRepository.getCartItems().firstOrNull() }
            val addressesResult = userRepository.getMyAddresses()

            val cartItems = cartItemsDeferred.await() ?: emptyList()
            val addresses = addressesResult.getOrDefault(emptyList())

            if (addressesResult.isFailure) {
                emitEffect(CheckoutContract.Effect.ShowError(addressesResult.exceptionOrNull()?.message ?: "Failed to load addresses"))
            }

            setState {
                copy(
                    isLoading = false,
                    cartItems = cartItems,
                    addresses = addresses,
                    selectedAddressId = addresses.firstOrNull()?.id
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
