package com.skopenweb.exchange.conversion.data

import com.skopenweb.exchange.business.ExchangeRepository
import com.skopenweb.exchange.business.model.ConversionModel
import com.skopenweb.exchange.business.model.CurrencyModel
import com.skopenweb.exchange.business.network.ApiResponse
import com.skopenweb.exchange.business.source.CacheKeys
import com.skopenweb.exchange.business.source.LocalDataSource
import com.skopenweb.exchange.business.source.RemoteDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class ExchangeRepositoryImpl @Inject constructor(
    private val localDataSource: LocalDataSource,
    private val remoteDataSource: RemoteDataSource,
) : ExchangeRepository {

    override suspend fun getAllCurrencies(): Flow<ApiResponse<List<CurrencyModel>>> = flow {
        if (localDataSource.contains(CacheKeys.CURRENCY)) {
            emit(ApiResponse.Success(localDataSource.getAllCurrencyItem()))
        } else {
            when (val response = fetchAllCurrencies()) {
                is ApiResponse.Success -> {
                    localDataSource.updateAllCurrencyItem(response.data.toSortedMap())
                    emit(ApiResponse.Success(response.data.map {
                        CurrencyModel(id = it.key, text = it.value)
                    }))
                }

                is ApiResponse.Failure -> emit(ApiResponse.Failure(response.error))
                is ApiResponse.Exception -> emit(ApiResponse.Exception(response.throwable))
            }
        }
    }

    override suspend fun getAllConversionsCached(shouldRefresh: Boolean):
            Flow<ApiResponse<List<ConversionModel>>> =
        flow {
            if (localDataSource.contains(CacheKeys.CONVERSION)) {
                emit(ApiResponse.Success(localDataSource.getAllConversions()))
            }
            if (shouldRefresh.not()) return@flow
            when (val response = remoteDataSource.getAllConversions()) {
                is ApiResponse.Success -> {
                    val sortedRates = response.data.rates.toSortedMap()
                    localDataSource.apply {
                        updateLastFetchedConversionTimeStamp(System.currentTimeMillis())
                        updateAllConversions(sortedRates)
                    }
                    emit(ApiResponse.Success(sortedRates.map {
                        ConversionModel(id = it.key, factor = it.value)
                    }))
                }

                else -> {}
            }
        }

    override suspend fun fetchAllConversions(): Flow<ApiResponse<List<ConversionModel>>> {
        return flow {
            when (val response = remoteDataSource.getAllConversions()) {
                is ApiResponse.Success -> {
                    localDataSource.apply {
                        updateLastFetchedConversionTimeStamp(System.currentTimeMillis())
                        updateAllConversions(response.data.rates)
                    }
                    emit(ApiResponse.Success(response.data.rates.map {
                        ConversionModel(id = it.key, factor = it.value)
                    }))
                }

                is ApiResponse.Failure -> emit(ApiResponse.Failure(response.error))
                is ApiResponse.Exception -> emit(ApiResponse.Exception(response.throwable))
            }
        }
    }

    private suspend fun fetchAllCurrencies(): ApiResponse<Map<String, String>> {
        return remoteDataSource.getAllCurrencyItem()
    }
}