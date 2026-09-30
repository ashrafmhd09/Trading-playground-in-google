package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "watchlist")
data class WatchlistEntity(
    @PrimaryKey val symbol: String,
    val addedTimestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "compliance_alerts")
data class ComplianceAlertEntity(
    @PrimaryKey val id: String,
    val symbol: String,
    val companyName: String,
    val oldStatus: String,
    val newStatus: String,
    val headline: String,
    val reason: String,
    val timestamp: Long,
    val severity: String,
    val isRead: Boolean = false
)

@Entity(tableName = "saved_strategies")
data class SavedStrategyEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val strategyType: String,
    val symbol: String,
    val initialCapital: Double,
    val totalReturnPercent: Double,
    val winRatePercent: Double,
    val savedAtTimestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "portfolio_positions")
data class PortfolioPositionEntity(
    @PrimaryKey val symbol: String,
    val companyName: String,
    val shares: Double,
    val averageCost: Double,
    val isZeroDebt: Boolean,
    val isHalal: Boolean
)

@Entity(tableName = "portfolio_cash")
data class PortfolioCashEntity(
    @PrimaryKey val id: Int = 1,
    val cashBalance: Double = 100000.0
)
