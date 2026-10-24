package com.example.weekendx.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.weekendx.data.model.DateFormatOption
import com.example.weekendx.data.model.WeekendGroup
import com.example.weekendx.domain.formatter.WeekendFormatter
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun WeekendGroupList(
    groups: List<WeekendGroup>,
    dateFormat: DateFormatOption,
    formatter: WeekendFormatter,
    onDateClick: (LocalDate) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        groups.forEach { group ->
            val firstDateFormatted = if (group.dates.isNotEmpty()) formatter.formatDate(group.dates.first(), dateFormat) else ""
            val lastDateFormatted = if (group.dates.isNotEmpty()) formatter.formatDate(group.dates.last(), dateFormat) else ""

            val groupDesc = if (group.isComplete) {
                "Complete weekend, $firstDateFormatted through $lastDateFormatted."
            } else {
                "Partial weekend, ${group.dates.joinToString { formatter.formatDateWithDay(it, dateFormat) }}."
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .semantics { contentDescription = groupDesc },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp)
                ) {
                    // Header row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "WEEKEND ${group.groupNumber}".uppercase(),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.2.sp,
                            color = MaterialTheme.colorScheme.primary
                        )

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (group.isComplete) {
                                MaterialTheme.colorScheme.primaryContainer
                            } else {
                                MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.7f)
                            }
                        ) {
                            Text(
                                text = if (group.isComplete) "Complete" else "Partial",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = if (group.isComplete) {
                                    MaterialTheme.colorScheme.onPrimaryContainer
                                } else {
                                    MaterialTheme.colorScheme.onErrorContainer
                                },
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                    Spacer(modifier = Modifier.height(8.dp))

                    // Dates in this weekend group
                    group.dates.forEachIndexed { index, date ->
                        val dateFormatted = formatter.formatDate(date, dateFormat)
                        val dow = date.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.getDefault())
                        val itemDesc = "$dow, $dateFormatted, weekend."

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { onDateClick(date) }
                                .padding(vertical = 6.dp, horizontal = 4.dp)
                                .semantics { contentDescription = itemDesc },
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = dateFormatted,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = dow,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Normal,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }

                        if (index < group.dates.lastIndex) {
                            Spacer(modifier = Modifier.height(4.dp))
                        }
                    }
                }
            }
        }
    }
}
