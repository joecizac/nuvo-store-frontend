package com.jozze.nuvo.feature.cart

import androidx.lifecycle.viewModelScope
import com.jozze.nuvo.core.mvi.BaseViewModel
import com.jozze.nuvo.domain.entity.CartItem
import com.jozze.nuvo.domain.repository.CartRepository
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class CartViewModel(
    private val cartRepository: CartRepository
) : BaseViewModel<CartContract.State, CartContract.Intent, CartContract.Effect>(CartContract.State()) {

    init {
        handleIntent(CartContract.Intent.LoadCart)
    }

    override fun handleIntent(intent: CartContract.Intent) {
        when (intent) {
            is CartContract.Intent.LoadCart -> observeCart()
            is CartContract.Intent.UpdateQuantity -> updateQuantity(intent.itemId, intent.quantity)
            is CartContract.Intent.RemoveItem -> removeItem(intent.itemId)
            is CartContract.Intent.ClearCart -> clearCart()
            is CartContract.Intent.AddItemWithClear -> addItemWithClear(intent.item)
            is CartContract.Intent.DismissDialog -> setState { copy(showClearCartDialog = null) }
            is CartContract.Intent.Checkout -> emitEffect(CartContract.Effect.NavigateToCheckout)
        }
    }

    private fun observeCart() {
        cartRepository.getCartItems()
            .onEach { items ->
                setState { copy(items = items, isLoading = false) }
            }
            .launchIn(viewModelScope)
    }

    private fun updateQuantity(itemId: String, quantity: Int) {
        viewModelScope.launch {
            cartRepository.updateQuantity(itemId, quantity)
        }
    }

    private fun removeItem(itemId: String) {
        viewModelScope.launch {
            cartRepository.removeItem(itemId)
        }
    }

    private fun clearCart() {
        viewModelScope.launch {
            cartRepository.clearCart()
        }
    }

    private fun addItemWithClear(item: CartItem) {
        viewModelScope.launch {
            cartRepository.clearCart()
            cartRepository.addItem(item)
            setState { copy(showClearCartDialog = null) }
        }
    }
}
