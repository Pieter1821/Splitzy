package app.splitzy.ui.calculator.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import app.splitzy.R
import app.splitzy.domain.BillBreakdown
import app.splitzy.domain.SplitCurrency
import app.splitzy.ui.theme.Spacing

@Composable
fun BreakdownCard(
    breakdown: BillBreakdown,
    currency: SplitCurrency,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surfaceContainerLow,
    ) {
        Column(Modifier.padding(horizontal = Spacing.md, vertical = Spacing.xs)) {
            BreakdownRow(stringResource(R.string.breakdown_bill), currency.format(breakdown.bill))
            BreakdownRow(
                stringResource(R.string.breakdown_tip, breakdown.tipPercent.toPlainString()),
                currency.format(breakdown.tip),
            )
            HorizontalDivider(
                color = MaterialTheme.colorScheme.outline,
                modifier = Modifier.padding(vertical = Spacing.xxs),
            )
            BreakdownRow(stringResource(R.string.breakdown_total), currency.format(breakdown.total), emphasised = true)
            BreakdownRow(stringResource(R.string.breakdown_people), breakdown.people.toString())
        }
    }
}

@Composable
private fun BreakdownRow(label: String, value: String, emphasised: Boolean = false) {
    val base: TextStyle = MaterialTheme.typography.bodyLarge.copy(fontFeatureSettings = "tnum")
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = Spacing.xs),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            style = base,
            fontWeight = if (emphasised) FontWeight.Bold else FontWeight.Normal,
            color = if (emphasised) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f),
        )
        Text(
            text = value,
            style = base,
            fontWeight = if (emphasised) FontWeight.Bold else FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}
