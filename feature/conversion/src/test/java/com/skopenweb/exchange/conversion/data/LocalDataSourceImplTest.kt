package com.skopenweb.exchange.conversion.data

import android.content.SharedPreferences
import com.skopenweb.exchange.business.model.ConversionModel
import com.skopenweb.exchange.business.model.CurrencyModel
import com.skopenweb.exchange.business.source.CacheKeys
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import org.junit.Test

class LocalDataSourceImplTest {
    companion object {
        val mockCurrencyMapping =
            """{"USD": "American Dollar", "INR": "Indian Rupees", "EUR": "Euro"}"""

        val mockCurrencyModel =
            listOf(
                CurrencyModel(id = "USD", text = "American Dollar"),
                CurrencyModel(id = "INR", text = "Indian Rupees"),
                CurrencyModel(id = "EUR", text = "Euro")
            )

        const val mockConversionString =
            """{"AED":3.672538,"USD":66.809999,"YEN":125.716501}"""

        val mockConversionModel = listOf(
            ConversionModel(id = "AED", factor = 3.672538),
            ConversionModel(id = "USD", factor = 66.809999),
            ConversionModel(id = "YEN", factor = 125.716501)
        )
    }

    private val mockSharedPreferences: SharedPreferences = mockk(relaxed = true)
    private val fakeJson: Json = Json

    private val systemUnderTest = LocalDataSourceImpl(
        sharedPreferences = mockSharedPreferences,
        json = fakeJson,
    )

    @Test
    fun `get last fetched conversion works fine`() = runTest {
        systemUnderTest.getLastFetchedConversionTimeStamp()

        verify(exactly = 1) {
            mockSharedPreferences.getLong(CacheKeys.LAST_FETCHED_CONVERSION, any())
        }
    }

    @Test
    fun `last updated time method works fine`() = runTest {
        systemUnderTest.updateLastFetchedConversionTimeStamp(1000L)

        verify(exactly = 1) {
            mockSharedPreferences.edit().putLong(CacheKeys.LAST_FETCHED_CONVERSION, 1000L).apply()
        }
    }

    @Test
    fun `getAllCurrency updated time method works fine`() = runTest {
        every {
            mockSharedPreferences.getString(
                CacheKeys.CURRENCY,
                any()
            )
        } returns mockCurrencyMapping

        val data = systemUnderTest.getAllCurrencyItem()

        verify(exactly = 1) {
            mockSharedPreferences.getString(CacheKeys.CURRENCY, any())
        }
        assert(data == mockCurrencyModel)
    }

    @Test
    fun `update currency works fine`() = runTest {
        systemUnderTest.updateAllCurrencyItem(mapOf())

        verify(exactly = 1) {
            mockSharedPreferences.edit().putString(CacheKeys.CURRENCY, "{}").apply()
        }
    }

    @Test
    fun `getAllConversion updated time method works fine`() = runTest {
        every {
            mockSharedPreferences.getString(
                CacheKeys.CONVERSION,
                any(),
            )
        } returns mockConversionString

        val data = systemUnderTest.getAllConversions()

        verify(exactly = 1) {
            mockSharedPreferences.getString(CacheKeys.CONVERSION, any())
        }
        assert(data == mockConversionModel)
    }

    @Test
    fun `update All Conversion currency works fine`() = runTest {
        systemUnderTest.updateAllConversions(mapOf())

        verify(exactly = 1) {
            mockSharedPreferences.edit().putString(CacheKeys.CONVERSION, "{}").apply()
        }
    }

    @Test
    fun `delete method works fine`() = runTest {
        val mockKey = "key"
        systemUnderTest.delete(mockKey)

        verify(exactly = 1) {
            mockSharedPreferences.edit().remove(mockKey).apply()
        }
    }
}