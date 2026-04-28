package com.jozze.nuvo.core.mvi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Base ViewModel implementing MVI pattern.
 *
 * @param S The UI state type.
 * @param I The UI intent type.
 */
abstract class BaseViewModel<S : MviState, I : MviIntent>(
    initialState: S
) : ViewModel() {

    private val _uiState = MutableStateFlow(initialState)
    val uiState = _uiState.asStateFlow()

    private val _intent = MutableSharedFlow<I>(
        extraBufferCapacity = 16,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    private val intent = _intent.asSharedFlow()

    init {
        viewModelScope.launch {
            intent.collect { handleIntent(it) }
        }
    }

    /**
     * Dispatches an intent to the ViewModel.
     */
    fun onIntent(intent: I) {
        _intent.tryEmit(intent)
    }

    /**
     * Handles the dispatched intent.
     */
    protected abstract fun handleIntent(intent: I)

    /**
     * Updates the current UI state.
     */
    protected fun setState(reducer: S.() -> S) {
        _uiState.value = _uiState.value.reducer()
    }
}
