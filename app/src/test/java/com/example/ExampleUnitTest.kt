package com.example

import com.example.data.model.AaoifiStatus
import com.example.data.model.EthicalAuditResult
import com.example.data.model.YahooBalanceSheet
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun aaoifi_halal_stock_passes_all_four_ratios() {
        val balanceSheet = YahooBalanceSheet(
            totalDebtUsd = 20_000_000.0,
            totalCashUsd = 25_000_000.0,
            totalAssetsUsd = 200_000_000.0,
            totalEquityUsd = 100_000_000.0,
            operatingCashflowUsd = 30_000_000.0,
            totalRevenueUsd = 150_000_000.0,
            marketCapUsd = 100_000_000.0,
            receivablesUsd = 30_000_000.0,
            interestIncomeUsd = 1_000_000.0,
            reportedPeriod = "FY2024",
            dataSource = "Yahoo Finance"
        )

        // AAOIFI Ratios:
        // Debt / Market Cap = 20M / 100M = 20% (< 30%) -> PASS
        // Cash / Market Cap = 25M / 100M = 25% (< 30%) -> PASS
        // (Cash + Receivables) / Assets = 55M / 200M = 27.5% (< 70%) -> PASS
        // Interest Income / Revenue = 1M / 150M = 0.67% (< 5%) -> PASS
        assertTrue(balanceSheet.debtToMarketCapPercent < 30.0)
        assertTrue(balanceSheet.cashToMarketCapPercent < 30.0)
        assertTrue(balanceSheet.liquidAssetsToAssetsPercent < 70.0)
        assertTrue(balanceSheet.impermissibleIncomeToRevenuePercent < 5.0)

        val audit = EthicalAuditResult(
            aaoifiStatus = AaoifiStatus.HALAL,
            debtRatioPercent = balanceSheet.debtToMarketCapPercent,
            cashRatioPercent = balanceSheet.cashToMarketCapPercent,
            liquidityRatioPercent = balanceSheet.liquidAssetsToAssetsPercent,
            impermissibleIncomePercent = balanceSheet.impermissibleIncomeToRevenuePercent,
            passesDebtScreen = true,
            passesCashScreen = true,
            passesLiquidityScreen = true,
            passesIncomeScreen = true,
            violations = emptyList(),
            passesSectorExclusions = true
        )

        assertEquals(AaoifiStatus.HALAL, audit.aaoifiStatus)
        assertTrue(audit.isFullyCompliant)
    }

    @Test
    fun aaoifi_excessive_debt_classified_as_haram() {
        val balanceSheet = YahooBalanceSheet(
            totalDebtUsd = 50_000_000.0,
            totalCashUsd = 10_000_000.0,
            totalAssetsUsd = 100_000_000.0,
            totalEquityUsd = 30_000_000.0,
            operatingCashflowUsd = 5_000_000.0,
            totalRevenueUsd = 40_000_000.0,
            marketCapUsd = 100_000_000.0, // Debt is 50% >= 30%
            reportedPeriod = "FY2024",
            dataSource = "Yahoo Finance"
        )

        val passesDebt = balanceSheet.debtToMarketCapPercent < 30.0
        assertFalse(passesDebt)

        val audit = EthicalAuditResult(
            aaoifiStatus = AaoifiStatus.HARAM,
            debtRatioPercent = balanceSheet.debtToMarketCapPercent,
            cashRatioPercent = balanceSheet.cashToMarketCapPercent,
            liquidityRatioPercent = balanceSheet.liquidAssetsToAssetsPercent,
            impermissibleIncomePercent = balanceSheet.impermissibleIncomeToRevenuePercent,
            passesDebtScreen = false,
            passesCashScreen = true,
            passesLiquidityScreen = true,
            passesIncomeScreen = true,
            violations = emptyList(),
            passesSectorExclusions = true
        )

        assertEquals(AaoifiStatus.HARAM, audit.aaoifiStatus)
        assertFalse(audit.isFullyCompliant)
    }
}
