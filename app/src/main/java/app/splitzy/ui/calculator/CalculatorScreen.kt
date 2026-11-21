package app.splitzy.ui.calculator

import android.os.Build
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import app.splitzy.R
import app.splitzy.domain.SplitCurrency
import app.splitzy.domain.TipSelection
import app.splitzy.ui.calculator.components.BillAmountField
import app.splitzy.ui.calculator.components.BreakdownCard
import app.splitzy.ui.calculator.components.CurrencyChip
import app.splitzy.ui.calculator.components.CurrencyPickerSheet
import app.splitzy.ui.calculator.components.CustomTipDialog
import app.splitzy.ui.calculator.components.HeroHeader
import app.splitzy.ui.calculator.components.PeopleStepper
import app.splitzy.ui.calculator.components.PerPersonCard
import app.splitzy.ui.calculator.components.ShareActions
import app.splitzy.ui.calculator.components.TipSelector
import app.splitzy.ui.theme.Motion
import app.splitzy.ui.theme.Sizes
import app.splitzy.ui.theme.Spacing
import app.splitzy.ui.theme.SplitzyTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val COPIED_FEEDBACK_MS = 1_600L
private val TwoPaneMaxWidth = 1040.dp

@Composable
fun CalculatorRoute(
    isDarkTheme: Boolean,
    viewModel: CalculatorViewModel = viewModel(factory = CalculatorViewModel.Factory),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val resources = LocalResources.current
    CalculatorScreen(
        state = state,
        actions = CalculatorActions(
            onToggleTheme = { viewModel.onToggleTheme(isDarkTheme) },
            onBillChange = viewModel::onBillChange,
            onTipSelected = viewModel::onTipSelected,
            onCurrencySelected = viewModel::onCurrencySelected,
            onAddPerson = viewModel::onAddPerson,
            onRemovePerson = viewModel::onRemovePerson,
            onReset = viewModel::onReset,
            onCopy = {
                context.copyToClipboard(
                    label = resources.getString(R.string.app_name),
                    text = state.currency.format(state.breakdown.perPerson),
                )
            },
            onShare = { context.shareText(buildShareText(resources, state)) },
        ),
        isDarkTheme = isDarkTheme,
    )
}

/** Everything the screen can ask for, grouped so composables take one parameter. */
class CalculatorActions(
    val onToggleTheme: () -> Unit = {},
    val onBillChange: (String) -> Unit = {},
    val onTipSelected: (TipSelection) -> Unit = {},
    val onCurrencySelected: (SplitCurrency) -> Unit = {},
    val onAddPerson: () -> Unit = {},
    val onRemovePerson: () -> Unit = {},
    val onReset: () -> Unit = {},
    val onCopy: () -> Unit = {},
    val onShare: () -> Unit = {},
)

@Composable
fun CalculatorScreen(
    state: CalculatorUiState,
    actions: CalculatorActions,
    isDarkTheme: Boolean,
    modifier: Modifier = Modifier,
) {
    var showCustomTip by rememberSaveable { mutableStateOf(false) }
    var showCurrencyPicker by rememberSaveable { mutableStateOf(false) }
    var justCopied by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val copiedMessage = stringResource(R.string.copied_message, state.currency.format(state.breakdown.perPerson))

    LaunchedEffect(justCopied) {
        if (justCopied) {
            // Android 13+ shows its own clipboard confirmation; avoid doubling up.
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
                launch { snackbarHostState.showSnackbar(copiedMessage) }
            }
            delay(COPIED_FEEDBACK_MS)
            justCopied = false
        }
    }

    val sections = CalculatorSections(
        state = state,
        actions = actions,
        justCopied = justCopied,
        onCopied = { justCopied = true },
        onOpenCurrencyPicker = { showCurrencyPicker = true },
        onOpenCustomTip = { showCustomTip = true },
    )

    BoxWithConstraints(modifier.fillMaxSize()) {
        val layout = CalculatorLayout.forWindow(maxWidth, maxHeight, LocalDensity.current.fontScale)
        val maxContentWidth = if (layout.twoPane) TwoPaneMaxWidth else Sizes.maxContentWidth
        // Keeps content clear of display cutouts and side navigation bars in landscape.
        val sideInsets = WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal)

        Surface(color = SplitzyTheme.extraColors.hero, modifier = Modifier.fillMaxSize()) {
            Column(Modifier.fillMaxSize()) {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Top))
                        .windowInsetsPadding(sideInsets),
                    contentAlignment = Alignment.TopCenter,
                ) {
                    HeroHeader(
                        showTagline = layout.showTagline,
                        gutter = layout.gutter,
                        isDarkTheme = isDarkTheme,
                        onToggleTheme = actions.onToggleTheme,
                        modifier = Modifier.widthIn(max = maxContentWidth),
                    )
                }

                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            // One padding for whichever is taller: the keyboard or the gesture bar.
                            .windowInsetsPadding(WindowInsets.ime.union(WindowInsets.navigationBars))
                            .windowInsetsPadding(sideInsets)
                            .verticalScroll(rememberScrollState()),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        val contentModifier = Modifier
                            .widthIn(max = maxContentWidth)
                            .fillMaxWidth()
                            .padding(horizontal = layout.gutter)
                            .padding(top = Spacing.lg, bottom = Spacing.md)

                        if (layout.twoPane) {
                            Row(contentModifier, horizontalArrangement = Arrangement.spacedBy(layout.gutter)) {
                                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(Spacing.lg)) {
                                    sections.Inputs(showLiveShare = false)
                                }
                                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(Spacing.lg)) {
                                    sections.Results(stackActions = layout.stackActions)
                                }
                            }
                        } else {
                            Column(contentModifier, verticalArrangement = Arrangement.spacedBy(Spacing.lg)) {
                                sections.Inputs(showLiveShare = true)
                                sections.Results(stackActions = layout.stackActions)
                            }
                        }
                    }
                }
            }
        }
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(Spacing.md),
        )
    }

    if (showCurrencyPicker) {
        CurrencyPickerSheet(
            selected = state.currency,
            onSelect = {
                actions.onCurrencySelected(it)
                showCurrencyPicker = false
            },
            onDismiss = { showCurrencyPicker = false },
        )
    }

    if (showCustomTip) {
        CustomTipDialog(
            initialPercent = (state.tip as? TipSelection.Custom)?.percent,
            onConfirm = {
                actions.onTipSelected(TipSelection.Custom(it))
                showCustomTip = false
            },
            onDismiss = { showCustomTip = false },
        )
    }
}

