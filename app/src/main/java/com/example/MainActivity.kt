package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.MainViewModel
import com.example.ui.screens.EthicalScreenerScreen
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                EthicalStockApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EthicalStockApp(
    viewModel: MainViewModel = viewModel()
) {
    val allStocks by viewModel.allStocks.collectAsStateWithLifecycle()
    val filteredStocks by viewModel.filteredStocks.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedFilter by viewModel.selectedFilter.collectAsStateWithLifecycle()
    val selectedStock by viewModel.selectedStock.collectAsStateWithLifecycle()
    val stressDebtDelta by viewModel.stressDebtDelta.collectAsStateWithLifecycle()
    val purificationSharesInput by viewModel.purificationSharesInput.collectAsStateWithLifecycle()
    val isRefreshing by viewModel.isRefreshing.collectAsStateWithLifecycle()
    val lastSyncTime by viewModel.lastSyncTime.collectAsStateWithLifecycle()
    val onlineSearchResults by viewModel.onlineSearchResults.collectAsStateWithLifecycle()
    val isSearchingOnline by viewModel.isSearchingOnline.collectAsStateWithLifecycle()
    val isFetchingLiveProfile by viewModel.isFetchingLiveProfile.collectAsStateWithLifecycle()
    val onlineFetchError by viewModel.onlineFetchError.collectAsStateWithLifecycle()

    val halalCount = allStocks.count { it.audit.isFullyCompliant }
    val zeroDebtCount = allStocks.count { it.audit.isFullyCompliant && it.balanceSheet.isZeroDebt }
    val haramCount = allStocks.count { !it.audit.isFullyCompliant }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF0C2E21))
                                .border(1.dp, Color(0xFFF59E0B), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Security,
                                contentDescription = null,
                                tint = Color(0xFFF59E0B),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "AAOIFI SHARIAH SCREENER",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = "AAOIFI Standard 21 • Halal & Haram Classifications",
                                fontSize = 10.sp,
                                color = Color(0xFF34D399)
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFF040D0A)
                ),
                modifier = Modifier.testTag("screener_top_bar")
            )
        }
    ) { innerPadding ->
        EthicalScreenerScreen(
            stocks = filteredStocks,
            allStocksCount = allStocks.size,
            halalCount = halalCount,
            zeroDebtCount = zeroDebtCount,
            haramCount = haramCount,
            searchQuery = searchQuery,
            onSearchQueryChange = viewModel::onSearchQueryChange,
            onlineSearchResults = onlineSearchResults,
            isSearchingOnline = isSearchingOnline,
            isFetchingLiveProfile = isFetchingLiveProfile,
            onlineFetchError = onlineFetchError,
            onClearFetchError = viewModel::clearOnlineFetchError,
            onFetchStock = viewModel::fetchAndSelectStock,
            selectedFilter = selectedFilter,
            onFilterSelected = viewModel::onFilterSelected,
            selectedStock = selectedStock,
            onSelectStock = viewModel::onSelectStock,
            stressDebtDelta = stressDebtDelta,
            onStressDebtDeltaChange = viewModel::onStressDebtDeltaChange,
            purificationSharesInput = purificationSharesInput,
            onPurificationSharesChange = viewModel::onPurificationSharesChange,
            isRefreshing = isRefreshing,
            lastSyncTime = lastSyncTime,
            onRefresh = viewModel::loadData,
            modifier = Modifier.padding(innerPadding)
        )
    }
}
