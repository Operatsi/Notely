package com.operatsi.notely.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.operatsi.notely.R
import com.operatsi.notely.model.NoteItem
import com.operatsi.notely.ui.components.EmptyState
import com.operatsi.notely.ui.components.NoteCard
import com.operatsi.notely.ui.theme.Dimens

@Composable
fun NotesScreen(
    pinnedNotes: List<NoteItem>,
    otherNotes: List<NoteItem>,
    isGridView: Boolean,
    searchQuery: String,
    onNoteClick: (NoteItem) -> Unit,
    onTogglePin: (String) -> Unit,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier,
    isSelectionMode: Boolean = false,
    selectedNoteIds: Set<String> = emptySet(),
    onStartSelection: (String) -> Unit = {},
    onToggleSelection: (String) -> Unit = {}
) {
    val gridState = rememberLazyStaggeredGridState()
    val totalCount = pinnedNotes.size + otherNotes.size

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
                    title = "No matching notes",
                    description = "We couldn't find any notes matching \"$searchQuery\"."
                )
            } else {
                EmptyState(
                    iconRes = R.drawable.ic_nav_notes,
                    title = "Capture your thoughts",
                    description = "Notes you create will appear here. Tap the + button to write your first note."
                )
            }
        }
    } else {
        val columns = if (!isGridView) {
            StaggeredGridCells.Fixed(1)
        } else {
            StaggeredGridCells.Adaptive(minSize = 170.dp)
        }

        LazyVerticalStaggeredGrid(
            columns = columns,
            state = gridState,
            modifier = modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                start = Dimens.SpaceNormal,
                end = Dimens.SpaceNormal,
                top = contentPadding.calculateTopPadding() + Dimens.SpaceMedium,
                bottom = contentPadding.calculateBottomPadding() + 80.dp // ensure clearance over FAB or Selection Bar
            ),
            horizontalArrangement = Arrangement.spacedBy(Dimens.SpaceMedium),
            verticalItemSpacing = Dimens.SpaceMedium
        ) {
            // Pinned Notes Section
            if (pinnedNotes.isNotEmpty()) {
                item(span = StaggeredGridItemSpan.FullLine) {
                    Text(
                        text = "PINNED",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            letterSpacing = 1.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = Dimens.SpaceSmall)
                    )
                }

                items(pinnedNotes, key = { it.id }) { note ->
                    NoteCard(
                        note = note,
                        onClick = { onNoteClick(note) },
                        onTogglePin = { onTogglePin(note.id) },
                        isInSelectionMode = isSelectionMode,
                        isSelected = selectedNoteIds.contains(note.id),
                        onLongClick = { onStartSelection(note.id) },
                        onToggleSelection = { onToggleSelection(note.id) }
                    )
                }
            }

            // Other Notes Section
            if (otherNotes.isNotEmpty()) {
                if (pinnedNotes.isNotEmpty()) {
                    item(span = StaggeredGridItemSpan.FullLine) {
                        Text(
                            text = "OTHERS",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                letterSpacing = 1.sp
                            ),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(
                                    top = Dimens.SpaceNormal,
                                    bottom = Dimens.SpaceSmall
                                )
                        )
                    }
                }

                items(otherNotes, key = { it.id }) { note ->
                    NoteCard(
                        note = note,
                        onClick = { onNoteClick(note) },
                        onTogglePin = { onTogglePin(note.id) },
                        isInSelectionMode = isSelectionMode,
                        isSelected = selectedNoteIds.contains(note.id),
                        onLongClick = { onStartSelection(note.id) },
                        onToggleSelection = { onToggleSelection(note.id) }
                    )
                }
            }
        }
    }
}

@androidx.compose.ui.tooling.preview.Preview(name = "Notes Screen Grid", showBackground = true, widthDp = 390, heightDp = 800)
@Composable
fun NotesScreenPreview() {
    com.operatsi.notely.ui.theme.NotelyTheme {
        NotesScreen(
            pinnedNotes = listOf(
                NoteItem(
                    title = "Welcome to Notely",
                    content = "Organize thoughts, pin key documents, and boost productivity with Google Keep inspired elegance.",
                    colorIndex = 4,
                    isPinned = true
                ),
                NoteItem(
                    title = "Executive Q4 Strategy",
                    content = "1. Finalize product roadmap.\n2. Verify cross-platform responsiveness.",
                    colorIndex = 2,
                    isPinned = true,
                    hasReminder = true,
                    reminderText = "Tomorrow, 9:00 AM"
                )
            ),
            otherNotes = listOf(
                NoteItem(
                    title = "Architecture Principles",
                    content = "Maintain clean separation of concerns and edge-to-edge support.",
                    colorIndex = 3,
                    isPinned = false
                ),
                NoteItem(
                    title = "Meeting Notes",
                    content = "Reviewed multi-column grid performance on Chromebooks.",
                    colorIndex = 5,
                    isPinned = false
                )
            ),
            isGridView = true,
            searchQuery = "",
            onNoteClick = {},
            onTogglePin = {},
            contentPadding = PaddingValues(0.dp)
        )
    }
}
