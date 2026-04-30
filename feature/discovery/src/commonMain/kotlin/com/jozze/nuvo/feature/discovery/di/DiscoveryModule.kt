package com.jozze.nuvo.feature.discovery.di

import com.jozze.nuvo.feature.discovery.DiscoveryViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val discoveryModule = module {
    viewModel { DiscoveryViewModel(get(), get()) }
}
