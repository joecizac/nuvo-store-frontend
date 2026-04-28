package com.jozze.nuvo.feature.catalog.di

import com.jozze.nuvo.feature.catalog.CatalogViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val catalogModule = module {
    viewModel { CatalogViewModel(get(), get()) }
}
