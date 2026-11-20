package app.splitzy.ui.calculator.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.ExpandMore
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import app.splitzy.R
import app.splitzy.domain.Currencies
import app.splitzy.domain.SplitCurrency
import app.splitzy.ui.theme.Spacing

/** Compact "ZAR ▾" chip that sits inside the bill field. */
@Composable
fun CurrencyChip(currency: SplitCurrency, onClick: () -> Unit) {
    val description = stringResource(R.string.currency_change_a11y, currency.displayName)
    Surface(
        onClick = onClick,
        shape = CircleShape,
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
        contentColor = MaterialTheme.colorScheme.onSurface,
        modifier = Modifier
            .minimumInteractiveComponentSize()
            .semantics { contentDescription = description },
    ) {
        Row(
            modifier = Modifier.padding(start = Spacing.sm, end = Spacing.xs, top = 6.dp, bottom = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(currency.code, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
            Icon(Icons.Rounded.ExpandMore, contentDescription = null, modifier = Modifier.size(20.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurrencyPickerSheet(
    selected: SplitCurrency,
    onSelect: (SplitCurrency) -> Unit,
    onDismiss: () -> Unit,
) {
    var query by rememberSaveable { mutableStateOf("") }
    val results = remember(query) { Currencies.search(query) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = MaterialTheme.colorScheme.surface,
    ) {
        Column(Modifier.padding(horizontal = Spacing.lg)) {
            Text(
                text = stringResource(R.string.currency_title),
                style = MaterialTheme.typography.headlineSmall,
            )
            Spacer(Modifier.size(Spacing.md))
            OutlinedTextField(
                value = query,
                onValueChange = { query = it.take(30) },
                placeholder = { Text(stringResource(R.string.currency_search)) },
                leadingIcon = { Icon(Icons.Rounded.Search, contentDescription = null) },
                singleLine = true,
                shape = MaterialTheme.shapes.medium,
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                ),
                modifier = Modifier.fillMaxWidth(),
            )
        }
        Spacer(Modifier.size(Spacing.xs))
        if (results.isEmpty()) {
            Text(
                text = stringResource(R.string.currency_no_results),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(Spacing.lg),
            )
        }
        LazyColumn(contentPadding = androidx.compose.foundation.layout.PaddingValues(bottom = Spacing.lg)) {
            items(results, key = { it.code }) { currency ->
                CurrencyRow(
                    currency = currency,
                    isSelected = currency.code == selected.code,
                    onClick = { onSelect(currency) },
                )
            }
        }
    }
}

@Composable
private fun CurrencyRow(currency: SplitCurrency, isSelected: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 64.dp)
            .semantics { selected = isSelected }
            .clickable(role = Role.RadioButton, onClick = onClick)
            .padding(horizontal = Spacing.lg),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.md),
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .background(
                    if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceContainer,
                    CircleShape,
                ),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = currency.symbol,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
            )
        }
        Column(Modifier.weight(1f)) {
            Text(
                text = currency.displayName,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = currency.code,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        if (isSelected) {
            Icon(
                Icons.Rounded.CheckCircle,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
            )
            Spacer(Modifier.width(Spacing.xxs))
        }
    }
}
