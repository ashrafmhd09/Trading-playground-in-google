package com.example.data.datasource

import com.example.data.model.AlertSeverity
import com.example.data.model.CandlePoint
import com.example.data.model.ComplianceAlert
import com.example.data.model.ComplianceStatus
import com.example.data.model.ProhibitedSector
import com.example.data.model.ShariaReport
import com.example.data.model.Stock
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.random.Random

object StockUniverseDataSource {

    fun getInitialUniverse(): List<Stock> {
        return listOf(
            // 1. ZERO-DEBT CHAMPIONS
            createStock(
                symbol = "ISRG",
                name = "Intuitive Surgical Inc.",
                sector = "Healthcare & Robotic Surgery",
                currentPrice = 486.25,
                change = 5.40,
                changePercent = 1.12,
                dayHigh = 489.10,
                dayLow = 481.50,
                volume = 1420500,
                marketCap = "172.4B",
                peRatio = 74.2,
                dividendYield = 0.0,
                shariaReport = ShariaReport(
                    isCompliant = true,
                    status = ComplianceStatus.ZERO_DEBT_HALAL,
                    isZeroDebt = true,
                    totalDebtUsdMillion = 0.0,
                    marketCapUsdMillion = 172400.0,
                    debtToMarketCapRatio = 0.0,
                    cashAndInterestSecuritiesRatio = 0.048,
                    impureRevenuePercent = 0.0,
                    purificationPerShare = 0.0,
                    businessActivitySummary = "Global pioneer in robotic-assisted Da Vinci surgical platforms. 100% debt-free balance sheet with $8.2B net cash reserves.",
                    lastAuditDate = "Q3 2026 Shariah Audit"
                ),
                baseTrend = 0.0012,
                volatility = 0.015
            ),
            createStock(
                symbol = "MNST",
                name = "Monster Beverage Corp.",
                sector = "Consumer Non-Cyclical",
                currentPrice = 52.80,
                change = 0.65,
                changePercent = 1.25,
                dayHigh = 53.20,
                dayLow = 51.90,
                volume = 3850200,
                marketCap = "54.8B",
                peRatio = 31.5,
                dividendYield = 0.0,
                shariaReport = ShariaReport(
                    isCompliant = true,
                    status = ComplianceStatus.ZERO_DEBT_HALAL,
                    isZeroDebt = true,
                    totalDebtUsdMillion = 0.0,
                    marketCapUsdMillion = 54800.0,
                    debtToMarketCapRatio = 0.0,
                    cashAndInterestSecuritiesRatio = 0.058,
                    impureRevenuePercent = 0.0,
                    purificationPerShare = 0.0,
                    businessActivitySummary = "Non-alcoholic energy beverages, fruit juices, and wellness drinks. Completely zero debt capital structure.",
                    lastAuditDate = "Q3 2026 Shariah Audit"
                ),
                baseTrend = 0.0008,
                volatility = 0.012
            ),
            createStock(
                symbol = "VRTX",
                name = "Vertex Pharmaceuticals",
                sector = "Biotechnology",
                currentPrice = 458.60,
                change = -2.10,
                changePercent = -0.46,
                dayHigh = 463.00,
                dayLow = 455.20,
                volume = 980400,
                marketCap = "118.2B",
                peRatio = 28.4,
                dividendYield = 0.0,
                shariaReport = ShariaReport(
                    isCompliant = true,
                    status = ComplianceStatus.ZERO_DEBT_HALAL,
                    isZeroDebt = true,
                    totalDebtUsdMillion = 0.0,
                    marketCapUsdMillion = 118200.0,
                    debtToMarketCapRatio = 0.0,
                    cashAndInterestSecuritiesRatio = 0.122,
                    impureRevenuePercent = 0.0,
                    purificationPerShare = 0.0,
                    businessActivitySummary = "Life-saving therapies for cystic fibrosis, sickle cell gene-editing, and pain medicine. Zero long-term debt.",
                    lastAuditDate = "Q3 2026 Shariah Audit"
                ),
                baseTrend = 0.0014,
                volatility = 0.016
            ),
            createStock(
                symbol = "GRMN",
                name = "Garmin Ltd.",
                sector = "Navigation & Wearable Tech",
                currentPrice = 194.50,
                change = 2.80,
                changePercent = 1.46,
                dayHigh = 196.10,
                dayLow = 191.00,
                volume = 1250000,
                marketCap = "37.5B",
                peRatio = 24.1,
                dividendYield = 1.54,
                shariaReport = ShariaReport(
                    isCompliant = true,
                    status = ComplianceStatus.ZERO_DEBT_HALAL,
                    isZeroDebt = true,
                    totalDebtUsdMillion = 0.0,
                    marketCapUsdMillion = 37500.0,
                    debtToMarketCapRatio = 0.0,
                    cashAndInterestSecuritiesRatio = 0.091,
                    impureRevenuePercent = 0.0,
                    purificationPerShare = 0.0,
                    businessActivitySummary = "GPS navigation, aviation systems, marine sonar, and health smartwatches. Fully zero long-term debt with positive free cash flow.",
                    lastAuditDate = "Q3 2026 Shariah Audit"
                ),
                baseTrend = 0.0010,
                volatility = 0.013
            ),
            createStock(
                symbol = "ANET",
                name = "Arista Networks Inc.",
                sector = "Cloud & AI Networking",
                currentPrice = 388.90,
                change = 7.30,
                changePercent = 1.91,
                dayHigh = 392.50,
                dayLow = 380.10,
                volume = 2100500,
                marketCap = "121.6B",
                peRatio = 46.8,
                dividendYield = 0.0,
                shariaReport = ShariaReport(
                    isCompliant = true,
                    status = ComplianceStatus.ZERO_DEBT_HALAL,
                    isZeroDebt = true,
                    totalDebtUsdMillion = 0.0,
                    marketCapUsdMillion = 121600.0,
                    debtToMarketCapRatio = 0.0,
                    cashAndInterestSecuritiesRatio = 0.046,
                    impureRevenuePercent = 0.0,
                    purificationPerShare = 0.0,
                    businessActivitySummary = "Cognitive cloud networking solutions and high-speed switches for AI clusters. Pristine debt-free balance sheet.",
                    lastAuditDate = "Q3 2026 Shariah Audit"
                ),
                baseTrend = 0.0016,
                volatility = 0.018
            ),
            createStock(
                symbol = "SEIC",
                name = "SEI Investments Co.",
                sector = "Financial Technology Platforms",
                currentPrice = 69.40,
                change = 0.85,
                changePercent = 1.24,
                dayHigh = 70.10,
                dayLow = 68.30,
                volume = 620000,
                marketCap = "9.1B",
                peRatio = 18.2,
                dividendYield = 1.30,
                shariaReport = ShariaReport(
                    isCompliant = true,
                    status = ComplianceStatus.ZERO_DEBT_HALAL,
                    isZeroDebt = true,
                    totalDebtUsdMillion = 0.0,
                    marketCapUsdMillion = 9100.0,
                    debtToMarketCapRatio = 0.0,
                    cashAndInterestSecuritiesRatio = 0.098,
                    impureRevenuePercent = 0.008,
                    purificationPerShare = 0.02,
                    businessActivitySummary = "Enterprise software and operations processing for institutional asset management. Fully debt-free.",
                    lastAuditDate = "Q3 2026 Shariah Audit"
                ),
                baseTrend = 0.0006,
                volatility = 0.011
            ),

            // 2. SHARIAH COMPLIANT TECH & HEALTHCARE LEADERS (Ultra-Low Debt)
            createStock(
                symbol = "NVDA",
                name = "NVIDIA Corporation",
                sector = "Semiconductors & AI",
                currentPrice = 129.40,
                change = 3.20,
                changePercent = 2.54,
                dayHigh = 131.20,
                dayLow = 126.50,
                volume = 48500200,
                marketCap = "3.17T",
                peRatio = 52.4,
                dividendYield = 0.03,
                shariaReport = ShariaReport(
                    isCompliant = true,
                    status = ComplianceStatus.HALAL_LOW_DEBT,
                    isZeroDebt = false,
                    totalDebtUsdMillion = 9750.0,
                    marketCapUsdMillion = 3170000.0,
                    debtToMarketCapRatio = 0.003, // 0.3%
                    cashAndInterestSecuritiesRatio = 0.011,
                    impureRevenuePercent = 0.002,
                    purificationPerShare = 0.01,
                    businessActivitySummary = "High-performance GPU computing hardware and AI acceleration software. Passes all business and financial Sharia screens.",
                    lastAuditDate = "Q3 2026 Shariah Audit"
                ),
                baseTrend = 0.0018,
                volatility = 0.022
            ),
            createStock(
                symbol = "ASML",
                name = "ASML Holding N.V.",
                sector = "Semiconductor Equipment",
                currentPrice = 868.50,
                change = -4.20,
                changePercent = -0.48,
                dayHigh = 878.00,
                dayLow = 861.00,
                volume = 1120000,
                marketCap = "342.1B",
                peRatio = 38.6,
                dividendYield = 0.72,
                shariaReport = ShariaReport(
                    isCompliant = true,
                    status = ComplianceStatus.HALAL_LOW_DEBT,
                    isZeroDebt = false,
                    totalDebtUsdMillion = 4620.0,
                    marketCapUsdMillion = 342100.0,
                    debtToMarketCapRatio = 0.0135, // 1.35%
                    cashAndInterestSecuritiesRatio = 0.021,
                    impureRevenuePercent = 0.001,
                    purificationPerShare = 0.05,
                    businessActivitySummary = "Exclusive manufacturer of Extreme Ultraviolet (EUV) lithography systems enabling cutting-edge microchips. Solid balance sheet.",
                    lastAuditDate = "Q3 2026 Shariah Audit"
                ),
                baseTrend = 0.0011,
                volatility = 0.017
            ),
            createStock(
                symbol = "GOOGL",
                name = "Alphabet Inc.",
                sector = "Internet & Software",
                currentPrice = 179.80,
                change = 1.15,
                changePercent = 0.64,
                dayHigh = 181.40,
                dayLow = 178.20,
                volume = 19400000,
                marketCap = "2.22T",
                peRatio = 24.8,
                dividendYield = 0.44,
                shariaReport = ShariaReport(
                    isCompliant = true,
                    status = ComplianceStatus.HALAL_LOW_DEBT,
                    isZeroDebt = false,
                    totalDebtUsdMillion = 28400.0,
                    marketCapUsdMillion = 2220000.0,
                    debtToMarketCapRatio = 0.0128, // 1.28%
                    cashAndInterestSecuritiesRatio = 0.048,
                    impureRevenuePercent = 0.012,
                    purificationPerShare = 0.04,
                    businessActivitySummary = "Search engine, cloud computing infrastructure, and Android ecosystem. Screened for minor non-compliant ad revenue purification.",
                    lastAuditDate = "Q3 2026 Shariah Audit"
                ),
                baseTrend = 0.0009,
                volatility = 0.014
            ),
            createStock(
                symbol = "MSFT",
                name = "Microsoft Corporation",
                sector = "Enterprise Software & Cloud",
                currentPrice = 432.10,
                change = 2.45,
                changePercent = 0.57,
                dayHigh = 435.00,
                dayLow = 429.60,
                volume = 16800000,
                marketCap = "3.21T",
                peRatio = 35.1,
                dividendYield = 0.70,
                shariaReport = ShariaReport(
                    isCompliant = true,
                    status = ComplianceStatus.HALAL_LOW_DEBT,
                    isZeroDebt = false,
                    totalDebtUsdMillion = 79800.0,
                    marketCapUsdMillion = 3210000.0,
                    debtToMarketCapRatio = 0.0248, // 2.48%
                    cashAndInterestSecuritiesRatio = 0.032,
                    impureRevenuePercent = 0.009,
                    purificationPerShare = 0.06,
                    businessActivitySummary = "Azure cloud services, office productivity suites, and enterprise AI. Well within the 33% debt threshold.",
                    lastAuditDate = "Q3 2026 Shariah Audit"
                ),
                baseTrend = 0.0008,
                volatility = 0.013
            ),
            createStock(
                symbol = "ADBE",
                name = "Adobe Inc.",
                sector = "Digital Media & Creative",
                currentPrice = 518.20,
                change = 6.80,
                changePercent = 1.33,
                dayHigh = 523.40,
                dayLow = 512.10,
                volume = 2450000,
                marketCap = "231.5B",
                peRatio = 42.0,
                dividendYield = 0.0,
                shariaReport = ShariaReport(
                    isCompliant = true,
                    status = ComplianceStatus.HALAL_LOW_DEBT,
                    isZeroDebt = false,
                    totalDebtUsdMillion = 6200.0,
                    marketCapUsdMillion = 231500.0,
                    debtToMarketCapRatio = 0.0268,
                    cashAndInterestSecuritiesRatio = 0.035,
                    impureRevenuePercent = 0.0,
                    purificationPerShare = 0.0,
                    businessActivitySummary = "Creative Cloud graphic design, video editing, and PDF workflow tools. Minimal debt and ethical operations.",
                    lastAuditDate = "Q3 2026 Shariah Audit"
                ),
                baseTrend = 0.0007,
                volatility = 0.016
            ),

            // 3. STOCKS UNDER SHARIAH REVIEW
            createStock(
                symbol = "AAPL",
                name = "Apple Inc.",
                sector = "Consumer Electronics",
                currentPrice = 227.30,
                change = -0.90,
                changePercent = -0.39,
                dayHigh = 229.40,
                dayLow = 225.80,
                volume = 38200000,
                marketCap = "3.48T",
                peRatio = 33.8,
                dividendYield = 0.44,
                shariaReport = ShariaReport(
                    isCompliant = true,
                    status = ComplianceStatus.UNDER_REVIEW,
                    isZeroDebt = false,
                    totalDebtUsdMillion = 104500.0,
                    marketCapUsdMillion = 3480000.0,
                    debtToMarketCapRatio = 0.030,
                    cashAndInterestSecuritiesRatio = 0.045,
                    impureRevenuePercent = 0.015,
                    purificationPerShare = 0.08,
                    businessActivitySummary = "Hardware & services. Note: Apple Card financial interest operations and cash bond yields are currently under active Shariah review.",
                    lastAuditDate = "Pending Q4 Board Review"
                ),
                baseTrend = 0.0006,
                volatility = 0.012
            ),
            createStock(
                symbol = "TSLA",
                name = "Tesla Inc.",
                sector = "Automotive & Clean Energy",
                currentPrice = 244.50,
                change = 8.10,
                changePercent = 3.43,
                dayHigh = 248.00,
                dayLow = 237.50,
                volume = 62400000,
                marketCap = "780.2B",
                peRatio = 68.4,
                dividendYield = 0.0,
                shariaReport = ShariaReport(
                    isCompliant = true,
                    status = ComplianceStatus.UNDER_REVIEW,
                    isZeroDebt = false,
                    totalDebtUsdMillion = 9500.0,
                    marketCapUsdMillion = 780200.0,
                    debtToMarketCapRatio = 0.0122,
                    cashAndInterestSecuritiesRatio = 0.038,
                    impureRevenuePercent = 0.024,
                    purificationPerShare = 0.03,
                    businessActivitySummary = "Electric vehicles & solar storage. Auto leasing interest structures and regulatory credit sales under evaluation.",
                    lastAuditDate = "Review in Progress"
                ),
                baseTrend = 0.0015,
                volatility = 0.028
            ),

            // 4. NON-COMPLIANT BENCHMARKS & EXCLUSIONS
            createStock(
                symbol = "JPM",
                name = "JPMorgan Chase & Co.",
                sector = "Conventional Commercial Banking",
                currentPrice = 216.80,
                change = -1.40,
                changePercent = -0.64,
                dayHigh = 219.00,
                dayLow = 215.10,
                volume = 9400000,
                marketCap = "620.5B",
                peRatio = 12.4,
                dividendYield = 2.12,
                shariaReport = ShariaReport(
                    isCompliant = false,
                    status = ComplianceStatus.NON_COMPLIANT_PROHIBITED_SECTOR,
                    isZeroDebt = false,
                    totalDebtUsdMillion = 412000.0,
                    marketCapUsdMillion = 620500.0,
                    debtToMarketCapRatio = 0.664, // 66.4%
                    cashAndInterestSecuritiesRatio = 0.85,
                    impureRevenuePercent = 0.92,
                    purificationPerShare = 0.0,
                    prohibitedSectorsViolated = listOf(ProhibitedSector.INTEREST_BANKING),
                    businessActivitySummary = "Primary core business is conventional commercial and investment banking deriving revenue directly from interest (Riba). Strictly prohibited.",
                    lastAuditDate = "Excluded per AAOIFI Std 21"
                ),
                baseTrend = 0.0004,
                volatility = 0.014
            ),
            createStock(
                symbol = "LMT",
                name = "Lockheed Martin Corp.",
                sector = "Aerospace & Defense Contractor",
                currentPrice = 568.20,
                change = 3.50,
                changePercent = 0.62,
                dayHigh = 571.40,
                dayLow = 564.00,
                volume = 1200000,
                marketCap = "136.2B",
                peRatio = 20.8,
                dividendYield = 2.22,
                shariaReport = ShariaReport(
                    isCompliant = false,
                    status = ComplianceStatus.NON_COMPLIANT_PROHIBITED_SECTOR,
                    isZeroDebt = false,
                    totalDebtUsdMillion = 21400.0,
                    marketCapUsdMillion = 136200.0,
                    debtToMarketCapRatio = 0.157,
                    cashAndInterestSecuritiesRatio = 0.021,
                    impureRevenuePercent = 0.88,
                    purificationPerShare = 0.0,
                    prohibitedSectorsViolated = listOf(ProhibitedSector.DEFENSE_WEAPONS),
                    businessActivitySummary = "Primary manufacturer of military munitions, ballistic missiles, and defense warplanes. Violates ethical screen.",
                    lastAuditDate = "Excluded per Shariah Screening"
                ),
                baseTrend = 0.0005,
                volatility = 0.012
            ),
            createStock(
                symbol = "PM",
                name = "Philip Morris International",
                sector = "Tobacco & Nicotine",
                currentPrice = 123.40,
                change = 0.40,
                changePercent = 0.33,
                dayHigh = 124.20,
                dayLow = 122.50,
                volume = 4100000,
                marketCap = "191.4B",
                peRatio = 21.3,
                dividendYield = 4.21,
                shariaReport = ShariaReport(
                    isCompliant = false,
                    status = ComplianceStatus.NON_COMPLIANT_PROHIBITED_SECTOR,
                    isZeroDebt = false,
                    totalDebtUsdMillion = 46200.0,
                    marketCapUsdMillion = 191400.0,
                    debtToMarketCapRatio = 0.241,
                    cashAndInterestSecuritiesRatio = 0.025,
                    impureRevenuePercent = 0.98,
                    purificationPerShare = 0.0,
                    prohibitedSectorsViolated = listOf(ProhibitedSector.TOBACCO),
                    businessActivitySummary = "Manufacturing and distribution of cigarettes and heated tobacco. Prohibited under ethical health criteria.",
                    lastAuditDate = "Excluded per Shariah Screening"
                ),
                baseTrend = 0.0003,
                volatility = 0.010
            ),
            createStock(
                symbol = "WYNN",
                name = "Wynn Resorts Ltd.",
                sector = "Casinos & Gaming Resorts",
                currentPrice = 97.20,
                change = -1.80,
                changePercent = -1.82,
                dayHigh = 99.10,
                dayLow = 96.40,
                volume = 1800000,
                marketCap = "10.8B",
                peRatio = 14.5,
                dividendYield = 1.03,
                shariaReport = ShariaReport(
                    isCompliant = false,
                    status = ComplianceStatus.NON_COMPLIANT_PROHIBITED_SECTOR,
                    isZeroDebt = false,
                    totalDebtUsdMillion = 11800.0,
                    marketCapUsdMillion = 10800.0,
                    debtToMarketCapRatio = 1.092, // 109% (Excessive debt)
                    cashAndInterestSecuritiesRatio = 0.18,
                    impureRevenuePercent = 0.74,
                    purificationPerShare = 0.0,
                    prohibitedSectorsViolated = listOf(ProhibitedSector.GAMBLING, ProhibitedSector.ALCOHOL),
                    businessActivitySummary = "Casino gaming, betting, sports wagering, and alcoholic hospitality. Also fails financial debt test (debt exceeds market cap).",
                    lastAuditDate = "Excluded per AAOIFI Std 21"
                ),
                baseTrend = -0.0002,
                volatility = 0.026
            ),
            createStock(
                symbol = "BUD",
                name = "Anheuser-Busch InBev",
                sector = "Brewery & Alcoholic Beverages",
                currentPrice = 64.10,
                change = 0.20,
                changePercent = 0.31,
                dayHigh = 64.60,
                dayLow = 63.80,
                volume = 1500000,
                marketCap = "128.4B",
                peRatio = 19.8,
                dividendYield = 1.48,
                shariaReport = ShariaReport(
                    isCompliant = false,
                    status = ComplianceStatus.NON_COMPLIANT_PROHIBITED_SECTOR,
                    isZeroDebt = false,
                    totalDebtUsdMillion = 78200.0,
                    marketCapUsdMillion = 128400.0,
                    debtToMarketCapRatio = 0.609, // 60.9%
                    cashAndInterestSecuritiesRatio = 0.08,
                    impureRevenuePercent = 0.96,
                    purificationPerShare = 0.0,
                    prohibitedSectorsViolated = listOf(ProhibitedSector.ALCOHOL),
                    businessActivitySummary = "Global brewery producing beer and alcoholic beverages. Prohibited industry.",
                    lastAuditDate = "Excluded per Shariah Screening"
                ),
                baseTrend = 0.0001,
                volatility = 0.011
            )
        )
    }

