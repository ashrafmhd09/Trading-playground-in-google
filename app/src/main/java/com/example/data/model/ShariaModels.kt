package com.example.data.model

enum class ComplianceStatus(val label: String, val badgeText: String) {
    ZERO_DEBT_HALAL("100% Zero-Debt Halal", "ZERO DEBT"),
    HALAL_LOW_DEBT("Shariah Compliant (Low Debt)", "HALAL"),
    UNDER_REVIEW("Under Shariah Audit", "REVIEW"),
    NON_COMPLIANT_PROHIBITED_SECTOR("Excluded (Prohibited Industry)", "NON-HALAL"),
    NON_COMPLIANT_HIGH_DEBT("Excluded (Excessive Debt / Interest)", "HIGH DEBT")
}

enum class ProhibitedSector(val title: String, val description: String) {
    GAMBLING("Gambling & Casinos", "Revenue from betting, lotteries, or casino gaming"),
    INTEREST_BANKING("Interest-Based Banking", "Conventional finance generating or paying Riba (interest)"),
    TOBACCO("Tobacco & Nicotine", "Production or wholesale distribution of tobacco products"),
    DEFENSE_WEAPONS("Defense & Armaments", "Manufacture of military defense weaponry and lethal munitions"),
    ALCOHOL("Alcoholic Beverages", "Production, distribution, or marketing of alcohol"),
    ADULT_ENTERTAINMENT("Adult Entertainment", "Explicit content and unwholesome media"),
    CONVENTIONAL_INSURANCE("Conventional Insurance", "Insurance models involving Gharar (uncertainty) and Maysir"),
    PORK_PRODUCTS("Pork Products", "Processing or sale of non-halal meat")
}

data class ShariaReport(
    val isCompliant: Boolean,
    val status: ComplianceStatus,
    val isZeroDebt: Boolean,
    val totalDebtUsdMillion: Double,
    val marketCapUsdMillion: Double,
    val debtToMarketCapRatio: Double, // e.g. 0.00 for zero debt, or 0.12 for 12%
    val cashAndInterestSecuritiesRatio: Double, // max 33% standard
    val impureRevenuePercent: Double, // max 5% threshold
    val purificationPerShare: Double, // amount in $ to donate per share
    val prohibitedSectorsViolated: List<ProhibitedSector> = emptyList(),
    val businessActivitySummary: String,
    val lastAuditDate: String,
    val certifiedBy: String = "AAOIFI & Shariah Standards Board"
)
