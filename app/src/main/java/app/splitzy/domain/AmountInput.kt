package app.splitzy.domain

import java.math.BigDecimal

/**
 * Keeps the bill field to something that always parses: digits, at most one
 * decimal separator (either `.` or `,`), and no more decimals than the currency allows.
 */
object AmountInput {

    const val MAX_INTEGER_DIGITS = 9

    fun sanitize(raw: String, fractionDigits: Int): String {
        val integer = StringBuilder()
        val fraction = StringBuilder()
        var seenSeparator = false

        for (char in raw) {
            when {
                char.isAsciiDigit() && !seenSeparator ->
                    if (integer.length < MAX_INTEGER_DIGITS) integer.append(char)
                char.isAsciiDigit() ->
                    if (fraction.length < fractionDigits) fraction.append(char)
                char.isSeparator() && !seenSeparator && fractionDigits > 0 ->
                    seenSeparator = true
            }
        }

        val cleanInteger = integer.toString().trimStart('0').ifEmpty { if (integer.isNotEmpty() || seenSeparator) "0" else "" }
        return if (seenSeparator) "$cleanInteger.$fraction" else cleanInteger
    }

    /** Parses sanitized text; empty or bare "0." counts as zero. */
    fun parse(text: String): BigDecimal =
        text.trimEnd('.').toBigDecimalOrNull() ?: BigDecimal.ZERO

    /** Formats a decimal back into editable text, e.g. 250.00 -> "250", 7.50 -> "7.5". */
    fun toEditable(value: BigDecimal): String =
        if (value.signum() == 0) "" else value.stripTrailingZeros().toPlainString()

    private fun Char.isAsciiDigit() = this in '0'..'9'
    private fun Char.isSeparator() = this == '.' || this == ','
}
