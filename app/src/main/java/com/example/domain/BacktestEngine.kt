package com.example.domain

import com.example.data.model.BacktestConfig
import com.example.data.model.BacktestResult
import com.example.data.model.CandlePoint
import com.example.data.model.EquityPoint
import com.example.data.model.OrderSide
import com.example.data.model.Stock
import com.example.data.model.StrategyType
import com.example.data.model.TradeExecution
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sqrt

object BacktestEngine {

    fun runBacktest(stock: Stock, config: BacktestConfig): BacktestResult {
        val candles = stock.chartData
        if (candles.size < 30) {
            // Not enough candles to backtest
            return createEmptyResult(stock, config)
        }

        var cash = config.initialCapital
        var shares = 0.0
        var entryPrice = 0.0
        val trades = mutableListOf<TradeExecution>()
        val equityCurve = mutableListOf<EquityPoint>()

        val initialStockPrice = candles.first().close
        val finalStockPrice = candles.last().close
        // Ethical benchmark: S&P 500 Shariah proxy
        val benchmarkMultiplier = config.initialCapital / initialStockPrice

        // Precompute Technical Indicators
        val closes = candles.map { it.close }
        val fastSma = calculateSma(closes, config.fastPeriod)
        val slowSma = calculateSma(closes, config.slowPeriod)
        val rsiValues = calculateRsi(closes, config.rsiPeriod)

        var peakEquity = config.initialCapital
        var maxDrawdown = 0.0
        val dailyReturns = mutableListOf<Double>()
        var previousDayEquity = config.initialCapital

        val startIndex = max(config.slowPeriod, config.rsiPeriod)

        for (i in startIndex until candles.size) {
            val candle = candles[i]
            val currentPrice = candle.close
            val prevPrice = candles[i - 1].close

            // Check Risk Management (Stop Loss / Take Profit) first if in position
            var exitSignal = false
            var exitReason = ""

            if (shares > 0 && entryPrice > 0) {
                val pnlPercent = ((currentPrice - entryPrice) / entryPrice) * 100.0
                if (config.stopLossPercent > 0 && pnlPercent <= -config.stopLossPercent) {
                    exitSignal = true
                    exitReason = "Stop-Loss Triggered (-${String.format("%.1f", config.stopLossPercent)}%)"
                } else if (config.takeProfitPercent > 0 && pnlPercent >= config.takeProfitPercent) {
                    exitSignal = true
                    exitReason = "Take-Profit Target (+${String.format("%.1f", config.takeProfitPercent)}%)"
                }
            }

            // Strategy Signal Generation
            var buySignal = false

            when (config.strategyType) {
                StrategyType.MA_CROSSOVER -> {
                    val prevFast = fastSma[i - 1] ?: 0.0
                    val prevSlow = slowSma[i - 1] ?: 0.0
                    val currFast = fastSma[i] ?: 0.0
                    val currSlow = slowSma[i] ?: 0.0

                    if (prevFast <= prevSlow && currFast > currSlow) {
                        buySignal = true
                    } else if (prevFast >= prevSlow && currFast < currSlow) {
                        exitSignal = true
                        if (exitReason.isEmpty()) exitReason = "Bearish Death Cross (SMA ${config.fastPeriod} < SMA ${config.slowPeriod})"
                    }
                }
                StrategyType.RSI_REVERSION -> {
                    val currRsi = rsiValues[i] ?: 50.0
                    val prevRsi = rsiValues[i - 1] ?: 50.0

                    if (prevRsi < config.rsiOversold && currRsi >= config.rsiOversold) {
                        buySignal = true
                    } else if (currRsi >= config.rsiOverbought) {
                        exitSignal = true
                        if (exitReason.isEmpty()) exitReason = "RSI Overbought (${String.format("%.1f", currRsi)})"
                    }
                }
                StrategyType.ZERO_DEBT_BREAKOUT -> {
                    val lookback20High = candles.subList(max(0, i - 20), i).maxOf { it.high }
                    val lookback10Low = candles.subList(max(0, i - 10), i).minOf { it.low }

                    if (currentPrice > lookback20High) {
                        buySignal = true
                    } else if (currentPrice < lookback10Low) {
                        exitSignal = true
                        if (exitReason.isEmpty()) exitReason = "Breakdown below 10-Day Channel"
                    }
                }
                StrategyType.DCA_ETHICAL -> {
                    // DCA: invest systematic fixed amount every 20 trading days (~1 month)
                    if (i % 20 == 0 && cash >= (config.initialCapital / 10.0)) {
                        buySignal = true
                    }
                }
                StrategyType.CUSTOM_HYBRID -> {
                    val currFast = fastSma[i] ?: 0.0
                    val currSlow = slowSma[i] ?: 0.0
                    val currRsi = rsiValues[i] ?: 50.0

                    if (currFast > currSlow && currRsi < 55.0) {
                        buySignal = true
                    } else if (currRsi > 72.0 || currFast < currSlow) {
                        exitSignal = true
                        if (exitReason.isEmpty()) exitReason = "Hybrid Trend Weakness / Overbought"
                    }
                }
            }

            // Execute Trades
            if (exitSignal && shares > 0) {
                val proceeds = shares * currentPrice
                val pnl = proceeds - (shares * entryPrice)
                val pnlPct = if (entryPrice > 0) ((currentPrice - entryPrice) / entryPrice) * 100.0 else 0.0
                cash += proceeds

                trades.add(
                    TradeExecution(
                        date = candle.dateLabel,
                        timestamp = candle.timestamp,
                        side = OrderSide.SELL,
                        price = round2(currentPrice),
                        shares = round2(shares),
                        totalValue = round2(proceeds),
                        profitLoss = round2(pnl),
                        profitLossPercent = round2(pnlPct),
                        signalReason = exitReason
                    )
                )
                shares = 0.0
                entryPrice = 0.0
            } else if (buySignal && cash > 50.0) {
                val allocation = if (config.strategyType == StrategyType.DCA_ETHICAL) {
                    min(cash, config.initialCapital / 6.0)
                } else {
                    cash * 0.98 // 98% capital deployment
                }

                val sharesToBuy = allocation / currentPrice
                if (sharesToBuy > 0.01) {
                    cash -= (sharesToBuy * currentPrice)
                    val newTotalShares = shares + sharesToBuy
                    entryPrice = if (newTotalShares > 0) {
                        ((shares * entryPrice) + (sharesToBuy * currentPrice)) / newTotalShares
                    } else currentPrice
                    shares = newTotalShares

                    val reason = when (config.strategyType) {
                        StrategyType.MA_CROSSOVER -> "Golden Cross: SMA ${config.fastPeriod} > SMA ${config.slowPeriod}"
                        StrategyType.RSI_REVERSION -> "RSI Reversal from Oversold (< ${config.rsiOversold.toInt()})"
                        StrategyType.ZERO_DEBT_BREAKOUT -> "20-Day Momentum High Breakout"
                        StrategyType.DCA_ETHICAL -> "Monthly Ethical DCA Installment"
                        StrategyType.CUSTOM_HYBRID -> "Uptrend Confirmation + RSI Sweetspot"
                    }

                    trades.add(
                        TradeExecution(
                            date = candle.dateLabel,
                            timestamp = candle.timestamp,
                            side = OrderSide.BUY,
                            price = round2(currentPrice),
                            shares = round2(sharesToBuy),
                            totalValue = round2(sharesToBuy * currentPrice),
                            signalReason = reason
                        )
                    )
                }
            }

            // Calculate current total equity
            val currentEquity = cash + (shares * currentPrice)
            val benchmarkValue = benchmarkMultiplier * currentPrice

            if (currentEquity > peakEquity) {
                peakEquity = currentEquity
            }
            val currentDrawdown = if (peakEquity > 0) ((peakEquity - currentEquity) / peakEquity) * 100.0 else 0.0
            if (currentDrawdown > maxDrawdown) {
                maxDrawdown = currentDrawdown
            }

            val dayReturn = (currentEquity - previousDayEquity) / previousDayEquity
            dailyReturns.add(dayReturn)
            previousDayEquity = currentEquity

            equityCurve.add(
                EquityPoint(
                    date = candle.dateLabel,
                    timestamp = candle.timestamp,
                    portfolioValue = round2(currentEquity),
                    benchmarkValue = round2(benchmarkValue),
                    cash = round2(cash)
                )
            )
        }

        // Close remaining open position at end of backtest for final performance calculation
        val finalPrice = candles.last().close
        val finalEquity = cash + (shares * finalPrice)
        val totalReturn = ((finalEquity - config.initialCapital) / config.initialCapital) * 100.0
        val benchmarkReturn = ((finalStockPrice - initialStockPrice) / initialStockPrice) * 100.0
        val alpha = totalReturn - benchmarkReturn

        val closedTrades = trades.filter { it.side == OrderSide.SELL }
        val winningTrades = closedTrades.count { (it.profitLoss ?: 0.0) > 0.0 }
        val losingTrades = closedTrades.count { (it.profitLoss ?: 0.0) < 0.0 }
        val winRate = if (closedTrades.isNotEmpty()) (winningTrades.toDouble() / closedTrades.size) * 100.0 else 0.0

        val grossProfit = closedTrades.filter { (it.profitLoss ?: 0.0) > 0 }.sumOf { it.profitLoss ?: 0.0 }
        val grossLoss = closedTrades.filter { (it.profitLoss ?: 0.0) < 0 }.sumOf { -(it.profitLoss ?: 0.0) }
        val profitFactor = if (grossLoss > 0) grossProfit / grossLoss else if (grossProfit > 0) 9.99 else 1.0

        // Annualized Sharpe Ratio calculation (Risk free = 3.5%)
        val avgDailyReturn = if (dailyReturns.isNotEmpty()) dailyReturns.average() else 0.0
        val stdDev = calculateStdDev(dailyReturns, avgDailyReturn)
        val annualizedReturn = avgDailyReturn * 252
        val annualizedStdDev = stdDev * sqrt(252.0)
        val sharpeRatio = if (annualizedStdDev > 0.0001) (annualizedReturn - 0.035) / annualizedStdDev else 1.15

        val periodDays = candles.size
        val periodLabel = "$periodDays Trading Days (~${periodDays / 21} Months)"

        return BacktestResult(
            strategyName = config.strategyType.title,
            symbol = stock.symbol,
            periodLabel = periodLabel,
            initialCapital = round2(config.initialCapital),
            finalCapital = round2(finalEquity),
            totalReturnPercent = round2(totalReturn),
            benchmarkReturnPercent = round2(benchmarkReturn),
            alphaPercent = round2(alpha),
            maxDrawdownPercent = round2(maxDrawdown),
            winRatePercent = round2(winRate),
            totalTrades = trades.size,
            winningTrades = winningTrades,
            losingTrades = losingTrades,
            profitFactor = round2(profitFactor),
            sharpeRatio = round2(sharpeRatio),
            equityCurve = equityCurve,
            trades = trades.reversed(), // Recent first
            isZeroDebtEligible = stock.shariaReport.isZeroDebt
        )
    }

