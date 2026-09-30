package com.example.data.repository

import android.content.Context
import com.example.data.datasource.StockUniverseDataSource
import com.example.data.local.AppDatabase
import com.example.data.local.ComplianceAlertEntity
import com.example.data.local.PortfolioCashEntity
import com.example.data.local.PortfolioPositionEntity
import com.example.data.local.SavedStrategyEntity
import com.example.data.local.WatchlistEntity
import com.example.data.model.AlertSeverity
import com.example.data.model.CandlePoint
import com.example.data.model.ComplianceAlert
import com.example.data.model.ComplianceStatus
import com.example.data.model.PortfolioPosition
import com.example.data.model.PortfolioSummary
import com.example.data.model.Stock
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID
import kotlin.random.Random

class StockRepository(context: Context) {

    private val db = AppDatabase.getInstance(context)
    private val watchlistDao = db.watchlistDao()
    private val alertDao = db.complianceAlertDao()
    private val strategyDao = db.savedStrategyDao()
    private val portfolioDao = db.portfolioDao()

    private val scope = CoroutineScope(Dispatchers.IO)

    private val _stocks = MutableStateFlow<List<Stock>>(emptyList())
    val stocks: StateFlow<List<Stock>> = _stocks.asStateFlow()

    // Real-time compliance alert event trigger for toasts / in-app notifications
    private val _complianceAlertChannel = MutableSharedFlow<ComplianceAlert>(extraBufferCapacity = 10)
    val complianceAlertChannel: SharedFlow<ComplianceAlert> = _complianceAlertChannel.asSharedFlow()

    val watchlistSymbols: Flow<Set<String>> = watchlistDao.getAllWatched().map { list ->
        list.map { it.symbol }.toSet()
    }

    val alerts: Flow<List<ComplianceAlert>> = alertDao.getAllAlerts().map { list ->
        list.map { entity ->
            ComplianceAlert(
                id = entity.id,
                symbol = entity.symbol,
                companyName = entity.companyName,
                oldStatus = runCatching { ComplianceStatus.valueOf(entity.oldStatus) }.getOrDefault(ComplianceStatus.HALAL_LOW_DEBT),
                newStatus = runCatching { ComplianceStatus.valueOf(entity.newStatus) }.getOrDefault(ComplianceStatus.HALAL_LOW_DEBT),
                headline = entity.headline,
                reason = entity.reason,
                timestamp = entity.timestamp,
                formattedTime = formatTimeAgo(entity.timestamp),
                severity = runCatching { AlertSeverity.valueOf(entity.severity) }.getOrDefault(AlertSeverity.INFO),
                isRead = entity.isRead
            )
        }
    }

    val savedStrategies: Flow<List<SavedStrategyEntity>> = strategyDao.getAllStrategies()

    val portfolioSummary: Flow<PortfolioSummary> = combine(
        portfolioDao.getPositions(),
        _stocks
    ) { posEntities, currentStocks ->
        val stockMap = currentStocks.associateBy { it.symbol }
        val cash = portfolioDao.getCash()?.cashBalance ?: 100000.0

        val positions = posEntities.map { entity ->
            val stock = stockMap[entity.symbol]
            val currentPrice = stock?.currentPrice ?: entity.averageCost
            PortfolioPosition(
                symbol = entity.symbol,
                companyName = entity.companyName,
                shares = entity.shares,
                averageCost = entity.averageCost,
                currentPrice = currentPrice,
                isZeroDebt = entity.isZeroDebt,
                isHalal = entity.isHalal
            )
        }
        PortfolioSummary(cashBalance = cash, positions = positions)
    }

    init {
        scope.launch {
            // Seed initial alerts if empty
            val existingAlerts = alertDao.getAllAlerts().first()
            if (existingAlerts.isEmpty()) {
                val seed = StockUniverseDataSource.getInitialAlerts().map {
                    ComplianceAlertEntity(
                        id = it.id,
                        symbol = it.symbol,
                        companyName = it.companyName,
                        oldStatus = it.oldStatus.name,
                        newStatus = it.newStatus.name,
                        headline = it.headline,
                        reason = it.reason,
                        timestamp = it.timestamp,
                        severity = it.severity.name,
                        isRead = it.isRead
                    )
                }
                alertDao.insertAlerts(seed)
            }

            // Seed initial cash if empty
            if (portfolioDao.getCash() == null) {
                portfolioDao.setCash(PortfolioCashEntity(cashBalance = 100000.0))
            }

            // Seed initial watchlist with top Zero-Debt stocks
            val watched = watchlistDao.getAllWatched().first()
            if (watched.isEmpty()) {
                watchlistDao.addToWatchlist(WatchlistEntity("ISRG"))
                watchlistDao.addToWatchlist(WatchlistEntity("MNST"))
                watchlistDao.addToWatchlist(WatchlistEntity("NVDA"))
            }

            // Load initial stocks
            val initial = StockUniverseDataSource.getInitialUniverse()
            _stocks.value = initial

            // Observe watchlist changes to update Stock.isWatched
            launch {
                watchlistSymbols.collect { watchedSet ->
                    _stocks.value = _stocks.value.map { stock ->
                        stock.copy(isWatched = watchedSet.contains(stock.symbol))
                    }
                }
            }

            // Start live ticker ticks simulation
            startRealtimeMarketTicks()
        }
    }

