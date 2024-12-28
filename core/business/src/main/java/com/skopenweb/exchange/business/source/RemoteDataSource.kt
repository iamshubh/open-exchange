package com.skopenweb.exchange.business.source

import com.skopenweb.exchange.business.model.ExchangeRatesResponse
import com.skopenweb.exchange.business.network.ApiResponse

interface RemoteDataSource {
    suspend fun getAllCurrencyItem(): ApiResponse<Map<String, String>>
    suspend fun getAllConversions(): ApiResponse<ExchangeRatesResponse>
}