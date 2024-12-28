package com.skopenweb.exchange.business.usecase

import com.skopenweb.exchange.business.ExchangeRepository
import com.skopenweb.exchange.business.model.ConversionModel
import com.skopenweb.exchange.business.network.ApiResponse
import com.skopenweb.exchange.business.source.CacheKeys
import com.skopenweb.exchange.business.source.LocalDataSource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllConversionUseCase @Inject constructor(
    private val repository: ExchangeRepository,
    private val checkRatesRefreshUseCase: CheckRatesRefreshUseCase,
    private val localDataSource: LocalDataSource,
) {
    suspend operator fun invoke(): Flow<ApiResponse<List<ConversionModel>>> {
        if (localDataSource.contains(CacheKeys.CONVERSION)) {
            val shouldRefresh = checkRatesRefreshUseCase()
            return repository.getAllConversionsCached(shouldRefresh = shouldRefresh)
        } else {
            return repository.fetchAllConversions()
        }
    }
}