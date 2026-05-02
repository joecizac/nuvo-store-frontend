package com.jozze.nuvo.core.logging

@OptIn(kotlin.experimental.ExperimentalNativeApi::class)
actual fun isDebugBuild(): Boolean = Platform.isDebugBinary

actual fun platformLog(
    level: LogLevel,
    tag: String,
    message: String,
    throwable: Throwable?
) {
    val throwableMessage = throwable?.let { ": ${it::class.simpleName}: ${it.message}" }.orEmpty()
    println("${level.name}/$tag: $message$throwableMessage")
}
