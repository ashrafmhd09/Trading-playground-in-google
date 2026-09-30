package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.StockProfile
import com.example.data.model.YahooSearchResult
import com.example.ui.ScreenerFilter
import com.example.ui.components.StockCard
import com.example.ui.components.StockDetailSheet

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EthicalScreenerScreen(
    stocks: List<StockProfile>,
    allStocksCount: Int,
    halalCount: Int,
    zeroDebtCount: Int,
    haramCount: Int,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onlineSearchResults: List<YahooSearchResult>,
    isSearchingOnline: Boolean,
    isFetchingLiveProfile: Boolean,
    onlineFetchError: String?,
    onClearFetchError: () -> Unit,
    onFetchStock: (String, String?) -> Unit,
    selectedFilter: ScreenerFilter,
    onFilterSelected: (ScreenerFilter) -> Unit,
    selectedStock: StockProfile?,
    onSelectStock: (StockProfile?) -> Unit,
    stressDebtDelta: Double,
    onStressDebtDeltaChange: (Double) -> Unit,
    purificationSharesInput: String,
    onPurificationSharesChange: (String) -> Unit,
    isRefreshing: Boolean,
    lastSyncTime: String,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    Box(modifier = modifier.fillMaxSize().background(Color(0xFF040D0A))) {
        Column(modifier = Modifier.fillMaxSize().padding(horizontal = 14.dp)) {
            Spacer(modifier = Modifier.height(6.dp))

            // Top Status Bar: Yahoo Finance Source of Truth Badge & Refresh
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFF0A2219))
                    .border(1.dp, Color(0xFF134E39), RoundedCornerShape(10.dp))
                    .padding(horizontal = 12.dp, vertical = 7.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF10B981))
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Column {
                        Text(
                            text = "SOURCE OF TRUTH: YAHOO FINANCE",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF34D399),
                            letterSpacing = 0.5.sp
                        )
                        Text(
                            text = "Live API Quotes & Official Balance Sheets • $lastSyncTime",
                            fontSize = 9.sp,
                            color = Color(0xFF9CA3AF)
                        )
                    }
                }

                IconButton(
                    onClick = onRefresh,
                    modifier = Modifier.size(28.dp).testTag("button_refresh_quotes")
                ) {
                    if (isRefreshing) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            color = Color(0xFF34D399),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Filled.Refresh,
                            contentDescription = "Refresh live Yahoo data",
                            tint = Color(0xFF34D399),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Search Bar (Requirement 2: Search by Symbol OR Name)
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                placeholder = {
                    Text(
                        text = "Search any ticker (AAPL, TSLA, ISRG) or name...",
                        fontSize = 13.sp,
                        color = Color(0xFF6B7280)
                    )
                },
                leadingIcon = {
                    if (isSearchingOnline) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            color = Color(0xFF34D399),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Filled.Search,
                            contentDescription = "Search",
                            tint = Color(0xFF34D399)
                        )
                    }
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { onSearchQueryChange("") }) {
                            Icon(
                                imageVector = Icons.Filled.Clear,
                                contentDescription = "Clear Search",
                                tint = Color(0xFF9CA3AF)
                            )
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF10B981),
                    unfocusedBorderColor = Color(0xFF1F352C),
                    focusedContainerColor = Color(0xFF091C15),
                    unfocusedContainerColor = Color(0xFF091C15),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("search_stock_input")
            )

            // Loading state while pulling full live profile from Yahoo
            if (isFetchingLiveProfile) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF0D281E))
                        .border(1.dp, Color(0xFF10B981), RoundedCornerShape(8.dp))
                        .padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        color = Color(0xFF34D399),
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Fetching balance sheet & analyzing from Yahoo Finance API...",
                        fontSize = 11.sp,
                        color = Color(0xFF34D399),
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Error message if online fetch fails
            if (onlineFetchError != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF3B1212))
                        .border(1.dp, Color(0xFFEF4444), RoundedCornerShape(8.dp))
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.ErrorOutline, contentDescription = null, tint = Color(0xFFEF4444), modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = onlineFetchError, fontSize = 11.sp, color = Color(0xFFFCA5A5))
                    }
                    IconButton(onClick = onClearFetchError, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Filled.Clear, contentDescription = "Dismiss", tint = Color(0xFFFCA5A5), modifier = Modifier.size(14.dp))
                    }
                }
            }

            // Live Yahoo Search Suggestions Box
            if (onlineSearchResults.isNotEmpty() && !isFetchingLiveProfile) {
                Spacer(modifier = Modifier.height(8.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFF081913))
                        .border(1.dp, Color(0xFF134E39), RoundedCornerShape(10.dp))
                        .padding(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.Bolt, contentDescription = null, tint = Color(0xFFF59E0B), modifier = Modifier.size(13.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "YAHOO FINANCE LIVE RESULTS",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF34D399),
                                letterSpacing = 0.5.sp
                            )
                        }
                        Text(text = "Tap to pull & screen", fontSize = 9.sp, color = Color(0xFF9CA3AF))
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    onlineSearchResults.take(4).forEach { res ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(6.dp))
                                .clickable { onFetchStock(res.symbol, res.name) }
                                .padding(vertical = 5.dp, horizontal = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = res.symbol,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                    if (res.exchange.isNotEmpty()) {
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(text = res.exchange, fontSize = 9.sp, color = Color(0xFF9CA3AF))
                                    }
                                }
                                Text(
                                    text = res.name,
                                    fontSize = 11.sp,
                                    color = Color(0xFF9CA3AF),
                                    maxLines = 1
                                )
                            }

                            Button(
                                onClick = { onFetchStock(res.symbol, res.name) },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                                shape = RoundedCornerShape(6.dp),
                                modifier = Modifier.height(28.dp)
                            ) {
                                Text("Analyze", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // Direct "Fetch from Yahoo" Banner if query is typed
            if (searchQuery.trim().length >= 2 && onlineSearchResults.isEmpty() && !isFetchingLiveProfile) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF0A241B))
                        .border(1.dp, Color(0xFF134E39), RoundedCornerShape(8.dp))
                        .clickable { onFetchStock(searchQuery.trim().uppercase(), null) }
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.CloudDownload, contentDescription = null, tint = Color(0xFF34D399), modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Fetch '${searchQuery.trim().uppercase()}' live from Yahoo API",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                    }

                    Text(text = "Fetch ➔", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF34D399))
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Screener Filter Chips Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                ScreenerFilter.entries.forEach { filter ->
                    val isSelected = selectedFilter == filter
                    val countBadge = when (filter) {
                        ScreenerFilter.ALL -> allStocksCount
                        ScreenerFilter.HALAL -> halalCount
                        ScreenerFilter.ZERO_DEBT -> zeroDebtCount
                        ScreenerFilter.HARAM -> haramCount
                        else -> null
                    }

                    FilterChip(
                        selected = isSelected,
                        onClick = { onFilterSelected(filter) },
                        label = {
                            Text(
                                text = "${filter.label}${if (countBadge != null) " ($countBadge)" else ""}",
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFF10B981).copy(alpha = 0.25f),
                            selectedLabelColor = Color(0xFF34D399),
                            containerColor = Color(0xFF091C15),
                            labelColor = Color(0xFF9CA3AF)
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = isSelected,
                            borderColor = if (isSelected) Color(0xFF10B981) else Color(0xFF1F352C),
                            borderWidth = 1.dp
                        ),
                        modifier = Modifier.testTag("filter_chip_${filter.name}")
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // List of Stocks
            if (stocks.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Filled.Search,
                            contentDescription = null,
                            tint = Color(0xFF4B5563),
                            modifier = Modifier.size(42.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "No local matching stocks found",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF9CA3AF)
                        )
                        Text(
                            text = "Type any ticker above to fetch directly from Yahoo Finance",
                            fontSize = 11.sp,
                            color = Color(0xFF6B7280)
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .testTag("stocks_list"),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(stocks, key = { it.symbol }) { stock ->
                        StockCard(
                            stock = stock,
                            onClick = { onSelectStock(stock) }
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(20.dp))
                    }
                }
            }
        }

        // Modal Sheet for Deep-Dive Analysis (Yahoo Balance Sheet, Checklist, AI Predictions, Real-Time Calculator)
        if (selectedStock != null) {
            ModalBottomSheet(
                onDismissRequest = { onSelectStock(null) },
                sheetState = sheetState,
                containerColor = Color(0xFF091712),
                scrimColor = Color.Black.copy(alpha = 0.7f)
            ) {
                StockDetailSheet(
                    stock = selectedStock,
                    stressDebtDelta = stressDebtDelta,
                    onStressDebtDeltaChange = onStressDebtDeltaChange,
                    purificationSharesInput = purificationSharesInput,
                    onPurificationSharesChange = onPurificationSharesChange,
                    onDismiss = { onSelectStock(null) }
                )
            }
        }
    }
}
