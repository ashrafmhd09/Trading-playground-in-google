package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AiPrediction
import com.example.data.model.ChartPoint
import kotlin.math.max
import kotlin.math.min

@Composable
fun AiProjectionChart(
    currentPrice: Double,
    history: List<ChartPoint>,
    prediction: AiPrediction,
    modifier: Modifier = Modifier
) {
    val historyPrices = history.map { it.close }
    val corridor = prediction.projectionCorridor

    val allPrices = mutableListOf<Double>()
    allPrices.addAll(historyPrices)
    corridor.forEach {
        allPrices.add(it.upper95)
        allPrices.add(it.lower95)
        allPrices.add(it.expectedPrice)
    }

    val minPrice = max(0.5, (allPrices.minOrNull() ?: currentPrice) * 0.96)
    val maxPrice = (allPrices.maxOrNull() ?: currentPrice) * 1.04
    val priceRange = max(1.0, maxPrice - minPrice)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF071B14))
            .border(1.dp, Color(0xFF134E39), RoundedCornerShape(16.dp))
            .padding(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "ON-DEVICE AI 30-DAY PREDICTIVE CORRIDOR",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF34D399),
                    letterSpacing = 0.5.sp
                )
                Text(
                    text = "Monte Carlo Stochastic Simulation (95% Confidence)",
                    fontSize = 10.sp,
                    color = Color(0xFF9CA3AF)
                )
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color(prediction.signal.colorHex).copy(alpha = 0.2f))
                    .border(1.dp, Color(prediction.signal.colorHex).copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                    .padding(horizontal = 8.dp, vertical = 3.dp)
            ) {
                Text(
                    text = "${prediction.signal.label} (${prediction.aiConfidenceScore}% Conf)",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(prediction.signal.colorHex)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Canvas Chart
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(170.dp)
        ) {
            val width = size.width
            val height = size.height

            // Split width: 50% history, 50% AI projection corridor
            val historyWidth = width * 0.48f
            val corridorWidth = width * 0.52f

            // Draw horizontal grid lines
            val gridLines = 4
            for (i in 0..gridLines) {
                val y = height * (i.toFloat() / gridLines)
                drawLine(
                    color = Color(0xFF1E3A2F).copy(alpha = 0.4f),
                    start = Offset(0f, y),
                    end = Offset(width, y),
                    strokeWidth = 1f
                )
            }

            // Draw vertical split line separating history from AI forecast
            drawLine(
                color = Color(0xFF34D399).copy(alpha = 0.35f),
                start = Offset(historyWidth, 0f),
                end = Offset(historyWidth, height),
                strokeWidth = 1.5f
            )

            // 1. Draw Historical Price Line
            if (historyPrices.size >= 2) {
                val histPath = Path()
                val stepX = historyWidth / (historyPrices.size - 1)

                for (i in historyPrices.indices) {
                    val p = historyPrices[i]
                    val normY = ((maxPrice - p) / priceRange).toFloat().coerceIn(0f, 1f)
                    val x = i * stepX
                    val y = normY * height

                    if (i == 0) histPath.moveTo(x, y) else histPath.lineTo(x, y)
                }

                drawPath(
                    path = histPath,
                    color = Color(0xFF94A3B8),
                    style = Stroke(width = 2.2f, cap = StrokeCap.Round)
                )
            }

            // 2. Draw 95% Confidence Corridor Area (Upper & Lower 95%)
            if (corridor.isNotEmpty()) {
                val corridorPath = Path()
                val stepX = corridorWidth / corridor.size
                val currentNormY = ((maxPrice - currentPrice) / priceRange).toFloat().coerceIn(0f, 1f)
                val startX = historyWidth
                val startY = currentNormY * height

                // Upper bound
                corridorPath.moveTo(startX, startY)
                for (i in corridor.indices) {
                    val point = corridor[i]
                    val normY = ((maxPrice - point.upper95) / priceRange).toFloat().coerceIn(0f, 1f)
                    val x = startX + (i + 1) * stepX
                    val y = normY * height
                    corridorPath.lineTo(x, y)
                }

                // Lower bound backwards
                for (i in corridor.indices.reversed()) {
                    val point = corridor[i]
                    val normY = ((maxPrice - point.lower95) / priceRange).toFloat().coerceIn(0f, 1f)
                    val x = startX + (i + 1) * stepX
                    val y = normY * height
                    corridorPath.lineTo(x, y)
                }
                corridorPath.close()

                drawPath(
                    path = corridorPath,
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF10B981).copy(alpha = 0.25f),
                            Color(0xFF047857).copy(alpha = 0.08f)
                        )
                    )
                )

                // 3. Draw Expected Price Line
                val expectedPath = Path()
                expectedPath.moveTo(startX, startY)
                for (i in corridor.indices) {
                    val point = corridor[i]
                    val normY = ((maxPrice - point.expectedPrice) / priceRange).toFloat().coerceIn(0f, 1f)
                    val x = startX + (i + 1) * stepX
                    val y = normY * height
                    expectedPath.lineTo(x, y)
                }

                drawPath(
                    path = expectedPath,
                    color = Color(0xFF34D399),
                    style = Stroke(width = 2.5f, cap = StrokeCap.Round)
                )

                // Draw Current Price Dot at split line
                drawCircle(
                    color = Color(0xFF10B981),
                    radius = 5.5f,
                    center = Offset(startX, startY)
                )
                drawCircle(
                    color = Color.White,
                    radius = 2.5f,
                    center = Offset(startX, startY)
                )

                // Draw 30D Target Point
                val lastPoint = corridor.last()
                val targetNormY = ((maxPrice - lastPoint.expectedPrice) / priceRange).toFloat().coerceIn(0f, 1f)
                drawCircle(
                    color = Color(0xFFFBBF24),
                    radius = 5.5f,
                    center = Offset(width - 2f, targetNormY * height)
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Legend & Price Milestones
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF94A3B8))
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("Past 30D", fontSize = 10.sp, color = Color(0xFF9CA3AF))
            }

            Spacer(modifier = Modifier.width(12.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF34D399))
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("AI Expected", fontSize = 10.sp, color = Color(0xFF34D399), fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.width(12.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(Color(0xFF10B981).copy(alpha = 0.4f))
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("95% Range", fontSize = 10.sp, color = Color(0xFF9CA3AF))
            }

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = "30D Target: $${String.format("%.2f", prediction.projectedPrice30d)} (${if (prediction.expectedReturn30dPercent >= 0) "+" else ""}${String.format("%.1f", prediction.expectedReturn30dPercent)}%)",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = if (prediction.expectedReturn30dPercent >= 0) Color(0xFF34D399) else Color(0xFFEF4444)
            )
        }
    }
}
