package com.example.weekendx.ui.history

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.weekendx.data.model.WeekendXHistory
import com.example.weekendx.ui.components.ConfirmDialog
import com.example.weekendx.ui.home.WeekendXViewModel
import com.example.weekendx.util.HapticUtil
import java.text.SimpleDateFormat
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    viewModel: WeekendXViewModel,
    onNavigateToHome: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val view = LocalView.current
    var itemToDelete by remember { mutableStateOf<WeekendXHistory?>(null) }
    var showClearConfirm by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Saved History",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                    if (uiState.history.isNotEmpty()) {
                        IconButton(onClick = {
                            if (uiState.settings.confirmDelete) {
                                showClearConfirm = true
                            } else {
                                viewModel.clearHistory()
                            }
                        }) {
                            Icon(
                                imageVector = Icons.Default.DeleteSweep,
                                contentDescription = "Clear all history"
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { paddingValues ->
        if (uiState.history.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.History,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "No saved searches",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Find weekends on Home and tap Save to keep up to 10 searches offline.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.secondary,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(uiState.history, key = { it.id }) { item ->
                    HistoryCard(
                        item = item,
                        viewModel = viewModel,
                        onClickRestore = {
                            HapticUtil.performLightHaptic(view, uiState.settings.hapticFeedback)
                            viewModel.restoreHistory(item)
                            onNavigateToHome()
                        },
                        onClickDelete = {
                            HapticUtil.performLightHaptic(view, uiState.settings.hapticFeedback)
                            if (uiState.settings.confirmDelete) {
                                itemToDelete = item
                            } else {
                                viewModel.deleteHistory(item.id)
                            }
                        }
                    )
                }
            }
        }
    }

    // Confirmation for single item deletion
    itemToDelete?.let { entry ->
        ConfirmDialog(
            title = "Delete Saved Search?",
            message = "Are you sure you want to delete this saved search?",
            confirmText = "Delete",
            onConfirm = {
                viewModel.deleteHistory(entry.id)
                itemToDelete = null
            },
            onDismiss = { itemToDelete = null }
        )
    }

    // Confirmation for clear all
    if (showClearConfirm) {
        ConfirmDialog(
            title = "Clear All Searches?",
            message = "Clear all saved searches? This action cannot be undone.",
            confirmText = "Clear",
            onConfirm = {
                viewModel.clearHistory()
                showClearConfirm = false
            },
            onDismiss = { showClearConfirm = false }
        )
    }
}

@Composable
private fun HistoryCard(
    item: WeekendXHistory,
    viewModel: WeekendXViewModel,
    onClickRestore: () -> Unit,
    onClickDelete: () -> Unit
) {
    val startDate = runCatching { LocalDate.parse(item.startDate) }.getOrNull()
    val endDate = runCatching { LocalDate.parse(item.endDate) }.getOrNull()

    val startFormatted = startDate?.let { viewModel.formatter.formatDate(it, viewModel.uiState.value.settings.dateFormat) } ?: item.startDate
    val endFormatted = endDate?.let { viewModel.formatter.formatDate(it, viewModel.uiState.value.settings.dateFormat) } ?: item.endDate

    val weekendDaysLabel = item.weekendDays.mapNotNull {
        runCatching { DayOfWeek.of(it).getDisplayName(TextStyle.SHORT, Locale.getDefault()) }.getOrNull()
    }.joinToString(" + ")

    val savedDateFormatted = SimpleDateFormat("MMM d, yyyy · HH:mm", Locale.getDefault()).format(Date(item.savedAt))

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClickRestore() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "$startFormatted → $endFormatted",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                IconButton(
                    onClick = onClickDelete,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete search",
                        tint = MaterialTheme.colorScheme.error.copy(alpha = 0.8f),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Text(
                        text = weekendDaysLabel,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }

                Text(
                    text = savedDateFormatted,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }
    }
}
