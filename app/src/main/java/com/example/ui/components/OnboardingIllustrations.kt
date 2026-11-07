package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.unit.dp

private val IllustrationBlue = Color(0xFF1D7BF2)
private val IllustrationBlueLight = Color(0xFFE3EFFD)
private val IllustrationBlueMuted = Color(0xFF7CB2F8)

@Composable
fun SlideIllustration1(modifier: Modifier = Modifier) {
    // Phone in hand with banknotes and ruble coin
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val cx = w / 2f
        val cy = h / 2f

        // Outer dashed circular orbit
        drawCircle(
            color = Color(0xFFD4E7FD),
            radius = w * 0.44f,
            center = Offset(cx, cy),
            style = Stroke(
                width = 2.dp.toPx(),
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(14f, 14f), 0f)
            )
        )

        // Sparkle crosses
        drawSparkle(cx - w * 0.35f, cy - h * 0.32f, 8f)
        drawSparkle(cx + w * 0.38f, cy - h * 0.25f, 6f)
        drawSparkle(cx - w * 0.38f, cy + h * 0.18f, 7f)
        drawSparkle(cx + w * 0.35f, cy + h * 0.28f, 8f)

        // Floating banknote on left
        val noteW = w * 0.26f
        val noteH = h * 0.16f
        val noteLeft = cx - w * 0.42f
        val noteTop = cy - h * 0.28f
        drawRoundRect(
            color = IllustrationBlue,
            topLeft = Offset(noteLeft, noteTop),
            size = Size(noteW, noteH),
            cornerRadius = CornerRadius(8f, 8f),
            style = Stroke(width = 3.dp.toPx())
        )
        // Banknote inner rect & circle
        drawRoundRect(
            color = IllustrationBlueMuted,
            topLeft = Offset(noteLeft + 8f, noteTop + 8f),
            size = Size(noteW - 16f, noteH - 16f),
            cornerRadius = CornerRadius(4f, 4f),
            style = Stroke(width = 1.5.dp.toPx())
        )
        drawCircle(
            color = IllustrationBlue,
            radius = noteH * 0.2f,
            center = Offset(noteLeft + noteW / 2f, noteTop + noteH / 2f),
            style = Stroke(width = 2.dp.toPx())
        )

        // Phone outline
        val phoneW = w * 0.34f
        val phoneH = h * 0.52f
        val phoneX = cx - phoneW * 0.35f
        val phoneY = cy - phoneH * 0.5f

        drawRoundRect(
            color = IllustrationBlue,
            topLeft = Offset(phoneX, phoneY),
            size = Size(phoneW, phoneH),
            cornerRadius = CornerRadius(22f, 22f),
            style = Stroke(width = 4.dp.toPx())
        )

        // Phone screen inner lines
        val notchY = phoneY + 16f
        drawLine(
            color = IllustrationBlueMuted,
            start = Offset(phoneX + phoneW * 0.35f, notchY),
            end = Offset(phoneX + phoneW * 0.65f, notchY),
            strokeWidth = 3.dp.toPx(),
            cap = StrokeCap.Round
        )

        // Hand holding the phone
        val handPath = Path().apply {
            moveTo(phoneX - 35f, cy + phoneH * 0.35f)
            cubicTo(
                phoneX - 25f, cy + 10f,
                phoneX - 10f, cy - 20f,
                phoneX + 15f, cy - 40f
            )
            // Thumb gripping
            quadraticBezierTo(phoneX + 28f, cy - 20f, phoneX + 8f, cy + 20f)
            // Fingers curling around right
            moveTo(phoneX + phoneW, cy - 30f)
            cubicTo(
                phoneX + phoneW + 35f, cy - 10f,
                phoneX + phoneW + 30f, cy + 50f,
                phoneX + phoneW - 10f, cy + 70f
            )
            // Wrist
            moveTo(phoneX - 20f, cy + phoneH * 0.5f)
            lineTo(phoneX - 10f, cy + phoneH * 0.5f + 40f)
        }
        drawPath(
            path = handPath,
            color = IllustrationBlue,
            style = Stroke(width = 3.5.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
        )

        // Russian Ruble (₽) coin on bottom right
        val coinRadius = w * 0.15f
        val coinCx = cx + w * 0.28f
        val coinCy = cy + h * 0.25f

        drawCircle(
            color = IllustrationBlue,
            radius = coinRadius,
            center = Offset(coinCx, coinCy),
            style = Stroke(width = 3.5.dp.toPx())
        )
        drawCircle(
            color = IllustrationBlueLight,
            radius = coinRadius - 8f,
            center = Offset(coinCx, coinCy),
            style = Stroke(width = 1.5.dp.toPx())
        )

        // Ruble symbol lines
        val paint = android.graphics.Paint().apply {
            color = android.graphics.Color.parseColor("#1D7BF2")
            textSize = 54f
            isAntiAlias = true
            textAlign = android.graphics.Paint.Align.CENTER
            typeface = android.graphics.Typeface.DEFAULT_BOLD
        }
        drawContext.canvas.nativeCanvas.drawText("₽", coinCx, coinCy + 20f, paint)
    }
}

