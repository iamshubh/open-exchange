package com.skopenweb.exchange.conversion.ui.model

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import com.skopenweb.exchange.business.Constants.SOMETHING_WENT_WRONG


sealed interface UiState<out T> {
    data object Loading : UiState<Nothing>
    data class Success<T>(val data: T) : UiState<T>
    data class Error(
        val errorCode: Int = -1, val message: String = SOMETHING_WENT_WRONG
    ) : UiState<Nothing>
}

@Immutable
data class CurrencyUiItem(
    val id: String,
    val text: String,
    val selected: Boolean = false,
)

@Immutable
data class ConvertedCurrencyUiItem(
    val id: String,
    val currentValue: String,
    val base: Double = 1.0,
    val selected: Boolean = false,
)

@Stable
data class ExchangeUiContent(
    val currentSelectedCurrency: CurrencyUiItem? = null,
    val currencyList: List<CurrencyUiItem> = emptyList(),
    val convertedCurrencyList: List<ConvertedCurrencyUiItem> = emptyList(),
    val errorText: Int? = null,
)

val dummyCurrency = listOf(
    CurrencyUiItem(
        id = "USD",
        text = "American Dollar",
        selected = false
    ),
    CurrencyUiItem(
        id = "AUD",
        text = "Australia Dollar",
        selected = true
    ),
    CurrencyUiItem(
        id = "CAN",
        text = "Canada Dollar",
        selected = false
    ),
    CurrencyUiItem(
        id = "RUP",
        text = "Indian Rupee",
        selected = false
    ),
)

val dummyConvertedCurrencyList = listOf(
    ConvertedCurrencyUiItem(
        id = "USD",
        currentValue = "5.0",
        base = 1.0,
        selected = true
    ),
    ConvertedCurrencyUiItem(
        id = "AUD",
        currentValue = "4.0",
        base = 1.0,
        selected = true
    ),

    ConvertedCurrencyUiItem(
        id = "RUP",
        currentValue = "3.0",
        base = 1.0,
        selected = true
    ),

    ConvertedCurrencyUiItem(
        id = "KUW",
        currentValue = "2.0",
        base = 1.0,
        selected = true
    ),
)

val dummyUiData = ExchangeUiContent(
    null,
    dummyCurrency,
    dummyConvertedCurrencyList
)

