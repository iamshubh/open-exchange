package com.skopenweb.exchange.business.usecase

import com.skopenweb.exchange.business.ExchangeRepository
import com.skopenweb.exchange.business.model.CurrencyModel
import com.skopenweb.exchange.business.network.ApiResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetAllCurrencyUseCase @Inject constructor(
    private val repository: ExchangeRepository,
) {
    suspend operator fun invoke(): Flow<ApiResponse<List<CurrencyModel>>> =
        repository.getAllCurrencies().map {
            if (it is ApiResponse.Success) {
                ApiResponse.Success(it.data.sortedBy { item -> item.text })
            } else it
        }
}