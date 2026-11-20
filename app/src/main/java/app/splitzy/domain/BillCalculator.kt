package app.splitzy.domain

import java.math.BigDecimal
import java.math.BigInteger
import java.math.RoundingMode

/**
 * Pure bill-splitting maths. All amounts are [BigDecimal] scaled to the currency's
 * minor unit (cents for ZAR, none for JPY), so totals always add up exactly.
 */
object BillCalculator {

    const val MIN_PEOPLE = 1
    const val MAX_PEOPLE = 99
    val MAX_TIP_PERCENT: BigDecimal = BigDecimal(100)
    val MAX_BILL: BigDecimal = BigDecimal("999999999.99")

    private val HUNDRED = BigDecimal(100)

    fun calculate(
        bill: BigDecimal,
        tipPercent: BigDecimal,
        people: Int,
        currency: SplitCurrency,
    ): BillBreakdown {
        val digits = currency.fractionDigits
        val safeBill = bill.coerceIn(BigDecimal.ZERO, MAX_BILL).setScale(digits, RoundingMode.HALF_UP)
        val safeTip = tipPercent.coerceIn(BigDecimal.ZERO, MAX_TIP_PERCENT)
        val safePeople = people.coerceIn(MIN_PEOPLE, MAX_PEOPLE)

        val tip = safeBill.multiply(safeTip)
            .divide(HUNDRED, digits, RoundingMode.HALF_UP)
        val total = safeBill + tip

        return BillBreakdown(
            bill = safeBill,
            tipPercent = safeTip.stripTrailingZeros(),
            tip = tip,
            total = total,
            people = safePeople,
            split = splitEvenly(total, safePeople, digits),
        )
    }

    /**
     * Divides [total] into [people] shares in whole minor units. When it doesn't
     * divide evenly, the leftover cents go one each to the first few people, so
     * the shares always sum back to the exact total.
     */
    fun splitEvenly(total: BigDecimal, people: Int, fractionDigits: Int): Split {
        require(people >= 1) { "people must be at least 1" }
        val totalMinor: BigInteger = total.setScale(fractionDigits, RoundingMode.HALF_UP).unscaledValue()
        val (baseMinor, remainder) = totalMinor.divideAndRemainder(BigInteger.valueOf(people.toLong()))
        val base = BigDecimal(baseMinor, fractionDigits)
        val extraPayers = remainder.toInt()
        return Split(
            share = base,
            roundUp = if (extraPayers == 0) null else RoundUp(
                people = extraPayers,
                amount = base + BigDecimal.ONE.movePointLeft(fractionDigits),
            ),
        )
    }

    private fun BigDecimal.coerceIn(min: BigDecimal, max: BigDecimal): BigDecimal =
        when {
            this < min -> min
            this > max -> max
            else -> this
        }
}

data class BillBreakdown(
    val bill: BigDecimal,
    val tipPercent: BigDecimal,
    val tip: BigDecimal,
    val total: BigDecimal,
    val people: Int,
    val split: Split,
) {
    /** What most people pay. */
    val perPerson: BigDecimal get() = split.share
}

/**
 * @property share what every person pays, before rounding up.
 * @property roundUp present when some people pay one minor unit more to cover the total.
 */
data class Split(val share: BigDecimal, val roundUp: RoundUp?)

data class RoundUp(val people: Int, val amount: BigDecimal)
