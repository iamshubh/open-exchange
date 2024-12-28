package com.skopenweb.exchange.business.model

import kotlinx.serialization.Serializable

data class CurrencyModel(val id: String, val text: String)

data class ConversionModel(val id: String, val factor: Double)

@Serializable
data class ExchangeRatesResponse(
    val disclaimer: String,
    val license: String,
    val timestamp: Long,
    val base: String,
    val rates: Map<String, Double>,
)