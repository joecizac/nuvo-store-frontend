package com.jozze.nuvo.core.mvi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jozze.nuvo.core.logging.NuvoLogger
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
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
        NuvoLogger.d(tag) { "Intent received: $intent" }
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
        _uiState.update { currentState ->
            currentState.reducer().also { newState ->
                NuvoLogger.d(tag) { "State updated: $newState" }
            }
        }
    }

    /**
     * Emits a side effect.
     */
    protected fun emitEffect(effect: E) {
        NuvoLogger.d(tag) { "Effect emitted: $effect" }
        _effect.tryEmit(effect)
    }

    private val tag: String
        get() = this::class.simpleName ?: "BaseViewModel"
}
