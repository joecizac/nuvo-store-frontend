package com.jozze.nuvo.feature.checkout

import com.jozze.nuvo.core.mvi.*
import com.jozze.nuvo.domain.entity.Address
import com.jozze.nuvo.domain.entity.CartItem

interface CheckoutContract : MviContract<CheckoutContract.State, CheckoutContract.Intent, CheckoutContract.Effect> {

    data class State(
        val cartItems: List<CartItem> = emptyList(),
        val addresses: List<Address> = emptyList(),
        val selectedAddressId: String? = null,
        val isLoading: Boolean = false,
        val isPlacingOrder: Boolean = false,
        val error: String? = null
    ) : MviState

    sealed interface Intent : MviIntent {
        object LoadData : Intent
        data class SelectAddress(val addressId: String) : Intent
        object PlaceOrder : Intent
    }

    sealed interface Effect : MviEffect {
        object NavigateToOrderConfirmation : Effect
        data class ShowError(val message: String) : Effect
    }
}
