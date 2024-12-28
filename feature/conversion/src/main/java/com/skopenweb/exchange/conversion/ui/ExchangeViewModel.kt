package com.skopenweb.exchange.conversion.ui

import androidx.annotation.StringRes
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skopenweb.exchange.business.Constants
import com.skopenweb.exchange.business.network.ApiResponse
import com.skopenweb.exchange.business.usecase.GetAllConversionUseCase
import com.skopenweb.exchange.business.usecase.GetAllCurrencyUseCase
import com.skopenweb.exchange.conversion.R
import com.skopenweb.exchange.conversion.ui.model.ConvertedCurrencyUiItem
import com.skopenweb.exchange.conversion.ui.model.CurrencyUiItem
import com.skopenweb.exchange.conversion.ui.model.ExchangeUiContent
import com.skopenweb.exchange.conversion.ui.model.UiAction
import com.skopenweb.exchange.conversion.ui.model.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class ExchangeViewModel @Inject constructor(
    private val getAllConversionUseCase: GetAllConversionUseCase,
    private val getAllCurrencyUseCase: GetAllCurrencyUseCase,
) : ViewModel() {

    private val _uiState: MutableStateFlow<UiState<ExchangeUiContent>> =
        MutableStateFlow(UiState.Loading)
    val uiState = _uiState.asStateFlow()

    private val convertActionTriggerFlow = MutableSharedFlow<Pair<Double, String>>(
        replay = 0,
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    @VisibleForTesting
    fun setUiState(uiState: UiState<ExchangeUiContent>) {

    }

    private var currExchangeScreenData = ExchangeUiContent()

    fun initialize() {
        loadAllCurrency()
        startListeningForConversions()
    }

    private fun loadAllCurrency() {
        viewModelScope.launch(Dispatchers.IO) {
            getAllCurrencyUseCase().collect { currencyResult ->
                when (currencyResult) {
                    is ApiResponse.Success -> {
                        val currencyUiData = currencyResult.data.map {
                            CurrencyUiItem(
                                id = it.id,
                                text = it.text,
                                selected = it.id == currExchangeScreenData.currentSelectedCurrency?.id
                            )
                        }
                        currExchangeScreenData =
                            currExchangeScreenData.copy(currencyList = currencyUiData)
                        currExchangeScreenData.update()
                    }

                    is ApiResponse.Failure -> {
                        _uiState.update {
                            UiState.Error(
                                errorCode = currencyResult.error.status,
                                message = currencyResult.error.description
                                    ?: Constants.SOMETHING_WENT_WRONG
                            )
                        }
                    }

                    is ApiResponse.Exception -> {
                        _uiState.update {
                            UiState.Error(
                                message = currencyResult.throwable?.message
                                    ?: Constants.SOMETHING_WENT_WRONG
                            )
                        }
                    }
                }
            }
        }
    }

    @OptIn(FlowPreview::class)
    private fun startListeningForConversions() {
        viewModelScope.launch {
            convertActionTriggerFlow.debounce(500).collect { data ->
                convertCurrency(data.first, data.second)
            }
        }
    }

    private fun convertCurrency(value: Double, currencyId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            getAllConversionUseCase.invoke().catch {
                updateMessageToUser()
            }.collect { conversionResult ->
                when (conversionResult) {
                    is ApiResponse.Success -> {
                        val baseFactorWithUsd =
                            conversionResult.data.find { it.id == currencyId }?.factor ?: 1.0
                        val convertedCurrencyList = conversionResult.data.map {
                            ConvertedCurrencyUiItem(
                                id = it.id,
                                currentValue = String.format(
                                    Locale.getDefault(),
                                    "%.2f",
                                    value * it.factor / baseFactorWithUsd
                                ),
                            )
                        }
                        currExchangeScreenData = currExchangeScreenData.copy(
                            convertedCurrencyList = convertedCurrencyList,
                            errorText = null,
                        )
                        currExchangeScreenData.update()
                    }

                    is ApiResponse.Failure -> {
                        _uiState.update {
                            UiState.Error(
                                errorCode = conversionResult.error.status,
                                message = conversionResult.error.description
                                    ?: Constants.SOMETHING_WENT_WRONG
                            )
                        }
                    }

                    is ApiResponse.Exception -> {
                        _uiState.update {
                            UiState.Error(
                                message = conversionResult.throwable?.message
                                    ?: Constants.SOMETHING_WENT_WRONG
                            )
                        }
                    }
                }
            }
        }
    }

    private fun ExchangeUiContent.update() {
        _uiState.update {
            UiState.Success(this)
        }
    }

    private fun updateMessageToUser(@StringRes errorTextId: Int = R.string.default_error_msg) {
        _uiState.update {
            if (it is UiState.Success) {
                currExchangeScreenData =
                    currExchangeScreenData.copy(
                        convertedCurrencyList = emptyList(),
                        errorText = errorTextId
                    )
                UiState.Success(currExchangeScreenData)
            } else return@update it
        }
    }

    fun handleAction(uiAction: UiAction) {
        when (uiAction) {
            UiAction.RetryCta -> {
                onRetry()
            }

            is UiAction.CurrencySelection -> {
                currExchangeScreenData =
                    currExchangeScreenData.copy(
                        currentSelectedCurrency = uiAction.currencyUiItem,
                        currencyList = currExchangeScreenData.currencyList.map {
                            it.copy(selected = it.id == uiAction.currencyUiItem.id)
                        }
                    )
                currExchangeScreenData.update()

                // optionally try to convert the amount as well
                handleAction(
                    UiAction.ConvertAction(
                        uiAction.amount,
                        uiAction.currencyUiItem
                    )
                )
            }

            is UiAction.ConvertAction -> {
                if (uiAction.currency == null) {
                    updateMessageToUser(R.string.please_select_currency)
                } else {
                    val amt = uiAction.amount.toDoubleOrNull()
                    if (amt == null) {
                        if (uiAction.amount.isNotEmpty()) {
                            updateMessageToUser(R.string.please_enter_valid_amount)
                        } else {
                            updateMessageToUser(R.string.no_amount_entered)
                        }
                    } else {
                        convertActionTriggerFlow.tryEmit(Pair(amt, uiAction.currency.id))
                    }
                }
            }

            else -> {}
        }
    }

    fun onRetry() {
        // TODO handle collector leaking scenario
        initialize()
    }
}