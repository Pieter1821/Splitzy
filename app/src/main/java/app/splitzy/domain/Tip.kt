package app.splitzy.domain

import java.math.BigDecimal

/** The tip the user picked: one of the presets, or their own percentage. */
sealed interface TipSelection {
    val percent: BigDecimal

    data class Preset(val value: Int) : TipSelection {
        override val percent: BigDecimal get() = BigDecimal(value)
    }

    data class Custom(override val percent: BigDecimal) : TipSelection

    companion object {
        val Presets: List<Preset> = listOf(0, 10, 12, 15, 18, 20).map(::Preset)
        val Default = Preset(10)
    }
}
