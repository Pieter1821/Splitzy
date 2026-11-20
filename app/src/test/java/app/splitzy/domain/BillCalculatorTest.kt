package app.splitzy.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.math.BigDecimal

class BillCalculatorTest {

    private val zar = Currencies.ZAR
    private val jpy = Currencies.byCode("JPY")

    private fun calc(bill: String, tip: String, people: Int, currency: SplitCurrency = zar) =
        BillCalculator.calculate(BigDecimal(bill), BigDecimal(tip), people, currency)

    @Test
    fun `brand board example - R250 plus 10 percent split four ways is R68_75`() {
        val result = calc("250", "10", 4)

        assertEquals(BigDecimal("250.00"), result.bill)
        assertEquals(BigDecimal("25.00"), result.tip)
        assertEquals(BigDecimal("275.00"), result.total)
        assertEquals(BigDecimal("68.75"), result.perPerson)
        assertNull(result.split.roundUp)
    }

    @Test
    fun `zero tip splits the bill as is`() {
        val result = calc("90", "0", 3)

        assertEquals(BigDecimal("0.00"), result.tip)
        assertEquals(BigDecimal("30.00"), result.perPerson)
    }

    @Test
    fun `decimal custom tip is rounded half up to cents`() {
        // 123.45 * 7.5% = 9.25875 -> 9.26
        val result = calc("123.45", "7.5", 1)

        assertEquals(BigDecimal("9.26"), result.tip)
        assertEquals(BigDecimal("132.71"), result.total)
    }

    @Test
    fun `uneven split rounds up for the fewest people so shares sum to the total`() {
        val result = calc("100", "0", 3)

        assertEquals(BigDecimal("33.33"), result.perPerson)
        assertEquals(RoundUp(people = 1, amount = BigDecimal("33.34")), result.split.roundUp)
        assertSharesSumToTotal(result)
    }

    @Test
    fun `remainder of several cents goes to that many people`() {
        // 10.00 / 7 = 1.42 r 6 cents
        val result = calc("10", "0", 7)

        assertEquals(BigDecimal("1.42"), result.perPerson)
        assertEquals(6, result.split.roundUp?.people)
        assertSharesSumToTotal(result)
    }

    @Test
    fun `yen has no minor unit`() {
        val result = calc("1000", "15", 3, jpy)

        assertEquals(BigDecimal("150"), result.tip)
        assertEquals(BigDecimal("1150"), result.total)
        assertEquals(BigDecimal("383"), result.perPerson)
        assertEquals(RoundUp(1, BigDecimal("384")), result.split.roundUp)
    }

    @Test
    fun `empty bill gives an all-zero breakdown`() {
        val result = calc("0", "20", 2)

        assertEquals(BigDecimal("0.00"), result.total)
        assertEquals(BigDecimal("0.00"), result.perPerson)
        assertNull(result.split.roundUp)
    }

    @Test
    fun `people are clamped between 1 and 99`() {
        assertEquals(1, calc("50", "0", 0).people)
        assertEquals(1, calc("50", "0", -4).people)
        assertEquals(99, calc("50", "0", 500).people)
    }

    @Test
    fun `tip is clamped between 0 and 100 percent`() {
        assertEquals(BigDecimal("0.00"), calc("50", "-5", 1).tip)
        assertEquals(BigDecimal("50.00"), calc("50", "250", 1).tip)
    }

    @Test
    fun `very large bills stay exact`() {
        val result = calc("999999999.99", "20", 99)

        assertEquals(BigDecimal("200000000.00"), result.tip)
        assertEquals(BigDecimal("1199999999.99"), result.total)
        assertSharesSumToTotal(result)
    }

    @Test
    fun `bill above the cap is capped`() {
        assertEquals(BillCalculator.MAX_BILL, calc("5000000000", "0", 1).bill)
    }

    private fun assertSharesSumToTotal(result: BillBreakdown) {
        val roundUp = result.split.roundUp
        val extra = roundUp?.people ?: 0
        val sum = result.perPerson.multiply(BigDecimal(result.people - extra)) +
            (roundUp?.amount?.multiply(BigDecimal(extra)) ?: BigDecimal.ZERO)
        assertEquals(result.total, sum)
    }
}
