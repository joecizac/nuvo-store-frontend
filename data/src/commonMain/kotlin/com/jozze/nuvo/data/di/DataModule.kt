package com.jozze.nuvo.data.di

import com.jozze.nuvo.core.network.TokenProvider
import com.jozze.nuvo.data.local.NuvoDatabase
import com.jozze.nuvo.data.remote.CartApi
import com.jozze.nuvo.data.remote.CatalogApi
import com.jozze.nuvo.data.remote.OrderApi
import com.jozze.nuvo.data.remote.StoreApi
import com.jozze.nuvo.data.remote.UserApi
import com.jozze.nuvo.data.repository.*
import com.jozze.nuvo.domain.repository.*
import org.koin.dsl.module

val dataModule = module {
    includes(platformDataModule)

    single { get<NuvoDatabase>().cartDao() }

    single { AuthRepositoryImpl() }
    single<AuthRepository> { get<AuthRepositoryImpl>() }
    single<TokenProvider> { get<AuthRepositoryImpl>() }

    single { UserApi(get()) }
    single<UserRepository> { UserRepositoryImpl(get()) }

    single { StoreApi(get()) }
    single<StoreRepository> { StoreRepositoryImpl(get()) }

    single { CatalogApi(get()) }
    single<CatalogRepository> { CatalogRepositoryImpl(get()) }

    single { CartApi(get()) }
    single<CartRepository> { CartRepositoryImpl(get(), get()) }

    single { OrderApi(get()) }
    single<OrderRepository> { OrderRepositoryImpl(get()) }
}
