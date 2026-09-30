package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ExclusionCategory
import com.example.data.model.StockProfile
import kotlin.math.max

@Composable
fun StockDetailSheet(
    stock: StockProfile,
    stressDebtDelta: Double,
    onStressDebtDeltaChange: (Double) -> Unit,
    purificationSharesInput: String,
    onPurificationSharesChange: (String) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bs = stock.balanceSheet
    val audit = stock.audit
    val ai = stock.aiPrediction
    val isPosChange = stock.change >= 0.0

    val shares = purificationSharesInput.toDoubleOrNull() ?: 0.0
    val annualDividendPerShare = stock.currentPrice * (stock.dividendYieldPercent / 100.0)
    val totalAnnualDividend = shares * annualDividendPerShare
    val purificationDollarAmount = totalAnnualDividend * (audit.nonOperatingInterestIncomePercent / 100.0)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight(0.92f)
            .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
            .background(Color(0xFF091712))
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState())
            .testTag("stock_detail_sheet")
    ) {
        Spacer(modifier = Modifier.height(10.dp))

        // Drag handle indicator
        Box(
            modifier = Modifier
                .size(width = 36.dp, height = 4.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(Color(0xFF374151))
                .align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Header with Close
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = stock.symbol,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                    if (bs.isZeroDebt) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color(0xFF2E2405))
                                .border(0.8.dp, Color(0xFFF59E0B), RoundedCornerShape(4.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text("ZERO DEBT", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFFF59E0B))
                        }
                    }
                }
                Text(
                    text = "${stock.name} • ${stock.industry}",
                    fontSize = 12.sp,
                    color = Color(0xFF9CA3AF)
                )
            }

            IconButton(
                onClick = onDismiss,
                modifier = Modifier.testTag("button_close_detail")
            ) {
                Icon(Icons.Filled.Close, contentDescription = "Close", tint = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // AAOIFI Shariah Standard No. 21 Verdict Banner
        val isHalal = audit.isFullyCompliant
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(if (isHalal) Color(0xFF064E3B) else Color(0xFF450A0A))
                .border(
                    1.2.dp,
                    if (isHalal) Color(0xFF10B981) else Color(0xFFEF4444),
                    RoundedCornerShape(12.dp)
                )
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                Icon(
                    imageVector = if (isHalal) Icons.Filled.CheckCircle else Icons.Filled.Block,
                    contentDescription = null,
                    tint = if (isHalal) Color(0xFF34D399) else Color(0xFFF87171),
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = if (isHalal) "AAOIFI CLASSIFICATION: HALAL" else "AAOIFI CLASSIFICATION: HARAM",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Black,
                        color = if (isHalal) Color(0xFF34D399) else Color(0xFFF87171),
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = if (isHalal) "Meets all financial ratio thresholds (<30% debt, <30% cash, <70% liquid) and sector rules under AAOIFI Standard 21."
                        else (audit.violations.firstOrNull()?.reason ?: "Fails AAOIFI Standard No. 21 criteria."),
                        fontSize = 11.sp,
                        color = if (isHalal) Color(0xFFD1FAE5) else Color(0xFFFEE2E2),
                        lineHeight = 15.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Price Hero Banner
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFF0F261E))
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "$${String.format("%.2f", stock.currentPrice)}",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )
                Text(
                    text = "${if (isPosChange) "+" else ""}${String.format("%.2f", stock.change)} (${if (isPosChange) "+" else ""}${String.format("%.2f", stock.changePercent)}%) Today",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isPosChange) Color(0xFF34D399) else Color(0xFFEF4444)
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(text = "Market Cap", fontSize = 10.sp, color = Color(0xFF9CA3AF))
                Text(
                    text = "$${String.format("%.1fB", bs.marketCapUsd / 1_000_000_000.0)}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFE5E7EB)
                )
                Text(
                    text = "Source: Yahoo Finance",
                    fontSize = 9.sp,
                    color = Color(0xFF10B981)
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 1. YAHOO FINANCE BALANCE SHEET SECTION
        Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0E2019)),
            border = CardDefaults.outlinedCardBorder().copy(
                brush = androidx.compose.ui.graphics.SolidColor(Color(0xFF134E39))
            )
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "YAHOO FINANCE BALANCE SHEET",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF34D399),
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = bs.reportedPeriod,
                        fontSize = 10.sp,
                        color = Color(0xFF9CA3AF)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Progress Bar: Debt vs 30% Threshold
                val debtProgress = (audit.debtRatioPercent / 50.0).toFloat().coerceIn(0f, 1f)
                val debtColor = if (audit.passesDebtScreen) Color(0xFF10B981) else Color(0xFFEF4444)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Debt / Market Cap Ratio: ${String.format("%.2f", audit.debtRatioPercent)}%",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = debtColor
                    )
                    Text(
                        text = "Threshold: < 30.0%",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFF59E0B)
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))
                LinearProgressIndicator(
                    progress = { debtProgress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = debtColor,
                    trackColor = Color(0xFF1F352C)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Balance Sheet Metrics Grid
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    BalanceMetricItem("Total Debt", "$${String.format("%.2fB", bs.totalDebtUsd / 1_000_000_000.0)}")
                    BalanceMetricItem("Cash & Equiv", "$${String.format("%.2fB", bs.totalCashUsd / 1_000_000_000.0)}")
                    BalanceMetricItem("Net Debt", "$${String.format("%.2fB", bs.netDebtUsd / 1_000_000_000.0)}")
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    BalanceMetricItem("Total Assets", "$${String.format("%.1fB", bs.totalAssetsUsd / 1_000_000_000.0)}")
                    BalanceMetricItem("Operating Cashflow", "$${String.format("%.1fB", bs.operatingCashflowUsd / 1_000_000_000.0)}")
                    BalanceMetricItem("Debt / Assets", "${String.format("%.1f", bs.debtToAssetsPercent)}%")
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 2. AAOIFI SHARIAH STANDARD NO. 21 SCREENING AUDIT
        Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0E2019)),
            border = CardDefaults.outlinedCardBorder().copy(
                brush = androidx.compose.ui.graphics.SolidColor(Color(0xFF134E39))
            )
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "AAOIFI STANDARD NO. 21 AUDIT",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFF59E0B),
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = if (isHalal) "STATUS: HALAL" else "STATUS: HARAM",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (isHalal) Color(0xFF34D399) else Color(0xFFEF4444)
                    )
                }
                Text(
                    text = "Accounting and Auditing Organization for Islamic Financial Institutions (AAOIFI) benchmark evaluation.",
                    fontSize = 10.sp,
                    color = Color(0xFF9CA3AF)
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "QUANTITATIVE FINANCIAL RATIO SCREENS",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF34D399),
                    letterSpacing = 0.5.sp
                )
                Spacer(modifier = Modifier.height(6.dp))

                val financialRatios = listOf(
                    Triple(
                        "1. Debt / Market Cap (< 30.0%)",
                        audit.passesDebtScreen,
                        "Calculated: ${String.format("%.2f", audit.debtRatioPercent)}% (Ceiling: < 30.0%)"
                    ),
                    Triple(
                        "2. Cash & Deposits / Market Cap (< 30.0%)",
                        audit.passesCashScreen,
                        "Calculated: ${String.format("%.2f", bs.cashToMarketCapPercent)}% (Ceiling: < 30.0%)"
                    ),
                    Triple(
                        "3. Liquid Assets / Total Assets (< 70.0%)",
                        audit.passesLiquidityScreen,
                        "Calculated: ${String.format("%.2f", bs.liquidAssetsToAssetsPercent)}% (Ceiling: < 70.0%)"
                    ),
                    Triple(
                        "4. Impermissible Income / Revenue (< 5.0%)",
                        audit.passesIncomeScreen,
                        "Calculated: ${String.format("%.2f", audit.impermissibleIncomePercent)}% (Ceiling: < 5.0%)"
                    )
                )

                financialRatios.forEach { (title, passed, desc) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (passed) Icons.Filled.CheckCircle else Icons.Filled.Block,
                            contentDescription = null,
                            tint = if (passed) Color(0xFF10B981) else Color(0xFFEF4444),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = title, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                            Text(text = desc, fontSize = 10.sp, color = Color(0xFF9CA3AF))
                        }
                        Text(
                            text = if (passed) "PASS" else "FAIL",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (passed) Color(0xFF10B981) else Color(0xFFEF4444)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(Color(0xFF1F352C)))
                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "SECTOR & BUSINESS ACTIVITY EXCLUSIONS",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF34D399),
                    letterSpacing = 0.5.sp
                )
                Spacer(modifier = Modifier.height(6.dp))

                val sectorExclusions = listOf(
                    Triple("Interest-Based Banking (Riba)", audit.violations.none { it.category == ExclusionCategory.INTEREST_BASED }, "Conventional lending or debt instruments prohibited"),
                    Triple("Gambling & Casinos (Maysir)", audit.violations.none { it.category == ExclusionCategory.GAMBLING }, "Zero tolerance for wagering or gaming operations"),
                    Triple("Alcohol & Breweries (Khamr)", audit.violations.none { it.category == ExclusionCategory.ALCOHOL }, "Prohibition of alcoholic beverages and spirits"),
                    Triple("Pork & Non-Halal Meat", audit.violations.none { it.category == ExclusionCategory.PORK }, "No pork processing or swine derivatives"),
                    Triple("Tobacco & Nicotine", audit.violations.none { it.category == ExclusionCategory.TOBACCO }, "Zero revenue from cigarette or nicotine manufacturing"),
                    Triple("Defense & Weapons Systems", audit.violations.none { it.category == ExclusionCategory.DEFENSE }, "No military weapons, munitions, or warfare technology"),
                    Triple("Human Rights & Sanctions", audit.violations.none { it.category == ExclusionCategory.HUMAN_RIGHTS }, "Passed international ethical and labor checks")
                )

                sectorExclusions.forEach { (title, passed, desc) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (passed) Icons.Filled.CheckCircle else Icons.Filled.Block,
                            contentDescription = null,
                            tint = if (passed) Color(0xFF10B981) else Color(0xFFEF4444),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = title, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                            Text(text = desc, fontSize = 10.sp, color = Color(0xFF9CA3AF))
                        }
                        Text(
                            text = if (passed) "PASS" else "HARAM",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (passed) Color(0xFF10B981) else Color(0xFFEF4444)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 3. ON-DEVICE AI PREDICTIVE ANALYTICS
        AiProjectionChart(
            currentPrice = stock.currentPrice,
            history = stock.chartHistory,
            prediction = ai
        )

        Spacer(modifier = Modifier.height(12.dp))

        // AI Key Targets & Rationale
        Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0B1F17)),
            border = CardDefaults.outlinedCardBorder().copy(
                brush = androidx.compose.ui.graphics.SolidColor(Color(0xFF10B981).copy(alpha = 0.4f))
            )
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.AutoAwesome, contentDescription = null, tint = Color(0xFFF59E0B), modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "ON-DEVICE AI PREDICTIVE TARGETS",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF34D399)
                        )
                    }

                    Text(
                        text = "Confidence: ${ai.aiConfidenceScore}%",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFF59E0B)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    AiTargetBox("7-Day Target", "$${String.format("%.2f", ai.projectedPrice7d)}", "${if (ai.expectedReturn7dPercent >= 0) "+" else ""}${String.format("%.1f", ai.expectedReturn7dPercent)}%")
                    AiTargetBox("30-Day Target", "$${String.format("%.2f", ai.projectedPrice30d)}", "${if (ai.expectedReturn30dPercent >= 0) "+" else ""}${String.format("%.1f", ai.expectedReturn30dPercent)}%")
                    AiTargetBox("Bullish 95%", "$${String.format("%.2f", ai.bullishTargetPrice)}", "Upper Range")
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "AI Rationale: ${ai.aiExplanation}",
                    fontSize = 11.sp,
                    color = Color(0xFFD1D5DB),
                    lineHeight = 16.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 4. REAL-TIME DEBT STRESS TEST SLIDER (Requirement 4)
        Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0E2019)),
            border = CardDefaults.outlinedCardBorder().copy(
                brush = androidx.compose.ui.graphics.SolidColor(Color(0xFFF59E0B).copy(alpha = 0.5f))
            )
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "REAL-TIME DEBT SENSITIVITY STRESS TEST",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFF59E0B)
                    )
                    Text(
                        text = "+${String.format("%.1f", stressDebtDelta)}% Debt",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Text(
                    text = "Simulate additional debt leverage to predict impact on 30% compliance and AI valuation.",
                    fontSize = 10.sp,
                    color = Color(0xFF9CA3AF)
                )

                Slider(
                    value = stressDebtDelta.toFloat(),
                    onValueChange = { onStressDebtDeltaChange(it.toDouble()) },
                    valueRange = 0f..25f,
                    colors = SliderDefaults.colors(
                        thumbColor = Color(0xFFF59E0B),
                        activeTrackColor = Color(0xFFF59E0B),
                        inactiveTrackColor = Color(0xFF1F352C)
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("stress_test_slider")
                )

                val simulatedTotalDebtRatio = bs.debtToMarketCapPercent + stressDebtDelta
                val staysCompliant = simulatedTotalDebtRatio < 30.0

                val isSectorExcluded = audit.violations.any {
                    it.category != ExclusionCategory.EXCESSIVE_DEBT &&
                            it.category != ExclusionCategory.EXCESSIVE_CASH &&
                            it.category != ExclusionCategory.EXCESSIVE_RECEIVABLES &&
                            it.category != ExclusionCategory.IMPERMISSIBLE_INCOME
                }
                val staysHalal = staysCompliant && !isSectorExcluded && audit.passesCashScreen && audit.passesLiquidityScreen

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (staysHalal) Color(0xFF064E3B).copy(alpha = 0.4f) else Color(0xFF7F1D1D).copy(alpha = 0.4f))
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Simulated Debt: ${String.format("%.1f", simulatedTotalDebtRatio)}%",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = if (staysHalal) "✓ HALAL (<30% AAOIFI)" else "✗ HARAM (≥30% AAOIFI)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (staysHalal) Color(0xFF34D399) else Color(0xFFEF4444)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 5. REAL-TIME DIVIDEND PURIFICATION CALCULATOR (AAOIFI Standard 21)
        Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0E2019)),
            border = CardDefaults.outlinedCardBorder().copy(
                brush = androidx.compose.ui.graphics.SolidColor(Color(0xFF134E39))
            )
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "AAOIFI DIVIDEND PURIFICATION CALCULATOR",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF34D399)
                )
                Text(
                    text = "Per AAOIFI Standard No. 21, Halal dividend distributions must be purified by calculating and donating the incidental interest income portion (${String.format("%.2f", audit.nonOperatingInterestIncomePercent)}%) to approved charity.",
                    fontSize = 10.sp,
                    color = Color(0xFF9CA3AF)
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = purificationSharesInput,
                        onValueChange = onPurificationSharesChange,
                        label = { Text("Shares Owned", fontSize = 11.sp) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF10B981),
                            unfocusedBorderColor = Color(0xFF374151),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        modifier = Modifier.weight(1f).testTag("input_purification_shares")
                    )

                    Column(
                        modifier = Modifier
                            .weight(1.2f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF071B14))
                            .border(1.dp, Color(0xFF134E39), RoundedCornerShape(8.dp))
                            .padding(10.dp)
                    ) {
                        Text(text = "Charity Donation Due", fontSize = 10.sp, color = Color(0xFF9CA3AF))
                        Text(
                            text = "$${String.format("%.2f", purificationDollarAmount)}",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFF59E0B)
                        )
                        Text(
                            text = "from $${String.format("%.2f", totalAnnualDividend)} dividend",
                            fontSize = 9.sp,
                            color = Color(0xFF6B7280)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun BalanceMetricItem(label: String, value: String) {
    Column {
        Text(text = label, fontSize = 10.sp, color = Color(0xFF9CA3AF))
        Text(text = value, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
    }
}

@Composable
private fun AiTargetBox(title: String, price: String, pct: String) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFF071510))
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Text(text = title, fontSize = 9.sp, color = Color(0xFF9CA3AF))
        Text(text = price, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
        Text(text = pct, fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF34D399))
    }
}
