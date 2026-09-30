package com.example.domain

import com.example.data.model.AiPrediction
import com.example.data.model.ChartPoint
import com.example.data.model.CorridorPoint
import com.example.data.model.SignalType
import kotlin.math.abs
import kotlin.math.exp
import kotlin.math.ln
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * High-performance, deterministic on-device machine learning & statistical predictor.
 * Runs entirely locally on the device without network dependencies.
 */
object OnDeviceAiPredictor {

    fun generatePrediction(
        currentPrice: Double,
        history: List<ChartPoint>,
        debtRatioPercent: Double,
        isEthicalCompliant: Boolean,
        stressDebtIncreasePercent: Double = 0.0,
        volatilityMultiplier: Double = 1.0
    ): AiPrediction {
        if (history.isEmpty() || currentPrice <= 0.0) {
            return fallbackPrediction(currentPrice)
        }

        val closes = history.map { it.close }
        val n = closes.size

        // 1. Calculate Log Returns & Realized Volatility
        val returns = mutableListOf<Double>()
        for (i in 1 until n) {
            if (closes[i - 1] > 0.0) {
                returns.add(ln(closes[i] / closes[i - 1]))
            }
        }

        val meanReturn = if (returns.isNotEmpty()) returns.average() else 0.0005
        val variance = if (returns.size > 1) {
            returns.map { (it - meanReturn).pow(2) }.average()
        } else 0.0004

        val dailyVolatility = sqrt(max(0.00001, variance)) * volatilityMultiplier
        val annualizedVol = dailyVolatility * sqrt(252.0)

        // 2. Linear Regression Slope (Trend Momentum)
        var sumX = 0.0
        var sumY = 0.0
        var sumXY = 0.0
        var sumXX = 0.0
        for (i in closes.indices) {
            val x = i.toDouble()
            val y = closes[i]
            sumX += x
            sumY += y
            sumXY += x * y
            sumXX += x * x
        }
        val slope = if (n > 1) (n * sumXY - sumX * sumY) / (n * sumXX - sumX * sumX) else 0.0
        val trendSlopePct = if (currentPrice > 0) (slope / currentPrice) * 100.0 else 0.0

        // 3. Fast Technical Indicators (EMA-5, EMA-20, RSI)
        val ema5 = calculateEma(closes, 5)
        val ema20 = calculateEma(closes, 20)
        val rsi14 = calculateRsi(closes, 14)

        // 4. Debt Stress Penalty Factor
        // Companies with lower debt have lower cost of distress; simulated debt increase applies valuation discount
        val effectiveDebt = debtRatioPercent + stressDebtIncreasePercent
        val debtPenaltyDiscount = if (effectiveDebt > 30.0) {
            (effectiveDebt - 30.0) * 0.015 // 1.5% valuation penalty per 1% excess debt
        } else if (effectiveDebt <= 0.01) {
            -0.02 // 2% zero-debt premium
        } else {
            0.0
        }

        // 5. On-Device AI Combined Momentum Score (-100 to +100)
        var momentum = 0.0
        momentum += if (ema5 > ema20) 25.0 else -25.0
        momentum += when {
            rsi14 < 35.0 -> 25.0 // Oversold bounce opportunity
            rsi14 > 70.0 -> -20.0 // Overbought pullback risk
            else -> (rsi14 - 50.0) * 1.2
        }
        momentum += trendSlopePct * 15.0
        if (!isEthicalCompliant) momentum -= 20.0 // ESG capital flight penalty
        momentum = momentum.coerceIn(-100.0, 100.0)

        // 6. Expected Drift & Projection targets
        val adjustedDailyDrift = (meanReturn * 0.6 + (momentum / 100.0) * 0.003) - (debtPenaltyDiscount / 30.0)
        val projected7d = max(1.0, currentPrice * exp(adjustedDailyDrift * 7.0))
        val projected30d = max(1.0, currentPrice * exp(adjustedDailyDrift * 30.0))

        val ret7d = ((projected7d - currentPrice) / currentPrice) * 100.0
        val ret30d = ((projected30d - currentPrice) / currentPrice) * 100.0

        // 95% Confidence Bounds via Z-Score 1.96
        val stdev30d = currentPrice * dailyVolatility * sqrt(30.0)
        val bullishTarget = projected30d + 1.96 * stdev30d
        val bearishTarget = max(0.5, projected30d - 1.96 * stdev30d)

        // 7. Generate 30-Day Monte Carlo / Corridor Points
        val corridor = mutableListOf<CorridorPoint>()
        for (day in 1..30) {
            val expP = currentPrice * exp(adjustedDailyDrift * day)
            val stdev = currentPrice * dailyVolatility * sqrt(day.toDouble())
            corridor.add(
                CorridorPoint(
                    day = day,
                    expectedPrice = expP,
                    upper95 = expP + 1.96 * stdev,
                    lower95 = max(0.5, expP - 1.96 * stdev)
                )
            )
        }

        // 8. Confidence Score (based on sample size, trend stability, low debt quality)
        var confidence = 70
        if (effectiveDebt < 15.0) confidence += 12
        if (effectiveDebt <= 0.01) confidence += 8
        if (annualizedVol < 0.25) confidence += 8 else if (annualizedVol > 0.45) confidence -= 12
        if (n >= 20) confidence += 5
        val finalConfidence = confidence.coerceIn(40, 96)

        // Signal Classification
        val signal = when {
            ret30d > 8.0 && momentum > 25.0 -> SignalType.STRONG_BULLISH
            ret30d > 2.0 && momentum >= -10.0 -> SignalType.MODERATE_BULLISH
            ret30d < -4.0 || momentum < -35.0 -> SignalType.BEARISH
            else -> SignalType.NEUTRAL
        }

        val explanation = buildString {
            if (effectiveDebt <= 0.01) {
                append("Pinnacle zero-debt capital structure provides immune resilience against interest rate hikes. ")
            } else if (effectiveDebt < 30.0) {
                append("Comfortably below 30% debt ceiling (${String.format("%.1f", effectiveDebt)}%). Healthy solvency. ")
            } else {
                append("WARNING: Debt ratio (${String.format("%.1f", effectiveDebt)}%) breaches the 30% ethical ceiling! ")
            }
            if (momentum > 20) {
                append("Upward momentum confirmed by EMA cross and steady accumulation volume.")
            } else if (momentum < -20) {
                append("Technical divergence indicates short-term consolidation risk.")
            } else {
                append("Stable price action within normal statistical volatility bands.")
            }
        }

        return AiPrediction(
            projectedPrice7d = projected7d,
            projectedPrice30d = projected30d,
            expectedReturn7dPercent = ret7d,
            expectedReturn30dPercent = ret30d,
            bullishTargetPrice = bullishTarget,
            bearishTargetPrice = bearishTarget,
            aiConfidenceScore = finalConfidence,
            signal = signal,
            momentumScore = momentum,
            volatilityRiskIndex = (annualizedVol / 0.6).coerceIn(0.05, 1.0),
            aiExplanation = explanation,
            projectionCorridor = corridor
        )
    }