/** The two halves of the calculator, placed in one column or side by side by the screen. */
private class CalculatorSections(
    private val state: CalculatorUiState,
    private val actions: CalculatorActions,
    private val justCopied: Boolean,
    private val onCopied: () -> Unit,
    private val onOpenCurrencyPicker: () -> Unit,
    private val onOpenCustomTip: () -> Unit,
) {
    /**
     * @param showLiveShare in a single column the keyboard hides the result card, so a
     * one-line share appears under the field while it's being edited.
     */
    @Composable
    fun Inputs(showLiveShare: Boolean) {
        var billFocused by remember { mutableStateOf(false) }
        Section(stringResource(R.string.bill_amount)) {
            BillAmountField(
                text = state.billText,
                currency = state.currency,
                onTextChange = actions.onBillChange,
                trailing = { CurrencyChip(state.currency, onClick = onOpenCurrencyPicker) },
                modifier = Modifier.onFocusChanged { billFocused = it.hasFocus },
            )
            AnimatedVisibility(
                visible = showLiveShare && billFocused && state.hasBill,
                enter = fadeIn(tween(Motion.MEDIUM)) + expandVertically(tween(Motion.MEDIUM)),
                exit = fadeOut(tween(Motion.FAST)) + shrinkVertically(tween(Motion.MEDIUM)),
            ) {
                Text(
                    text = stringResource(R.string.live_share, state.currency.format(state.breakdown.perPerson)),
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(start = Spacing.xxs, top = Spacing.xxs),
                )
            }
        }
        Section(stringResource(R.string.tip_percentage)) {
            TipSelector(
                selected = state.tip,
                onPresetSelected = actions.onTipSelected,
                onCustomClick = onOpenCustomTip,
            )
        }
        Section(stringResource(R.string.people)) {
            PeopleStepper(
                count = state.people,
                canDecrease = state.canRemovePerson,
                canIncrease = state.canAddPerson,
                onDecrease = actions.onRemovePerson,
                onIncrease = actions.onAddPerson,
            )
        }
    }

    @Composable
    fun Results(stackActions: Boolean) {
        val focusManager = LocalFocusManager.current
        PerPersonCard(
            breakdown = state.breakdown,
            currency = state.currency,
            hasBill = state.hasBill,
        )
        Section(stringResource(R.string.breakdown)) {
            BreakdownCard(breakdown = state.breakdown, currency = state.currency)
        }
        ShareActions(
            enabled = state.hasBill,
            justCopied = justCopied,
            stacked = stackActions,
            onCopy = {
                actions.onCopy()
                onCopied()
            },
            onShare = actions.onShare,
        )
        ResetButton(onClick = {
            focusManager.clearFocus()
            actions.onReset()
        })
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
            .heightIn(min = 52.dp),
    ) {
        Icon(Icons.Rounded.Refresh, contentDescription = null, modifier = Modifier.size(20.dp))
        Spacer(Modifier.width(Spacing.xs))
        Text(stringResource(R.string.action_reset), style = MaterialTheme.typography.titleSmall)
    }
}

@Preview(name = "Compact 360dp", widthDp = 360, heightDp = 1100)
@Preview(name = "Compact 320dp, 2x font", widthDp = 320, heightDp = 1400, fontScale = 2f)
@Preview(name = "Tablet landscape", widthDp = 1280, heightDp = 800)
@Composable
private fun CalculatorScreenPreview() {
    SplitzyTheme(darkTheme = false) {
        CalculatorScreen(
            state = CalculatorUiState(billText = "250"),
            actions = CalculatorActions(),
            isDarkTheme = false,
        )
    }
}
