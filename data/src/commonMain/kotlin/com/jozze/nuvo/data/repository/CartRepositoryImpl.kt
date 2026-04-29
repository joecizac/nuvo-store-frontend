package com.jozze.nuvo.data.repository

import com.jozze.nuvo.data.local.dao.CartDao
import com.jozze.nuvo.data.local.entity.toDomain
import com.jozze.nuvo.data.local.entity.toEntity
import com.jozze.nuvo.data.remote.CartApi
import com.jozze.nuvo.domain.entity.CartItem
import com.jozze.nuvo.domain.exception.DifferentStoreCartException
import com.jozze.nuvo.domain.repository.CartRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class CartRepositoryImpl(
    private val cartDao: CartDao,
    private val cartApi: CartApi
) : CartRepository {

    // TODO: Inject appScope or move to work manager for better lifecycle management
    private val repositoryScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private val syncMutex = Mutex()

    override fun getCartItems(): Flow<List<CartItem>> {
        return cartDao.getCartItems().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun addItem(item: CartItem) {
        val existingItem = cartDao.getAnyItem()
        if (existingItem != null && existingItem.storeId != item.storeId) {
            throw DifferentStoreCartException(
                existingStoreId = existingItem.storeId,
                newStoreId = item.storeId
            )
        }
        cartDao.insert(item.toEntity())
        syncWithRemote()
    }

    override suspend fun updateQuantity(itemId: String, quantity: Int) {
        cartDao.updateQuantity(itemId, quantity)
        syncWithRemote()
    }

    override suspend fun removeItem(itemId: String) {
        cartDao.deleteById(itemId)
        syncWithRemote()
    }

    override suspend fun clearCart() {
        cartDao.clearAll()
        syncWithRemote()
    }

    private fun syncWithRemote() {
        repositoryScope.launch {
            syncMutex.withLock {
                try {
                    val items = cartDao.getAllItems().map { it.toDomain() }
                    cartApi.syncCart(items)
                } catch (e: Exception) {
                    // TODO: Use proper multiplatform logger (e.g., Kermit or Napier)
                    println("Cart sync failed: ${e.message}")
                }
            }
        }
    }
}
