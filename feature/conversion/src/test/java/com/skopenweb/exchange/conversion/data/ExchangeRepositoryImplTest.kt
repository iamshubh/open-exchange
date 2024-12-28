package com.skopenweb.exchange.conversion.data

import app.cash.turbine.test
import com.skopenweb.exchange.business.model.ConversionModel
import com.skopenweb.exchange.business.model.CurrencyModel
import com.skopenweb.exchange.business.model.ExchangeRatesResponse
import com.skopenweb.exchange.business.network.ApiResponse
import com.skopenweb.exchange.business.source.CacheKeys
import com.skopenweb.exchange.business.source.LocalDataSource
import com.skopenweb.exchange.business.source.RemoteDataSource
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class ExchangeRepositoryImplTest {
    private val mockLocalDataSource: LocalDataSource = mockk(relaxed = true)
    private val mockRemoteDataSource: RemoteDataSource = mockk(relaxed = true)

    private val dummyCurrencyItem = CurrencyModel(id = "USD", text = "American Dollar")
    private val listOfDummyCurrencyItems = listOf(dummyCurrencyItem)

    private val dummyCurrencyConversion = ConversionModel(id = "USD", factor = 1.0)
    private val listOfDummyConversions = listOf(dummyCurrencyConversion)

    private val listOfDummyConversions2 = listOf(
        ConversionModel(id = "AED", factor = 3.1),
        ConversionModel(id = "USD", factor = 1.0),
    )

    private val mockCurrencyMap = mapOf(
        "EUR" to "Euro",
    )
    private val mockCurrencyModel = CurrencyModel(id = "EUR", text = "Euro")

    val mockConversionResponse = ExchangeRatesResponse(
        disclaimer = "",
        license = "",
        timestamp = 0,
        base = "USD",
        rates = mapOf(
            "AED" to 3.1, "USD" to 1.0,
        )
    )

    private val systemUnderTest = ExchangeRepositoryImpl(
        localDataSource = mockLocalDataSource,
        remoteDataSource = mockRemoteDataSource,
    )

    @Test
    fun `currencies are returned from cache if available`() = runTest {
        every { mockLocalDataSource.contains(CacheKeys.CURRENCY) } returns true
        every { mockLocalDataSource.getAllCurrencyItem() } returns listOfDummyCurrencyItems

        val data = systemUnderTest.getAllCurrencies()

        data.test {
            verify(exactly = 1) {
                mockLocalDataSource.contains(CacheKeys.CURRENCY)
                mockLocalDataSource.getAllCurrencyItem()
            }
            val response = awaitItem()

            assert(response is ApiResponse.Success)
            assert((response as ApiResponse.Success).data == listOfDummyCurrencyItems)

            awaitComplete()
        }
    }

    @Test
    fun `currencies are downloaded and cached after fresh download`() = runTest {
        every { mockLocalDataSource.contains(CacheKeys.CURRENCY) } returns false
        coEvery { mockRemoteDataSource.getAllCurrencyItem() } returns ApiResponse.Success(
            mockCurrencyMap
        )

        val data = systemUnderTest.getAllCurrencies()

        data.test {
            verify(exactly = 0) {
                mockLocalDataSource.getAllCurrencyItem()
            }
            verify(exactly = 1) {
                mockLocalDataSource.updateAllCurrencyItem(any())
            }

            val response = awaitItem()

            assert(response is ApiResponse.Success)
            assert((response as ApiResponse.Success).data == listOf(mockCurrencyModel))

            awaitComplete()
        }
    }

    @Test
    fun `conversions rates from cache works correctly`() = runTest {
        every { mockLocalDataSource.contains(CacheKeys.CONVERSION) } returns true
        every { mockLocalDataSource.getAllConversions() } returns listOfDummyConversions

        val data = systemUnderTest.getAllConversionsCached(false)

        data.test {
            verify(exactly = 1) {
                mockLocalDataSource.contains(CacheKeys.CONVERSION)
                mockLocalDataSource.getAllConversions()
            }

            val response = awaitItem()
            assert(response is ApiResponse.Success)
            assert((response as ApiResponse.Success).data == listOfDummyConversions)
            coVerify(exactly = 0) {
                mockRemoteDataSource.getAllConversions()
            }
            awaitComplete()
        }
    }

    @Test
    fun `conversions rates refresh works correctly`() = runTest {
        every { mockLocalDataSource.contains(CacheKeys.CONVERSION) } returns true
        every { mockLocalDataSource.getAllConversions() } returns listOfDummyConversions
        coEvery { mockRemoteDataSource.getAllConversions() } returns ApiResponse.Success(
            mockConversionResponse
        )

        val data = systemUnderTest.getAllConversionsCached(true)

        data.test {
            verify(exactly = 1) {
                mockLocalDataSource.contains(CacheKeys.CONVERSION)
                mockLocalDataSource.getAllConversions()
            }
            val response = awaitItem()
            assert(response is ApiResponse.Success)
            assert((response as ApiResponse.Success).data == listOfDummyConversions)

            val response2 = awaitItem()
            assert(response2 is ApiResponse.Success)
            assert((response2 as ApiResponse.Success).data == listOfDummyConversions2)

            verify {
                mockLocalDataSource.updateAllConversions(any())
            }
            awaitComplete()
        }
    }

    @Test
    fun `cache is updated after fetching conversion rates from remote`() = runTest {
        coEvery { mockRemoteDataSource.getAllConversions() } returns ApiResponse.Success(
            mockConversionResponse
        )

        val data = systemUnderTest.fetchAllConversions()

        data.test {
            val response = awaitItem()
            assert(response is ApiResponse.Success)
            assertEquals((response as ApiResponse.Success).data, listOfDummyConversions2)

            verify {
                mockLocalDataSource.updateAllConversions(any())
            }
            awaitComplete()
        }
    }

    @Test
    fun `fetch all conversions return failure if there is failure`() = runTest {
        coEvery { mockRemoteDataSource.getAllConversions() } returns ApiResponse.Failure(
            mockk(
                relaxed = true
            )
        )

        val data = systemUnderTest.fetchAllConversions()

        data.test {
            verify(exactly = 0) {
                mockLocalDataSource.getAllConversions()
            }
            val response = awaitItem()
            assert(response is ApiResponse.Failure)
            awaitComplete()
        }
    }


}