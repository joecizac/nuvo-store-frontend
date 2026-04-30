package com.jozze.nuvo.di

import com.jozze.nuvo.core.network.networkModule
import com.jozze.nuvo.data.di.dataModule
import com.jozze.nuvo.feature.auth.di.authModule
import com.jozze.nuvo.feature.cart.di.cartModule
import com.jozze.nuvo.feature.catalog.di.catalogModule
import com.jozze.nuvo.feature.discovery.di.discoveryModule
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration

fun initKoin(config: KoinAppDeclaration? = null) {
    startKoin {
        config?.invoke(this)
        modules(
            networkModule,
            dataModule,
            authModule,
            discoveryModule,
            catalogModule,
            cartModule
        )
    }
}
