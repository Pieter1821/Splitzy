package app.splitzy.domain

import org.junit.Assert.assertEquals
import org.junit.Test
import java.math.BigDecimal

class AmountInputTest {

    private fun cents(raw: String) = AmountInput.sanitize(raw, fractionDigits = 2)

    @Test
    fun `keeps plain amounts`() {
        assertEquals("250", cents("250"))
        assertEquals("250.5", cents("250.5"))
        assertEquals("250.55", cents("250.55"))
    }

    @Test
    fun `drops letters and symbols`() {
        assertEquals("1250", cents("R 1 2a5b0!"))
        assertEquals("", cents("abc"))
    }

    @Test
    fun `accepts comma as decimal separator`() {
        assertEquals("12.5", cents("12,5"))
    }

    @Test
    fun `ignores a second separator`() {
        assertEquals("12.34", cents("12.3.4"))
    }

    @Test
    fun `truncates extra decimals`() {
        assertEquals("9.99", cents("9.999"))
    }

    @Test
    fun `strips leading zeros but keeps a single zero`() {
        assertEquals("7", cents("007"))
        assertEquals("0", cents("000"))
        assertEquals("0.5", cents("0.5"))
        assertEquals("0.", cents("."))
    }

    @Test
    fun `limits integer digits`() {
        assertEquals("123456789", cents("1234567890123"))
    }

    @Test
    fun `currencies without decimals drop the separator`() {
        assertEquals("1250", AmountInput.sanitize("1250.75", fractionDigits = 0))
    }

    @Test
    fun `parses partial input`() {
        assertEquals(BigDecimal.ZERO, AmountInput.parse(""))
        assertEquals(BigDecimal("0"), AmountInput.parse("0."))
        assertEquals(BigDecimal("12.5"), AmountInput.parse("12.5"))
    }

    @Test
    fun `editable text drops trailing zeros`() {
        assertEquals("7.5", AmountInput.toEditable(BigDecimal("7.50")))
        assertEquals("", AmountInput.toEditable(BigDecimal.ZERO))
    }
}
