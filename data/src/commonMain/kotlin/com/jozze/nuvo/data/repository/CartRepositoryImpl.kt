package com.jozze.nuvo.data.repository

import com.jozze.nuvo.data.local.dao.CartDao
import com.jozze.nuvo.data.local.entity.toDomain
import com.jozze.nuvo.data.local.entity.toEntity
import com.jozze.nuvo.data.remote.CartApi
import com.jozze.nuvo.domain.entity.CartItem
import com.jozze.nuvo.domain.exception.DifferentStoreCartException
import com.jozze.nuvo.domain.repository.CartRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class CartRepositoryImpl(
    private val cartDao: CartDao,
    private val cartApi: CartApi,
    private val repositoryScope: CoroutineScope
) : CartRepository {

    private val syncMutex = Mutex()
    private val syncTrigger = MutableSharedFlow<Unit>(
        replay = 0,
        extraBufferCapacity = 64,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    init {
        observeSyncTrigger()
        initialRemoteSync()
    }

    override fun getCartItems(): Flow<List<CartItem>> {
        return cartDao.getCartItems().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun addItem(item: CartItem) {
        val anyItem = cartDao.getAnyItem()
        if (anyItem != null && anyItem.storeId != item.storeId) {
            throw DifferentStoreCartException(
                existingStoreId = anyItem.storeId,
                newStoreId = item.storeId
            )
        }
        
        val existingItem = cartDao.getById(item.id)
        if (existingItem != null) {
            cartDao.updateQuantity(item.id, existingItem.quantity + item.quantity)
        } else {
            cartDao.insert(item.toEntity())
        }
        syncTrigger.tryEmit(Unit)
    }

    override suspend fun updateQuantity(itemId: String, quantity: Int) {
        cartDao.updateQuantity(itemId, quantity)
        syncTrigger.tryEmit(Unit)
    }

    override suspend fun removeItem(itemId: String) {
        cartDao.deleteById(itemId)
        syncTrigger.tryEmit(Unit)
    }

    override suspend fun clearCart() {
        cartDao.clearAll()
        syncTrigger.tryEmit(Unit)
    }

    @OptIn(FlowPreview::class)
    private fun observeSyncTrigger() {
        syncTrigger
            .debounce(2000L) // Wait for 2s of inactivity before syncing
            .onEach {
                syncWithRemote()
            }
            .launchIn(repositoryScope)
    }

    private fun initialRemoteSync() {
        repositoryScope.launch {
            try {
                val remoteItems = cartApi.getCart()
                if (remoteItems.isNotEmpty()) {
                    remoteItems.forEach { cartDao.insert(it.toEntity()) }
                    // Trigger a push to ensure remote is updated if any local merge happened
                    syncTrigger.emit(Unit)
                }
            } catch (e: Exception) {
                println("Initial cart sync failed: ${e.message}")
            }
        }
    }

    private suspend fun syncWithRemote() {
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
