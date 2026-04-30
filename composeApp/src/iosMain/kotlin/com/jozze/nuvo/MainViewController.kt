package com.jozze.nuvo

import androidx.compose.ui.window.ComposeUIViewController
import com.jozze.nuvo.di.initKoin

fun MainViewController() = ComposeUIViewController(
    configure = {
        initKoin {
            properties(mapOf("SERVER_URL" to "http://localhost:8080/"))
        }
    }
) { App() }