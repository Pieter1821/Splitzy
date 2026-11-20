package app.splitzy.ui.calculator.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.splitzy.R
import app.splitzy.domain.BillBreakdown
import app.splitzy.domain.SplitCurrency
import app.splitzy.ui.theme.FigureStyles
import app.splitzy.ui.theme.Motion
import app.splitzy.ui.theme.Spacing
import java.math.BigDecimal

/** The hero of the screen: what each person owes, on the brand's mint card. */
@Composable
fun PerPersonCard(
    breakdown: BillBreakdown,
    currency: SplitCurrency,
    hasBill: Boolean,
    modifier: Modifier = Modifier,
) {
    val onCard = MaterialTheme.colorScheme.onPrimaryContainer
    val amountText = currency.format(breakdown.perPerson)
    val a11y = stringResource(R.string.result_a11y, amountText)

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.primaryContainer,
        contentColor = onCard,
    ) {
        Box {
            Sparkle(
                color = onCard.copy(alpha = if (hasBill) 0.55f else 0f),
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = Spacing.md, end = Spacing.md)
                    .size(22.dp),
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Spacing.lg, vertical = Spacing.lg)
                    .clearAndSetSemantics {
                        contentDescription = a11y
                        liveRegion = LiveRegionMode.Polite
                    },
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                AnimatedContent(
                    targetState = breakdown.perPerson,
                    transitionSpec = {
                        val up = targetState > initialState
                        val enter = slideInVertically(tween(Motion.MEDIUM)) { if (up) it / 2 else -it / 2 } +
                            fadeIn(tween(Motion.MEDIUM))
                        val exit = slideOutVertically(tween(Motion.MEDIUM)) { if (up) -it / 2 else it / 2 } +
                            fadeOut(tween(Motion.FAST))
                        (enter togetherWith exit).using(SizeTransform(clip = false))
                    },
                    label = "perPerson",
                ) { amount: BigDecimal ->
                    BasicText(
                        text = currency.format(amount),
                        style = FigureStyles.Hero.copy(
                            color = if (hasBill) onCard else onCard.copy(alpha = 0.45f),
                            textAlign = TextAlign.Center,
                        ),
                        maxLines = 1,
                        autoSize = TextAutoSize.StepBased(minFontSize = 24.sp, maxFontSize = FigureStyles.Hero.fontSize),
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
                Text(
                    text = if (hasBill) stringResource(R.string.per_person) else stringResource(R.string.result_empty_hint),
                    style = MaterialTheme.typography.titleMedium,
                )
                val roundUp = breakdown.split.roundUp
                AnimatedVisibility(
                    visible = hasBill && roundUp != null,
                    enter = fadeIn(tween(Motion.SLOW)) + expandVertically(tween(Motion.SLOW)),
                    exit = fadeOut(tween(Motion.FAST)) + shrinkVertically(tween(Motion.SLOW)),
                ) {
                    if (roundUp != null) {
                        Text(
                            text = pluralStringResource(
                                R.plurals.round_up_note,
                                roundUp.people,
                                roundUp.people,
                                currency.format(roundUp.amount),
                            ),
                            style = MaterialTheme.typography.bodySmall,
                            color = onCard.copy(alpha = 0.75f),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(top = Spacing.xs),
                        )
                    }
                }
            }
        }
    }
}

/** Three short strokes, the "celebration" accent from the brand board. */
@Composable
private fun Sparkle(color: Color, modifier: Modifier = Modifier) {
    Canvas(modifier) {
        val stroke = 2.5.dp.toPx()
        val w = size.width
        val h = size.height
        drawLine(color, Offset(w * 0.15f, h * 0.05f), Offset(w * 0.25f, h * 0.38f), stroke, StrokeCap.Round)
        drawLine(color, Offset(w * 0.55f, h * 0.30f), Offset(w * 0.92f, h * 0.12f), stroke, StrokeCap.Round)
        drawLine(color, Offset(w * 0.55f, h * 0.62f), Offset(w * 0.95f, h * 0.72f), stroke, StrokeCap.Round)
    }
}
