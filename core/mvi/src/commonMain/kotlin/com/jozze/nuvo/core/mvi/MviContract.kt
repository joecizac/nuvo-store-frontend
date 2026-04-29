package com.jozze.nuvo.core.mvi

/**
 * Base interface for all UI states.
 */
interface MviState

/**
 * Base interface for all UI intents (actions).
 */
interface MviIntent

/**
 * Base interface for all UI side effects.
 */
interface MviEffect

/**
 * Interface representing the MVI contract.
 */
interface MviContract<S : MviState, I : MviIntent, E : MviEffect>
