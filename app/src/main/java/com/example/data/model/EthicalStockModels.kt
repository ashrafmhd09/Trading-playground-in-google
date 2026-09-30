package com.example.data.model

import androidx.compose.ui.graphics.Color
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocalBar
import androidx.compose.material.icons.filled.MoneyOff
import androidx.compose.material.icons.filled.NoDrinks
import androidx.compose.material.icons.filled.Report
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.SmokingRooms
import androidx.compose.material.icons.filled.Warning
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Historical daily price point from Yahoo Finance chart.
 */
data class ChartPoint(
    val timestamp: Long,
    val dateLabel: String,
    val open: Double,
    val high: Double,
    val low: Double,
    val close: Double,
    val volume: Long
)

/**
 * Real balance sheet figures sourced from Yahoo Finance with AAOIFI Standard 21 metrics.
 */
data class YahooBalanceSheet(
    val totalDebtUsd: Double,
    val totalCashUsd: Double,
    val totalAssetsUsd: Double,
    val totalEquityUsd: Double,
    val operatingCashflowUsd: Double,
    val totalRevenueUsd: Double,
    val marketCapUsd: Double,
    val receivablesUsd: Double = 0.0,
    val interestIncomeUsd: Double = 0.0,
    val reportedPeriod: String = "FY2024 / Q3-2024",
    val dataSource: String = "Yahoo Finance (balanceSheetHistory & financialData)"
) {
    val netDebtUsd: Double get() = totalDebtUsd - totalCashUsd

    // AAOIFI Ratio 1: Total Debt to Market Capitalization (< 30.0%)
    val debtToMarketCapPercent: Double
        get() = if (marketCapUsd > 0) (totalDebtUsd / marketCapUsd) * 100.0 else 0.0

    // AAOIFI Ratio 2: Cash & Interest-Bearing Deposits/Securities to Market Capitalization (< 30.0%)
    val cashToMarketCapPercent: Double
        get() = if (marketCapUsd > 0) (totalCashUsd / marketCapUsd) * 100.0 else 0.0

    // AAOIFI Ratio 3: Liquidity / Receivables Screening (Cash + Receivables / Total Assets < 70.0%)
    val liquidAssetsToAssetsPercent: Double
        get() = if (totalAssetsUsd > 0) ((totalCashUsd + receivablesUsd) / totalAssetsUsd) * 100.0 else 0.0

    // AAOIFI Ratio 4: Impermissible / Non-Operating Interest Income to Total Revenue (< 5.0%)
    val interestIncomeToRevenuePercent: Double
        get() = if (totalRevenueUsd > 0) (interestIncomeUsd / totalRevenueUsd) * 100.0 else 0.0

    val impermissibleIncomeToRevenuePercent: Double
        get() = interestIncomeToRevenuePercent

    // Secondary ratio: Total Debt to Total Assets
    val debtToAssetsPercent: Double
        get() = if (totalAssetsUsd > 0) (totalDebtUsd / totalAssetsUsd) * 100.0 else 0.0

    // Cash coverage ratio
    val cashToDebtRatio: Double
        get() = if (totalDebtUsd > 0) totalCashUsd / totalDebtUsd else 999.0

    val isZeroDebt: Boolean get() = totalDebtUsd <= 0.0
}

/**
 * AAOIFI Standard No. 21 Compliance Status.
 */
enum class AaoifiStatus(val label: String, val isHalal: Boolean) {
    HALAL("HALAL", true),
    HARAM("HARAM", false)
}

/**
 * Prohibited sectors and ethical violation categories under AAOIFI Shariah Standard 21.
 */
enum class ExclusionCategory(
    val title: String,
    val description: String
) {
    GAMBLING("Gambling & Casinos (Maysir)", "Casinos, sports betting, and lottery operations"),
    INTEREST_BASED("Interest-Based Finance (Riba)", "Commercial banks, credit card networks, and conventional lending"),
    TOBACCO("Tobacco & Nicotine", "Cigarette manufacturing, vaping, and tobacco supply chains"),
    PORK("Pork & Non-Halal Products", "Swine meat processing and non-permissible animal derivatives"),
    ALCOHOL("Alcohol & Distilleries (Khamr)", "Beer, liquor, wine breweries and alcoholic beverage distribution"),
    DEFENSE("Defense & Weapons", "Military arms, missile systems, cluster munitions, and warfare tech"),
    HUMAN_RIGHTS("Human Rights Violations", "Forced labor, severe supply chain abuse, or international sanctions"),
    EXCESSIVE_DEBT("Excessive Debt (≥30%)", "Interest-bearing debt exceeds 30.0% of market capitalization under AAOIFI Standard 21"),
    EXCESSIVE_CASH("Excessive Cash/Deposits (≥30%)", "Interest-bearing cash and deposits exceed 30.0% of market cap under AAOIFI Standard 21"),
    EXCESSIVE_RECEIVABLES("Excessive Liquid Assets (≥70%)", "Cash and receivables exceed 70.0% of total assets under AAOIFI Standard 21"),
    IMPERMISSIBLE_INCOME("Impermissible Income (≥5%)", "Non-operating interest or impermissible revenue exceeds 5.0% under AAOIFI Standard 21")
}

