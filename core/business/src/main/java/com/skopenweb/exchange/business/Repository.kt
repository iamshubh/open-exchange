package com.skopenweb.exchange.business

import com.skopenweb.exchange.business.model.ConversionModel
import com.skopenweb.exchange.business.model.CurrencyModel
import com.skopenweb.exchange.business.network.ApiResponse
import kotlinx.coroutines.flow.Flow

interface ExchangeRepository {
    suspend fun getAllCurrencies(): Flow<ApiResponse<List<CurrencyModel>>>
    suspend fun getAllConversionsCached(shouldRefresh: Boolean = false): Flow<ApiResponse<List<ConversionModel>>>
    suspend fun fetchAllConversions(): Flow<ApiResponse<List<ConversionModel>>>
}