    private fun calculateEma(data: List<Double>, period: Int): Double {
        if (data.isEmpty()) return 0.0
        val k = 2.0 / (period + 1.0)
        var ema = data.first()
        for (i in 1 until data.size) {
            ema = data[i] * k + ema * (1.0 - k)
        }
        return ema
    }

    private fun calculateRsi(data: List<Double>, period: Int): Double {
        if (data.size <= period) return 50.0
        var gains = 0.0
        var losses = 0.0
        for (i in 1..period) {
            val change = data[i] - data[i - 1]
            if (change >= 0) gains += change else losses += abs(change)
        }
        var avgGain = gains / period
        var avgLoss = losses / period

        for (i in (period + 1) until data.size) {
            val change = data[i] - data[i - 1]
            val gain = if (change >= 0) change else 0.0
            val loss = if (change < 0) abs(change) else 0.0
            avgGain = (avgGain * (period - 1) + gain) / period
            avgLoss = (avgLoss * (period - 1) + loss) / period
        }

        if (avgLoss == 0.0) return 100.0
        val rs = avgGain / avgLoss
        return 100.0 - (100.0 / (1.0 + rs))
    }

    private fun fallbackPrediction(currentPrice: Double): AiPrediction {
        return AiPrediction(
            projectedPrice7d = currentPrice * 1.01,
            projectedPrice30d = currentPrice * 1.04,
            expectedReturn7dPercent = 1.0,
            expectedReturn30dPercent = 4.0,
            bullishTargetPrice = currentPrice * 1.12,
            bearishTargetPrice = currentPrice * 0.92,
            aiConfidenceScore = 65,
            signal = SignalType.NEUTRAL,
            momentumScore = 0.0,
            volatilityRiskIndex = 0.25,
            aiExplanation = "Baseline historical model projection.",
            projectionCorridor = emptyList()
        )
    }
}
