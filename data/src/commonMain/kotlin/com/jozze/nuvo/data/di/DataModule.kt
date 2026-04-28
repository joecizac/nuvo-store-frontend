package com.jozze.nuvo.data.di

import com.jozze.nuvo.core.network.TokenProvider
import com.jozze.nuvo.data.remote.CatalogApi
import com.jozze.nuvo.data.remote.StoreApi
import com.jozze.nuvo.data.remote.UserApi
import com.jozze.nuvo.data.repository.AuthRepositoryImpl
import com.jozze.nuvo.data.repository.CatalogRepositoryImpl
import com.jozze.nuvo.data.repository.StoreRepositoryImpl
import com.jozze.nuvo.data.repository.UserRepositoryImpl
import com.jozze.nuvo.domain.repository.AuthRepository
import com.jozze.nuvo.domain.repository.CatalogRepository
import com.jozze.nuvo.domain.repository.StoreRepository
import com.jozze.nuvo.domain.repository.UserRepository
import org.koin.dsl.module

val dataModule = module {
    single { AuthRepositoryImpl() }
    single<AuthRepository> { get<AuthRepositoryImpl>() }
    single<TokenProvider> { get<AuthRepositoryImpl>() }

    single { UserApi(get()) }
    single<UserRepository> { UserRepositoryImpl(get()) }

    single { StoreApi(get()) }
    single<StoreRepository> { StoreRepositoryImpl(get()) }

    single { CatalogApi(get()) }
    single<CatalogRepository> { CatalogRepositoryImpl(get()) }
}
