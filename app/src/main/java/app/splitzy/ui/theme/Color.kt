package app.splitzy.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

/** Official Splitzy palette. Every colour in the app derives from these. */
object BrandColors {
    val Navy = Color(0xFF101B2D)
    val Teal = Color(0xFF20C9A6)
    val Mint = Color(0xFFA5F3D6)
    val Cloud = Color(0xFFF5F7FA)
    val Indigo = Color(0xFF6366F1)
    val Success = Color(0xFF10B981)
    val White = Color(0xFFFFFFFF)
}

/** Neutral tints of Navy and Cloud used for surfaces, borders and secondary text. */
private object Neutrals {
    val NavySurface = Color(0xFF17243A)
    val NavyContainer = Color(0xFF1F2E48)
    val NavyContainerHigh = Color(0xFF283A58)
    val NavyOutline = Color(0xFF34476A)
    val SlateText = Color(0xFF5B6679)
    val SlateTextOnDark = Color(0xFFA3AFC2)
    val CloudContainer = Color(0xFFEBEFF5)
    val CloudContainerHigh = Color(0xFFE1E7EF)
    val CloudOutline = Color(0xFFD3DBE6)
}

internal val LightColors = lightColorScheme(
    primary = BrandColors.Teal,
    onPrimary = BrandColors.Navy,
    primaryContainer = BrandColors.Mint,
    onPrimaryContainer = BrandColors.Navy,
    secondary = BrandColors.Navy,
    onSecondary = BrandColors.White,
    tertiary = BrandColors.Indigo,
    onTertiary = BrandColors.White,
    background = BrandColors.Cloud,
    onBackground = BrandColors.Navy,
    surface = BrandColors.White,
    onSurface = BrandColors.Navy,
    surfaceVariant = Neutrals.CloudContainer,
    onSurfaceVariant = Neutrals.SlateText,
    surfaceContainerLowest = BrandColors.White,
    surfaceContainerLow = BrandColors.Cloud,
    surfaceContainer = Neutrals.CloudContainer,
    surfaceContainerHigh = Neutrals.CloudContainerHigh,
    surfaceContainerHighest = Neutrals.CloudContainerHigh,
    outline = Neutrals.CloudOutline,
    outlineVariant = Neutrals.CloudContainerHigh,
    inverseSurface = BrandColors.Navy,
    inverseOnSurface = BrandColors.Cloud,
)

internal val DarkColors = darkColorScheme(
    primary = BrandColors.Teal,
    onPrimary = BrandColors.Navy,
    primaryContainer = BrandColors.Mint,
    onPrimaryContainer = BrandColors.Navy,
    secondary = Neutrals.NavyContainerHigh,
    onSecondary = BrandColors.Cloud,
    tertiary = BrandColors.Indigo,
    onTertiary = BrandColors.White,
    background = BrandColors.Navy,
    onBackground = BrandColors.Cloud,
    surface = Neutrals.NavySurface,
    onSurface = BrandColors.Cloud,
    surfaceVariant = Neutrals.NavyContainer,
    onSurfaceVariant = Neutrals.SlateTextOnDark,
    surfaceContainerLowest = BrandColors.Navy,
    surfaceContainerLow = Neutrals.NavySurface,
    surfaceContainer = Neutrals.NavyContainer,
    surfaceContainerHigh = Neutrals.NavyContainerHigh,
    surfaceContainerHighest = Neutrals.NavyContainerHigh,
    outline = Neutrals.NavyOutline,
    outlineVariant = Neutrals.NavyContainer,
    inverseSurface = BrandColors.Cloud,
    inverseOnSurface = BrandColors.Navy,
)

/**
 * Brand roles that Material's scheme has no slot for. The hero header is navy in
 * both themes, so its colours are fixed rather than derived from the scheme.
 */
@Immutable
data class SplitzyExtraColors(
    val hero: Color = BrandColors.Navy,
    val onHero: Color = BrandColors.White,
    val onHeroMuted: Color = Color(0xFFA3AFC2),
    val heroAccent: Color = BrandColors.Teal,
    val success: Color = BrandColors.Success,
)
