package com.jozze.nuvo.util

import kotlin.math.absoluteValue
import kotlin.math.pow
import kotlin.math.roundToLong

fun Double.format(decimals: Int): String {
    val isNegative = this < 0
    val factor = 10.0.pow(decimals)
    val totalUnits = (this.absoluteValue * factor).roundToLong()
    
    val integerPart = totalUnits / factor.toLong()
    val fractionalPart = totalUnits % factor.toLong()
    
    val sign = if (isNegative) "-" else ""
    return if (decimals <= 0) {
        "$sign$integerPart"
    } else {
        val fractionalStr = fractionalPart.toString().padStart(decimals, '0')
        "$sign$integerPart.$fractionalStr"
    }
}
