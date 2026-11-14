package com.operatsi.notely.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.operatsi.notely.R
import com.operatsi.notely.model.NoteItem
import com.operatsi.notely.ui.theme.AccentDanger
import com.operatsi.notely.ui.theme.Dimens
import com.operatsi.notely.ui.theme.NoteBordersDark
import com.operatsi.notely.ui.theme.NoteBordersLight
import com.operatsi.notely.ui.theme.NoteTintsDark
import com.operatsi.notely.ui.theme.NoteTintsLight

@Composable
fun NoteEditorDialog(
    note: NoteItem?,
    onDismiss: () -> Unit,
    onSave: (
        id: String?,
        title: String,
        content: String,
        colorIndex: Int,
        isPinned: Boolean,
        hasReminder: Boolean,
        reminderText: String?
    ) -> Unit,
    onDelete: ((String) -> Unit)?
) {
    var title by rememberSaveable { mutableStateOf(note?.title ?: "") }
    var content by rememberSaveable { mutableStateOf(note?.content ?: "") }
    var selectedColorIndex by rememberSaveable { mutableIntStateOf(note?.colorIndex ?: 0) }
    var isPinned by rememberSaveable { mutableStateOf(note?.isPinned ?: false) }
    var hasReminder by rememberSaveable { mutableStateOf(note?.hasReminder ?: false) }
    var reminderText by rememberSaveable { mutableStateOf(note?.reminderText ?: "Tomorrow, 9:00 AM") }

    val isDark = isSystemInDarkTheme()
    val tintColors = if (isDark) NoteTintsDark else NoteTintsLight
    val borderColors = if (isDark) NoteBordersDark else NoteBordersLight

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .heightIn(max = 680.dp)
                .clip(RoundedCornerShape(Dimens.RadiusCard)),
            shape = RoundedCornerShape(Dimens.RadiusCard),
            color = tintColors[selectedColorIndex.coerceIn(0, tintColors.lastIndex)],
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Dimens.SpaceLarge)
                    .verticalScroll(rememberScrollState())
            ) {
                // Top Action Bar in Dialog
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_close),
                            contentDescription = "Close",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Reminder toggle
                        IconButton(
                            onClick = { hasReminder = !hasReminder },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_reminder),
                                contentDescription = "Toggle reminder",
                                tint = if (hasReminder) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                                },
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        // Pin toggle
                        IconButton(
                            onClick = { isPinned = !isPinned },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                painter = painterResource(
                                    id = if (isPinned) R.drawable.ic_pin_filled else R.drawable.ic_pin
                                ),
                                contentDescription = "Toggle pin",
                                tint = if (isPinned) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                                },
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        // Delete button if existing note
                        if (note != null && onDelete != null) {
                            IconButton(
                                onClick = { onDelete(note.id) },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_delete),
                                    contentDescription = "Delete note",
                                    tint = AccentDanger,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(Dimens.SpaceSmall))

                // Title Input
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    placeholder = {
                        Text(
                            "Title",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                            )
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(Dimens.SpaceMedium))

                // Content Input
                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    placeholder = {
                        Text(
                            "Note content...",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                            )
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 160.dp, max = 280.dp),
                    textStyle = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onSurface
                    )
                )

                // Reminder text setting (if enabled)
                if (hasReminder) {
                    Spacer(modifier = Modifier.height(Dimens.SpaceMedium))
                    OutlinedTextField(
                        value = reminderText,
                        onValueChange = { reminderText = it },
                        label = { Text("Reminder description") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(Dimens.SpaceLarge))

                // Color Palette Picker
                Text(
                    text = "NOTE COLOR",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(Dimens.SpaceSmall))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(Dimens.SpaceMedium),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    itemsIndexed(tintColors) { index, color ->
                        val isSelected = selectedColorIndex == index
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(color)
                                .border(
                                    width = if (isSelected) 2.5.dp else 1.dp,
                                    color = if (isSelected) {
                                        MaterialTheme.colorScheme.primary
                                    } else {
                                        borderColors[index]
                                    },
                                    shape = CircleShape
                                )
                                .clickable { selectedColorIndex = index },
                            contentAlignment = Alignment.Center
                        ) {
                            if (isSelected) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_nav_todo),
                                    contentDescription = "Selected",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(Dimens.SpaceExtraLarge))
                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                Spacer(modifier = Modifier.height(Dimens.SpaceMedium))

                // Bottom Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }

                    Spacer(modifier = Modifier.width(Dimens.SpaceSmall))

                    Button(
                        onClick = {
                            if (title.isNotBlank() || content.isNotBlank()) {
                                onSave(
                                    note?.id,
                                    title.trim(),
                                    content.trim(),
                                    selectedColorIndex,
                                    isPinned,
                                    hasReminder,
                                    if (hasReminder) reminderText else null
                                )
                            } else {
                                onDismiss()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text(if (note == null) "Create" else "Save")
                    }
                }
            }
        }
    }
}
