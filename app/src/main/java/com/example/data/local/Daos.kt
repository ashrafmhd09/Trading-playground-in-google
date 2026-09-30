package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface WatchlistDao {
    @Query("SELECT * FROM watchlist ORDER BY addedTimestamp DESC")
    fun getAllWatched(): Flow<List<WatchlistEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addToWatchlist(item: WatchlistEntity)

    @Query("DELETE FROM watchlist WHERE symbol = :symbol")
    suspend fun removeFromWatchlist(symbol: String)

    @Query("SELECT EXISTS(SELECT 1 FROM watchlist WHERE symbol = :symbol)")
    suspend fun isWatched(symbol: String): Boolean
}

@Dao
interface ComplianceAlertDao {
    @Query("SELECT * FROM compliance_alerts ORDER BY timestamp DESC")
    fun getAllAlerts(): Flow<List<ComplianceAlertEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlert(alert: ComplianceAlertEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlerts(alerts: List<ComplianceAlertEntity>)

    @Query("UPDATE compliance_alerts SET isRead = 1 WHERE id = :id")
    suspend fun markAsRead(id: String)

    @Query("UPDATE compliance_alerts SET isRead = 1")
    suspend fun markAllAsRead()

    @Query("DELETE FROM compliance_alerts WHERE id = :id")
    suspend fun deleteAlert(id: String)
}

@Dao
interface SavedStrategyDao {
    @Query("SELECT * FROM saved_strategies ORDER BY savedAtTimestamp DESC")
    fun getAllStrategies(): Flow<List<SavedStrategyEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStrategy(strategy: SavedStrategyEntity)

    @Query("DELETE FROM saved_strategies WHERE id = :id")
    suspend fun deleteStrategy(id: Long)
}

@Dao
interface PortfolioDao {
    @Query("SELECT * FROM portfolio_positions")
    fun getPositions(): Flow<List<PortfolioPositionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertPosition(position: PortfolioPositionEntity)

    @Query("DELETE FROM portfolio_positions WHERE symbol = :symbol")
    suspend fun deletePosition(symbol: String)

    @Query("SELECT * FROM portfolio_cash WHERE id = 1")
    suspend fun getCash(): PortfolioCashEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun setCash(cash: PortfolioCashEntity)
}
