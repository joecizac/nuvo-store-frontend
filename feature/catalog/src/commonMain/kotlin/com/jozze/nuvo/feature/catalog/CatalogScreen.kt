package com.jozze.nuvo.feature.catalog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.jozze.nuvo.core.designsystem.component.ProductCard
import nuvostore.feature.catalog.generated.resources.Res
import nuvostore.feature.catalog.generated.resources.*
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogScreen(
    storeId: String,
    onBack: () -> Unit,
    onCartClick: () -> Unit,
    viewModel: CatalogViewModel = koinViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is CatalogContract.Effect.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(effect.message)
                }
            }
        }
    }

    LaunchedEffect(storeId) {
        viewModel.onIntent(CatalogIntent.LoadCatalog(storeId))
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text(state.store?.name ?: stringResource(Res.string.catalog_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(Res.string.back))
                    }
                },
                actions = {
                    IconButton(onClick = onCartClick) {
                        Icon(Icons.Default.ShoppingCart, contentDescription = "Cart")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (state.isLoading && state.products.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }

            state.error?.let {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = it, color = MaterialTheme.colorScheme.error)
                }
            }

            if (state.categories.isNotEmpty()) {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        FilterChip(
                            selected = state.selectedCategoryId == null,
                            onClick = {
                                viewModel.onIntent(
                                    CatalogIntent.FilterByCategory(
                                        storeId,
                                        null
                                    )
                                )
                            },
                            label = { Text(stringResource(Res.string.all_categories)) }
                        )
                    }
                    items(state.categories) { category ->
                        FilterChip(
                            selected = state.selectedCategoryId == category.id,
                            onClick = {
                                viewModel.onIntent(
                                    CatalogIntent.FilterByCategory(
                                        storeId,
                                        category.id
                                    )
                                )
                            },
                            label = { Text(category.name) }
                        )
                    }
                }
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(state.products) { product ->
                    ProductCard(
                        product = product,
                        onAddToCart = { viewModel.onIntent(CatalogIntent.AddToCart(product)) },
                        onFavouriteToggle = { viewModel.onIntent(CatalogIntent.ToggleFavourite(product.id)) }
                    )
                }
            }

            state.showClearCartDialog?.let { product ->
                AlertDialog(
                    onDismissRequest = { viewModel.onIntent(CatalogIntent.DismissDialog) },
                    title = { Text(stringResource(Res.string.clear_cart_title)) },
                    text = { Text(stringResource(Res.string.clear_cart_msg)) },
                    confirmButton = {
                        Button(onClick = { viewModel.onIntent(CatalogIntent.ClearCartAndAdd(product)) }) {
                            Text(stringResource(Res.string.clear_and_add))
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { viewModel.onIntent(CatalogIntent.DismissDialog) }) {
                            Text(stringResource(Res.string.cancel))
                        }
                    }
                )
            }
        }
    }
}
