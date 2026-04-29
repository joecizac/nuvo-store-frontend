package com.jozze.nuvo.domain.location

import com.jozze.nuvo.domain.entity.Location
import kotlinx.coroutines.flow.Flow

interface LocationProvider {
    fun getLocationUpdates(): Flow<Location>
}
