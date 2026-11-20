package app.splitzy.ui.calculator.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import app.splitzy.R
import app.splitzy.domain.AmountInput
import app.splitzy.domain.SplitCurrency
import app.splitzy.ui.theme.FigureStyles
import app.splitzy.ui.theme.Motion
import app.splitzy.ui.theme.Spacing

/**
 * Large amount input with the currency symbol in front and an optional trailing
 * slot (the currency picker chip). Sanitises as the user types.
 */
@Composable
fun BillAmountField(
    text: String,
    currency: SplitCurrency,
    onTextChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    trailing: @Composable () -> Unit = {},
) {
    // Local field state keeps the cursor stable while typing; the ViewModel stays
    // the source of truth and external changes (reset, currency switch) flow back in.
    var field by remember { mutableStateOf(TextFieldValue(text, TextRange(text.length))) }
    LaunchedEffect(text) {
        if (text != field.text) field = TextFieldValue(text, TextRange(text.length))
    }

    val interactions = remember { MutableInteractionSource() }
    val focused by interactions.collectIsFocusedAsState()
    val borderColor by animateColorAsState(
        targetValue = if (focused) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
        animationSpec = tween(Motion.FAST),
        label = "billBorder",
    )
    val focusManager = LocalFocusManager.current
    val label = stringResource(R.string.bill_amount)
    val shape = MaterialTheme.shapes.medium

    Row(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 64.dp)
            .background(MaterialTheme.colorScheme.surfaceContainerLow, shape)
            .border(if (focused) 2.dp else 1.dp, borderColor, shape)
            .padding(start = Spacing.md, end = Spacing.xs),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = currency.symbol,
            style = FigureStyles.Input,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(Modifier.width(Spacing.xs))
        Box(Modifier.weight(1f), contentAlignment = Alignment.CenterStart) {
            if (field.text.isEmpty()) {
                Text(
                    text = stringResource(R.string.bill_placeholder),
                    style = FigureStyles.Input,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                )
            }
            BasicTextField(
                value = field,
                onValueChange = { new ->
                    val clean = AmountInput.sanitize(new.text, currency.fractionDigits)
                    field = if (clean == new.text) new else TextFieldValue(clean, TextRange(clean.length))
                    if (clean != text) onTextChange(clean)
                },
                singleLine = true,
                textStyle = FigureStyles.Input.copy(color = MaterialTheme.colorScheme.onSurface),
                cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Decimal,
                    imeAction = ImeAction.Done,
                ),
                keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                interactionSource = interactions,
                modifier = Modifier
                    .fillMaxWidth()
                    .semantics { contentDescription = label },
            )
        }
        trailing()
    }
}
