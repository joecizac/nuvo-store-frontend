package com.jozze.nuvo.feature.auth

import com.jozze.nuvo.core.mvi.*
import com.jozze.nuvo.domain.entity.User

sealed interface AuthState : MviState {
    object Idle : AuthState
    object Loading : AuthState
    data class Authenticated(val user: User) : AuthState
    data class Error(val message: String) : AuthState
}

sealed interface AuthIntent : MviIntent {
    data class Login(val email: String, val password: String) : AuthIntent
    object Logout : AuthIntent
}

sealed interface AuthEffect : MviEffect {
    data class ShowError(val message: String) : AuthEffect
}
