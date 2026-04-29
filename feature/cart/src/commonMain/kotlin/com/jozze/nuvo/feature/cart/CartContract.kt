package com.jozze.nuvo.feature.cart

import com.jozze.nuvo.core.mvi.*
import com.jozze.nuvo.domain.entity.CartItem

interface CartContract : MviContract<CartContract.State, CartContract.Intent, CartContract.Effect> {

    data class State(
        val items: List<CartItem> = emptyList(),
        val isLoading: Boolean = false,
        val error: String? = null,
        val showClearCartDialog: String? = null // newStoreId if dialog should show
    ) : MviState

    sealed interface Intent : MviIntent {
        object LoadCart : Intent
        data class UpdateQuantity(val itemId: String, val quantity: Int) : Intent
        data class RemoveItem(val itemId: String) : Intent
        object ClearCart : Intent
        data class AddItemWithClear(val item: CartItem) : Intent
        object DismissDialog : Intent
        object Checkout : Intent
    }

    sealed interface Effect : MviEffect {
        object NavigateToCheckout : Effect
        data class ShowError(val message: String) : Effect
    }
}
