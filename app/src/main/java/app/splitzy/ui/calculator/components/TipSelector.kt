package app.splitzy.ui.calculator.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import app.splitzy.R
import app.splitzy.domain.TipSelection
import app.splitzy.ui.theme.Motion
import app.splitzy.ui.theme.Sizes
import app.splitzy.ui.theme.Spacing

/** Preset tip chips in two rows, with a wide Custom chip that opens a dialog. */
@Composable
fun TipSelector(
    selected: TipSelection,
    onPresetSelected: (TipSelection.Preset) -> Unit,
    onCustomClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val haptics = LocalHapticFeedback.current
    val (firstRow, secondRow) = TipSelection.Presets.chunked(4).let { it[0] to it[1] }

    @Composable
    fun PresetChip(preset: TipSelection.Preset, modifier: Modifier) = TipChip(
        label = stringResource(R.string.tip_value, preset.value.toString()),
        selected = selected == preset,
        onClick = {
            haptics.performHapticFeedback(HapticFeedbackType.SegmentTick)
            onPresetSelected(preset)
        },
        modifier = modifier,
    )

    Column(modifier, verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
        Row(horizontalArrangement = Arrangement.spacedBy(Spacing.xs)) {
            firstRow.forEach { PresetChip(it, Modifier.weight(1f)) }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(Spacing.xs)) {
            secondRow.forEach { PresetChip(it, Modifier.weight(1f)) }
            val custom = selected as? TipSelection.Custom
            TipChip(
                label = custom?.let {
                    stringResource(R.string.tip_value, it.percent.stripTrailingZeros().toPlainString())
                } ?: stringResource(R.string.tip_custom),
                selected = custom != null,
                onClick = {
                    haptics.performHapticFeedback(HapticFeedbackType.SegmentTick)
                    onCustomClick()
                },
                leadingEdit = true,
                modifier = Modifier.weight(2f),
            )
        }
    }
}

@Composable
private fun TipChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    leadingEdit: Boolean = false,
) {
    val colors = MaterialTheme.colorScheme
    val container by animateColorAsState(
        if (selected) colors.primary else colors.surfaceContainer,
        tween(Motion.FAST),
        label = "chipContainer",
    )
    val content by animateColorAsState(
        if (selected) colors.onPrimary else colors.onSurface,
        tween(Motion.FAST),
        label = "chipContent",
    )
    Surface(
        shape = CircleShape,
        color = container,
        contentColor = content,
        modifier = modifier
            .heightIn(min = Sizes.minTouchTarget)
            .selectable(selected = selected, onClick = onClick, role = Role.RadioButton),
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (leadingEdit) {
                Icon(Icons.Rounded.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(6.dp))
            }
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.SemiBold,
                maxLines = 1,
            )
        }
    }
}
