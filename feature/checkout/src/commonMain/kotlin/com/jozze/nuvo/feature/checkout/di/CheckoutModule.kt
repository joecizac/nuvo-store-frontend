package com.jozze.nuvo.feature.checkout.di

import com.jozze.nuvo.feature.checkout.CheckoutViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val checkoutModule = module {
    viewModel { CheckoutViewModel(get(), get(), get()) }
}
