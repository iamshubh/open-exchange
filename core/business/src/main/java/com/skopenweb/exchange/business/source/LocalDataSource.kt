package com.skopenweb.exchange.business.source

import com.skopenweb.exchange.business.model.ConversionModel
import com.skopenweb.exchange.business.model.CurrencyModel

interface LocalDataSource {
    fun getLastFetchedConversionTimeStamp(): Long
    fun updateLastFetchedConversionTimeStamp(time: Long)

    fun getAllCurrencyItem(): List<CurrencyModel>
    fun updateAllCurrencyItem(map: Map<String, String>)

    fun getAllConversions(): List<ConversionModel>
    fun updateAllConversions(map: Map<String, Double>)

    fun delete(cache: String)

    fun contains(cache: String): Boolean
}