    private fun startRealtimeMarketTicks() {
        scope.launch {
            val random = Random(System.currentTimeMillis())
            while (true) {
                delay(3500) // update a random stock every 3.5 seconds
                val currentList = _stocks.value
                if (currentList.isNotEmpty()) {
                    val targetIndex = random.nextInt(currentList.size)
                    val target = currentList[targetIndex]
                    val priceDeltaPercent = (random.nextDouble() - 0.49) * 0.005 // +/- 0.25%
                    val newPrice = Math.round((target.currentPrice * (1.0 + priceDeltaPercent)) * 100.0) / 100.0
                    val newChange = Math.round((target.change + (newPrice - target.currentPrice)) * 100.0) / 100.0
                    val basePrice = target.currentPrice - target.change
                    val newChangePct = if (basePrice > 0) Math.round((newChange / basePrice) * 10000.0) / 100.0 else 0.0

                    // Update last candle
                    val updatedCandles = target.chartData.toMutableList()
                    if (updatedCandles.isNotEmpty()) {
                        val last = updatedCandles.last()
                        val updatedLast = last.copy(
                            close = newPrice,
                            high = maxOf(last.high, newPrice),
                            low = minOf(last.low, newPrice),
                            volume = last.volume + random.nextLong(100, 2500)
                        )
                        updatedCandles[updatedCandles.size - 1] = updatedLast
                    }

                    val updatedStock = target.copy(
                        currentPrice = newPrice,
                        change = newChange,
                        changePercent = newChangePct,
                        dayHigh = maxOf(target.dayHigh, newPrice),
                        dayLow = minOf(target.dayLow, newPrice),
                        volume = target.volume + random.nextLong(1000, 25000),
                        chartData = updatedCandles
                    )

                    val newList = currentList.toMutableList()
                    newList[targetIndex] = updatedStock
                    _stocks.value = newList
                }
            }
        }
    }

    suspend fun toggleWatchlist(symbol: String) {
        if (watchlistDao.isWatched(symbol)) {
            watchlistDao.removeFromWatchlist(symbol)
        } else {
            watchlistDao.addToWatchlist(WatchlistEntity(symbol))
        }
    }

    suspend fun markAlertRead(alertId: String) {
        alertDao.markAsRead(alertId)
    }

    suspend fun markAllAlertsRead() {
        alertDao.markAllAsRead()
    }

    suspend fun saveStrategy(
        name: String,
        strategyType: String,
        symbol: String,
        initialCapital: Double,
        totalReturn: Double,
        winRate: Double
    ) {
        strategyDao.insertStrategy(
            SavedStrategyEntity(
                name = name,
                strategyType = strategyType,
                symbol = symbol,
                initialCapital = initialCapital,
                totalReturnPercent = totalReturn,
                winRatePercent = winRate
            )
        )
    }

    suspend fun deleteStrategy(id: Long) {
        strategyDao.deleteStrategy(id)
    }

    suspend fun buyStock(symbol: String, sharesToBuy: Double): Boolean {
        val stock = _stocks.value.firstOrNull { it.symbol == symbol } ?: return false
        val currentCash = portfolioDao.getCash()?.cashBalance ?: 100000.0
        val cost = sharesToBuy * stock.currentPrice

        if (cost > currentCash || sharesToBuy <= 0.0) {
            return false
        }

        val existingPositions = portfolioDao.getPositions().first()
        val existing = existingPositions.firstOrNull { it.symbol == symbol }

        val newCash = currentCash - cost
        portfolioDao.setCash(PortfolioCashEntity(cashBalance = newCash))

        if (existing != null) {
            val totalShares = existing.shares + sharesToBuy
            val totalCost = (existing.shares * existing.averageCost) + cost
            val newAvgCost = totalCost / totalShares
            portfolioDao.upsertPosition(
                existing.copy(
                    shares = totalShares,
                    averageCost = newAvgCost
                )
            )
        } else {
            portfolioDao.upsertPosition(
                PortfolioPositionEntity(
                    symbol = stock.symbol,
                    companyName = stock.name,
                    shares = sharesToBuy,
                    averageCost = stock.currentPrice,
                    isZeroDebt = stock.shariaReport.isZeroDebt,
                    isHalal = stock.shariaReport.isCompliant
                )
            )
        }
        return true
    }

