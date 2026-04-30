package com.jozze.nuvo.feature.discovery

import com.jozze.nuvo.domain.entity.Store
import com.jozze.nuvo.domain.repository.FavouriteRepository
import com.jozze.nuvo.domain.repository.StoreRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
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
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class DiscoveryViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: DiscoveryViewModel
    private lateinit var repository: FakeStoreRepository
    private lateinit var favouriteRepository: FakeFavouriteRepository

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = FakeStoreRepository()
        favouriteRepository = FakeFavouriteRepository()
        viewModel = DiscoveryViewModel(repository, favouriteRepository)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadNearbyStores success updates state with stores`() = runTest {
        val mockStores = listOf(
            Store(
                id = "1",
                name = "Store 1",
                description = "",
                imageUrl = null,
                latitude = 0.0,
                longitude = 0.0,
                rating = 4.5,
                distance = 1.0
            )
        )
        repository.storesResult = Result.success(mockStores)

        viewModel.onIntent(DiscoveryIntent.LoadNearbyStores(0.0, 0.0, 10))
        advanceUntilIdle()

        assertEquals(mockStores, viewModel.uiState.value.stores)
        assertFalse(viewModel.uiState.value.isLoading)
        assertEquals(null, viewModel.uiState.value.error)
    }

    @Test
    fun `toggleFavourite updates store favourite status`() = runTest {
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
        repository.storesResult = Result.success(listOf(store))
        
        // Load initial stores
        viewModel.onIntent(DiscoveryIntent.LoadNearbyStores(0.0, 0.0, 10))
        advanceUntilIdle()
        assertFalse(viewModel.uiState.value.stores.first().isFavourite)

        // Toggle favourite
        viewModel.onIntent(DiscoveryIntent.ToggleFavourite("1"))
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.stores.first().isFavourite)
    }

    @Test
    fun `loadNearbyStores failure updates state with error`() = runTest {
        val errorMessage = "Network Error"
        repository.storesResult = Result.failure(Exception(errorMessage))

        viewModel.onIntent(DiscoveryIntent.LoadNearbyStores(0.0, 0.0, 10))
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.stores.isEmpty())
        assertFalse(viewModel.uiState.value.isLoading)
        assertEquals(errorMessage, viewModel.uiState.value.error)
    }
}

class FakeStoreRepository : StoreRepository {
    var storesResult: Result<List<Store>> = Result.success(emptyList())
    var storeResult: Result<Store> = Result.failure(Exception("Not implemented"))

    override suspend fun getNearbyStores(lat: Double, lng: Double, radius: Int): Result<List<Store>> {
        return storesResult
    }

    override suspend fun getStoreById(id: String): Result<Store> {
        return storeResult
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
