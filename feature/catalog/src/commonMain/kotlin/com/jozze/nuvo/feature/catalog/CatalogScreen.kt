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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.jozze.nuvo.core.designsystem.component.ProductCard
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogScreen(
    storeId: String,
    onBack: () -> Unit,
    viewModel: CatalogViewModel = koinViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(storeId) {
        viewModel.onIntent(CatalogIntent.LoadCatalog(storeId))
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(state.store?.name ?: "Catalog") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
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
                            label = { Text("All") }
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
                        onAddToCart = { /* TODO: Implement cart logic */ }
                    )
                }
            }
        }
    }
}