    suspend fun sellStock(symbol: String, sharesToSell: Double): Boolean {
        val stock = _stocks.value.firstOrNull { it.symbol == symbol } ?: return false
        val existingPositions = portfolioDao.getPositions().first()
        val existing = existingPositions.firstOrNull { it.symbol == symbol } ?: return false

        if (sharesToSell <= 0.0 || sharesToSell > existing.shares) {
            return false
        }

        val currentCash = portfolioDao.getCash()?.cashBalance ?: 100000.0
        val proceeds = sharesToSell * stock.currentPrice

        portfolioDao.setCash(PortfolioCashEntity(cashBalance = currentCash + proceeds))

        val remainingShares = existing.shares - sharesToSell
        if (remainingShares <= 0.0001) {
            portfolioDao.deletePosition(symbol)
        } else {
            portfolioDao.upsertPosition(existing.copy(shares = remainingShares))
        }
        return true
    }

    // Interactive simulation to trigger real-time compliance audit changes
    suspend fun triggerSimulatedComplianceEvent() {
        val currentList = _stocks.value
        if (currentList.isEmpty()) return

        // Pick a scenario
        val scenarios = listOf(
            Triple(
                "ANET",
                ComplianceStatus.ZERO_DEBT_HALAL to ComplianceStatus.ZERO_DEBT_HALAL,
                Pair(
                    "Q3 Balance Sheet Audit: $0.00 Debt Maintained",
                    "Auditors verified $5.6B liquid cash & zero financial indebtedness. Pristine ethical standing confirmed."
                )
            ),
            Triple(
                "TSLA",
                ComplianceStatus.UNDER_REVIEW to ComplianceStatus.HALAL_LOW_DEBT,
                Pair(
                    "Tesla Cleared by Shariah Advisory Board",
                    "Automotive financing operations restructured with Islamic Murabaha facility. Debt ratio at 1.2% meets standard."
                )
            ),
            Triple(
                "ADBE",
                ComplianceStatus.HALAL_LOW_DEBT to ComplianceStatus.ZERO_DEBT_HALAL,
                Pair(
                    "Adobe Retires Final $6.2B Senior Notes: Now Zero-Debt!",
                    "Adobe completed full redemption of outstanding term debt using operational cash flows. Certified 100% Zero-Debt Halal."
                )
            ),
            Triple(
                "NVDA",
                ComplianceStatus.HALAL_LOW_DEBT to ComplianceStatus.UNDER_REVIEW,
                Pair(
                    "Semiconductor Consortium Debt Spike Under Review",
                    "New strategic foundry co-investment under evaluation for debt covenant thresholds."
                )
            )
        )

        val picked = scenarios.random()
        val targetSymbol = picked.first
        val targetStock = currentList.firstOrNull { it.symbol == targetSymbol } ?: return

        val oldStatus = picked.second.first
        val newStatus = picked.second.second
        val headline = picked.third.first
        val reason = picked.third.second

        val severity = when {
            newStatus == ComplianceStatus.ZERO_DEBT_HALAL -> AlertSeverity.POSITIVE
            newStatus == ComplianceStatus.UNDER_REVIEW -> AlertSeverity.WARNING
            !newStatus.name.contains("HALAL") -> AlertSeverity.CRITICAL
            else -> AlertSeverity.INFO
        }

        val alert = ComplianceAlert(
            id = UUID.randomUUID().toString(),
            symbol = targetSymbol,
            companyName = targetStock.name,
            oldStatus = oldStatus,
            newStatus = newStatus,
            headline = headline,
            reason = reason,
            timestamp = System.currentTimeMillis(),
            formattedTime = "Just now",
            severity = severity,
            isRead = false
        )

        alertDao.insertAlert(
            ComplianceAlertEntity(
                id = alert.id,
                symbol = alert.symbol,
                companyName = alert.companyName,
                oldStatus = alert.oldStatus.name,
                newStatus = alert.newStatus.name,
                headline = alert.headline,
                reason = alert.reason,
                timestamp = alert.timestamp,
                severity = alert.severity.name,
                isRead = false
            )
        )

        // Update in-memory stock sharia report
        _stocks.value = currentList.map { s ->
            if (s.symbol == targetSymbol) {
                val isZero = newStatus == ComplianceStatus.ZERO_DEBT_HALAL
                s.copy(
                    shariaReport = s.shariaReport.copy(
                        status = newStatus,
                        isZeroDebt = isZero,
                        debtToMarketCapRatio = if (isZero) 0.0 else s.shariaReport.debtToMarketCapRatio,
                        businessActivitySummary = reason,
                        lastAuditDate = "Real-time Live Audit"
                    )
                )
            } else s
        }

        // Emit to alert channel
        _complianceAlertChannel.emit(alert)
    }

    private fun formatTimeAgo(timestamp: Long): String {
        val diffMs = System.currentTimeMillis() - timestamp
        val minutes = diffMs / (60 * 1000)
        val hours = minutes / 60
        val days = hours / 24

        return when {
            minutes < 1 -> "Just now"
            minutes < 60 -> "${minutes}m ago"
            hours < 24 -> "${hours}h ago"
            days < 7 -> "${days}d ago"
            else -> SimpleDateFormat("MMM dd", Locale.US).format(Date(timestamp))
        }
    }
}
