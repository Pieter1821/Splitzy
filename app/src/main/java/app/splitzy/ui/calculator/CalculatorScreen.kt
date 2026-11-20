package app.splitzy.ui.calculator

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import app.splitzy.R
import app.splitzy.domain.SplitCurrency
import app.splitzy.domain.TipSelection
import app.splitzy.ui.brand.SplitzyLogo
import app.splitzy.ui.calculator.components.BillAmountField
import app.splitzy.ui.calculator.components.BreakdownCard
import app.splitzy.ui.calculator.components.CurrencyChip
import app.splitzy.ui.calculator.components.CurrencyPickerSheet
import app.splitzy.ui.calculator.components.CustomTipDialog
import app.splitzy.ui.calculator.components.PeopleStepper
import app.splitzy.ui.calculator.components.PerPersonCard
import app.splitzy.ui.calculator.components.TipSelector
import app.splitzy.ui.theme.Sizes
import app.splitzy.ui.theme.Spacing
import app.splitzy.ui.theme.SplitzyTheme

@Composable
fun CalculatorRoute(viewModel: CalculatorViewModel = viewModel(factory = CalculatorViewModel.Factory)) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    CalculatorScreen(
        state = state,
        onBillChange = viewModel::onBillChange,
        onTipSelected = viewModel::onTipSelected,
        onCurrencySelected = viewModel::onCurrencySelected,
        onAddPerson = viewModel::onAddPerson,
        onRemovePerson = viewModel::onRemovePerson,
        onReset = viewModel::onReset,
    )
}

@Composable
fun CalculatorScreen(
    state: CalculatorUiState,
    onBillChange: (String) -> Unit,
    onTipSelected: (TipSelection) -> Unit,
    onCurrencySelected: (SplitCurrency) -> Unit,
    onAddPerson: () -> Unit,
    onRemovePerson: () -> Unit,
    onReset: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var showCustomTip by rememberSaveable { mutableStateOf(false) }
    var showCurrencyPicker by rememberSaveable { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    BoxWithConstraints(modifier.fillMaxSize()) {
        // On short screens (landscape phones) the tagline gives its space to the calculator.
        val compactHeight = maxHeight < 640.dp

        Surface(color = SplitzyTheme.extraColors.hero, modifier = Modifier.fillMaxSize()) {
            Column(Modifier.fillMaxSize()) {
                HeroHeader(showTagline = !compactHeight)

                Surface(
                    color = MaterialTheme.colorScheme.background,
                    shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .imePadding()
                            .verticalScroll(rememberScrollState()),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Column(
                            modifier = Modifier
                                .widthIn(max = Sizes.maxContentWidth)
                                .fillMaxWidth()
                                .padding(horizontal = Spacing.lg)
                                .padding(top = Spacing.lg, bottom = Spacing.md)
                                .navigationBarsPadding(),
                            verticalArrangement = Arrangement.spacedBy(Spacing.lg),
                        ) {
                            Section(stringResource(R.string.bill_amount)) {
                                BillAmountField(
                                    text = state.billText,
                                    currency = state.currency,
                                    onTextChange = onBillChange,
                                    trailing = {
                                        CurrencyChip(state.currency, onClick = { showCurrencyPicker = true })
                                    },
                                )
                            }
                            Section(stringResource(R.string.tip_percentage)) {
                                TipSelector(
                                    selected = state.tip,
                                    onPresetSelected = onTipSelected,
                                    onCustomClick = { showCustomTip = true },
                                )
                            }
                            Section(stringResource(R.string.people)) {
                                PeopleStepper(
                                    count = state.people,
                                    canDecrease = state.canRemovePerson,
                                    canIncrease = state.canAddPerson,
                                    onDecrease = onRemovePerson,
                                    onIncrease = onAddPerson,
                                )
                            }
                            PerPersonCard(
                                breakdown = state.breakdown,
                                currency = state.currency,
                                hasBill = state.hasBill,
                            )
                            Section(stringResource(R.string.breakdown)) {
                                BreakdownCard(breakdown = state.breakdown, currency = state.currency)
                            }
                            ResetButton(onClick = {
                                focusManager.clearFocus()
                                onReset()
                            })
                        }
                    }
                }
            }
        }
    }

    if (showCurrencyPicker) {
        CurrencyPickerSheet(
            selected = state.currency,
            onSelect = {
                onCurrencySelected(it)
                showCurrencyPicker = false
            },
            onDismiss = { showCurrencyPicker = false },
        )
    }

    if (showCustomTip) {
        CustomTipDialog(
            initialPercent = (state.tip as? TipSelection.Custom)?.percent,
            onConfirm = {
                onTipSelected(TipSelection.Custom(it))
                showCustomTip = false
            },
            onDismiss = { showCustomTip = false },
        )
    }
}

@Composable
private fun HeroHeader(showTagline: Boolean) {
    val colors = SplitzyTheme.extraColors
    Column(
        Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = Spacing.lg)
            .padding(top = Spacing.xs, bottom = Spacing.lg),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .height(Sizes.minTouchTarget),
        ) {
            SplitzyLogo(modifier = Modifier.weight(1f))
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
private fun Section(title: String, content: @Composable () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.semantics { heading() },
        )
        content()
    }
}

@Composable
private fun ResetButton(onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        shape = MaterialTheme.shapes.medium,
        border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.onSurface),
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp),
    ) {
        Icon(Icons.Rounded.Refresh, contentDescription = null, modifier = Modifier.size(20.dp))
        Spacer(Modifier.width(Spacing.xs))
        Text(stringResource(R.string.action_reset), style = MaterialTheme.typography.titleSmall)
    }
}

@Preview(name = "Light", heightDp = 1100)
@Composable
private fun CalculatorScreenPreview() {
    SplitzyTheme(darkTheme = false) {
        CalculatorScreen(
            state = CalculatorUiState(billText = "250"),
            onBillChange = {}, onTipSelected = {}, onCurrencySelected = {}, onAddPerson = {}, onRemovePerson = {}, onReset = {},
        )
    }
}
