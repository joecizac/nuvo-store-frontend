package com.jozze.nuvo.feature.cart.di

import com.jozze.nuvo.feature.cart.CartViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val cartModule = module {
    viewModel { CartViewModel(get()) }
}
