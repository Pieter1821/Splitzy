package app.splitzy.ui.brand

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Text
import app.splitzy.R
import app.splitzy.ui.theme.BrandColors
import app.splitzy.ui.theme.PlusJakartaSans

/** The Splitzy symbol on its navy tile, as used for the app icon. */
@Composable
fun SplitzyMark(modifier: Modifier = Modifier, size: Dp = 40.dp) {
    Box(
        modifier = modifier
            .size(size)
            .background(BrandColors.Navy, RoundedCornerShape(size * 0.28f)),
        contentAlignment = Alignment.Center,
    ) {
        Image(
            painter = painterResource(R.drawable.ic_splitzy_mark),
            contentDescription = null,
            modifier = Modifier.size(size * 0.92f),
        )
    }
}

/** "Split" + teal "zy", underlined by the brand swoosh. */
@Composable
fun SplitzyWordmark(
    modifier: Modifier = Modifier,
    color: Color = BrandColors.White,
    accent: Color = BrandColors.Teal,
    fontSize: Int = 26,
) {
    val text = buildAnnotatedString {
        append("Split")
        withStyle(SpanStyle(color = accent)) { append("zy") }
    }
    Text(
        text = text,
        color = color,
        style = TextStyle(
            fontFamily = PlusJakartaSans,
            fontWeight = FontWeight.ExtraBold,
            fontSize = fontSize.sp,
            letterSpacing = (-0.03).em,
        ),
        modifier = modifier
            .padding(bottom = 4.dp)
            .drawBehind { drawSwoosh(accent) },
    )
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawSwoosh(accent: Color) {
    val start = Offset(size.width * 0.42f, size.height + 1.dp.toPx())
    val end = Offset(size.width * 1.0f, size.height - 3.dp.toPx())
    val swoosh = Path().apply {
        moveTo(start.x, start.y)
        quadraticTo(size.width * 0.72f, size.height + 2.dp.toPx(), end.x, end.y)
    }
    drawPath(swoosh, accent, style = Stroke(width = 2.5.dp.toPx(), cap = StrokeCap.Round))
}

/** Primary horizontal logo: symbol + wordmark. */
@Composable
fun SplitzyLogo(
    modifier: Modifier = Modifier,
    wordmarkColor: Color = BrandColors.White,
) {
    Row(
        modifier = modifier.clearAndSetSemantics { contentDescription = "Splitzy" },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        SplitzyMark(size = 40.dp)
        Spacer(Modifier.width(10.dp))
        SplitzyWordmark(color = wordmarkColor)
    }
}
