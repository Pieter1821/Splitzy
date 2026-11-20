package app.splitzy.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

/** 8dp grid. Use these instead of raw dp values in components. */
object Spacing {
    val xxs = 4.dp
    val xs = 8.dp
    val sm = 12.dp
    val md = 16.dp
    val lg = 24.dp
    val xl = 32.dp
    val xxl = 48.dp
}

object Sizes {
    /** Accessibility minimum for anything tappable. */
    val minTouchTarget = 48.dp
    val stepperButton = 48.dp
    val maxContentWidth = 560.dp
}

/** Cards 24dp, buttons and inputs 16dp, chips fully rounded. */
object Radii {
    val control = 16.dp
    val card = 24.dp
    val sheet = 32.dp
}

val SplitzyShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(Radii.control),
    large = RoundedCornerShape(Radii.card),
    extraLarge = RoundedCornerShape(Radii.sheet),
)

/** Brand motion: every transition sits within 150–220ms. */
object Motion {
    const val FAST = 150
    const val MEDIUM = 180
    const val SLOW = 220
}