@Composable
fun SlideIllustration2(modifier: Modifier = Modifier) {
    // Circular security shield with labyrinth and digital circuit rings
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val cx = w / 2f
        val cy = h / 2f

        // Concentric circular orbit lines (thick & fine)
        drawCircle(
            color = IllustrationBlue,
            radius = w * 0.42f,
            center = Offset(cx, cy),
            style = Stroke(width = 3.5.dp.toPx())
        )
        drawCircle(
            color = Color(0xFFD4E7FD),
            radius = w * 0.46f,
            center = Offset(cx, cy),
            style = Stroke(
                width = 2.dp.toPx(),
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 16f), 0f)
            )
        )

        // Labyrinth-like circuit arcs
        val arcRadius1 = w * 0.35f
        drawArc(
            color = IllustrationBlue,
            startAngle = 30f,
            sweepAngle = 100f,
            useCenter = false,
            topLeft = Offset(cx - arcRadius1, cy - arcRadius1),
            size = Size(arcRadius1 * 2, arcRadius1 * 2),
            style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
        )
        drawArc(
            color = IllustrationBlue,
            startAngle = 160f,
            sweepAngle = 190f,
            useCenter = false,
            topLeft = Offset(cx - arcRadius1, cy - arcRadius1),
            size = Size(arcRadius1 * 2, arcRadius1 * 2),
            style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
        )

        // Inner circle
        val arcRadius2 = w * 0.28f
        drawCircle(
            color = IllustrationBlueMuted,
            radius = arcRadius2,
            center = Offset(cx, cy),
            style = Stroke(width = 2.dp.toPx())
        )

        // Shield in center
        val shieldW = w * 0.26f
        val shieldH = h * 0.32f
        val shieldPath = Path().apply {
            moveTo(cx, cy - shieldH / 2f)
            lineTo(cx + shieldW / 2f, cy - shieldH / 2f + 12f)
            quadraticBezierTo(
                cx + shieldW / 2f, cy + shieldH * 0.15f,
                cx, cy + shieldH / 2f
            )
            quadraticBezierTo(
                cx - shieldW / 2f, cy + shieldH * 0.15f,
                cx - shieldW / 2f, cy - shieldH / 2f + 12f
            )
            close()
        }

        drawPath(
            path = shieldPath,
            color = IllustrationBlue,
            style = Stroke(width = 3.5.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
        )

        // Inner shield contour
        val innerShieldPath = Path().apply {
            moveTo(cx, cy - shieldH / 2f + 14f)
            lineTo(cx + shieldW / 2f - 10f, cy - shieldH / 2f + 22f)
            quadraticBezierTo(
                cx + shieldW / 2f - 10f, cy + shieldH * 0.12f,
                cx, cy + shieldH / 2f - 14f
            )
            quadraticBezierTo(
                cx - shieldW / 2f + 10f, cy + shieldH * 0.12f,
                cx - shieldW / 2f + 10f, cy - shieldH / 2f + 22f
            )
            close()
        }
        drawPath(
            path = innerShieldPath,
            color = IllustrationBlueMuted,
            style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
        )

        // Sparkles and circuit dots
        drawCircle(color = IllustrationBlue, radius = 5f, center = Offset(cx - w * 0.22f, cy - h * 0.22f))
        drawCircle(color = IllustrationBlue, radius = 6f, center = Offset(cx + w * 0.24f, cy - h * 0.18f))
        drawCircle(color = IllustrationBlue, radius = 5f, center = Offset(cx - w * 0.26f, cy + h * 0.16f))
        drawCircle(color = IllustrationBlue, radius = 5f, center = Offset(cx + w * 0.25f, cy + h * 0.2f))

        drawSparkle(cx - w * 0.38f, cy - h * 0.32f, 7f)
        drawSparkle(cx + w * 0.38f, cy - h * 0.26f, 8f)
        drawSparkle(cx - w * 0.38f, cy + h * 0.25f, 6f)
        drawSparkle(cx + w * 0.38f, cy + h * 0.28f, 7f)
    }
}

