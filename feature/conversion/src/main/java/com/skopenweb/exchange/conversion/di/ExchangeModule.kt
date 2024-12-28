package com.skopenweb.exchange.conversion.di

import android.content.SharedPreferences
import com.skopenweb.exchange.business.ExchangeRepository
import com.skopenweb.exchange.business.network.OpenExchangeApi
import com.skopenweb.exchange.business.source.LocalDataSource
import com.skopenweb.exchange.business.source.RemoteDataSource
import com.skopenweb.exchange.conversion.data.ExchangeRepositoryImpl
import com.skopenweb.exchange.conversion.data.LocalDataSourceImpl
import com.skopenweb.exchange.conversion.data.RemoteDataSourceImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ExchangeModule {

    @Singleton
    @Provides
    fun providesLocalDataSource(sharedPreferences: SharedPreferences, json: Json): LocalDataSource {
        return LocalDataSourceImpl(sharedPreferences = sharedPreferences, json = json)
    }

    @Provides
    fun providesRemoteDataSource(service: OpenExchangeApi, json: Json): RemoteDataSource {
        return RemoteDataSourceImpl(service = service, json = json)
    }

    @Singleton
    @Provides
    fun providesRepository(
        localDataSource: LocalDataSource,
        remoteDataSource: RemoteDataSource
    ): ExchangeRepository {
        return ExchangeRepositoryImpl(
            localDataSource = localDataSource,
            remoteDataSource = remoteDataSource
        )
    }
}