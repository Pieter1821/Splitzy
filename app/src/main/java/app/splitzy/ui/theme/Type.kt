package app.splitzy.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import app.splitzy.R

val PlusJakartaSans = FontFamily(
    Font(R.font.plus_jakarta_sans_regular, FontWeight.Normal),
    Font(R.font.plus_jakarta_sans_medium, FontWeight.Medium),
    Font(R.font.plus_jakarta_sans_semibold, FontWeight.SemiBold),
    Font(R.font.plus_jakarta_sans_bold, FontWeight.Bold),
    Font(R.font.plus_jakarta_sans_extrabold, FontWeight.ExtraBold),
)

private fun style(size: Int, lineHeight: Int, weight: FontWeight, tracking: Double = 0.0) = TextStyle(
    fontFamily = PlusJakartaSans,
    fontWeight = weight,
    fontSize = size.sp,
    lineHeight = lineHeight.sp,
    letterSpacing = tracking.em,
)

/**
 * Brand type scale mapped onto Material roles:
 * Display 32 Bold · Heading 24 Bold · Subheading 18 SemiBold · Body 16 Regular ·
 * Labels 14 Medium · Captions 12 Regular.
 */
val SplitzyTypography = Typography(
    displayLarge = style(32, 38, FontWeight.Bold, -0.02),
    displayMedium = style(32, 38, FontWeight.Bold, -0.02),
    displaySmall = style(32, 38, FontWeight.Bold, -0.02),
    headlineLarge = style(24, 30, FontWeight.Bold, -0.01),
    headlineMedium = style(24, 30, FontWeight.Bold, -0.01),
    headlineSmall = style(24, 30, FontWeight.Bold, -0.01),
    titleLarge = style(18, 24, FontWeight.SemiBold),
    titleMedium = style(18, 24, FontWeight.SemiBold),
    titleSmall = style(16, 22, FontWeight.SemiBold),
    bodyLarge = style(16, 24, FontWeight.Normal),
    bodyMedium = style(14, 20, FontWeight.Normal),
    bodySmall = style(12, 16, FontWeight.Normal),
    labelLarge = style(14, 20, FontWeight.Medium),
    labelMedium = style(12, 16, FontWeight.Medium),
    labelSmall = style(12, 16, FontWeight.Medium),
)

/** Large financial figures. Tabular digits keep amounts from jittering as they change. */
object FigureStyles {
    val Hero = TextStyle(
        fontFamily = PlusJakartaSans,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 44.sp,
        lineHeight = 52.sp,
        letterSpacing = (-0.03).em,
        fontFeatureSettings = "tnum",
    )
    val Input = TextStyle(
        fontFamily = PlusJakartaSans,
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,
        lineHeight = 34.sp,
        letterSpacing = (-0.01).em,
        fontFeatureSettings = "tnum",
    )
    val Counter = TextStyle(
        fontFamily = PlusJakartaSans,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp,
        lineHeight = 30.sp,
        fontFeatureSettings = "tnum",
    )
}
