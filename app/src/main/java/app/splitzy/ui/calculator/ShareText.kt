package app.splitzy.ui.calculator

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import androidx.core.content.getSystemService
import app.splitzy.R

/** Human-friendly summary of the split, ready to paste into a group chat. */
fun buildShareText(resources: Resources, state: CalculatorUiState): String {
    val b = state.breakdown
    val c = state.currency
    return buildString {
        appendLine(resources.getString(R.string.share_heading))
        appendLine()
        appendLine(resources.getString(R.string.share_line, resources.getString(R.string.breakdown_bill), c.format(b.bill)))
        appendLine(
            resources.getString(
                R.string.share_line,
                resources.getString(R.string.breakdown_tip, b.tipPercent.toPlainString()),
                c.format(b.tip),
            ),
        )
        appendLine(resources.getString(R.string.share_line, resources.getString(R.string.breakdown_total), c.format(b.total)))
        appendLine(resources.getString(R.string.share_line, resources.getString(R.string.breakdown_people), b.people.toString()))
        appendLine()
        append(resources.getString(R.string.share_each_pays, c.format(b.perPerson)))
        b.split.roundUp?.let { roundUp ->
            appendLine()
            append(
                resources.getQuantityString(R.plurals.round_up_note, roundUp.people, roundUp.people, c.format(roundUp.amount)),
            )
        }
        appendLine()
        appendLine()
        append(resources.getString(R.string.share_signature))
    }
}

fun Context.shareText(text: String) {
    val send = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, text)
    }
    startActivity(Intent.createChooser(send, getString(R.string.share_chooser_title)))
}

fun Context.copyToClipboard(label: String, text: String) {
    getSystemService<ClipboardManager>()?.setPrimaryClip(ClipData.newPlainText(label, text))
}
