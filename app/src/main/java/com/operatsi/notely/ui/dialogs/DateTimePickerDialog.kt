package com.operatsi.notely.ui.dialogs

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.operatsi.notely.R
import com.operatsi.notely.ui.theme.Dimens
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

data class DateOption(
    val label: String,
    val calendar: Calendar
)

@Composable
fun DateTimePickerDialog(
    title: String = "Set Reminder",
    onDismiss: () -> Unit,
    onSetReminder: (displayText: String, timestamp: Long) -> Unit
) {
    // 14 upcoming days
    val dateOptions = remember {
        val list = mutableListOf<DateOption>()
        val cal = Calendar.getInstance()
        val dayFormat = SimpleDateFormat("EEE, MMM d", Locale.getDefault())

        for (i in 0..13) {
            val c = cal.clone() as Calendar
            c.add(Calendar.DAY_OF_YEAR, i)
            val label = when (i) {
                0 -> "Today"
                1 -> "Tomorrow"
                else -> dayFormat.format(c.time)
            }
            list.add(DateOption(label, c))
        }
        list
    }

    var selectedDateIndex by remember { mutableIntStateOf(0) } // Default to Today
    var selectedHour by remember {
        val currentH = Calendar.getInstance().get(Calendar.HOUR)
        mutableIntStateOf(if (currentH == 0) 12 else currentH)
    } // 1-12
    var selectedMinute by remember { mutableIntStateOf(0) } // 0, 5, 10, ..., 55
    var isAm by remember {
        val amPm = Calendar.getInstance().get(Calendar.AM_PM)
        mutableStateOf(amPm == Calendar.AM)
    } // AM / PM

    val computedCustomTime = remember(selectedDateIndex, selectedHour, selectedMinute, isAm) {
        val targetCal = dateOptions[selectedDateIndex].calendar.clone() as Calendar
        var hour24 = if (selectedHour == 12) 0 else selectedHour
        if (!isAm) hour24 += 12
        targetCal.set(Calendar.HOUR_OF_DAY, hour24)
        targetCal.set(Calendar.MINUTE, selectedMinute)
        targetCal.set(Calendar.SECOND, 0)
        targetCal.set(Calendar.MILLISECOND, 0)

        // If computed time is in the past for today, push to +1 hour
        if (targetCal.timeInMillis <= System.currentTimeMillis() && selectedDateIndex == 0) {
            targetCal.timeInMillis = System.currentTimeMillis() + 60 * 60 * 1000L
        }

        val dateLabel = dateOptions[selectedDateIndex].label
        val minStr = if (selectedMinute < 10) "0$selectedMinute" else "$selectedMinute"
        val hourStr = if (selectedHour < 10) "0$selectedHour" else "$selectedHour"
        val amPmStr = if (isAm) "AM" else "PM"
        val timeLabel = "$hourStr:$minStr $amPmStr"
        "$dateLabel, $timeLabel" to targetCal.timeInMillis
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_reminder),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(Dimens.SpaceSmall))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                // Section 1: Date Selection
                Text(
                    text = "DATE",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(Dimens.SpaceSmall))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(dateOptions.indices.toList()) { idx ->
                        val option = dateOptions[idx]
                        val isSelected = selectedDateIndex == idx
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedDateIndex = idx },
                            label = {
                                Text(
                                    text = option.label,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            shape = RoundedCornerShape(Dimens.RadiusFull)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(Dimens.SpaceMedium))
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                Spacer(modifier = Modifier.height(Dimens.SpaceMedium))

                // Section 2: Time Selection in 12-Hour format with AM/PM
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "TIME (12-HOUR FORMAT)",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    // Corporate AM / PM Segmented Control
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(Dimens.RadiusFull))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
                            .padding(2.dp)
                    ) {
                        listOf(true to "AM", false to "PM").forEach { (am, label) ->
                            val isSelected = isAm == am
                            Surface(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(Dimens.RadiusFull))
                                    .clickable { isAm = am },
                                color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                                shadowElevation = if (isSelected) 2.dp else 0.dp
                            ) {
                                Text(
                                    text = label,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(Dimens.SpaceSmall))

                // Hours (1-12)
                Text(
                    text = "Hour: $selectedHour",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items((1..12).toList()) { h ->
                        val isSelected = selectedHour == h
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedHour = h },
                            label = { Text("$h") },
                            shape = RoundedCornerShape(Dimens.RadiusFull)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(Dimens.SpaceSmall))

                // Minutes (00 to 55 by 5-min intervals)
                val currentMinStr = if (selectedMinute < 10) "0$selectedMinute" else "$selectedMinute"
                Text(
                    text = "Minute: $currentMinStr",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    val minuteOptions = listOf(0, 5, 10, 15, 20, 25, 30, 35, 40, 45, 50, 55)
                    items(minuteOptions) { m ->
                        val isSelected = selectedMinute == m
                        val mLabel = if (m < 10) "0$m" else "$m"
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedMinute = m },
                            label = { Text(mLabel) },
                            shape = RoundedCornerShape(Dimens.RadiusFull)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(Dimens.SpaceMedium))
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                Spacer(modifier = Modifier.height(Dimens.SpaceMedium))

                // Corporate Scheduled Summary Banner
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.25f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_reminder),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "SCHEDULED ALERT",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.8.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = computedCustomTime.first,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(Dimens.SpaceMedium))

                Button(
                    onClick = {
                        onSetReminder(computedCustomTime.first, computedCustomTime.second)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(Dimens.RadiusFull),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("Set Reminder", fontWeight = FontWeight.Bold)
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
