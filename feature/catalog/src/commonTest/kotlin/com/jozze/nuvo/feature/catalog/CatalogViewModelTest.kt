package com.jozze.nuvo.feature.catalog

import com.jozze.nuvo.domain.entity.CartItem
import com.jozze.nuvo.domain.entity.Category
import com.jozze.nuvo.domain.entity.Product
import com.jozze.nuvo.domain.entity.Store
import com.jozze.nuvo.domain.exception.DifferentStoreCartException
import com.jozze.nuvo.domain.repository.CartRepository
import com.jozze.nuvo.domain.repository.CatalogRepository
import com.jozze.nuvo.domain.repository.FavouriteRepository
import com.jozze.nuvo.domain.repository.StoreRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class CatalogViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: CatalogViewModel
    private lateinit var storeRepository: FakeStoreRepository
    private lateinit var catalogRepository: FakeCatalogRepository
    private lateinit var cartRepository: FakeCartRepository
    private lateinit var favouriteRepository: FakeFavouriteRepository

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        storeRepository = FakeStoreRepository()
        catalogRepository = FakeCatalogRepository()
        cartRepository = FakeCartRepository()
        favouriteRepository = FakeFavouriteRepository()
        
        // Defaults for successful loadCatalog
        storeRepository.storeResult = Result.success(
            Store("1", "Store 1", "", null, 0.0, 0.0, 4.5, 1.0)
        )
        catalogRepository.categoriesResult = Result.success(emptyList())
        catalogRepository.productsResult = Result.success(emptyList())
        
        viewModel = CatalogViewModel(storeRepository, catalogRepository, cartRepository, favouriteRepository)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadCatalog success updates state`() = runTest {
        val store = Store(
            id = "1",
            name = "Store 1",
            description = "",
            imageUrl = null,
            latitude = 0.0,
            longitude = 0.0,
            rating = 4.5,
            distance = 1.0
        )
        val categories = listOf(Category(id = "1", name = "Cat 1", imageUrl = null))
        val products = listOf(
            Product(
                id = "1",
                name = "Prod 1",
                description = "",
                priceCents = 100,
                imageUrl = null,
                isAvailable = true,
                categoryId = "1"
            )
        )

        storeRepository.storeResult = Result.success(store)
        catalogRepository.categoriesResult = Result.success(categories)
        catalogRepository.productsResult = Result.success(products)

        viewModel.onIntent(CatalogIntent.LoadCatalog("1"))
        advanceUntilIdle()

        assertEquals(store, viewModel.uiState.value.store)
        assertEquals(categories, viewModel.uiState.value.categories)
        assertEquals(products, viewModel.uiState.value.products)
        assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test
    fun `toggleProductFavourite updates product favourite status`() = runTest {
        val products = listOf(
            Product(
                id = "1",
                name = "Prod 1",
                description = "",
                priceCents = 100,
                imageUrl = null,
                isAvailable = true,
                categoryId = "1"
            )
        )
        catalogRepository.productsResult = Result.success(products)

        // Load catalog
        viewModel.onIntent(CatalogIntent.LoadCatalog("1"))
        advanceUntilIdle()
        assertEquals(1, viewModel.uiState.value.products.size)
        assertFalse(viewModel.uiState.value.products.first().isFavourite)

        // Toggle
        viewModel.onIntent(CatalogIntent.ToggleFavourite("1"))
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.products.first().isFavourite)
    }

    @Test
    fun `filterByCategory updates products`() = runTest {
        val products = listOf(
            Product(
                id = "1",
                name = "Prod 1",
                description = "",
                priceCents = 100,
                imageUrl = null,
                isAvailable = true,
                categoryId = "1"
            )
        )
        catalogRepository.productsResult = Result.success(products)

        viewModel.onIntent(CatalogIntent.FilterByCategory("1", "cat1"))
        advanceUntilIdle()

        assertEquals(products, viewModel.uiState.value.products)
        assertEquals("cat1", viewModel.uiState.value.selectedCategoryId)
    }

    @Test
    fun `addToCart with different store shows dialog`() = runTest {
        val product = Product(
            id = "1",
            name = "Prod 1",
            description = "",
            priceCents = 100,
            imageUrl = null,
            isAvailable = true,
            categoryId = "1"
        )
        cartRepository.shouldThrowDifferentStore = true

        viewModel.onIntent(CatalogIntent.AddToCart(product))
        advanceUntilIdle()

        assertEquals(product, viewModel.uiState.value.showClearCartDialog)
    }

    @Test
    fun `clearCartAndAdd clears dialog and adds item`() = runTest {
        val product = Product(
            id = "1",
            name = "Prod 1",
            description = "",
            priceCents = 100,
            imageUrl = null,
            isAvailable = true,
            categoryId = "1"
        )
        
        viewModel.onIntent(CatalogIntent.ClearCartAndAdd(product))
        advanceUntilIdle()

        assertEquals(null, viewModel.uiState.value.showClearCartDialog)
        assertNotNull(cartRepository.lastAddedItem)
    }
}

class FakeStoreRepository : StoreRepository {
    var storeResult: Result<Store> = Result.failure(Exception("Not implemented"))
    override suspend fun getNearbyStores(lat: Double, lng: Double, radius: Int): Result<List<Store>> = Result.success(emptyList())
    override suspend fun getStoreById(id: String): Result<Store> = storeResult
}

class FakeCatalogRepository : CatalogRepository {
    var categoriesResult: Result<List<Category>> = Result.success(emptyList())
    var productsResult: Result<List<Product>> = Result.success(emptyList())

    override suspend fun getCategories(storeId: String): Result<List<Category>> = categoriesResult
    override suspend fun getProducts(storeId: String, categoryId: String?): Result<List<Product>> = productsResult
    override suspend fun getProductById(productId: String): Result<Product> = Result.failure(Exception())
}

class FakeCartRepository : CartRepository {
    var lastAddedItem: CartItem? = null
    var shouldThrowDifferentStore = false

    override fun getCartItems(): Flow<List<CartItem>> = emptyFlow()
    override suspend fun addItem(item: CartItem) {
        if (shouldThrowDifferentStore) throw DifferentStoreCartException("1", "2")
        lastAddedItem = item
    }
    override suspend fun updateQuantity(itemId: String, quantity: Int) {
        // TODO: complete mock for update/remove in full UI testing phase
    }
    override suspend fun removeItem(itemId: String) {
        // TODO: complete mock for update/remove in full UI testing phase
    }
    override suspend fun clearCart() {
        // TODO: complete mock for update/remove in full UI testing phase
    }
}

class FakeFavouriteRepository : FavouriteRepository {
    private val favouriteStores = MutableStateFlow<List<String>>(emptyList())
    private val favouriteProducts = MutableStateFlow<List<String>>(emptyList())

    override fun getFavouriteStores(): Flow<List<String>> = favouriteStores
    override suspend fun toggleStoreFavourite(storeId: String) {
        val current = favouriteStores.value.toMutableList()
        if (current.contains(storeId)) current.remove(storeId) else current.add(storeId)
        favouriteStores.value = current
    }
    override fun isStoreFavourite(storeId: String): Flow<Boolean> = 
        favouriteStores.map { it.contains(storeId) }

    override fun getFavouriteProducts(): Flow<List<String>> = favouriteProducts
    override suspend fun toggleProductFavourite(productId: String) {
        val current = favouriteProducts.value.toMutableList()
        if (current.contains(productId)) current.remove(productId) else current.add(productId)
        favouriteProducts.value = current
    }
    override fun isProductFavourite(productId: String): Flow<Boolean> = 
        favouriteProducts.map { it.contains(productId) }
}
