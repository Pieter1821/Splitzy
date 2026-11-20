package app.splitzy.ui.calculator

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.splitzy.domain.AmountInput
import app.splitzy.domain.BillCalculator
import app.splitzy.domain.Currencies
import app.splitzy.domain.SplitCurrency
import app.splitzy.domain.TipSelection
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import java.math.BigDecimal

/**
 * Holds the calculator inputs and derives the breakdown from them. Inputs live in
 * [SavedStateHandle] so they survive rotation and process death.
 */
class CalculatorViewModel(
    private val savedState: SavedStateHandle,
) : ViewModel() {

    private val currency = MutableStateFlow(Currencies.Default)

    private val billText = savedState.getStateFlow(KEY_BILL, "")
    private val tipPercent = savedState.getStateFlow(KEY_TIP, TipSelection.Default.value.toString())
    private val tipIsCustom = savedState.getStateFlow(KEY_TIP_CUSTOM, false)
    private val people = savedState.getStateFlow(KEY_PEOPLE, CalculatorUiState.DEFAULT_PEOPLE)

    private val tip = combine(tipPercent, tipIsCustom) { percent, custom ->
        val value = percent.toBigDecimalOrNull() ?: TipSelection.Default.percent
        if (custom) TipSelection.Custom(value) else TipSelection.Preset(value.toInt())
    }

    val uiState: StateFlow<CalculatorUiState> =
        combine(billText, currency, tip, people) { bill, currency, tip, people ->
            CalculatorUiState(
                billText = bill,
                currency = currency,
                tip = tip,
                people = people,
                breakdown = BillCalculator.calculate(AmountInput.parse(bill), tip.percent, people, currency),
            )
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), CalculatorUiState())

    fun onBillChange(raw: String) {
        savedState[KEY_BILL] = AmountInput.sanitize(raw, currency.value.fractionDigits)
    }

    fun onTipSelected(selection: TipSelection) {
        savedState[KEY_TIP] = selection.percent.stripTrailingZeros().toPlainString()
        savedState[KEY_TIP_CUSTOM] = selection is TipSelection.Custom
    }

    fun onAddPerson() = setPeople(people.value + 1)

    fun onRemovePerson() = setPeople(people.value - 1)

    fun onCurrencySelected(selected: SplitCurrency) {
        currency.value = selected
        // Re-sanitise so e.g. "12.50" becomes "12" when switching to JPY.
        savedState[KEY_BILL] = AmountInput.sanitize(billText.value, selected.fractionDigits)
    }

    /** Clears the bill and restores default tip and people. Currency is a preference, so it stays. */
    fun onReset() {
        savedState[KEY_BILL] = ""
        onTipSelected(TipSelection.Default)
        savedState[KEY_PEOPLE] = CalculatorUiState.DEFAULT_PEOPLE
    }

    private fun setPeople(count: Int) {
        savedState[KEY_PEOPLE] = count.coerceIn(BillCalculator.MIN_PEOPLE, BillCalculator.MAX_PEOPLE)
    }

    private companion object {
        const val KEY_BILL = "bill"
        const val KEY_TIP = "tip_percent"
        const val KEY_TIP_CUSTOM = "tip_custom"
        const val KEY_PEOPLE = "people"
    }
}

/** Parses a user-typed custom tip, returning null when it's empty or out of range. */
fun parseCustomTip(text: String): BigDecimal? {
    val value = AmountInput.parse(AmountInput.sanitize(text, fractionDigits = 2))
    return value.takeIf { text.isNotBlank() && it <= BillCalculator.MAX_TIP_PERCENT }
}
