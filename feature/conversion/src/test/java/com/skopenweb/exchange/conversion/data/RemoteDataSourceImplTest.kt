package com.skopenweb.exchange.conversion.data

import com.skopenweb.exchange.business.model.ExchangeRatesResponse
import com.skopenweb.exchange.business.network.ApiError
import com.skopenweb.exchange.business.network.ApiResponse
import com.skopenweb.exchange.business.network.OpenExchangeApi
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import okhttp3.ResponseBody
import org.junit.Assert.assertEquals
import org.junit.Test
import retrofit2.Response

class RemoteDataSourceImplTest {
    companion object {
        val mockApiError = ApiError(
            error = true,
            status = 0,
            description = "Something went wrong"
        )
        const val mockCurrencyMapping =
            """{"USD": "American Dollar", "INR": "Indian Rupees", "EUR": "Euro"}"""
        val mockCurrencyMappingObject =
            hashMapOf("USD" to "American Dollar", "INR" to "Indian Rupees", "EUR" to "Euro")

        const val mockConversionString =
            """{"disclaimer": "", "license": "", "timestamp": 0, base: "USD", "rates":{"AED":3.672538,"USD":66.809999,"YEN":125.716501}}"""

        val mockConversionResponse = ExchangeRatesResponse(
            disclaimer = "",
            license = "",
            timestamp = 0,
            base = "USD",
            rates = mapOf(
                "AED" to 3.672538, "USD" to 66.809999, "YEN" to 125.716501
            )
        )
    }

    private val mockCurrencySuccessResponse: Response<ResponseBody> = mockk(relaxed = true) {
        every { isSuccessful } returns true
        every { body() } returns mockk(relaxed = true) {
            every { string() } returns mockCurrencyMapping
        }
    }

    private val mockCurrencyFailureResponse: Response<ResponseBody> = mockk(relaxed = true) {
        every { isSuccessful } returns false
        every { body() } returns mockk(relaxed = true)
    }

    private val mockConversionSuccessResponse: Response<ResponseBody> = mockk(relaxed = true) {
        every { isSuccessful } returns true
        every { body() } returns mockk(relaxed = true) {
            every { string() } returns mockConversionString
        }
    }

    private val mockConversionFailureResponse: Response<ResponseBody> = mockk(relaxed = true) {
        every { isSuccessful } returns false
        every { body() } returns mockk(relaxed = true)
    }

    private val service: OpenExchangeApi = mockk(relaxed = true) {
        coEvery { getAllCurrency() } returns mockCurrencySuccessResponse
        coEvery { getAllConversionRates() } returns mockConversionSuccessResponse
    }

    private val json: Json = mockk(relaxed = true) {
        every { decodeFromString<HashMap<String, String>>(any()) } returns mockCurrencyMappingObject
        every { decodeFromString<ExchangeRatesResponse>(any()) } returns mockConversionResponse
        every { decodeFromString<ApiError>(any()) } returns mockApiError
    }

    private val systemUnderTest = RemoteDataSourceImpl(
        service = service,
        json = json,
    )

    @Test
    fun `currency api returns successfully`() = runTest {
        coEvery { service.getAllCurrency() } returns mockCurrencySuccessResponse

        val result = systemUnderTest.getAllCurrencyItem()

        assert(result is ApiResponse.Success)
    }

    @Test
    fun `currency api returns failure in case of fail`() = runTest {
        coEvery { service.getAllCurrency() } returns mockCurrencyFailureResponse

        val result = systemUnderTest.getAllCurrencyItem()

        assert(result is ApiResponse.Failure)
    }

    @Test
    fun `conversion api returns successfully`() = runTest {
        coEvery { service.getAllConversionRates() } returns mockCurrencySuccessResponse

        val result = systemUnderTest.getAllConversions()

        assert(result is ApiResponse.Success)
        assertEquals((result as ApiResponse.Success).data, mockConversionResponse)
    }

    @Test
    fun `conversion api returns failure in case of fail`() = runTest {
        coEvery { service.getAllConversionRates() } returns mockConversionFailureResponse

        val result = systemUnderTest.getAllConversions()

        assert(result is ApiResponse.Failure)
    }
}