package com.jozze.nuvo.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.jozze.nuvo.domain.entity.CartItem

@Entity(tableName = "cart_items")
data class CartItemEntity(
    @PrimaryKey val id: String,
    val productId: String,
    val name: String,
    val priceCents: Long,
    val quantity: Int,
    val storeId: String,
    val imageUrl: String?
)

fun CartItemEntity.toDomain() = CartItem(
    id = id,
    productId = productId,
    name = name,
    priceCents = priceCents,
    quantity = quantity,
    storeId = storeId,
    imageUrl = imageUrl
)

fun CartItem.toEntity() = CartItemEntity(
    id = id,
    productId = productId,
    name = name,
    priceCents = priceCents,
    quantity = quantity,
    storeId = storeId,
    imageUrl = imageUrl
)
