package com.skopenweb.exchange.conversion.ui.model

import android.graphics.Color

interface UiAction {
    data class CurrencySelection(val amount: String, val currencyUiItem: CurrencyUiItem) : UiAction
    data class ConvertAction(val amount: String, val currency: CurrencyUiItem?) : UiAction
    object RetryCta : UiAction
}


// make contrasting colors
fun makeContrastColors(
    foregroundColor: Int,
    backgroundColor: Int
): Pair<Int, Int> {
    val correctForegroundColor = Color.rgb(0, 0, 0)
    val correctBackgroundColor = Color.rgb(255, 255, 255)

    return Pair(correctForegroundColor, correctBackgroundColor)
}