package app.splitzy.ui.calculator.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Remove
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.liveRegion
import app.splitzy.R
import app.splitzy.ui.theme.FigureStyles
import app.splitzy.ui.theme.Motion
import app.splitzy.ui.theme.Sizes
import app.splitzy.ui.theme.Spacing

@Composable
fun PeopleStepper(
    count: Int,
    canDecrease: Boolean,
    canIncrease: Boolean,
    onDecrease: () -> Unit,
    onIncrease: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val haptics = LocalHapticFeedback.current
    val countDescription = pluralStringResource(R.plurals.people_count_a11y, count, count)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceContainerLow, MaterialTheme.shapes.medium)
            .padding(Spacing.xs),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        StepButton(
            icon = Icons.Rounded.Remove,
            description = stringResource(R.string.people_decrease),
            enabled = canDecrease,
            container = MaterialTheme.colorScheme.surfaceContainerHigh,
            content = MaterialTheme.colorScheme.onSurface,
            onClick = {
                haptics.performHapticFeedback(HapticFeedbackType.SegmentTick)
                onDecrease()
            },
        )
        Box(
            modifier = Modifier
                .weight(1f)
                .clearAndSetSemantics {
                    contentDescription = countDescription
                    liveRegion = LiveRegionMode.Polite
                },
            contentAlignment = Alignment.Center,
        ) {
            AnimatedContent(
                targetState = count,
                transitionSpec = {
                    val direction = if (targetState > initialState) 1 else -1
                    (slideInVertically(tween(Motion.MEDIUM)) { it * direction } + fadeIn(tween(Motion.MEDIUM)))
                        .togetherWith(
                            slideOutVertically(tween(Motion.MEDIUM)) { -it * direction } + fadeOut(tween(Motion.FAST)),
                        )
                },
                label = "peopleCount",
            ) { value ->
                Text(
                    text = value.toString(),
                    style = FigureStyles.Counter,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
        }
        StepButton(
            icon = Icons.Rounded.Add,
            description = stringResource(R.string.people_increase),
            enabled = canIncrease,
            container = MaterialTheme.colorScheme.primary,
            content = MaterialTheme.colorScheme.onPrimary,
            onClick = {
                haptics.performHapticFeedback(HapticFeedbackType.SegmentTick)
                onIncrease()
            },
        )
    }
}

@Composable
private fun StepButton(
    icon: ImageVector,
    description: String,
    enabled: Boolean,
    container: Color,
    content: Color,
    onClick: () -> Unit,
) {
    FilledIconButton(
        onClick = onClick,
        enabled = enabled,
        shape = CircleShape,
        colors = IconButtonDefaults.filledIconButtonColors(
            containerColor = container,
            contentColor = content,
            disabledContainerColor = MaterialTheme.colorScheme.surfaceContainer,
            disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
        ),
        modifier = Modifier.size(Sizes.stepperButton),
    ) {
        Icon(icon, contentDescription = description)
    }
}
