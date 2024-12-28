package com.skopenweb.exchange.business.usecases

import com.skopenweb.exchange.business.ExchangeRepository
import com.skopenweb.exchange.business.source.CacheKeys
import com.skopenweb.exchange.business.source.LocalDataSource
import com.skopenweb.exchange.business.usecase.CheckRatesRefreshUseCase
import com.skopenweb.exchange.business.usecase.GetAllConversionUseCase
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Test

class GetAllConversionUseCaseTest {

    private val mockRepository: ExchangeRepository = mockk(relaxed = true)
    private val mockLocalDataSource: LocalDataSource = mockk(relaxed = true)
    private val mockCheckRatesRefreshUseCase: CheckRatesRefreshUseCase = mockk(relaxed = true)

    private val systemUnderTest = GetAllConversionUseCase(
        repository = mockRepository,
        checkRatesRefreshUseCase = mockCheckRatesRefreshUseCase,
        localDataSource = mockLocalDataSource,
    )

    @Test
    fun `if conversion is cached, return cached result`() = runTest {
        every { mockLocalDataSource.contains(CacheKeys.CONVERSION) } returns true

        systemUnderTest()

        coVerify {
            mockCheckRatesRefreshUseCase.invoke()
            mockRepository.getAllConversionsCached(any())
        }
    }

    @Test
    fun `if conversion is not available, fetch from remote`() = runTest {
        every { mockLocalDataSource.contains(CacheKeys.CONVERSION) } returns false

        systemUnderTest()

        coVerify(exactly = 0) {
            mockCheckRatesRefreshUseCase.invoke()
            mockRepository.getAllConversionsCached(any())
        }
        coVerify {
            mockRepository.fetchAllConversions()
        }
    }
}