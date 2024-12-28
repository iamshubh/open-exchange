package com.skopenweb.exchange.business.usecases

import com.skopenweb.exchange.business.source.LocalDataSource
import com.skopenweb.exchange.business.usecase.CheckRatesRefreshUseCase
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.Test

class CheckRatesRefreshUseCaseTest {
    private val mockLocalDataSource: LocalDataSource = mockk(relaxed = true)
    private val systemUnderTest = CheckRatesRefreshUseCase(mockLocalDataSource)

    @Test
    fun `method checks if refresh is required`() = runTest {
        systemUnderTest.invoke()

        verify {
            mockLocalDataSource.getLastFetchedConversionTimeStamp()
        }
    }
}