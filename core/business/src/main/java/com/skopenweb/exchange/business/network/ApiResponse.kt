package com.skopenweb.exchange.business.network

import com.skopenweb.exchange.business.ApiErrorMessage
import com.skopenweb.exchange.business.Constants

data class ApiError(
    val error: Boolean,
    val status: Int,
    val message: ApiErrorMessage = ApiErrorMessage.UNKNOWN,
    val description: String? = Constants.SOMETHING_WENT_WRONG,
)

sealed class ApiResponse<out T : Any> {
    class Failure(val error: ApiError) : ApiResponse<Nothing>()
    class Exception(val throwable: Throwable?) : ApiResponse<Nothing>()
    class Success<out T : Any>(val data: T) : ApiResponse<T>()

    override fun toString(): String {
        return when (this) {
            is Success -> "Success ${this.data}"
            is Failure -> "Failure ${this.error}"
            is Exception -> "Exception $throwable"
        }
    }
}