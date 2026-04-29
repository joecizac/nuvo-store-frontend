package com.jozze.nuvo.core.mvi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * Base ViewModel implementing MVI pattern.
 *
 * @param S The UI state type.
 * @param I The UI intent type.
 * @param E The UI effect type.
 */
abstract class BaseViewModel<S : MviState, I : MviIntent, E : MviEffect>(
    initialState: S
) : ViewModel() {

    private val _uiState = MutableStateFlow(initialState)
    val uiState = _uiState.asStateFlow()

    protected val state: S get() = _uiState.value

    private val _intent = MutableSharedFlow<I>(
        extraBufferCapacity = 16,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    private val intent = _intent.asSharedFlow()

    private val _effect = MutableSharedFlow<E>(
        extraBufferCapacity = 16,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val effect = _effect.asSharedFlow()

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
        _uiState.update { it.reducer() }
    }

    /**
     * Emits a side effect.
     */
    protected fun emitEffect(effect: E) {
        _effect.tryEmit(effect)
    }
}
