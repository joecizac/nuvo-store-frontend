package com.jozze.nuvo.feature.catalog

import com.jozze.nuvo.domain.entity.CartItem
import com.jozze.nuvo.domain.entity.Category
import com.jozze.nuvo.domain.entity.Product
import com.jozze.nuvo.domain.entity.Store
import com.jozze.nuvo.domain.exception.DifferentStoreCartException
import com.jozze.nuvo.domain.repository.CartRepository
import com.jozze.nuvo.domain.repository.CatalogRepository
import com.jozze.nuvo.domain.repository.StoreRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
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

@OptIn(ExperimentalCoroutinesApi::class)
class CatalogViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: CatalogViewModel
    private lateinit var storeRepository: FakeStoreRepository
    private lateinit var catalogRepository: FakeCatalogRepository
    private lateinit var cartRepository: FakeCartRepository

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        storeRepository = FakeStoreRepository()
        catalogRepository = FakeCatalogRepository()
        cartRepository = FakeCartRepository()
        viewModel = CatalogViewModel(storeRepository, catalogRepository, cartRepository)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadCatalog success updates state`() = runTest {
        val store = Store("1", "Store 1", "", null, 0.0, 0.0, 4.5, 1.0)
        val categories = listOf(Category("1", "Cat 1", null))
        val products = listOf(Product("1", "Prod 1", "", 100, null, true, "1"))

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
    fun `filterByCategory updates products`() = runTest {
        val products = listOf(Product("1", "Prod 1", "", 100, null, true, "1"))
        catalogRepository.productsResult = Result.success(products)

        viewModel.onIntent(CatalogIntent.FilterByCategory("1", "cat1"))
        advanceUntilIdle()

        assertEquals(products, viewModel.uiState.value.products)
        assertEquals("cat1", viewModel.uiState.value.selectedCategoryId)
    }

    @Test
    fun `addToCart with different store shows dialog`() = runTest {
        val product = Product("1", "Prod 1", "", 100, null, true, "1")
        cartRepository.shouldThrowDifferentStore = true

        viewModel.onIntent(CatalogIntent.AddToCart(product))
        advanceUntilIdle()

        assertEquals(product, viewModel.uiState.value.showClearCartDialog)
    }

    @Test
    fun `clearCartAndAdd clears dialog and adds item`() = runTest {
        val product = Product("1", "Prod 1", "", 100, null, true, "1")
        
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
    override suspend fun updateQuantity(itemId: String, quantity: Int) {}
    override suspend fun removeItem(itemId: String) {}
    override suspend fun clearCart() {}
}
