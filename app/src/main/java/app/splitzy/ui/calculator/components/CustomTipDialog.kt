package app.splitzy.ui.calculator.components

import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import app.splitzy.R
import app.splitzy.domain.AmountInput
import app.splitzy.ui.calculator.parseCustomTip
import app.splitzy.ui.theme.FigureStyles
import java.math.BigDecimal

@Composable
fun CustomTipDialog(
    initialPercent: BigDecimal?,
    onConfirm: (BigDecimal) -> Unit,
    onDismiss: () -> Unit,
) {
    var text by rememberSaveable { mutableStateOf(initialPercent?.let(AmountInput::toEditable).orEmpty()) }
    val parsed = parseCustomTip(text)
    val focusRequester = remember { FocusRequester() }
    LaunchedEffect(Unit) { focusRequester.requestFocus() }

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = MaterialTheme.shapes.large,
        containerColor = MaterialTheme.colorScheme.surface,
        title = { Text(stringResource(R.string.tip_custom_title)) },
        text = {
            OutlinedTextField(
                value = text,
                onValueChange = { text = AmountInput.sanitize(it, fractionDigits = 2).take(6) },
                singleLine = true,
                textStyle = FigureStyles.Counter,
                suffix = { Text("%", style = FigureStyles.Counter) },
                supportingText = { Text(stringResource(R.string.tip_custom_hint)) },
                isError = text.isNotEmpty() && parsed == null,
                shape = MaterialTheme.shapes.medium,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { parsed?.let(onConfirm) }),
                modifier = Modifier.focusRequester(focusRequester),
            )
        },
        confirmButton = {
            TextButton(onClick = { parsed?.let(onConfirm) }, enabled = parsed != null, colors = dialogButtonColors()) {
                Text(stringResource(R.string.tip_custom_apply))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, colors = dialogButtonColors()) { Text(stringResource(R.string.tip_custom_cancel)) }
        },
    )
}

/** Teal text fails contrast on white, so dialog actions use the on-surface colour. */
@Composable
private fun dialogButtonColors() = ButtonDefaults.textButtonColors(
    contentColor = MaterialTheme.colorScheme.onSurface,
)
