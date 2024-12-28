package com.skopenweb.exchange.business.network

import com.skopenweb.exchange.business.Constants
import com.skopenweb.exchange.business.Constants.ENDPOINT_CONVERSION
import com.skopenweb.exchange.business.Constants.ENDPOINT_CURRENCIES
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface OpenExchangeApi {

    @GET(ENDPOINT_CURRENCIES)
    suspend fun getAllCurrency(
        @Query("app_id") appId: String = Constants.APP_ID,
        @Query("prettyprint") prettyPrint: Boolean = false,
        @Query("show_alternative") showAlternative: Boolean = false,
        @Query("show_inactive") showInactive: Boolean = false,
    ): Response<ResponseBody>

    @GET(ENDPOINT_CONVERSION)
    suspend fun getAllConversionRates(
        @Query("app_id") appId: String = Constants.APP_ID,
        @Query("base") base: String = Constants.USD,
        @Query("prettyprint") prettyPrint: Boolean = false,
        @Query("show_alternative") showAlternative: Boolean = false,
    ): Response<ResponseBody>
}