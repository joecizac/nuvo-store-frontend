package com.jozze.nuvo.feature.auth

import androidx.lifecycle.viewModelScope
import com.jozze.nuvo.core.mvi.BaseViewModel
import com.jozze.nuvo.domain.repository.AuthRepository
import kotlinx.coroutines.launch

class AuthViewModel(
    private val authRepository: AuthRepository
) : BaseViewModel<AuthState, AuthIntent>(AuthState.Idle) {

    init {
        observeUser()
    }

    private fun observeUser() {
        viewModelScope.launch {
            authRepository.getCurrentUser().collect { user ->
                if (user != null) {
                    setState { AuthState.Authenticated(user) }
                } else {
                    setState { AuthState.Idle }
                }
            }
        }
    }

    override fun handleIntent(intent: AuthIntent) {
        when (intent) {
            is AuthIntent.Login -> login(intent.email, intent.password)
            is AuthIntent.Logout -> logout()
        }
    }

    private fun login(email: String, password: String) {
        viewModelScope.launch {
            setState { AuthState.Loading }
            val result = authRepository.loginWithEmail(email, password)
            result.onSuccess { user ->
                setState { AuthState.Authenticated(user) }
            }.onFailure { error ->
                setState { AuthState.Error(error.message ?: "Login failed") }
            }
        }
    }

    private fun logout() {
        viewModelScope.launch {
            authRepository.logout()
        }
    }
}
