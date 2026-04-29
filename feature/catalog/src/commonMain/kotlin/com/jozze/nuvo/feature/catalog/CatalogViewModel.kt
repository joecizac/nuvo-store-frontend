package com.jozze.nuvo.feature.catalog

import androidx.lifecycle.viewModelScope
import com.jozze.nuvo.core.mvi.BaseViewModel
import com.jozze.nuvo.domain.entity.CartItem
import com.jozze.nuvo.domain.entity.Product
import com.jozze.nuvo.domain.exception.DifferentStoreCartException
import com.jozze.nuvo.domain.repository.CartRepository
import com.jozze.nuvo.domain.repository.CatalogRepository
import com.jozze.nuvo.domain.repository.StoreRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class CatalogViewModel(
    private val storeRepository: StoreRepository,
    private val catalogRepository: CatalogRepository,
    private val cartRepository: CartRepository
) : BaseViewModel<CatalogState, CatalogIntent, CatalogContract.Effect>(CatalogState()) {

    override fun handleIntent(intent: CatalogIntent) {
        when (intent) {
            is CatalogIntent.LoadCatalog -> loadCatalog(intent.storeId)
            is CatalogIntent.FilterByCategory -> filterByCategory(intent.storeId, intent.categoryId)
            is CatalogIntent.AddToCart -> addToCart(intent.product)
            is CatalogIntent.ClearCartAndAdd -> clearCartAndAdd(intent.product)
            is CatalogIntent.DismissDialog -> setState { copy(showClearCartDialog = null) }
        }
    }

    private fun addToCart(product: Product) {
        viewModelScope.launch {
            val currentStore = state.store
            try {
                val cartItem = CartItem(
                    id = product.id,
                    productId = product.id,
                    name = product.name,
                    priceCents = product.priceCents,
                    quantity = 1,
                    storeId = currentStore?.id ?: "",
                    imageUrl = product.imageUrl
                )
                cartRepository.addItem(cartItem)
                emitEffect(CatalogContract.Effect.ShowSnackbar("${product.name} added to cart"))
            } catch (e: DifferentStoreCartException) {
                setState { copy(showClearCartDialog = product) }
            }
        }
    }

    private fun clearCartAndAdd(product: Product) {
        viewModelScope.launch {
            cartRepository.clearCart()
            addToCart(product)
            setState { copy(showClearCartDialog = null) }
        }
    }

    private fun loadCatalog(storeId: String) {
        viewModelScope.launch {
            setState { copy(isLoading = true, error = null) }

            val storeDeferred = async { storeRepository.getStoreById(storeId) }
            val categoriesDeferred = async { catalogRepository.getCategories(storeId) }
            val productsDeferred = async { catalogRepository.getProducts(storeId) }

            val storeResult = storeDeferred.await()
            val categoriesResult = categoriesDeferred.await()
            val productsResult = productsDeferred.await()

            if (storeResult.isSuccess && categoriesResult.isSuccess && productsResult.isSuccess) {
                setState {
                    copy(
                        isLoading = false,
                        store = storeResult.getOrNull(),
                        categories = categoriesResult.getOrDefault(emptyList()),
                        products = productsResult.getOrDefault(emptyList())
                    )
                }
            } else {
                setState { copy(isLoading = false, error = "Failed to load catalog") }
            }
        }
    }

    private fun filterByCategory(storeId: String, categoryId: String?) {
        viewModelScope.launch {
            setState { copy(isLoading = true, selectedCategoryId = categoryId) }
            val result = catalogRepository.getProducts(storeId, categoryId)
            result.onSuccess { products ->
                setState { copy(isLoading = false, products = products) }
            }.onFailure { error ->
                setState { copy(isLoading = false, error = error.message ?: "Failed to filter") }
            }
        }
    }
}
