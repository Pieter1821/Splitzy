package app.splitzy.ui.calculator.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import app.splitzy.R
import app.splitzy.ui.theme.Motion
import app.splitzy.ui.theme.Spacing

/** Copy (navy) and Share (teal), side by side as on the brand board. */
@Composable
fun ShareActions(
    enabled: Boolean,
    justCopied: Boolean,
    onCopy: () -> Unit,
    onShare: () -> Unit,
    modifier: Modifier = Modifier,
    stacked: Boolean = false,
) {
    val colors = MaterialTheme.colorScheme
    val copy = @Composable { buttonModifier: Modifier ->
        ActionButton(
            label = stringResource(if (justCopied) R.string.action_copied else R.string.action_copy),
            icon = if (justCopied) Icons.Rounded.Check else Icons.Rounded.ContentCopy,
            enabled = enabled,
            onClick = onCopy,
            colors = ButtonDefaults.buttonColors(
                containerColor = colors.secondary,
                contentColor = colors.onSecondary,
            ),
            modifier = buttonModifier,
        )
    }
    val share = @Composable { buttonModifier: Modifier ->
        ActionButton(
            label = stringResource(R.string.action_share),
            icon = Icons.Rounded.Share,
            enabled = enabled,
            onClick = onShare,
            colors = ButtonDefaults.buttonColors(
                containerColor = colors.primary,
                contentColor = colors.onPrimary,
            ),
            modifier = buttonModifier,
        )
    }
    if (stacked) {
        Column(modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
            share(Modifier.fillMaxWidth())
            copy(Modifier.fillMaxWidth())
        }
    } else {
        Row(modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(Spacing.sm)) {
            copy(Modifier.weight(1f))
            share(Modifier.weight(1f))
        }
    }
}

@Composable
private fun ActionButton(
    label: String,
    icon: ImageVector,
    enabled: Boolean,
    onClick: () -> Unit,
    colors: ButtonColors,
    modifier: Modifier = Modifier,
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        shape = MaterialTheme.shapes.medium,
        colors = colors,
        modifier = modifier.heightIn(min = 52.dp),
    ) {
        AnimatedContent(
            targetState = icon,
            transitionSpec = {
                (scaleIn(tween(Motion.MEDIUM), initialScale = 0.6f) + fadeIn(tween(Motion.MEDIUM)))
                    .togetherWith(fadeOut(tween(Motion.FAST)))
            },
            contentAlignment = Alignment.Center,
            label = "actionIcon",
        ) { Icon(it, contentDescription = null, modifier = Modifier.size(20.dp)) }
        Spacer(Modifier.width(Spacing.xs))
        Text(label, style = MaterialTheme.typography.titleSmall, maxLines = 1)
    }
}
