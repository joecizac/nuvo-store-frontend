package com.jozze.nuvo.data.location

import com.jozze.nuvo.domain.entity.Location
import com.jozze.nuvo.domain.location.LocationProvider
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.isActive

class LocationProviderImpl : LocationProvider {
    override fun getLocationUpdates(): Flow<Location> = flow {
        // Mock location updates
        while (currentCoroutineContext().isActive) {
            emit(Location(-33.9249, 18.4241))
            delay(30000) // Update every 30 seconds
        }
    }
}
