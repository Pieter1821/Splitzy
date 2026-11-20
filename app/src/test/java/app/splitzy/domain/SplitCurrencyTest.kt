package app.splitzy.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.math.BigDecimal
import java.util.Locale

class SplitCurrencyTest {

    @Test
    fun `formats rand with symbol and grouping`() {
        assertEquals("R 1,250.50", Currencies.ZAR.format(BigDecimal("1250.5"), Locale.US))
    }

    @Test
    fun `formats yen without decimals`() {
        assertEquals("¥ 1,250", Currencies.byCode("JPY").format(BigDecimal("1250"), Locale.US))
    }

    @Test
    fun `fraction digits come from ISO 4217`() {
        assertEquals(2, Currencies.ZAR.fractionDigits)
        assertEquals(0, Currencies.byCode("JPY").fractionDigits)
    }

    @Test
    fun `unknown code falls back to the default`() {
        assertEquals(Currencies.Default, Currencies.byCode("XYZ"))
        assertEquals(Currencies.Default, Currencies.byCode(null))
    }

    @Test
    fun `search matches code, name and symbol`() {
        assertEquals("EUR", Currencies.search("eur").first().code)
        assertTrue(Currencies.search("rand").any { it.code == "ZAR" })
        assertTrue(Currencies.search("£").any { it.code == "GBP" })
        assertEquals(Currencies.all, Currencies.search("  "))
    }
}
