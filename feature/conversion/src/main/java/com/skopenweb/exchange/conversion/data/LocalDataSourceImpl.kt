package com.skopenweb.exchange.conversion.data

import android.content.SharedPreferences
import com.skopenweb.exchange.business.model.ConversionModel
import com.skopenweb.exchange.business.model.CurrencyModel
import com.skopenweb.exchange.business.source.CacheKeys
import com.skopenweb.exchange.business.source.LocalDataSource
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject

class LocalDataSourceImpl @Inject constructor(
    private val sharedPreferences: SharedPreferences,
    private val json: Json,
) : LocalDataSource {

    override fun getLastFetchedConversionTimeStamp(): Long {
        return sharedPreferences.getLong(CacheKeys.LAST_FETCHED_CONVERSION, 0L)
    }

    override fun updateLastFetchedConversionTimeStamp(time: Long) {
        sharedPreferences.edit().putLong(CacheKeys.LAST_FETCHED_CONVERSION, time).apply()
    }

    override fun getAllCurrencyItem(): List<CurrencyModel> {
        val currencyJson = sharedPreferences.getString(CacheKeys.CURRENCY, null)
        val decoded = json.decodeFromString<LinkedHashMap<String, String>>(currencyJson.orEmpty())
        return decoded.map { CurrencyModel(it.key, it.value) }
    }

    override fun updateAllCurrencyItem(map: Map<String, String>) {
        val encoded = json.encodeToString(map)
        sharedPreferences.edit().putString(CacheKeys.CURRENCY, encoded).apply()
    }

    override fun getAllConversions(): List<ConversionModel> {
        val conversionJson = sharedPreferences.getString(CacheKeys.CONVERSION, null)
        val decoded = json.decodeFromString<LinkedHashMap<String, Double>>(conversionJson.orEmpty())
        return decoded.map { ConversionModel(it.key, it.value) }
    }

    override fun updateAllConversions(map: Map<String, Double>) {
        val encoded = json.encodeToString(map)
        sharedPreferences.edit().putString(CacheKeys.CONVERSION, encoded).apply()
    }

    override fun delete(cache: String) {
        sharedPreferences.edit().remove(cache).apply()
    }

    override fun contains(cache: String): Boolean {
        return sharedPreferences.contains(cache)
    }
}