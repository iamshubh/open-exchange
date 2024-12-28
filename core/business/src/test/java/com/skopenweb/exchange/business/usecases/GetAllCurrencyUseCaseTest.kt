package com.skopenweb.exchange.business.usecases

import com.skopenweb.exchange.business.ExchangeRepository
import com.skopenweb.exchange.business.usecase.GetAllCurrencyUseCase
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Test

class GetAllCurrencyUseCaseTest {
    private val mockRepository: ExchangeRepository = mockk(relaxed = true)
    private val systemUnderTest = GetAllCurrencyUseCase(mockRepository)

    @Test
    fun `fetch all currencies fetches from repository`() = runTest {
        systemUnderTest.invoke()

        coVerify {
            mockRepository.getAllCurrencies()
        }
    }
}