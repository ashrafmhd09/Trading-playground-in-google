package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.ExclusionCategory
import com.example.data.model.StockProfile
import com.example.data.repository.YahooFinanceRepository
import com.example.domain.OnDeviceAiPredictor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class ScreenerFilter(val label: String) {
    ALL("All Stocks"),
    HALAL("Halal Only (AAOIFI)"),
    HARAM("Haram Only"),
    ZERO_DEBT("0% Debt Halal"),
    EXCLUDED_DEFENSE("Defense & Weapons"),
    EXCLUDED_INTEREST("Conventional Banks (Riba)"),
    EXCLUDED_VICE("Gambling / Alcohol / Pork")
}

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = YahooFinanceRepository()

    private val _allStocks = MutableStateFlow<List<StockProfile>>(emptyList())
    val allStocks: StateFlow<List<StockProfile>> = _allStocks.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _onlineSearchResults = MutableStateFlow<List<com.example.data.model.YahooSearchResult>>(emptyList())
    val onlineSearchResults: StateFlow<List<com.example.data.model.YahooSearchResult>> = _onlineSearchResults.asStateFlow()

    private val _isSearchingOnline = MutableStateFlow(false)
    val isSearchingOnline: StateFlow<Boolean> = _isSearchingOnline.asStateFlow()

    private val _isFetchingLiveProfile = MutableStateFlow(false)
    val isFetchingLiveProfile: StateFlow<Boolean> = _isFetchingLiveProfile.asStateFlow()

    private val _onlineFetchError = MutableStateFlow<String?>(null)
    val onlineFetchError: StateFlow<String?> = _onlineFetchError.asStateFlow()

    private var onlineSearchJob: kotlinx.coroutines.Job? = null

    private val _selectedFilter = MutableStateFlow(ScreenerFilter.ALL)
    val selectedFilter: StateFlow<ScreenerFilter> = _selectedFilter.asStateFlow()

    private val _selectedStock = MutableStateFlow<StockProfile?>(null)
    val selectedStock: StateFlow<StockProfile?> = _selectedStock.asStateFlow()

    // Real-time On-Device AI Stress Test Parameters
    private val _stressDebtDelta = MutableStateFlow(0.0) // 0% to +20% simulated debt increase
    val stressDebtDelta: StateFlow<Double> = _stressDebtDelta.asStateFlow()

    private val _volatilityMultiplier = MutableStateFlow(1.0) // 0.5x to 2.0x
    val volatilityMultiplier: StateFlow<Double> = _volatilityMultiplier.asStateFlow()

    // Real-time dividend purification calculator input
    private val _purificationSharesInput = MutableStateFlow("100")
    val purificationSharesInput: StateFlow<String> = _purificationSharesInput.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    private val _lastSyncTime = MutableStateFlow(
        SimpleDateFormat("h:mm:ss a", Locale.US).format(Date())
    )
    val lastSyncTime: StateFlow<String> = _lastSyncTime.asStateFlow()

    val filteredStocks: StateFlow<List<StockProfile>> = combine(
        _allStocks,
        _searchQuery,
        _selectedFilter
    ) { stocks, query, filter ->
        val trimmedQuery = query.trim().lowercase(Locale.ROOT)

        stocks.filter { stock ->
            // Requirement 2: Search stock either with symbol OR name
            val matchesSearch = if (trimmedQuery.isEmpty()) {
                true
            } else {
                stock.symbol.lowercase(Locale.ROOT).contains(trimmedQuery) ||
                        stock.name.lowercase(Locale.ROOT).contains(trimmedQuery) ||
                        stock.sector.lowercase(Locale.ROOT).contains(trimmedQuery)
            }

            val matchesFilter = when (filter) {
                ScreenerFilter.ALL -> true
                ScreenerFilter.HALAL -> stock.audit.isFullyCompliant
                ScreenerFilter.HARAM -> !stock.audit.isFullyCompliant
                ScreenerFilter.ZERO_DEBT -> stock.audit.isFullyCompliant && stock.balanceSheet.isZeroDebt
                ScreenerFilter.EXCLUDED_DEFENSE -> stock.audit.violations.any { it.category == ExclusionCategory.DEFENSE }
                ScreenerFilter.EXCLUDED_INTEREST -> stock.audit.violations.any { it.category == ExclusionCategory.INTEREST_BASED }
                ScreenerFilter.EXCLUDED_VICE -> stock.audit.violations.any {
                    it.category == ExclusionCategory.GAMBLING ||
                            it.category == ExclusionCategory.ALCOHOL ||
                            it.category == ExclusionCategory.TOBACCO ||
                            it.category == ExclusionCategory.PORK
                }
            }

            matchesSearch && matchesFilter
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            _isRefreshing.value = true
            val loaded = repository.getUniverse(stressDebtDelta = _stressDebtDelta.value)
            _allStocks.value = loaded

            // If selected stock exists, refresh its reference
            _selectedStock.value?.let { current ->
                _selectedStock.value = loaded.firstOrNull { it.symbol == current.symbol }
            }

            _lastSyncTime.value = SimpleDateFormat("h:mm:ss a", Locale.US).format(Date())
            _isRefreshing.value = false

            // Concurrently query live quotes for top stocks from Yahoo Finance API
            fetchLiveYahooQuotes()
        }
    }

    private fun fetchLiveYahooQuotes() {
        viewModelScope.launch {
            val symbolsToFetch = listOf("ISRG", "NVDA", "MSFT", "GOOGL", "VRTX", "LMT", "JPM")
            val currentList = _allStocks.value.toMutableList()
            var hasUpdates = false

            for (symbol in symbolsToFetch) {
                val live = repository.fetchLiveYahooQuote(symbol)
                if (live != null) {
                    val idx = currentList.indexOfFirst { it.symbol == symbol }
                    if (idx != -1) {
                        val old = currentList[idx]
                        val (livePrice, change) = live
                        val pct = if (livePrice - change > 0) (change / (livePrice - change)) * 100.0 else 0.0

                        // Recalculate on-device AI prediction with latest live price
                        val updatedPrediction = OnDeviceAiPredictor.generatePrediction(
                            currentPrice = livePrice,
                            history = old.chartHistory,
                            debtRatioPercent = old.balanceSheet.debtToMarketCapPercent,
                            isEthicalCompliant = old.audit.isFullyCompliant,
                            stressDebtIncreasePercent = _stressDebtDelta.value,
                            volatilityMultiplier = _volatilityMultiplier.value
                        )

                        currentList[idx] = old.copy(
                            currentPrice = livePrice,
                            change = change,
                            changePercent = pct,
                            aiPrediction = updatedPrediction
                        )
                        hasUpdates = true
                    }
                }
            }

            if (hasUpdates) {
                _allStocks.value = currentList
                _selectedStock.value?.let { current ->
                    _selectedStock.value = currentList.firstOrNull { it.symbol == current.symbol }
                }
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
        _onlineFetchError.value = null

        onlineSearchJob?.cancel()
        val trimmed = query.trim()
        if (trimmed.length < 2) {
            _onlineSearchResults.value = emptyList()
            _isSearchingOnline.value = false
            return
        }

        onlineSearchJob = viewModelScope.launch {
            kotlinx.coroutines.delay(400) // 400ms debounce
            _isSearchingOnline.value = true
            val results = repository.searchYahooOnline(trimmed)
            _onlineSearchResults.value = results
            _isSearchingOnline.value = false
        }
    }

    /**
     * Fetches any arbitrary stock directly from Yahoo Finance live API,
     * evaluates its balance sheet and ethical screening, and selects it.
     */
    fun fetchAndSelectStock(symbol: String, name: String? = null) {
        viewModelScope.launch {
            _isFetchingLiveProfile.value = true
            _onlineFetchError.value = null

            val cleanSymbol = symbol.trim().uppercase()

            // Check if already in current list
            val existing = _allStocks.value.firstOrNull { it.symbol.equals(cleanSymbol, ignoreCase = true) }
            if (existing != null) {
                _selectedStock.value = existing
                _isFetchingLiveProfile.value = false
                return@launch
            }

            val profile = repository.fetchStockProfileFromYahoo(
                symbol = cleanSymbol,
                knownName = name,
                stressDebtDelta = _stressDebtDelta.value
            )

            if (profile != null) {
                val updatedList = _allStocks.value.toMutableList()
                val idx = updatedList.indexOfFirst { it.symbol.equals(profile.symbol, ignoreCase = true) }
                if (idx != -1) {
                    updatedList[idx] = profile
                } else {
                    updatedList.add(0, profile) // Insert at top
                }
                _allStocks.value = updatedList
                _selectedStock.value = profile
                _onlineSearchResults.value = emptyList()
            } else {
                _onlineFetchError.value = "Could not fetch ticker '$cleanSymbol' from Yahoo Finance. Verify the symbol and try again."
            }

            _isFetchingLiveProfile.value = false
        }
    }

    fun clearOnlineFetchError() {
        _onlineFetchError.value = null
    }

    fun onFilterSelected(filter: ScreenerFilter) {
        _selectedFilter.value = filter
    }

    fun onSelectStock(stock: StockProfile?) {
        _selectedStock.value = stock
    }

    /**
     * Real-time Interactive Debt Sensitivity Slider (Requirement 4).
     * Re-runs the On-Device AI models and compliance checks immediately in memory.
     */
    fun onStressDebtDeltaChange(delta: Double) {
        _stressDebtDelta.value = delta
        viewModelScope.launch {
            val updated = repository.getUniverse(stressDebtDelta = delta)
            _allStocks.value = updated
            _selectedStock.value?.let { current ->
                _selectedStock.value = updated.firstOrNull { it.symbol == current.symbol }
            }
        }
    }

    fun onVolatilityMultiplierChange(multiplier: Double) {
        _volatilityMultiplier.value = multiplier
        val currentStock = _selectedStock.value ?: return
        val updatedAi = OnDeviceAiPredictor.generatePrediction(
            currentPrice = currentStock.currentPrice,
            history = currentStock.chartHistory,
            debtRatioPercent = currentStock.balanceSheet.debtToMarketCapPercent,
            isEthicalCompliant = currentStock.audit.isFullyCompliant,
            stressDebtIncreasePercent = _stressDebtDelta.value,
            volatilityMultiplier = multiplier
        )
        _selectedStock.value = currentStock.copy(aiPrediction = updatedAi)
    }

    fun onPurificationSharesChange(input: String) {
        if (input.all { it.isDigit() }) {
            _purificationSharesInput.value = input
        }
    }
}
