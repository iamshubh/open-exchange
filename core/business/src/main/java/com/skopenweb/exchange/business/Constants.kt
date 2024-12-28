package com.skopenweb.exchange.business

object Constants {
    const val APP_ID = "1b6f1386cc244622a1a6f06cc43e299a"
    const val BASE_URL = "https://openexchangerates.org/"

    const val ENDPOINT_CURRENCIES = "api/currencies.json"
    const val ENDPOINT_CONVERSION = "api/latest.json"

    const val USD = "USD"

    const val SOMETHING_WENT_WRONG = "Something went wrong!"
}

enum class ApiErrorMessage(private val message: String) {
    NOT_FOUND("not_found"),
    MISSING_APP_ID("missing_app_id"),
    INVALID_APP_ID("invalid_app_id"),
    NOT_ALLOWED("not_allowed"),
    ACCESS_RESTRICTED("access_restricted"),
    INVALID_BASE("invalid_base"),
    UNKNOWN("unknown");

    fun from(message: String): ApiErrorMessage = entries.find { it.message == message } ?: UNKNOWN
}
