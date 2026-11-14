package com.operatsi.notely.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.operatsi.notely.R
import com.operatsi.notely.model.TodoItem
import com.operatsi.notely.ui.components.EmptyState
import com.operatsi.notely.ui.components.TodoRow
import com.operatsi.notely.ui.theme.Dimens

@Composable
fun TodoScreen(
    activeTodos: List<TodoItem>,
    completedTodos: List<TodoItem>,
    searchQuery: String,
    onToggleCompleted: (String) -> Unit,
    onTodoClick: (TodoItem) -> Unit,
    onDeleteTodo: (String) -> Unit,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()
    var isCompletedExpanded by rememberSaveable { mutableStateOf(true) }
    val totalCount = activeTodos.size + completedTodos.size

    if (totalCount == 0) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(contentPadding),
            contentAlignment = Alignment.Center
        ) {
            if (searchQuery.isNotEmpty()) {
                EmptyState(
                    iconRes = R.drawable.ic_search,
                    title = "No matching to-dos",
                    description = "We couldn't find any tasks matching \"$searchQuery\"."
                )
            } else {
                EmptyState(
                    iconRes = R.drawable.ic_nav_todo,
                    title = "All tasks completed",
                    description = "Stay productive and organized. Tap the + button to add a new task."
                )
            }
        }
    } else {
        LazyColumn(
            state = listState,
            modifier = modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                start = Dimens.SpaceNormal,
                end = Dimens.SpaceNormal,
                top = contentPadding.calculateTopPadding() + Dimens.SpaceMedium,
                bottom = contentPadding.calculateBottomPadding() + 80.dp
            ),
            verticalArrangement = Arrangement.spacedBy(Dimens.SpaceSmall)
        ) {
            // Summary Header Chip
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = Dimens.SpaceSmall),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "TASKS",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            letterSpacing = 1.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Surface(
                        shape = RoundedCornerShape(Dimens.RadiusFull),
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                    ) {
                        Text(
                            text = "${completedTodos.size} of $totalCount completed",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            // Active Tasks List
            items(activeTodos, key = { it.id }) { todo ->
                TodoRow(
                    todo = todo,
                    onToggleCompleted = { onToggleCompleted(todo.id) },
                    onClick = { onTodoClick(todo) },
                    onDelete = { onDeleteTodo(todo.id) }
                )
            }

            // Completed Tasks Section
            if (completedTodos.isNotEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(Dimens.SpaceMedium))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(Dimens.RadiusSmall))
                            .clickable { isCompletedExpanded = !isCompletedExpanded }
                            .padding(vertical = Dimens.SpaceSmall),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "COMPLETED (${completedTodos.size})",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                letterSpacing = 1.sp
                            ),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Text(
                            text = if (isCompletedExpanded) "Hide" else "Show",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                if (isCompletedExpanded) {
                    items(completedTodos, key = { it.id }) { todo ->
                        TodoRow(
                            todo = todo,
                            onToggleCompleted = { onToggleCompleted(todo.id) },
                            onClick = { onTodoClick(todo) },
                            onDelete = { onDeleteTodo(todo.id) }
                        )
                    }
                }
            }
        }
    }
}

@androidx.compose.ui.tooling.preview.Preview(name = "To-do Screen", showBackground = true, widthDp = 390, heightDp = 800)
@Composable
fun TodoScreenPreview() {
    com.operatsi.notely.ui.theme.NotelyTheme {
        TodoScreen(
            activeTodos = listOf(
                TodoItem(
                    title = "Review Notely responsive UI on tablet landscape",
                    isCompleted = false,
                    dueDateText = "Today",
                    priority = com.operatsi.notely.model.TodoPriority.HIGH
                ),
                TodoItem(
                    title = "Validate light and dark mode contrast ratios",
                    isCompleted = false,
                    dueDateText = "Today, 5:00 PM",
                    priority = com.operatsi.notely.model.TodoPriority.HIGH
                ),
                TodoItem(
                    title = "Verify Chromebook window resizing and multi-column grid",
                    isCompleted = false,
                    dueDateText = "Tomorrow",
                    priority = com.operatsi.notely.model.TodoPriority.MEDIUM
                )
            ),
            completedTodos = listOf(
                TodoItem(
                    title = "Setup initial project architecture and Material 3 theme",
                    isCompleted = true,
                    dueDateText = "Completed",
                    priority = com.operatsi.notely.model.TodoPriority.LOW
                )
            ),
            searchQuery = "",
            onToggleCompleted = {},
            onTodoClick = {},
            onDeleteTodo = {},
            contentPadding = PaddingValues(0.dp)
        )
    }
}
