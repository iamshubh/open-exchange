package com.skopenweb.exchange.conversion.data

import com.skopenweb.exchange.business.model.ExchangeRatesResponse
import com.skopenweb.exchange.business.network.ApiError
import com.skopenweb.exchange.business.network.ApiResponse
import com.skopenweb.exchange.business.network.OpenExchangeApi
import com.skopenweb.exchange.business.source.RemoteDataSource
import kotlinx.serialization.json.Json
import okhttp3.ResponseBody
import retrofit2.HttpException
import retrofit2.Response
import javax.inject.Inject

class RemoteDataSourceImpl @Inject constructor(
    private val service: OpenExchangeApi,
    private val json: Json,
) : RemoteDataSource {

    private inline fun <reified T : Any> apiResponse(invokeApi: () -> Response<ResponseBody>): ApiResponse<T> =
        try {
            val response = invokeApi()
            if (response.isSuccessful && response.body() != null) {
                val data: T = json.decodeFromString(response.body()!!.string())
                ApiResponse.Success(data)
            } else {
                val apiError: ApiError = json.decodeFromString(response.errorBody()!!.string())
                ApiResponse.Failure(error = apiError)
            }
        } catch (e: HttpException) {
            e.printStackTrace()
            ApiResponse.Failure(
                error = ApiError(
                    error = true,
                    status = 0,
                    description = e.message()
                )
            )
        } catch (e: Exception) {
            e.printStackTrace()
            ApiResponse.Exception(e)
        }

    override suspend fun getAllCurrencyItem(): ApiResponse<HashMap<String, String>> = apiResponse {
        service.getAllCurrency()
    }

    override suspend fun getAllConversions(): ApiResponse<ExchangeRatesResponse> = apiResponse {
        service.getAllConversionRates()
    }
}