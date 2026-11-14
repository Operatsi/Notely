package com.operatsi.notely.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.operatsi.notely.R
import com.operatsi.notely.model.NoteItem
import com.operatsi.notely.ui.dialogs.DateTimePickerDialog
import com.operatsi.notely.ui.theme.AccentDanger
import com.operatsi.notely.ui.theme.Dimens
import com.operatsi.notely.ui.theme.LocalThemeIsDark
import com.operatsi.notely.ui.theme.NoteTintsDark
import com.operatsi.notely.ui.theme.NoteTintsLight
import com.operatsi.notely.ui.theme.getNoteBorderColor
import com.operatsi.notely.ui.theme.getNoteColor
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteEditScreen(
    note: NoteItem?,
    onSave: (
        id: String?,
        title: String,
        content: String,
        colorIndex: Int,
        isPinned: Boolean,
        hasReminder: Boolean,
        reminderText: String?,
        reminderTimestamp: Long?,
        isBold: Boolean,
        isItalic: Boolean,
        isUnderline: Boolean,
        isStrikethrough: Boolean,
        headingLevel: String,
        isChecklist: Boolean,
        checkedLines: List<Int>
    ) -> Unit,
    onDelete: (String) -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Sanitize any legacy markdown symbols so user NEVER sees raw symbols
    val initialCleanContent = remember(note) {
        (note?.cleanContent ?: note?.content ?: "")
            .replace(Regex("""\*\*|\*|<u>|</u>|~~|☐\s*"""), "")
            .replace(Regex("""^#{1,6}\s*""", RegexOption.MULTILINE), "")
            .replace(Regex("""^>\s*""", RegexOption.MULTILINE), "")
            .trim()
    }

    var title by remember { mutableStateOf(note?.title ?: "") }
    var contentValue by remember { mutableStateOf(TextFieldValue(initialCleanContent)) }
    var colorIndex by remember { mutableIntStateOf(note?.colorIndex ?: 0) }
    var isPinned by remember { mutableStateOf(note?.isPinned ?: false) }
    var hasReminder by remember { mutableStateOf(note?.hasReminder ?: false) }
    var reminderText by remember { mutableStateOf(note?.reminderText ?: "") }
    var scheduledReminderTime by remember { mutableStateOf<Long?>(null) }

    var isColorPaletteVisible by remember { mutableStateOf(false) }
    var isReminderDialogVisible by remember { mutableStateOf(false) }

    // Direct styling states (NO symbols inserted)
    var isBold by remember { mutableStateOf(note?.isBold ?: false) }
    var isItalic by remember { mutableStateOf(note?.isItalic ?: false) }
    var isUnderline by remember { mutableStateOf(note?.isUnderline ?: false) }
    var isStrikethrough by remember { mutableStateOf(note?.isStrikethrough ?: false) }
    var selectedHeading by remember { mutableStateOf(note?.headingLevel ?: "Body") }
    var isChecklist by remember { mutableStateOf(note?.isChecklist ?: false) }
    var checkedLines by remember { mutableStateOf(note?.checkedLines?.toSet() ?: emptySet()) }

    val isDark = LocalThemeIsDark.current
    val pageBackground = getNoteColor(colorIndex)
    val pageBorderColor = getNoteBorderColor(colorIndex)

    val textColor = MaterialTheme.colorScheme.onSurface
    val secondaryTextColor = MaterialTheme.colorScheme.onSurfaceVariant

    fun autoSaveAndClose() {
        val cleanText = contentValue.text
            .replace(Regex("""\*\*|\*|<u>|</u>|~~|☐\s*"""), "")
            .replace(Regex("""^#{1,6}\s*""", RegexOption.MULTILINE), "")
            .trim()

        if (title.isNotBlank() || cleanText.isNotBlank()) {
            onSave(
                note?.id,
                title.trim(),
                cleanText,
                colorIndex,
                isPinned,
                hasReminder,
                reminderText.takeIf { it.isNotBlank() },
                scheduledReminderTime,
                isBold,
                isItalic,
                isUnderline,
                isStrikethrough,
                selectedHeading,
                isChecklist,
                checkedLines.toList()
            )
        } else if (note != null) {
            onDelete(note.id)
        }
        onClose()
    }

    // Intercept back gesture & back button
    BackHandler {
        autoSaveAndClose()
    }

    // Computed text style based on active toggles (pure styling, NO symbols)
    val bodyFontSize = when (selectedHeading) {
        "H1" -> 26.sp
        "H2" -> 22.sp
        "H3" -> 20.sp
        "H4" -> 18.sp
        "H5" -> 16.sp
        else -> 16.sp
    }

    val bodyFontWeight = when {
        isBold || selectedHeading != "Body" -> FontWeight.Bold
        else -> FontWeight.Normal
    }

    val bodyFontStyle = if (isItalic) FontStyle.Italic else FontStyle.Normal

    val bodyTextDecoration = when {
        isUnderline && isStrikethrough -> TextDecoration.combine(listOf(TextDecoration.Underline, TextDecoration.LineThrough))
        isUnderline -> TextDecoration.Underline
        isStrikethrough -> TextDecoration.LineThrough
        else -> null
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(pageBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.statusBars)
        ) {
            // Top App Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(Dimens.HeaderHeight)
                    .padding(horizontal = Dimens.SpaceSmall),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Back Button (exact curved SVG)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = { autoSaveAndClose() },
                        modifier = Modifier.size(Dimens.MinTouchTarget)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_arrow_back),
                            contentDescription = "Save and Back",
                            tint = textColor,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(Dimens.SpaceSmall))

                    Text(
                        text = "Notes",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        ),
                        color = textColor
                    )
                }

                // Top Right Action Buttons
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Pin Button
                    IconButton(
                        onClick = { isPinned = !isPinned },
                        modifier = Modifier.size(Dimens.MinTouchTarget)
                    ) {
                        Icon(
                            painter = painterResource(
                                id = if (isPinned) R.drawable.ic_pin_filled else R.drawable.ic_pin
                            ),
                            contentDescription = if (isPinned) "Unpin Note" else "Pin Note",
                            tint = if (isPinned) MaterialTheme.colorScheme.primary else secondaryTextColor,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    // Reminder Button
                    IconButton(
                        onClick = { isReminderDialogVisible = true },
                        modifier = Modifier.size(Dimens.MinTouchTarget)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_reminder),
                            contentDescription = "Set Reminder",
                            tint = if (hasReminder) MaterialTheme.colorScheme.primary else secondaryTextColor,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    // Color Palette Swatch Picker Toggle
                    IconButton(
                        onClick = { isColorPaletteVisible = !isColorPaletteVisible },
                        modifier = Modifier.size(Dimens.MinTouchTarget)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(pageBackground)
                                .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
                        )
                    }

                    // Delete Note Button (if editing existing note)
                    if (note != null) {
                        IconButton(
                            onClick = {
                                onDelete(note.id)
                                onClose()
                            },
                            modifier = Modifier.size(Dimens.MinTouchTarget)
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_delete),
                                contentDescription = "Delete Note",
                                tint = AccentDanger,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }

            // Expandable Color Palette Bar
            AnimatedVisibility(visible = isColorPaletteVisible) {
                Surface(
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
                    modifier = Modifier.fillMaxWidth(),
                    tonalElevation = 4.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = Dimens.SpaceLarge, vertical = Dimens.SpaceMedium),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val tints = if (isDark) NoteTintsDark else NoteTintsLight
                        tints.forEachIndexed { index, tintColor ->
                            val isSelected = colorIndex == index
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(tintColor)
                                    .border(
                                        width = if (isSelected) 2.5.dp else 1.dp,
                                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                                        shape = CircleShape
                                    )
                                    .clickable { colorIndex = index },
                                contentAlignment = Alignment.Center
                            ) {
                                if (isSelected) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(CircleShape)
                                            .background(MaterialTheme.colorScheme.primary)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Reminder Badge Indicator (if active)
            AnimatedVisibility(visible = hasReminder && reminderText.isNotBlank()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Dimens.SpaceLarge, vertical = Dimens.SpaceExtraSmall),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AssistChip(
                        onClick = { isReminderDialogVisible = true },
                        label = {
                            Text(
                                text = reminderText,
                                style = MaterialTheme.typography.labelMedium
                            )
                        },
                        leadingIcon = {
                            Icon(
                                painter = painterResource(R.drawable.ic_reminder),
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        },
                        trailingIcon = {
                            Icon(
                                painter = painterResource(R.drawable.ic_close),
                                contentDescription = "Remove Reminder",
                                modifier = Modifier
                                    .size(14.dp)
                                    .clickable {
                                        hasReminder = false
                                        reminderText = ""
                                        scheduledReminderTime = null
                                    }
                            )
                        },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f),
                            labelColor = MaterialTheme.colorScheme.primary
                        )
                    )
                }
            }

            // Note Content Canvas (Scrollable)
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = Dimens.SpaceLarge)
            ) {
                Spacer(modifier = Modifier.height(Dimens.SpaceMedium))

                // Title Input
                BasicTextField(
                    value = title,
                    onValueChange = { title = it },
                    textStyle = TextStyle(
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = textColor
                    ),
                    cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                    modifier = Modifier.fillMaxWidth(),
                    decorationBox = { innerTextField ->
                        if (title.isEmpty()) {
                            Text(
                                text = "Title",
                                style = TextStyle(
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = secondaryTextColor.copy(alpha = 0.45f)
                                )
                            )
                        }
                        innerTextField()
                    }
                )

                Spacer(modifier = Modifier.height(Dimens.SpaceSmall))

                // Date metadata indicator
                val dateString = remember(note?.dateModified) {
                    val sdf = SimpleDateFormat("MMM d, h:mm a", Locale.getDefault())
                    "Edited ${sdf.format(Date(note?.dateModified ?: System.currentTimeMillis()))}"
                }
                Text(
                    text = dateString,
                    style = MaterialTheme.typography.bodySmall,
                    color = secondaryTextColor.copy(alpha = 0.6f)
                )

                Spacer(modifier = Modifier.height(Dimens.SpaceMedium))
                HorizontalDivider(color = pageBorderColor.copy(alpha = 0.5f), thickness = 0.5.dp)
                Spacer(modifier = Modifier.height(Dimens.SpaceMedium))

                // Checklist Mode vs Standard Note Input
                if (isChecklist) {
                    val lines = contentValue.text.split('\n')
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        lines.forEachIndexed { idx, lineText ->
                            val isChecked = checkedLines.contains(idx)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Interactive 48dp Checkbox touch target
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clickable {
                                            checkedLines = if (isChecked) {
                                                checkedLines - idx
                                            } else {
                                                checkedLines + idx
                                            }
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(20.dp)
                                            .clip(RoundedCornerShape(5.dp))
                                            .background(if (isChecked) MaterialTheme.colorScheme.primary else Color.Transparent)
                                            .border(
                                                1.5.dp,
                                                if (isChecked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                                                RoundedCornerShape(5.dp)
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (isChecked) {
                                            Icon(
                                                painter = painterResource(id = R.drawable.ic_nav_todo),
                                                contentDescription = "Checked",
                                                tint = Color.White,
                                                modifier = Modifier.size(13.dp)
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.width(8.dp))

                                BasicTextField(
                                    value = lineText,
                                    onValueChange = { newText ->
                                        val mutableLines = lines.toMutableList()
                                        mutableLines[idx] = newText
                                        contentValue = TextFieldValue(mutableLines.joinToString("\n"))
                                    },
                                    textStyle = TextStyle(
                                        fontSize = bodyFontSize,
                                        fontWeight = bodyFontWeight,
                                        fontStyle = bodyFontStyle,
                                        textDecoration = if (isChecked) TextDecoration.LineThrough else bodyTextDecoration,
                                        color = if (isChecked) secondaryTextColor.copy(alpha = 0.5f) else textColor
                                    ),
                                    cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                } else {
                    // Standard Body Content Input (Direct Styling, Zero Symbols)
                    BasicTextField(
                        value = contentValue,
                        onValueChange = { contentValue = it },
                        textStyle = TextStyle(
                            fontSize = bodyFontSize,
                            fontWeight = bodyFontWeight,
                            fontStyle = bodyFontStyle,
                            textDecoration = bodyTextDecoration,
                            color = textColor,
                            lineHeight = (bodyFontSize.value + 8).sp
                        ),
                        cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f, fill = false),
                        decorationBox = { innerTextField ->
                            if (contentValue.text.isEmpty()) {
                                Text(
                                    text = "Note something down",
                                    style = TextStyle(
                                        fontSize = 16.sp,
                                        color = secondaryTextColor.copy(alpha = 0.45f),
                                        lineHeight = 24.sp
                                    )
                                )
                            }
                            innerTextField()
                        }
                    )
                }

                Spacer(modifier = Modifier.height(80.dp))
            }

            // Bottom Formatting Toolbar (Transsion Notepad Style - Direct Styling Without Any Symbols)
            Surface(
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.96f),
                tonalElevation = 6.dp,
                shadowElevation = 8.dp,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .imePadding()
                    .padding(WindowInsets.navigationBars.asPaddingValues())
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Dimens.SpaceSmall, vertical = Dimens.SpaceExtraSmall)
                ) {
                    // Row 1: Heading Selectors (H1, H2, H3, H4, H5, Body)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(Dimens.SpaceExtraSmall),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val headingLevels = listOf("H1", "H2", "H3", "H4", "H5", "Body")
                        headingLevels.forEach { level ->
                            val isSelected = selectedHeading == level
                            FilterChip(
                                selected = isSelected,
                                onClick = {
                                    selectedHeading = level
                                },
                                label = {
                                    Text(
                                        text = level,
                                        fontSize = 12.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                    selectedLabelColor = MaterialTheme.colorScheme.primary
                                ),
                                shape = RoundedCornerShape(Dimens.RadiusFull)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    // Row 2: Text Styling & List Actions (Direct Toggles, NO symbols)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Bold (B) Toggle
                        FormatToggleButton(
                            label = "B",
                            isActive = isBold,
                            fontWeight = FontWeight.Bold,
                            onClick = { isBold = !isBold }
                        )

                        // Italic (I) Toggle
                        FormatToggleButton(
                            label = "I",
                            isActive = isItalic,
                            fontStyle = FontStyle.Italic,
                            onClick = { isItalic = !isItalic }
                        )

                        // Underline (U) Toggle
                        FormatToggleButton(
                            label = "U",
                            isActive = isUnderline,
                            textDecoration = TextDecoration.Underline,
                            onClick = { isUnderline = !isUnderline }
                        )

                        // Strikethrough (S) Toggle
                        FormatToggleButton(
                            label = "S",
                            isActive = isStrikethrough,
                            textDecoration = TextDecoration.LineThrough,
                            onClick = { isStrikethrough = !isStrikethrough }
                        )

                        // Interactive Checklist (☐) Toggle
                        FormatToggleButton(
                            label = "☐ Checklist",
                            isActive = isChecklist,
                            onClick = {
                                isChecklist = !isChecklist
                                if (contentValue.text.isBlank()) {
                                    contentValue = TextFieldValue("New item")
                                }
                            }
                        )

                        // Bullet list (•—)
                        FormatToggleButton(
                            label = "•— Bullet",
                            isActive = false,
                            onClick = {
                                val current = contentValue.text
                                val bulleted = current.split('\n').joinToString("\n") { line ->
                                    if (line.startsWith("• ")) line else "• $line"
                                }
                                contentValue = TextFieldValue(bulleted)
                            }
                        )

                        // Numbered list (1.)
                        FormatToggleButton(
                            label = "1. Number",
                            isActive = false,
                            onClick = {
                                val current = contentValue.text
                                val numbered = current.split('\n').mapIndexed { idx, line ->
                                    val cleaned = line.replace(Regex("""^\d+\.\s*"""), "")
                                    "${idx + 1}. $cleaned"
                                }.joinToString("\n")
                                contentValue = TextFieldValue(numbered)
                            }
                        )
                    }
                }
            }
        }
    }

    // Note Reminder Dialog with Presets and Custom Date & Time Picker
    if (isReminderDialogVisible) {
        DateTimePickerDialog(
            title = "Note Reminder",
            onDismiss = { isReminderDialogVisible = false },
            onSetReminder = { text, timestamp ->
                hasReminder = true
                reminderText = text
                scheduledReminderTime = timestamp
                isReminderDialogVisible = false
            }
        )
    }
}

@Composable
private fun FormatToggleButton(
    label: String,
    isActive: Boolean,
    onClick: () -> Unit,
    fontWeight: FontWeight = FontWeight.Normal,
    fontStyle: FontStyle = FontStyle.Normal,
    textDecoration: TextDecoration? = null
) {
    Surface(
        shape = RoundedCornerShape(Dimens.RadiusFull),
        color = if (isActive) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        border = BorderStroke(
            1.dp,
            if (isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
        ),
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Text(
            text = label,
            fontSize = 13.sp,
            fontWeight = if (isActive) FontWeight.Bold else fontWeight,
            fontStyle = fontStyle,
            textDecoration = textDecoration,
            color = if (isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}
