package app.splitzy.ui.calculator

import app.splitzy.domain.BillBreakdown
import app.splitzy.domain.BillCalculator
import app.splitzy.domain.Currencies
import app.splitzy.domain.SplitCurrency
import app.splitzy.domain.TipSelection
import java.math.BigDecimal

data class CalculatorUiState(
    val billText: String = "",
    val currency: SplitCurrency = Currencies.Default,
    val tip: TipSelection = TipSelection.Default,
    val people: Int = DEFAULT_PEOPLE,
    val breakdown: BillBreakdown = BillCalculator.calculate(BigDecimal.ZERO, tip.percent, people, currency),
) {
    val hasBill: Boolean get() = breakdown.bill.signum() > 0
    val canRemovePerson: Boolean get() = people > BillCalculator.MIN_PEOPLE
    val canAddPerson: Boolean get() = people < BillCalculator.MAX_PEOPLE

    companion object {
        const val DEFAULT_PEOPLE = 2
    }
}
