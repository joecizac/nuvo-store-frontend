package com.jozze.nuvo.data.repository

import com.jozze.nuvo.data.local.dao.FavouriteDao
import com.jozze.nuvo.data.local.entity.FavouriteProductEntity
import com.jozze.nuvo.data.local.entity.FavouriteStoreEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class FavouriteRepositoryImplTest {

    private lateinit var repository: FavouriteRepositoryImpl
    private lateinit var fakeDao: FakeFavouriteDao

    @BeforeTest
    fun setup() {
        fakeDao = FakeFavouriteDao()
        repository = FavouriteRepositoryImpl(fakeDao)
    }

    @Test
    fun `toggleStoreFavourite inserts when not present`() = runTest {
        val storeId = "store_1"
        repository.toggleStoreFavourite(storeId)

        assertTrue(fakeDao.stores.value.any { it.storeId == storeId })
        assertTrue(repository.isStoreFavourite(storeId).first())
    }

    @Test
    fun `toggleStoreFavourite deletes when present`() = runTest {
        val storeId = "store_1"
        repository.toggleStoreFavourite(storeId) // Insert
        repository.toggleStoreFavourite(storeId) // Delete

        assertFalse(fakeDao.stores.value.any { it.storeId == storeId })
        assertFalse(repository.isStoreFavourite(storeId).first())
    }

    @Test
    fun `toggleProductFavourite inserts when not present`() = runTest {
        val productId = "prod_1"
        repository.toggleProductFavourite(productId)

        assertTrue(fakeDao.products.value.any { it.productId == productId })
        assertTrue(repository.isProductFavourite(productId).first())
    }

    @Test
    fun `toggleProductFavourite deletes when present`() = runTest {
        val productId = "prod_1"
        repository.toggleProductFavourite(productId) // Insert
        repository.toggleProductFavourite(productId) // Delete

        assertFalse(fakeDao.products.value.any { it.productId == productId })
        assertFalse(repository.isProductFavourite(productId).first())
    }
}

class FakeFavouriteDao : FavouriteDao {
    val stores = MutableStateFlow<List<FavouriteStoreEntity>>(emptyList())
    val products = MutableStateFlow<List<FavouriteProductEntity>>(emptyList())

    override fun getFavouriteStores(): Flow<List<FavouriteStoreEntity>> = stores
    override suspend fun insertStore(store: FavouriteStoreEntity) {
        stores.value = stores.value + store
    }
    override suspend fun deleteStore(storeId: String) {
        stores.value = stores.value.filterNot { it.storeId == storeId }
    }
    override fun isStoreFavourite(storeId: String): Flow<Boolean> = 
        stores.map { it.any { s -> s.storeId == storeId } }

    override fun getFavouriteProducts(): Flow<List<FavouriteProductEntity>> = products
    override suspend fun insertProduct(product: FavouriteProductEntity) {
        products.value = products.value + product
    }
    override suspend fun deleteProduct(productId: String) {
        products.value = products.value.filterNot { it.productId == productId }
    }
    override fun isProductFavourite(productId: String): Flow<Boolean> = 
        products.map { it.any { p -> p.productId == productId } }
}
