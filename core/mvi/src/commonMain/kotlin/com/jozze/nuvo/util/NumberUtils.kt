package com.jozze.nuvo.util

import kotlin.math.pow
import kotlin.math.roundToLong

fun Double.format(decimals: Int): String {
    val factor = 10.0.pow(decimals)
    val rounded = (this * factor).roundToLong()
    val roundedStr = rounded.toString()
    
    return if (decimals <= 0) {
        roundedStr
    } else {
        val padded = roundedStr.padStart(decimals + 1, '0')
        val integerPart = padded.dropLast(decimals)
        val decimalPart = padded.takeLast(decimals)
        "$integerPart.$decimalPart"
    }
}