    private fun createStock(
        symbol: String,
        name: String,
        sector: String,
        currentPrice: Double,
        change: Double,
        changePercent: Double,
        dayHigh: Double,
        dayLow: Double,
        volume: Long,
        marketCap: String,
        peRatio: Double,
        dividendYield: Double,
        shariaReport: ShariaReport,
        baseTrend: Double,
        volatility: Double
    ): Stock {
        val candles = generateHistoricalCandles(currentPrice, 180, baseTrend, volatility)
        return Stock(
            symbol = symbol,
            name = name,
            sector = sector,
            currentPrice = currentPrice,
            change = change,
            changePercent = changePercent,
            dayHigh = dayHigh,
            dayLow = dayLow,
            volume = volume,
            marketCapFormatted = marketCap,
            peRatio = peRatio,
            dividendYield = dividendYield,
            shariaReport = shariaReport,
            chartData = candles
        )
    }

    private fun generateHistoricalCandles(
        finalPrice: Double,
        days: Int,
        drift: Double,
        volatility: Double
    ): List<CandlePoint> {
        val list = ArrayList<CandlePoint>(days)
        val now = System.currentTimeMillis()
        val oneDayMs = 86400000L
        val dateFormat = SimpleDateFormat("MMM dd", Locale.US)

        // Generate backwards from final price to ensure last candle matches current price
        val random = Random(symbolSeed(finalPrice))
        val prices = DoubleArray(days)
        prices[days - 1] = finalPrice

        for (i in days - 2 downTo 0) {
            val shock = random.nextGaussian() * volatility
            val ret = drift + shock
            // backward calculation
            prices[i] = (prices[i + 1] / (1.0 + ret)).coerceAtLeast(1.0)
        }

        for (i in 0 until days) {
            val timestamp = now - (days - 1 - i) * oneDayMs
            val close = prices[i]
            val dayRand = Random((timestamp xor (close * 100).toLong()).toInt())
            val open = if (i > 0) prices[i - 1] else close * (1.0 - dayRand.nextDouble(-0.01, 0.01))
            val high = maxOf(open, close) * (1.0 + dayRand.nextDouble(0.002, 0.018))
            val low = minOf(open, close) * (1.0 - dayRand.nextDouble(0.002, 0.018))
            val vol = (1000000L + dayRand.nextLong(0, 4000000L))

            list.add(
                CandlePoint(
                    timestamp = timestamp,
                    dateLabel = dateFormat.format(Date(timestamp)),
                    open = Math.round(open * 100.0) / 100.0,
                    high = Math.round(high * 100.0) / 100.0,
                    low = Math.round(low * 100.0) / 100.0,
                    close = Math.round(close * 100.0) / 100.0,
                    volume = vol
                )
            )
        }
        return list
    }

