package com.jozze.nuvo.feature.catalog

import com.jozze.nuvo.core.mvi.MviIntent
import com.jozze.nuvo.core.mvi.MviState
import com.jozze.nuvo.domain.entity.Category
import com.jozze.nuvo.domain.entity.Product
import com.jozze.nuvo.domain.entity.Store

data class CatalogState(
    val isLoading: Boolean = false,
    val store: Store? = null,
    val categories: List<Category> = emptyList(),
    val products: List<Product> = emptyList(),
    val selectedCategoryId: String? = null,
    val error: String? = null
) : MviState

sealed interface CatalogIntent : MviIntent {
    data class LoadCatalog(val storeId: String) : CatalogIntent
    data class FilterByCategory(val storeId: String, val categoryId: String?) : CatalogIntent
}
