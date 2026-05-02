package com.jozze.nuvo.core.logging

object NuvoLogger {
    val isEnabled: Boolean
        get() = isDebugBuild()

    fun d(tag: String, message: () -> String) {
        if (isEnabled) {
            platformLog(LogLevel.DEBUG, tag, message(), null)
        }
    }

    fun i(tag: String, message: () -> String) {
        if (isEnabled) {
            platformLog(LogLevel.INFO, tag, message(), null)
        }
    }

    fun e(tag: String, throwable: Throwable? = null, message: () -> String) {
        if (isEnabled) {
            platformLog(LogLevel.ERROR, tag, message(), throwable)
        }
    }
}

enum class LogLevel {
    DEBUG,
    INFO,
    ERROR
}

expect fun isDebugBuild(): Boolean

expect fun platformLog(
    level: LogLevel,
    tag: String,
    message: String,
    throwable: Throwable?
)
