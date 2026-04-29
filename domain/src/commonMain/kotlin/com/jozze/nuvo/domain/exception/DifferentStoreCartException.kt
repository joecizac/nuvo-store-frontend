package com.jozze.nuvo.domain.exception

class DifferentStoreCartException(
    val existingStoreId: String,
    val newStoreId: String
) : Exception("Cannot add item from store $newStoreId while cart contains items from $existingStoreId")
