package com.jozze.nuvo

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.jozze.nuvo.core.designsystem.NuvoTheme
import com.jozze.nuvo.feature.auth.AuthScreen
import com.jozze.nuvo.feature.auth.AuthViewModel
import com.jozze.nuvo.feature.auth.AuthIntent
import com.jozze.nuvo.feature.catalog.CatalogScreen
import com.jozze.nuvo.feature.discovery.DiscoveryScreen
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun App() {
    NuvoTheme {
        Surface(
            modifier = Modifier.fillMaxSize()
        ) {
            val navController = rememberNavController()
            val authViewModel: AuthViewModel = koinViewModel()

            NavHost(
                navController = navController,
                startDestination = "auth"
            ) {
                composable("auth") {
                    AuthScreen(
                        onLoginSuccess = {
                            navController.navigate("discovery") {
                                popUpTo("auth") { inclusive = true }
                            }
                        },
                        viewModel = authViewModel
                    )
                }
                composable("discovery") {
                    DiscoveryScreen(
                        onStoreClick = { storeId ->
                            navController.navigate("catalog/$storeId")
                        },
                        onLogout = {
                            authViewModel.onIntent(AuthIntent.Logout)
                            navController.navigate("auth") {
                                popUpTo("discovery") { inclusive = true }
                            }
                        }
                    )
                }
                composable(
                    route = "catalog/{storeId}",
                ) { backStackEntry ->
                    val storeId = backStackEntry.arguments?.getString("storeId") ?: ""
                    CatalogScreen(
                        storeId = storeId,
                        onBack = { navController.popBackStack() }
                    )
                }
            }
        }
    }
}