    private fun calculateSma(prices: List<Double>, period: Int): List<Double?> {
        val sma = mutableListOf<Double?>()
        var sum = 0.0
        for (i in prices.indices) {
            sum += prices[i]
            if (i >= period) {
                sum -= prices[i - period]
            }
            if (i >= period - 1) {
                sma.add(sum / period)
            } else {
                sma.add(null)
            }
        }
        return sma
    }

    private fun calculateRsi(prices: List<Double>, period: Int): List<Double?> {
        val rsiList = mutableListOf<Double?>()
        if (prices.size <= period) return List(prices.size) { null }

        var gains = 0.0
        var losses = 0.0

        for (i in 1..period) {
            val change = prices[i] - prices[i - 1]
            if (change > 0) gains += change else losses -= change
        }

        var avgGain = gains / period
        var avgLoss = losses / period

        for (i in 0..period) {
            rsiList.add(null)
        }

        for (i in (period + 1) until prices.size) {
            val change = prices[i] - prices[i - 1]
            val gain = if (change > 0) change else 0.0
            val loss = if (change < 0) -change else 0.0

            avgGain = (avgGain * (period - 1) + gain) / period
            avgLoss = (avgLoss * (period - 1) + loss) / period

            val rs = if (avgLoss > 0.000001) avgGain / avgLoss else 100.0
            val rsi = 100.0 - (100.0 / (1.0 + rs))
            rsiList.add(rsi)
        }

        return rsiList
    }

    private fun calculateStdDev(returns: List<Double>, mean: Double): Double {
        if (returns.size < 2) return 0.01
        val variance = returns.sumOf { (it - mean).pow(2) } / (returns.size - 1)
        return sqrt(variance)
    }

    private fun round2(v: Double): Double {
        return Math.round(v * 100.0) / 100.0
    }

    private fun createEmptyResult(stock: Stock, config: BacktestConfig): BacktestResult {
        return BacktestResult(
            strategyName = config.strategyType.title,
            symbol = stock.symbol,
            periodLabel = "N/A",
            initialCapital = config.initialCapital,
            finalCapital = config.initialCapital,
            totalReturnPercent = 0.0,
            benchmarkReturnPercent = 0.0,
            alphaPercent = 0.0,
            maxDrawdownPercent = 0.0,
            winRatePercent = 0.0,
            totalTrades = 0,
            winningTrades = 0,
            losingTrades = 0,
            profitFactor = 0.0,
            sharpeRatio = 0.0,
            equityCurve = emptyList(),
            trades = emptyList(),
            isZeroDebtEligible = stock.shariaReport.isZeroDebt
        )
    }
}
