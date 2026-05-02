package com.jozze.nuvo.data.repository

import com.jozze.nuvo.core.logging.NuvoLogger
import com.jozze.nuvo.data.local.dao.CartDao
import com.jozze.nuvo.data.local.entity.toDomain
import com.jozze.nuvo.data.local.entity.toEntity
import com.jozze.nuvo.data.remote.CartApi
import com.jozze.nuvo.domain.entity.CartItem
import com.jozze.nuvo.domain.exception.DifferentStoreCartException
import com.jozze.nuvo.domain.repository.CartRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
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
            NuvoLogger.e(TAG) {
                "Rejected cart add due to different store. existing=${anyItem.storeId}, new=${item.storeId}"
            }
            throw DifferentStoreCartException(
                existingStoreId = anyItem.storeId,
                newStoreId = item.storeId
            )
        }
        
        val existingItem = cartDao.getById(item.id)
        if (existingItem != null) {
            cartDao.updateQuantity(item.id, existingItem.quantity + item.quantity)
            NuvoLogger.d(TAG) {
                "Updated cart item=${item.id}, quantity=${existingItem.quantity + item.quantity}"
            }
        } else {
            cartDao.insert(item.toEntity())
            NuvoLogger.d(TAG) { "Inserted cart item=${item.id}, quantity=${item.quantity}" }
        }
        syncTrigger.tryEmit(Unit)
    }

    override suspend fun updateQuantity(itemId: String, quantity: Int) {
        cartDao.updateQuantity(itemId, quantity)
        NuvoLogger.d(TAG) { "Cart quantity updated. item=$itemId, quantity=$quantity" }
        syncTrigger.tryEmit(Unit)
    }

    override suspend fun removeItem(itemId: String) {
        cartDao.deleteById(itemId)
        NuvoLogger.d(TAG) { "Cart item removed. item=$itemId" }
        syncTrigger.tryEmit(Unit)
    }

    override suspend fun clearCart() {
        cartDao.clearAll()
        NuvoLogger.d(TAG) { "Cart cleared" }
        syncTrigger.tryEmit(Unit)
    }

    @OptIn(kotlinx.coroutines.FlowPreview::class)
    private fun observeSyncTrigger() {
        syncTrigger
            .debounce(500L) // Wait for 500ms of inactivity before syncing
            .onEach {
                syncWithRemote()
            }
            .launchIn(repositoryScope)
    }

    private fun initialRemoteSync() {
        repositoryScope.launch {
            try {
                val remoteItems = cartApi.getCart()
                NuvoLogger.d(TAG) { "Initial remote cart sync returned ${remoteItems.size} items" }
                if (remoteItems.isNotEmpty()) {
                    remoteItems.forEach { cartDao.insert(it.toEntity()) }
                    // Trigger a push to ensure remote is updated if any local merge happened
                    syncTrigger.emit(Unit)
                }
            } catch (e: Exception) {
                NuvoLogger.e(TAG, e) { "Initial cart sync failed" }
            }
        }
    }

    private suspend fun syncWithRemote() {
        syncMutex.withLock {
            try {
                val items = cartDao.getAllItems().map { it.toDomain() }
                NuvoLogger.d(TAG) { "Syncing cart with remote. itemCount=${items.size}" }
                cartApi.syncCart(items)
                NuvoLogger.d(TAG) { "Cart remote sync completed" }
            } catch (e: Exception) {
                NuvoLogger.e(TAG, e) { "Cart sync failed" }
            }
        }
    }

    private companion object {
        const val TAG = "CartRepository"
    }
}
