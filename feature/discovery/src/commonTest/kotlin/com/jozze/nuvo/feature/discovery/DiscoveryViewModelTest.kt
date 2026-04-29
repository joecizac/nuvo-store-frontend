package com.jozze.nuvo.feature.discovery

import com.jozze.nuvo.domain.entity.Store
import com.jozze.nuvo.domain.repository.StoreRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
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

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = FakeStoreRepository()
        viewModel = DiscoveryViewModel(repository)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadNearbyStores success updates state with stores`() = runTest {
        val mockStores = listOf(
            Store("1", "Store 1", "", null, 0.0, 0.0, 4.5, 1.0)
        )
        repository.storesResult = Result.success(mockStores)

        viewModel.onIntent(DiscoveryIntent.LoadNearbyStores(0.0, 0.0, 10))
        advanceUntilIdle()

        assertEquals(mockStores, viewModel.uiState.value.stores)
        assertFalse(viewModel.uiState.value.isLoading)
        assertEquals(null, viewModel.uiState.value.error)
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
