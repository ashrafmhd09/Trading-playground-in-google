package com.example.data.model

data class CandlePoint(
    val timestamp: Long,
    val dateLabel: String,
    val open: Double,
    val high: Double,
    val low: Double,
    val close: Double,
    val volume: Long
)

data class Stock(
    val symbol: String,
    val name: String,
    val sector: String,
    val currentPrice: Double,
    val change: Double,
    val changePercent: Double,
    val dayHigh: Double,
    val dayLow: Double,
    val volume: Long,
    val marketCapFormatted: String,
    val peRatio: Double,
    val dividendYield: Double,
    val shariaReport: ShariaReport,
    val chartData: List<CandlePoint> = emptyList(),
    val isWatched: Boolean = false
)

enum class AlertSeverity {
    CRITICAL, // Dropped to Non-Compliant
    POSITIVE, // Upgraded to Zero-Debt / Halal
    WARNING,  // Debt increased near 33% threshold
    INFO      // Regular audit re-certification
}

data class ComplianceAlert(
    val id: String,
    val symbol: String,
    val companyName: String,
    val oldStatus: ComplianceStatus,
    val newStatus: ComplianceStatus,
    val headline: String,
    val reason: String,
    val timestamp: Long,
    val formattedTime: String,
    val severity: AlertSeverity,
    val isRead: Boolean = false
)