@Composable
fun SlideIllustration3(modifier: Modifier = Modifier) {
    // Circle with "RUB TJS", bar chart columns and upward growth line with arrow
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val cx = w / 2f
        val cy = h / 2f

        // Outer dashed circle
        drawCircle(
            color = Color(0xFFD4E7FD),
            radius = w * 0.44f,
            center = Offset(cx, cy),
            style = Stroke(
                width = 2.dp.toPx(),
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 16f), 0f)
            )
        )

        // Background vertical bar charts (light blue)
        val barW = 10f
        val bars = listOf(
            Pair(cx - w * 0.38f, 70f),
            Pair(cx - w * 0.30f, 120f),
            Pair(cx - w * 0.22f, 90f),
            Pair(cx + w * 0.22f, 110f),
            Pair(cx + w * 0.30f, 140f),
            Pair(cx + w * 0.38f, 180f)
        )
        bars.forEach { (bx, bh) ->
            drawLine(
                color = Color(0xFFE2EDFD),
                start = Offset(bx, cy + 80f),
                end = Offset(bx, cy + 80f - bh),
                strokeWidth = barW,
                cap = StrokeCap.Round
            )
        }

        // Center main circle with RUB / TJS text
        val mainRadius = w * 0.28f
        drawCircle(
            color = IllustrationBlue,
            radius = mainRadius,
            center = Offset(cx, cy),
            style = Stroke(width = 3.5.dp.toPx())
        )

        // Upward zigzag growth line
        val chartPath = Path().apply {
            moveTo(cx - w * 0.38f, cy + 40f)
            lineTo(cx - w * 0.22f, cy - 20f)
            lineTo(cx - w * 0.08f, cy + 25f)
            lineTo(cx + w * 0.12f, cy - 60f)
            lineTo(cx + w * 0.26f, cy - 35f)
            lineTo(cx + w * 0.42f, cy - 110f)
        }
        drawPath(
            path = chartPath,
            color = IllustrationBlue,
            style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
        )

        // Arrow head on growth line
        val arrowTip = Offset(cx + w * 0.42f, cy - 110f)
        val arrowPath = Path().apply {
            moveTo(arrowTip.x, arrowTip.y)
            lineTo(arrowTip.x - 22f, arrowTip.y + 6f)
            moveTo(arrowTip.x, arrowTip.y)
            lineTo(arrowTip.x - 6f, arrowTip.y + 22f)
        }
        drawPath(
            path = arrowPath,
            color = IllustrationBlue,
            style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
        )

        // Draw "RUB" and "TJS" text inside center circle
        val paintRub = android.graphics.Paint().apply {
            color = android.graphics.Color.parseColor("#1D7BF2")
            textSize = 52f
            isAntiAlias = true
            textAlign = android.graphics.Paint.Align.CENTER
            typeface = android.graphics.Typeface.DEFAULT_BOLD
        }
        val paintTjs = android.graphics.Paint().apply {
            color = android.graphics.Color.parseColor("#1D7BF2")
            textSize = 52f
            isAntiAlias = true
            textAlign = android.graphics.Paint.Align.CENTER
            typeface = android.graphics.Typeface.DEFAULT_BOLD
        }
        drawContext.canvas.nativeCanvas.drawText("RUB", cx, cy - 12f, paintRub)
        drawContext.canvas.nativeCanvas.drawText("TJS", cx, cy + 48f, paintTjs)

        // Sparkles
        drawSparkle(cx - w * 0.35f, cy - h * 0.32f, 7f)
        drawSparkle(cx + w * 0.34f, cy + h * 0.32f, 8f)
        drawSparkle(cx - w * 0.40f, cy + h * 0.20f, 6f)
    }
}

private fun DrawScope.drawSparkle(x: Float, y: Float, arm: Float) {
    val path = Path().apply {
        moveTo(x, y - arm)
        lineTo(x, y + arm)
        moveTo(x - arm, y)
        lineTo(x + arm, y)
    }
    drawPath(path, color = IllustrationBlueMuted, style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round))
}
