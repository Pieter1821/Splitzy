package app.splitzy.ui.calculator

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * How the calculator arranges itself for the space it actually has. Decisions are
 * based on the app window (not the physical device), so split screen, foldables and
 * freeform windows all get the right layout.
 */
data class CalculatorLayout(
    /** Inputs on the left, results on the right. */
    val twoPane: Boolean,
    /** The large brand tagline; dropped when vertical space or font size makes it costly. */
    val showTagline: Boolean,
    /** Side gutter inside the sheet. */
    val gutter: Dp,
    /** Copy and Share stack vertically when side by side would truncate their labels. */
    val stackActions: Boolean,
) {
    companion object {
        /** Material window size class boundaries. */
        val MediumWidth = 600.dp
        val ExpandedWidth = 840.dp
        private val NarrowWidth = 360.dp
        private val TallEnoughForTagline = 640.dp
        private const val LARGE_FONT_SCALE = 1.5f

        fun forWindow(width: Dp, height: Dp, fontScale: Float): CalculatorLayout {
            val twoPane = width >= MediumWidth
            val gutter = when {
                width >= ExpandedWidth -> 40.dp
                width >= MediumWidth -> 32.dp
                width < NarrowWidth -> 16.dp
                else -> 24.dp
            }
            // Width available to the action row, in "unscaled" dp, so large fonts count.
            val paneWidth = if (twoPane) (width - gutter * 3) / 2 else width - gutter * 2
            return CalculatorLayout(
                twoPane = twoPane,
                showTagline = height >= TallEnoughForTagline && fontScale < LARGE_FONT_SCALE,
                gutter = gutter,
                stackActions = paneWidth / fontScale < 300.dp,
            )
        }
    }
}
