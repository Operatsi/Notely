package com.operatsi.notely.ui.dialogs

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.operatsi.notely.R
import com.operatsi.notely.model.TodoItem
import com.operatsi.notely.model.TodoPriority
import com.operatsi.notely.ui.theme.AccentDanger
import com.operatsi.notely.ui.theme.AccentWarning
import com.operatsi.notely.ui.theme.Dimens
import com.operatsi.notely.ui.theme.NotelyBlueLight

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoEditorDialog(
    todo: TodoItem?,
    onDismiss: () -> Unit,
    onSave: (id: String?, title: String, dueDateText: String?, priority: TodoPriority, hasReminder: Boolean, reminderTimestamp: Long?) -> Unit,
    onDelete: ((String) -> Unit)?
) {
    var title by rememberSaveable { mutableStateOf(todo?.title ?: "") }
    var dueDateText by rememberSaveable { mutableStateOf(todo?.dueDateText ?: "") }
    var priority by rememberSaveable { mutableStateOf(todo?.priority ?: TodoPriority.MEDIUM) }
    var hasReminder by rememberSaveable { mutableStateOf(todo?.hasReminder ?: false) }
    var reminderTimestamp by rememberSaveable { mutableStateOf<Long?>(todo?.reminderTimestamp) }

    var isCustomPickerOpen by remember { mutableStateOf(false) }

    val focusRequester = remember { FocusRequester() }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 6.dp,
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Dimens.SpaceLarge)
                .padding(bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding() + Dimens.SpaceMedium)
        ) {
            // Header: Title & Action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (todo == null) "New To-do" else "Edit To-do",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (todo != null && onDelete != null) {
                        IconButton(
                            onClick = { onDelete(todo.id) },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_delete),
                                contentDescription = "Delete task",
                                tint = AccentDanger,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(Dimens.SpaceSmall))
                    }

                    Button(
                        onClick = {
                            if (title.isNotBlank()) {
                                onSave(
                                    todo?.id,
                                    title.trim(),
                                    dueDateText.trim().takeIf { it.isNotBlank() },
                                    priority,
                                    hasReminder,
                                    reminderTimestamp
                                )
                            } else {
                                onDismiss()
                            }
                        },
                        enabled = title.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        ),
                        shape = RoundedCornerShape(Dimens.RadiusFull)
                    ) {
                        Text("Save", fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(Dimens.SpaceMedium))

            // Task Title Input
            BasicTextField(
                value = title,
                onValueChange = { title = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .focusRequester(focusRequester),
                textStyle = MaterialTheme.typography.bodyLarge.copy(
                    fontSize = 17.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    lineHeight = 24.sp
                ),
                cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                decorationBox = { innerTextField ->
                    if (title.isEmpty()) {
                        Text(
                            text = "Add a To-do item",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontSize = 17.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                            )
                        )
                    }
                    innerTextField()
                }
            )

            HorizontalDivider(
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                thickness = 0.5.dp
            )

            Spacer(modifier = Modifier.height(Dimens.SpaceMedium))

            // Corporate "SET ALARM": Only "Custom" and "None"
            Text(
                text = "SET ALARM",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    letterSpacing = 1.sp
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(Dimens.SpaceSmall))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Dimens.SpaceSmall)
            ) {
                // "None" option
                val isNoneSelected = !hasReminder || dueDateText.isBlank()
                FilterChip(
                    selected = isNoneSelected,
                    onClick = {
                        hasReminder = false
                        dueDateText = ""
                        reminderTimestamp = null
                    },
                    label = {
                        Text(
                            text = "None",
                            fontSize = 12.sp,
                            fontWeight = if (isNoneSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    shape = RoundedCornerShape(Dimens.RadiusFull)
                )

                // "Custom" option
                val isCustomSelected = hasReminder && dueDateText.isNotBlank()
                FilterChip(
                    selected = isCustomSelected,
                    onClick = {
                        isCustomPickerOpen = true
                    },
                    label = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_reminder),
                                contentDescription = null,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Custom",
                                fontSize = 12.sp,
                                fontWeight = if (isCustomSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    },
                    shape = RoundedCornerShape(Dimens.RadiusFull)
                )
            }

            // Scheduled Alarm Banner (if Custom is active)
            if (hasReminder && dueDateText.isNotBlank()) {
                Spacer(modifier = Modifier.height(Dimens.SpaceSmall))
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.25f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_reminder),
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = dueDateText,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        Text(
                            text = "Edit",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .clip(RoundedCornerShape(Dimens.RadiusSmall))
                                .clickable { isCustomPickerOpen = true }
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(Dimens.SpaceMedium))

            // Priority Selection
            Text(
                text = "PRIORITY",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    letterSpacing = 1.sp
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(Dimens.SpaceSmall))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Dimens.SpaceSmall)
            ) {
                TodoPriority.entries.forEach { p ->
                    val isSelected = priority == p
                    val chipColor = when (p) {
                        TodoPriority.HIGH -> AccentDanger
                        TodoPriority.MEDIUM -> AccentWarning
                        TodoPriority.LOW -> NotelyBlueLight
                    }

                    FilterChip(
                        selected = isSelected,
                        onClick = { priority = p },
                        label = {
                            Text(
                                text = p.name.lowercase().replaceFirstChar { it.uppercase() },
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = chipColor.copy(alpha = 0.16f),
                            selectedLabelColor = chipColor
                        ),
                        shape = RoundedCornerShape(Dimens.RadiusFull)
                    )
                }
            }
        }
    }

    if (isCustomPickerOpen) {
        DateTimePickerDialog(
            title = "Set To-do Alarm",
            onDismiss = { isCustomPickerOpen = false },
            onSetReminder = { text, timestamp ->
                hasReminder = true
                dueDateText = text
                reminderTimestamp = timestamp
                isCustomPickerOpen = false
            }
        )
    }
}
