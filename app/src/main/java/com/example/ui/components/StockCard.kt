package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.StockProfile

@Composable
fun StockCard(
    stock: StockProfile,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val audit = stock.audit
    val isCompliant = audit.isFullyCompliant
    val isZeroDebt = stock.balanceSheet.isZeroDebt
    val debtRatio = audit.debtRatioPercent
    val isPosChange = stock.change >= 0.0

    val cardBorderColor = when {
        isZeroDebt -> Color(0xFFF59E0B).copy(alpha = 0.6f) // Gold
        isCompliant -> Color(0xFF10B981).copy(alpha = 0.4f) // Emerald
        else -> Color(0xFFEF4444).copy(alpha = 0.35f) // Crimson
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .testTag("stock_card_${stock.symbol}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF0F1E19)
        ),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = androidx.compose.ui.graphics.SolidColor(cardBorderColor)
        )
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Top Row: Symbol, Name, Price, Change
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = stock.symbol,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFFF3F4F6)
                        )

                        // AAOIFI HALAL or HARAM status badge
                        if (isCompliant) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(Color(0xFF064E3B))
                                    .border(1.dp, Color(0xFF10B981), RoundedCornerShape(4.dp))
                                    .padding(horizontal = 7.dp, vertical = 2.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Filled.CheckCircle,
                                        contentDescription = "Halal",
                                        tint = Color(0xFF34D399),
                                        modifier = Modifier.size(11.dp)
                                    )
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text(
                                        text = "HALAL",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Black,
                                        color = Color(0xFF34D399),
                                        letterSpacing = 0.5.sp
                                    )
                                }
                            }
                        } else {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(Color(0xFF450A0A))
                                    .border(1.dp, Color(0xFFEF4444), RoundedCornerShape(4.dp))
                                    .padding(horizontal = 7.dp, vertical = 2.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Filled.Block,
                                        contentDescription = "Haram",
                                        tint = Color(0xFFF87171),
                                        modifier = Modifier.size(11.dp)
                                    )
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text(
                                        text = "HARAM",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Black,
                                        color = Color(0xFFF87171),
                                        letterSpacing = 0.5.sp
                                    )
                                }
                            }
                        }

                        if (isZeroDebt) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(Color(0xFF2E2405))
                                    .border(0.8.dp, Color(0xFFF59E0B), RoundedCornerShape(4.dp))
                                    .padding(horizontal = 5.dp, vertical = 1.5.dp)
                            ) {
                                Text(
                                    text = "0% DEBT",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFF59E0B)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = stock.name,
                        fontSize = 12.sp,
                        color = Color(0xFF9CA3AF),
                        maxLines = 1
                    )
                }

                // Price and Day Change
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "$${String.format("%.2f", stock.currentPrice)}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "${if (isPosChange) "+" else ""}${String.format("%.2f", stock.change)} (${if (isPosChange) "+" else ""}${String.format("%.2f", stock.changePercent)}%)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isPosChange) Color(0xFF34D399) else Color(0xFFEF4444)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Middle: AAOIFI Standard 21 Ratios & Screening Breakdown Pills
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // AAOIFI Debt Ratio Pill (<30%)
                val debtBadgeColor = if (audit.passesDebtScreen) Color(0xFF10B981) else Color(0xFFEF4444)
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(debtBadgeColor.copy(alpha = 0.15f))
                        .border(1.dp, debtBadgeColor.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                        .padding(horizontal = 7.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "Debt: ${String.format("%.1f", debtRatio)}% ${if (audit.passesDebtScreen) "✓" else "✗ ≥30%"}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = debtBadgeColor
                    )
                }

                // AAOIFI Cash Ratio Pill (<30%)
                val cashRatio = stock.balanceSheet.cashToMarketCapPercent
                val cashBadgeColor = if (audit.passesCashScreen) Color(0xFF10B981) else Color(0xFFEF4444)
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(cashBadgeColor.copy(alpha = 0.15f))
                        .border(1.dp, cashBadgeColor.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                        .padding(horizontal = 7.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "Cash: ${String.format("%.1f", cashRatio)}% ${if (audit.passesCashScreen) "✓" else "✗ ≥30%"}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = cashBadgeColor
                    )
                }

                // AAOIFI Status Description
                if (isCompliant) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFF064E3B).copy(alpha = 0.4f))
                            .padding(horizontal = 7.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "AAOIFI Halal",
                            fontSize = 11.sp,
                            color = Color(0xFF34D399),
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1
                        )
                    }
                } else {
                    val violationReason = audit.violations.firstOrNull()?.category?.title ?: "AAOIFI Non-Compliant"
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFF7F1D1D).copy(alpha = 0.4f))
                            .padding(horizontal = 7.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = violationReason,
                            fontSize = 11.sp,
                            color = Color(0xFFFCA5A5),
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Bottom: On-Device AI Predictive Snapshot & Yahoo Source Label
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF071510))
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.AutoAwesome,
                        contentDescription = null,
                        tint = Color(0xFFF59E0B),
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = "AI 30D: ${if (stock.aiPrediction.expectedReturn30dPercent >= 0) "+" else ""}${String.format("%.1f", stock.aiPrediction.expectedReturn30dPercent)}% (${stock.aiPrediction.signal.label})",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(stock.aiPrediction.signal.colorHex)
                    )
                }

                Text(
                    text = "Source: Yahoo Finance",
                    fontSize = 9.sp,
                    color = Color(0xFF6B7280)
                )
            }
        }
    }
}
