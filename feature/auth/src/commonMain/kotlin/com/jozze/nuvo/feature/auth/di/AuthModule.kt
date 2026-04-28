package com.jozze.nuvo.feature.auth.di

import com.jozze.nuvo.feature.auth.AuthViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val authModule = module {
    viewModel { AuthViewModel(get()) }
}
