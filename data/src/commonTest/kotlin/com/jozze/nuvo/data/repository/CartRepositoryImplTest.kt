package com.jozze.nuvo.data.repository

import com.jozze.nuvo.data.local.dao.CartDao
import com.jozze.nuvo.data.local.entity.CartItemEntity
import com.jozze.nuvo.data.remote.CartApi
import com.jozze.nuvo.domain.entity.CartItem
import com.jozze.nuvo.domain.exception.DifferentStoreCartException
import io.ktor.client.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class CartRepositoryImplTest {

    private lateinit var repository: CartRepositoryImpl
    private lateinit var fakeDao: FakeCartDao
    private lateinit var fakeApi: CartApi

    @BeforeTest
    fun setup() {
        fakeDao = FakeCartDao()
        fakeApi = CartApi(HttpClient())
        repository = CartRepositoryImpl(fakeDao, fakeApi, CoroutineScope(SupervisorJob()))
    }

    @Test
    fun `adding item from same store succeeds`() = runTest {
        val item1 = CartItem("1", "p1", "Product 1", 1000, 1, "store1")
        val item2 = CartItem("2", "p2", "Product 2", 2000, 1, "store1")

        repository.addItem(item1)
        repository.addItem(item2)

        assertEquals(2, fakeDao.items.size)
    }

    @Test
    fun `adding item from different store fails`() = runTest {
        val item1 = CartItem("1", "p1", "Product 1", 1000, 1, "store1")
        val item2 = CartItem("2", "p2", "Product 2", 2000, 1, "store2")

        repository.addItem(item1)

        assertFailsWith<DifferentStoreCartException> {
            repository.addItem(item2)
        }
    }

    @Test
    fun `clearing cart allows adding from different store`() = runTest {
        val item1 = CartItem("1", "p1", "Product 1", 1000, 1, "store1")
        val item2 = CartItem("2", "p2", "Product 2", 2000, 1, "store2")

        repository.addItem(item1)
        repository.clearCart()
        repository.addItem(item2)

        assertEquals(1, fakeDao.items.size)
        assertEquals("store2", fakeDao.items.values.first().storeId)
    }
}

class FakeCartDao : CartDao {
    val items = mutableMapOf<String, CartItemEntity>()
    private val _flow = MutableStateFlow<List<CartItemEntity>>(emptyList())

    override fun getCartItems(): Flow<List<CartItemEntity>> = _flow

    override suspend fun getAllItems(): List<CartItemEntity> = items.values.toList()

    override suspend fun getById(id: String): CartItemEntity? = items[id]

    override suspend fun insert(item: CartItemEntity) {
        items[item.id] = item
        _flow.value = items.values.toList()
    }

    override suspend fun update(item: CartItemEntity) {
        items[item.id] = item
        _flow.value = items.values.toList()
    }

    override suspend fun updateQuantity(id: String, quantity: Int) {
        val item = items[id] ?: return
        items[id] = item.copy(quantity = quantity)
        _flow.value = items.values.toList()
    }

    override suspend fun deleteById(id: String) {
        items.remove(id)
        _flow.value = items.values.toList()
    }

    override suspend fun clearAll() {
        items.clear()
        _flow.value = emptyList()
    }

    override suspend fun getAnyItem(): CartItemEntity? {
        return items.values.firstOrNull()
    }
}
