package com.jozze.nuvo.feature.catalog

import com.jozze.nuvo.core.mvi.*
import com.jozze.nuvo.domain.entity.Category
import com.jozze.nuvo.domain.entity.Product
import com.jozze.nuvo.domain.entity.Store

interface CatalogContract : MviContract<CatalogState, CatalogIntent, CatalogContract.Effect> {
    sealed interface Effect : MviEffect {
        data class ShowSnackbar(val message: String) : Effect
    }
}

data class CatalogState(
    val isLoading: Boolean = false,
    val store: Store? = null,
    val categories: List<Category> = emptyList(),
    val products: List<Product> = emptyList(),
    val selectedCategoryId: String? = null,
    val error: String? = null,
    val showClearCartDialog: Product? = null
) : MviState

sealed interface CatalogIntent : MviIntent {
    data class LoadCatalog(val storeId: String) : CatalogIntent
    data class FilterByCategory(val storeId: String, val categoryId: String?) : CatalogIntent
    data class AddToCart(val product: Product) : CatalogIntent
    data class ClearCartAndAdd(val product: Product) : CatalogIntent
    object DismissDialog : CatalogIntent
}
