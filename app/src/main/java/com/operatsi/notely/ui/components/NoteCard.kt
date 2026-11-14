package com.operatsi.notely.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.operatsi.notely.R
import com.operatsi.notely.model.NoteItem
import com.operatsi.notely.ui.theme.Dimens
import com.operatsi.notely.ui.theme.getNoteBorderColor
import com.operatsi.notely.ui.theme.getNoteColor

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NoteCard(
    note: NoteItem,
    onClick: () -> Unit,
    onTogglePin: () -> Unit,
    modifier: Modifier = Modifier,
    isInSelectionMode: Boolean = false,
    isSelected: Boolean = false,
    onLongClick: (() -> Unit)? = null,
    onToggleSelection: (() -> Unit)? = null
) {
    val backgroundColor = getNoteColor(note.colorIndex)
    val borderColor = getNoteBorderColor(note.colorIndex)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(Dimens.RadiusCard))
            .combinedClickable(
                onClick = {
                    if (isInSelectionMode) {
                        onToggleSelection?.invoke()
                    } else {
                        onClick()
                    }
                },
                onLongClick = {
                    if (!isInSelectionMode) {
                        onLongClick?.invoke()
                    }
                }
            ),
        shape = RoundedCornerShape(Dimens.RadiusCard),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        border = BorderStroke(
            width = if (isSelected) 2.dp else 1.dp,
            color = if (isSelected) MaterialTheme.colorScheme.primary else borderColor
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = Dimens.CardElevation)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimens.SpaceNormal)
        ) {
            // Header row: Title + Pin button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                if (note.title.isNotBlank()) {
                    Text(
                        text = note.title,
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 16.sp,
                            lineHeight = 22.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = Dimens.SpaceSmall)
                    )
                } else {
                    Spacer(modifier = Modifier.weight(1f))
                }

                if (!isInSelectionMode) {
                    IconButton(
                        onClick = onTogglePin,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            painter = painterResource(
                                id = if (note.isPinned) R.drawable.ic_pin_filled else R.drawable.ic_pin
                            ),
                            contentDescription = if (note.isPinned) "Unpin note" else "Pin note",
                            tint = if (note.isPinned) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                            },
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            // Note Content Preview (Direct styling, ZERO symbols)
            val cleanContent = androidx.compose.runtime.remember(note) { note.cleanContent }
            if (cleanContent.isNotBlank()) {
                Spacer(modifier = Modifier.height(Dimens.SpaceSmall))

                if (note.isChecklist) {
                    val lines = cleanContent.split('\n').take(4)
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        lines.forEachIndexed { idx, lineText ->
                            val isChecked = note.checkedLines.contains(idx)
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(14.dp)
                                        .clip(RoundedCornerShape(3.dp))
                                        .background(if (isChecked) MaterialTheme.colorScheme.primary else Color.Transparent)
                                        .border(
                                            1.dp,
                                            if (isChecked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                                            RoundedCornerShape(3.dp)
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (isChecked) {
                                        Icon(
                                            painter = painterResource(id = R.drawable.ic_nav_todo),
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(10.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = lineText,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontSize = 13.sp,
                                        textDecoration = if (isChecked) TextDecoration.LineThrough else null,
                                        fontWeight = if (note.isBold) FontWeight.Bold else FontWeight.Normal,
                                        fontStyle = if (note.isItalic) androidx.compose.ui.text.font.FontStyle.Italic else androidx.compose.ui.text.font.FontStyle.Normal
                                    ),
                                    color = if (isChecked) MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f) else MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                } else {
                    val cardFontSize = when (note.headingLevel) {
                        "H1" -> 16.sp
                        "H2" -> 15.sp
                        "H3" -> 14.5.sp
                        else -> 14.sp
                    }
                    val cardTextDecoration = when {
                        note.isUnderline && note.isStrikethrough -> TextDecoration.combine(listOf(TextDecoration.Underline, TextDecoration.LineThrough))
                        note.isUnderline -> TextDecoration.Underline
                        note.isStrikethrough -> TextDecoration.LineThrough
                        else -> null
                    }
                    Text(
                        text = cleanContent,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontSize = cardFontSize,
                            lineHeight = 20.sp,
                            fontWeight = if (note.isBold || note.headingLevel != "Body") FontWeight.Bold else FontWeight.Normal,
                            fontStyle = if (note.isItalic) androidx.compose.ui.text.font.FontStyle.Italic else androidx.compose.ui.text.font.FontStyle.Normal,
                            textDecoration = cardTextDecoration
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 6,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(modifier = Modifier.height(Dimens.SpaceMedium))

            // Footer row: Reminder chip (if any) + Date or Selection Checkbox
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (note.hasReminder && !isInSelectionMode) {
                    Surface(
                        shape = RoundedCornerShape(Dimens.RadiusFull),
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_reminder),
                                contentDescription = "Reminder set",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(12.dp)
                            )
                            if (!note.reminderText.isNullOrBlank()) {
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = note.reminderText,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.primary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                } else {
                    Box {}
                }

                if (isInSelectionMode) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent)
                            .border(
                                width = 2.dp,
                                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isSelected) {
                            Icon(
                                painter = painterResource(R.drawable.ic_nav_todo),
                                contentDescription = "Selected",
                                tint = Color.White,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                } else {
                    Text(
                        text = note.formattedDate,
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}
