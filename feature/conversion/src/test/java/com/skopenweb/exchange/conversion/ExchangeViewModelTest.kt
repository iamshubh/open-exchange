package com.skopenweb.exchange.conversion

import app.cash.turbine.test
import com.skopenweb.exchange.business.network.ApiResponse
import com.skopenweb.exchange.business.usecase.GetAllConversionUseCase
import com.skopenweb.exchange.business.usecase.GetAllCurrencyUseCase
import com.skopenweb.exchange.conversion.ui.ExchangeViewModel
import com.skopenweb.exchange.conversion.ui.model.CurrencyUiItem
import com.skopenweb.exchange.conversion.ui.model.ExchangeUiContent
import com.skopenweb.exchange.conversion.ui.model.UiAction
import com.skopenweb.exchange.conversion.ui.model.UiState
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Test

class ExchangeViewModelTest {

    private val mockGetAllConversionUseCase: GetAllConversionUseCase = mockk(relaxed = true)
    private val mockGetAllCurrencyUseCase: GetAllCurrencyUseCase = mockk(relaxed = true)
    private val dummySelectedCurrencyModel = CurrencyUiItem(id = "0", text = "INR", selected = true)

    private val systemUnderTest = ExchangeViewModel(
        getAllCurrencyUseCase = mockGetAllCurrencyUseCase,
        getAllConversionUseCase = mockGetAllConversionUseCase,
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    private val testDispatcher: TestDispatcher = UnconfinedTestDispatcher()

    /*
        @get:Rule
        val mainDispatcherRule = MainDispatcherRule()
    */

    private val dummyExchangeUiContent: ExchangeUiContent = mockk(relaxed = true)

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @Test
    fun `check if invalid amount is handled correctly`() = runTest {
        val uiAction = UiAction.ConvertAction(amount = "...", currency = dummySelectedCurrencyModel)
        coEvery { mockGetAllConversionUseCase.invoke() } returns flowOf(
            ApiResponse.Success(
                emptyList()
            )
        )
        systemUnderTest.initialize()

        systemUnderTest.handleAction(uiAction)

        coEvery {
            systemUnderTest.uiState.test {
                var data = awaitItem()
                assert(data is UiState.Loading)
//                data = awaitItem()
//                val uiData = (data as UiState.Success).data
//                assert(uiData.errorText != null)
            }
        }

    }
}