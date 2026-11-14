package com.operatsi.notely.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.runtime.getValue
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
import com.operatsi.notely.model.TodoItem
import com.operatsi.notely.model.TodoPriority
import com.operatsi.notely.ui.theme.AccentDanger
import com.operatsi.notely.ui.theme.AccentSuccess
import com.operatsi.notely.ui.theme.AccentWarning
import com.operatsi.notely.ui.theme.Dimens
import com.operatsi.notely.ui.theme.NotelyBlueLight

@Composable
fun TodoRow(
    todo: TodoItem,
    onToggleCompleted: () -> Unit,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val checkboxBg by animateColorAsState(
        targetValue = if (todo.isCompleted) MaterialTheme.colorScheme.primary else Color.Transparent,
        animationSpec = tween(durationMillis = 200),
        label = "todo_check_bg"
    )

    val checkboxBorder by animateColorAsState(
        targetValue = if (todo.isCompleted) {
            MaterialTheme.colorScheme.primary
        } else {
            MaterialTheme.colorScheme.outline
        },
        animationSpec = tween(durationMillis = 200),
        label = "todo_check_border"
    )

    val textColor by animateColorAsState(
        targetValue = if (todo.isCompleted) {
            MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
        } else {
            MaterialTheme.colorScheme.onSurface
        },
        animationSpec = tween(durationMillis = 200),
        label = "todo_text_color"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(Dimens.RadiusMedium)),
        shape = RoundedCornerShape(Dimens.RadiusMedium),
        colors = CardDefaults.cardColors(
            containerColor = if (todo.isCompleted) {
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
            } else {
                MaterialTheme.colorScheme.surface
            }
        ),
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.6f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 4.dp, end = Dimens.SpaceNormal, top = Dimens.SpaceSmall, bottom = Dimens.SpaceSmall),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Checkbox and Title section
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Interactive Checkbox with full 48dp touch target
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clickable(onClick = onToggleCompleted),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(checkboxBg)
                            .border(1.5.dp, checkboxBorder, RoundedCornerShape(6.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        if (todo.isCompleted) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_nav_todo),
                                contentDescription = "Completed",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(Dimens.SpaceSmall))

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clickable(onClick = onClick)
                        .padding(vertical = Dimens.SpaceSmall)
                ) {
                    Text(
                        text = todo.title,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = if (todo.isCompleted) FontWeight.Normal else FontWeight.Medium,
                            fontSize = 15.sp,
                            textDecoration = if (todo.isCompleted) TextDecoration.LineThrough else null
                        ),
                        color = textColor,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    if (!todo.dueDateText.isNullOrBlank() || todo.priority != TodoPriority.MEDIUM) {
                        Row(
                            modifier = Modifier.padding(top = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Priority indicator
                            val (priorityColor, priorityText) = when (todo.priority) {
                                TodoPriority.HIGH -> AccentDanger to "High"
                                TodoPriority.MEDIUM -> AccentWarning to "Medium"
                                TodoPriority.LOW -> NotelyBlueLight to "Low"
                            }

                            Surface(
                                shape = RoundedCornerShape(Dimens.RadiusFull),
                                color = priorityColor.copy(alpha = 0.12f)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(6.dp)
                                            .clip(CircleShape)
                                            .background(priorityColor)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = priorityText,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = priorityColor
                                    )
                                }
                            }

                            if (!todo.dueDateText.isNullOrBlank()) {
                                Spacer(modifier = Modifier.width(Dimens.SpaceSmall))
                                Text(
                                    text = "• ${todo.dueDateText}",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                                )
                            }
                        }
                    }
                }
            }

            // Delete button
            IconButton(
                onClick = onDelete,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_delete),
                    contentDescription = "Delete task",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@Composable
fun TodoRowPreview() {
    com.operatsi.notely.ui.theme.NotelyTheme {
        Column(modifier = Modifier.padding(16.dp)) {
            TodoRow(
                todo = TodoItem(
                    title = "Review Notely responsive UI on tablet landscape",
                    isCompleted = false,
                    dueDateText = "Today",
                    priority = TodoPriority.HIGH
                ),
                onToggleCompleted = {},
                onClick = {},
                onDelete = {}
            )
            Spacer(modifier = Modifier.height(8.dp))
            TodoRow(
                todo = TodoItem(
                    title = "Setup initial project architecture and Material 3 theme",
                    isCompleted = true,
                    dueDateText = "Completed",
                    priority = TodoPriority.LOW
                ),
                onToggleCompleted = {},
                onClick = {},
                onDelete = {}
            )
        }
    }
}
