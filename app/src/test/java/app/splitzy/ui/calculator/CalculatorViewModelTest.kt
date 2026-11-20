package app.splitzy.ui.calculator

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import app.splitzy.data.ThemeMode
import app.splitzy.data.UserPreferences
import app.splitzy.domain.Currencies
import app.splitzy.domain.TipSelection
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Before
import org.junit.Test
import java.math.BigDecimal

@OptIn(ExperimentalCoroutinesApi::class)
class CalculatorViewModelTest {

    private class FakePreferences : UserPreferences {
        override val currencyCode = MutableStateFlow<String?>(null)
        override val themeMode = MutableStateFlow(ThemeMode.System)
        override suspend fun setCurrencyCode(code: String) { currencyCode.value = code }
        override suspend fun setThemeMode(mode: ThemeMode) { themeMode.value = mode }
    }

    private lateinit var preferences: FakePreferences
    private lateinit var viewModel: CalculatorViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        preferences = FakePreferences()
        viewModel = CalculatorViewModel(SavedStateHandle(), preferences)
    }

    @After
    fun tearDown() = Dispatchers.resetMain()

    @Test
    fun `starts with sensible defaults`() = runTest {
        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals("", state.billText)
            assertEquals(2, state.people)
            assertEquals(TipSelection.Default, state.tip)
            assertEquals(Currencies.ZAR, state.currency)
            assertFalse(state.hasBill)
        }
    }

    @Test
    fun `updates the breakdown as inputs change`() = runTest {
        viewModel.uiState.test {
            awaitItem()
            viewModel.onBillChange("250")
            viewModel.onAddPerson()
            viewModel.onAddPerson()
            assertEquals(BigDecimal("68.75"), expectMostRecentItem().breakdown.perPerson)
        }
    }

    @Test
    fun `people never drop below one`() = runTest {
        viewModel.uiState.test {
            awaitItem()
            repeat(5) { viewModel.onRemovePerson() }
            assertEquals(1, expectMostRecentItem().people)
        }
    }

    @Test
    fun `custom tip is applied`() = runTest {
        viewModel.uiState.test {
            awaitItem()
            viewModel.onBillChange("200")
            viewModel.onTipSelected(TipSelection.Custom(BigDecimal("7.5")))
            val state = expectMostRecentItem()
            assertEquals(TipSelection.Custom(BigDecimal("7.5")), state.tip)
            assertEquals(BigDecimal("15.00"), state.breakdown.tip)
        }
    }

    @Test
    fun `switching to yen drops decimals and remembers the currency`() = runTest {
        viewModel.uiState.test {
            awaitItem()
            viewModel.onBillChange("12.50")
            viewModel.onCurrencySelected(Currencies.byCode("JPY"))
            val state = expectMostRecentItem()
            assertEquals("12", state.billText)
            assertEquals("JPY", state.currency.code)
            assertEquals("JPY", preferences.currencyCode.value)
        }
    }

    @Test
    fun `reset restores defaults but keeps the currency`() = runTest {
        viewModel.uiState.test {
            awaitItem()
            viewModel.onCurrencySelected(Currencies.byCode("EUR"))
            viewModel.onBillChange("80")
            viewModel.onTipSelected(TipSelection.Preset(20))
            viewModel.onAddPerson()
            viewModel.onReset()
            val state = expectMostRecentItem()
            assertEquals("", state.billText)
            assertEquals(TipSelection.Default, state.tip)
            assertEquals(2, state.people)
            assertEquals("EUR", state.currency.code)
        }
    }

    @Test
    fun `theme toggle flips the visible theme`() = runTest {
        viewModel.onToggleTheme(isDarkNow = false)
        assertEquals(ThemeMode.Dark, preferences.themeMode.value)
        viewModel.onToggleTheme(isDarkNow = true)
        assertEquals(ThemeMode.Light, preferences.themeMode.value)
    }

    @Test
    fun `custom tip parsing rejects empty and out of range values`() {
        assertEquals(null, parseCustomTip(""))
        assertEquals(null, parseCustomTip("150"))
        assertEquals(BigDecimal("12.5"), parseCustomTip("12.5"))
        assertEquals(BigDecimal("0"), parseCustomTip("0"))
    }
}
