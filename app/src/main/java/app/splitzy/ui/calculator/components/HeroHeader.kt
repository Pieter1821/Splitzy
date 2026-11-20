package app.splitzy.ui.calculator.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DarkMode
import androidx.compose.material.icons.rounded.LightMode
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import app.splitzy.R
import app.splitzy.ui.brand.SplitzyLogo
import app.splitzy.ui.theme.Motion
import app.splitzy.ui.theme.Sizes
import app.splitzy.ui.theme.Spacing
import app.splitzy.ui.theme.SplitzyTheme

/** Navy brand header: logo, theme toggle and (when there's room) the tagline. */
@Composable
fun HeroHeader(
    showTagline: Boolean,
    gutter: Dp,
    isDarkTheme: Boolean,
    onToggleTheme: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = SplitzyTheme.extraColors
    Column(
        modifier
            .fillMaxWidth()
            .padding(horizontal = gutter)
            .padding(top = Spacing.xs, bottom = if (showTagline) Spacing.lg else Spacing.md),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = Sizes.minTouchTarget),
        ) {
            SplitzyLogo(modifier = Modifier.weight(1f))
            ThemeToggle(isDarkTheme = isDarkTheme, onClick = onToggleTheme)
        }
        if (showTagline) {
            Spacer(Modifier.height(Spacing.md))
            Text(
                text = buildAnnotatedString {
                    append(stringResource(R.string.tagline_start))
                    append("\n")
                    append(stringResource(R.string.tagline_not))
                    append(" ")
                    withStyle(SpanStyle(color = colors.heroAccent)) {
                        append(stringResource(R.string.tagline_accent))
                    }
                },
                style = MaterialTheme.typography.displaySmall,
                color = colors.onHero,
                modifier = Modifier.semantics { heading() },
            )
        }
    }
}

@Composable
private fun ThemeToggle(isDarkTheme: Boolean, onClick: () -> Unit) {
    val description = stringResource(if (isDarkTheme) R.string.theme_switch_light else R.string.theme_switch_dark)
    FilledIconButton(
        onClick = onClick,
        shape = CircleShape,
        colors = IconButtonDefaults.filledIconButtonColors(
            containerColor = SplitzyTheme.extraColors.onHero.copy(alpha = 0.08f),
            contentColor = SplitzyTheme.extraColors.onHero,
        ),
        modifier = Modifier.size(Sizes.minTouchTarget),
    ) {
        AnimatedContent(
            targetState = isDarkTheme,
            transitionSpec = {
                (fadeIn(tween(Motion.SLOW)) + scaleIn(tween(Motion.SLOW), initialScale = 0.5f))
                    .togetherWith(fadeOut(tween(Motion.FAST)) + scaleOut(tween(Motion.FAST), targetScale = 0.5f))
            },
            label = "themeIcon",
        ) { dark ->
            Icon(
                imageVector = if (dark) Icons.Rounded.LightMode else Icons.Rounded.DarkMode,
                contentDescription = description,
            )
        }
    }
}