data class ExclusionDetail(
    val category: ExclusionCategory,
    val reason: String,
    val severityLevel: String = "STRICT_EXCLUSION"
)

/**
 * AAOIFI Shariah Standard No. 21 Screening Result.
 */
data class EthicalAuditResult(
    val aaoifiStatus: AaoifiStatus,
    val debtRatioPercent: Double,
    val cashRatioPercent: Double = 0.0,
    val liquidityRatioPercent: Double = 0.0,
    val impermissibleIncomePercent: Double = 0.0,
    val passesDebtScreen: Boolean, // < 30.0%
    val passesCashScreen: Boolean = true, // < 30.0%
    val passesLiquidityScreen: Boolean = true, // < 70.0%
    val passesIncomeScreen: Boolean = true, // < 5.0%
    val violations: List<ExclusionDetail>,
    val passesSectorExclusions: Boolean,
    val nonOperatingInterestIncomePercent: Double = 0.0 // For dividend purification
) {
    val isFullyCompliant: Boolean get() = aaoifiStatus == AaoifiStatus.HALAL

    val statusBadgeText: String get() = if (isFullyCompliant) "HALAL" else "HARAM"

    val complianceStatusLabel: String
        get() = when {
            isFullyCompliant && debtRatioPercent <= 0.01 -> "HALAL • 0% Zero Debt (AAOIFI)"
            isFullyCompliant -> "HALAL • AAOIFI Standard 21 Compliant"
            !passesDebtScreen && violations.any { it.category != ExclusionCategory.EXCESSIVE_DEBT } ->
                "HARAM • Debt (${String.format("%.1f", debtRatioPercent)}%) & Prohibited Business"
            !passesDebtScreen -> "HARAM • Debt Ratio ${String.format("%.1f", debtRatioPercent)}% ≥ 30%"
            !passesCashScreen -> "HARAM • Cash/Deposits ${String.format("%.1f", cashRatioPercent)}% ≥ 30%"
            !passesIncomeScreen -> "HARAM • Impure Income ${String.format("%.1f", impermissibleIncomePercent)}% ≥ 5%"
            else -> "HARAM • ${violations.firstOrNull()?.category?.title ?: "Prohibited by AAOIFI"}"
        }
}

/**
 * Predictive analytics generated by the on-device AI model.
 */
data class AiPrediction(
    val projectedPrice7d: Double,
    val projectedPrice30d: Double,
    val expectedReturn7dPercent: Double,
    val expectedReturn30dPercent: Double,
    val bullishTargetPrice: Double,
    val bearishTargetPrice: Double,
    val aiConfidenceScore: Int, // 0 - 100%
    val signal: SignalType,
    val momentumScore: Double, // -100 to +100
    val volatilityRiskIndex: Double, // 0.0 to 1.0
    val aiExplanation: String,
    val projectionCorridor: List<CorridorPoint>
)

enum class SignalType(val label: String, val colorHex: Long) {
    STRONG_BULLISH("Strong Buy", 0xFF10B981),
    MODERATE_BULLISH("Bullish", 0xFF34D399),
    NEUTRAL("Hold / Neutral", 0xFFF59E0B),
    BEARISH("Bearish Risk", 0xFFEF4444)
}

data class CorridorPoint(
    val day: Int,
    val expectedPrice: Double,
    val upper95: Double,
    val lower95: Double
)

/**
 * Full Stock entity containing price, Yahoo balance sheet, ethical audit, and AI prediction.
 */
data class StockProfile(
    val symbol: String,
    val name: String,
    val sector: String,
    val industry: String,
    val currentPrice: Double,
    val change: Double,
    val changePercent: Double,
    val dayHigh: Double,
    val dayLow: Double,
    val volume: Long,
    val dividendYieldPercent: Double,
    val balanceSheet: YahooBalanceSheet,
    val audit: EthicalAuditResult,
    val chartHistory: List<ChartPoint>,
    val aiPrediction: AiPrediction,
    val isFavorite: Boolean = false
)

data class YahooSearchResult(
    val symbol: String,
    val name: String,
    val exchange: String = "",
    val quoteType: String = "EQUITY",
    val sector: String = "",
    val industry: String = ""
)

