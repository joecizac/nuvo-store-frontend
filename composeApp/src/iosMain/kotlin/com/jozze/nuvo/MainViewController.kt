package com.jozze.nuvo

import androidx.compose.ui.window.ComposeUIViewController
import com.jozze.nuvo.di.initKoin

fun MainViewController() = ComposeUIViewController(
    configure = {
        initKoin()
    }
) { App() }