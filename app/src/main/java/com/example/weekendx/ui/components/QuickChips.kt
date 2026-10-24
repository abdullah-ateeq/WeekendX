package com.example.weekendx.ui.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.weekendx.ui.home.QuickRangeType
import com.example.weekendx.util.HapticUtil

@Composable
fun QuickChips(
    hapticsEnabled: Boolean,
    onSelectRange: (QuickRangeType) -> Unit,
    modifier: Modifier = Modifier
) {
    val view = LocalView.current
    val scrollState = rememberScrollState()

    val options = listOf(
        QuickRangeType.TODAY to "TODAY",
        QuickRangeType.THIS_WEEK to "THIS WEEK",
        QuickRangeType.THIS_MONTH to "THIS MONTH",
        QuickRangeType.NEXT_MONTH to "NEXT MONTH",
        QuickRangeType.NEXT_30_DAYS to "NEXT 30 DAYS",
        QuickRangeType.NEXT_90_DAYS to "NEXT 90 DAYS"
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(scrollState)
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        options.forEach { (type, label) ->
            FilterChip(
                selected = false,
                onClick = {
                    HapticUtil.performLightHaptic(view, hapticsEnabled)
                    onSelectRange(type)
                },
                label = {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
    }
}
