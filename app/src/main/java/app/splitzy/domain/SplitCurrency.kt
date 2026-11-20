package app.splitzy.domain

import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Currency
import java.util.Locale

/**
 * A currency Splitzy can split in. Decimal places come from ISO 4217 via
 * [java.util.Currency], so JPY gets 0 decimals and ZAR gets 2 without special cases.
 */
data class SplitCurrency(
    val code: String,
    val symbol: String,
    val displayName: String,
) {
    val fractionDigits: Int = Currency.getInstance(code).defaultFractionDigits.coerceAtLeast(0)

    /** "R 1,250.50" — symbol, no-break space, then the amount grouped for [locale]. */
    fun format(amount: BigDecimal, locale: Locale = Locale.getDefault()): String {
        val pattern = if (fractionDigits == 0) "#,##0" else "#,##0." + "0".repeat(fractionDigits)
        val formatter = DecimalFormat(pattern, DecimalFormatSymbols.getInstance(locale))
        val rounded = amount.setScale(fractionDigits, RoundingMode.HALF_UP)
        return "$symbol ${formatter.format(rounded)}"
    }
}

object Currencies {

    val ZAR = SplitCurrency("ZAR", "R", "South African Rand")

    /** Curated list: Southern Africa first, then the world's most-used currencies. */
    val all: List<SplitCurrency> = listOf(
        ZAR,
        SplitCurrency("USD", "$", "US Dollar"),
        SplitCurrency("EUR", "€", "Euro"),
        SplitCurrency("GBP", "£", "British Pound"),
        SplitCurrency("BWP", "P", "Botswana Pula"),
        SplitCurrency("NAD", "N$", "Namibian Dollar"),
        SplitCurrency("NGN", "₦", "Nigerian Naira"),
        SplitCurrency("KES", "KSh", "Kenyan Shilling"),
        SplitCurrency("AED", "AED", "UAE Dirham"),
        SplitCurrency("AUD", "A$", "Australian Dollar"),
        SplitCurrency("BRL", "R$", "Brazilian Real"),
        SplitCurrency("CAD", "C$", "Canadian Dollar"),
        SplitCurrency("CHF", "CHF", "Swiss Franc"),
        SplitCurrency("CNY", "CN¥", "Chinese Yuan"),
        SplitCurrency("INR", "₹", "Indian Rupee"),
        SplitCurrency("JPY", "¥", "Japanese Yen"),
        SplitCurrency("MXN", "MX$", "Mexican Peso"),
        SplitCurrency("NZD", "NZ$", "New Zealand Dollar"),
    )

    val Default: SplitCurrency = ZAR

    fun byCode(code: String?): SplitCurrency = all.firstOrNull { it.code == code } ?: Default

    /** Matches code, name or symbol, ignoring case. */
    fun search(query: String): List<SplitCurrency> {
        val q = query.trim()
        if (q.isEmpty()) return all
        return all.filter {
            it.code.contains(q, ignoreCase = true) ||
                it.displayName.contains(q, ignoreCase = true) ||
                it.symbol.equals(q, ignoreCase = true)
        }
    }
}
