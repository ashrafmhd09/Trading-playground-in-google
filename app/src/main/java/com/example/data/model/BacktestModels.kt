package com.example.data.model

enum class StrategyType(val title: String, val shortDesc: String) {
    MA_CROSSOVER("Moving Average Crossover", "Fast SMA crosses Slow SMA (Golden Cross / Death Cross)"),
    RSI_REVERSION("RSI Mean Reversion", "Buys oversold (<30) and sells overbought (>70)"),
    ZERO_DEBT_BREAKOUT("Zero-Debt Momentum", "Enters on 20-day high breakouts exclusively on Zero-Debt stocks"),
    DCA_ETHICAL("Ethical Dollar-Cost Averaging", "Systematic periodic allocation into halal stocks regardless of volatility"),
    CUSTOM_HYBRID("Custom Indicator Rules", "Configurable stop-loss, take-profit, and combined trend triggers")
}

data class BacktestConfig(
    val strategyType: StrategyType = StrategyType.MA_CROSSOVER,
    val targetSymbol: String = "ISRG",
    val initialCapital: Double = 10000.0,
    val fastPeriod: Int = 10,
    val slowPeriod: Int = 30,
    val rsiPeriod: Int = 14,
    val rsiOversold: Double = 30.0,
    val rsiOverbought: Double = 70.0,
    val stopLossPercent: Double = 5.0, // 0 = disabled
    val takeProfitPercent: Double = 15.0, // 0 = disabled
    val rebalanceMonthly: Boolean = true,
    val strictZeroDebtOnly: Boolean = true
)

enum class OrderSide {
    BUY, SELL
}

data class TradeExecution(
    val date: String,
    val timestamp: Long,
    val side: OrderSide,
    val price: Double,
    val shares: Double,
    val totalValue: Double,
    val profitLoss: Double? = null,
    val profitLossPercent: Double? = null,
    val signalReason: String
)

data class EquityPoint(
    val date: String,
    val timestamp: Long,
    val portfolioValue: Double,
    val benchmarkValue: Double,
    val cash: Double
)

data class BacktestResult(
    val strategyName: String,
    val symbol: String,
    val periodLabel: String,
    val initialCapital: Double,
    val finalCapital: Double,
    val totalReturnPercent: Double,
    val benchmarkReturnPercent: Double,
    val alphaPercent: Double,
    val maxDrawdownPercent: Double,
    val winRatePercent: Double,
    val totalTrades: Int,
    val winningTrades: Int,
    val losingTrades: Int,
    val profitFactor: Double,
    val sharpeRatio: Double,
    val equityCurve: List<EquityPoint>,
    val trades: List<TradeExecution>,
    val isZeroDebtEligible: Boolean
)

data class PortfolioPosition(
    val symbol: String,
    val companyName: String,
    val shares: Double,
    val averageCost: Double,
    val currentPrice: Double,
    val isZeroDebt: Boolean,
    val isHalal: Boolean
) {
    val totalCost: Double get() = shares * averageCost
    val currentValue: Double get() = shares * currentPrice
    val unrealizedPnl: Double get() = currentValue - totalCost
    val unrealizedPnlPercent: Double get() = if (totalCost > 0) (unrealizedPnl / totalCost) * 100 else 0.0
    val unrealizedProfitLoss: Double get() = unrealizedPnl
    val unrealizedProfitLossPercent: Double get() = unrealizedPnlPercent
}

data class PortfolioSummary(
    val cashBalance: Double,
    val positions: List<PortfolioPosition>,
    val initialCapital: Double = 100000.0
) {
    val positionsValue: Double get() = positions.sumOf { it.currentValue }
    val totalValue: Double get() = cashBalance + positionsValue
    val totalPnl: Double get() = totalValue - initialCapital
    val totalPnlPercent: Double get() = if (initialCapital > 0) (totalPnl / initialCapital) * 100 else 0.0

    val totalPortfolioValue: Double get() = totalValue
    val totalHoldingsValue: Double get() = positionsValue
    val totalProfitLoss: Double get() = totalPnl
    val totalProfitLossPercent: Double get() = totalPnlPercent

    val zeroDebtRatio: Double
        get() = if (positionsValue > 0) {
            positions.filter { it.isZeroDebt }.sumOf { it.currentValue } / positionsValue
        } else 0.0

    val shariaComplianceRatio: Double
        get() = if (positionsValue > 0) {
            positions.filter { it.isHalal }.sumOf { it.currentValue } / positionsValue
        } else 0.0

    val dividendPurificationDue: Double
        get() = positions.filter { it.isHalal && !it.isZeroDebt }.sumOf { it.shares * 0.12 }

    // Ethical ratios
    val zeroDebtAllocationPercent: Double
        get() = zeroDebtRatio * 100.0

    val halalAllocationPercent: Double
        get() = shariaComplianceRatio * 100.0

    val estimatedDividendPurificationUsd: Double
        get() = dividendPurificationDue
}
