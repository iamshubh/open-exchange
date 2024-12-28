package com.skopenweb.exchange.business.usecase

import com.skopenweb.exchange.business.source.LocalDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class CheckRatesRefreshUseCase @Inject constructor(
    private val localDataSource: LocalDataSource,
) {
    suspend operator fun invoke(): Boolean {
        return withContext(Dispatchers.Default) {
            val lastUpdatedTimeStamp = localDataSource.getLastFetchedConversionTimeStamp()
            val diff = System.currentTimeMillis() - lastUpdatedTimeStamp
            diff > TimeUnit.MINUTES.toMillis(30L)
        }
    }
}