    private fun symbolSeed(price: Double): Int {
        return (price * 1000).toInt()
    }

    fun getInitialAlerts(): List<ComplianceAlert> {
        val now = System.currentTimeMillis()
        return listOf(
            ComplianceAlert(
                id = "alert_1",
                symbol = "ISRG",
                companyName = "Intuitive Surgical",
                oldStatus = ComplianceStatus.HALAL_LOW_DEBT,
                newStatus = ComplianceStatus.ZERO_DEBT_HALAL,
                headline = "Certified 100% Zero Debt",
                reason = "Annual 10-K audit confirmed complete elimination of all credit facilities and zero debt balance sheet with $8.2B liquid cash.",
                timestamp = now - 1800000, // 30m ago
                formattedTime = "30m ago",
                severity = AlertSeverity.POSITIVE,
                isRead = false
            ),
            ComplianceAlert(
                id = "alert_2",
                symbol = "AAPL",
                companyName = "Apple Inc.",
                oldStatus = ComplianceStatus.HALAL_LOW_DEBT,
                newStatus = ComplianceStatus.UNDER_REVIEW,
                headline = "Placed Under Shariah Review",
                reason = "New $12B green bond issuance increased total debt to market ratio near 31.8% plus financial services yield concerns.",
                timestamp = now - 7200000, // 2h ago
                formattedTime = "2h ago",
                severity = AlertSeverity.WARNING,
                isRead = false
            ),
            ComplianceAlert(
                id = "alert_3",
                symbol = "VRTX",
                companyName = "Vertex Pharmaceuticals",
                oldStatus = ComplianceStatus.ZERO_DEBT_HALAL,
                newStatus = ComplianceStatus.ZERO_DEBT_HALAL,
                headline = "Zero-Debt Status Re-Certified",
                reason = "Quarterly Shariah supervisory board audit verified 0.00% debt-to-equity and zero revenue from non-permissible sources.",
                timestamp = now - 86400000, // 1d ago
                formattedTime = "1d ago",
                severity = AlertSeverity.INFO,
                isRead = true
            ),
            ComplianceAlert(
                id = "alert_4",
                symbol = "WYNN",
                companyName = "Wynn Resorts",
                oldStatus = ComplianceStatus.UNDER_REVIEW,
                newStatus = ComplianceStatus.NON_COMPLIANT_PROHIBITED_SECTOR,
                headline = "Reclassified: Non-Compliant",
                reason = "Expanded high-stakes gaming concession and debt ratio exceeded 100% of market capitalization.",
                timestamp = now - 172800000, // 2d ago
                formattedTime = "2d ago",
                severity = AlertSeverity.CRITICAL,
                isRead = true
            )
        )
    }

    private fun Random.nextGaussian(): Double {
        var v1: Double
        var v2: Double
        var s: Double
        do {
            v1 = 2 * nextDouble() - 1
            v2 = 2 * nextDouble() - 1
            s = v1 * v1 + v2 * v2
        } while (s >= 1 || s == 0.0)
        val multiplier = Math.sqrt(-2 * Math.log(s) / s)
        return v1 * multiplier
    }
}
