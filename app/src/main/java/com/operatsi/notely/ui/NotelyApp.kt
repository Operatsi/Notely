package com.operatsi.notely.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.operatsi.notely.ui.components.NotelyBottomBar
import com.operatsi.notely.ui.components.NotelyFab
import com.operatsi.notely.ui.components.NotelyHeader
import com.operatsi.notely.ui.components.NotelyNavRail
import com.operatsi.notely.ui.components.NotelySelectionBottomBar
import com.operatsi.notely.ui.dialogs.SettingsDialog
import com.operatsi.notely.ui.dialogs.TodoEditorDialog
import com.operatsi.notely.ui.screens.NoteEditScreen
import com.operatsi.notely.ui.screens.NotesScreen
import com.operatsi.notely.ui.screens.TodoScreen
import com.operatsi.notely.ui.theme.Dimens
import com.operatsi.notely.ui.theme.NotelyTheme
import com.operatsi.notely.ui.theme.ThemeSetting

@Composable
fun NotelyApp(
    viewModel: NotelyViewModel,
    modifier: Modifier = Modifier
) {
    val themeSetting by viewModel.themeSetting.collectAsState()
    val currentDestination by viewModel.currentDestination.collectAsState()
    val isGridView by viewModel.isGridView.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    val notes by viewModel.notes.collectAsState()
    val todos by viewModel.todos.collectAsState()

    val isNoteSelectionMode by viewModel.isNoteSelectionMode.collectAsState()
    val selectedNoteIds by viewModel.selectedNoteIds.collectAsState()

    // Back handlers
    BackHandler(enabled = isNoteSelectionMode) {
        viewModel.clearNoteSelection()
    }
    BackHandler(enabled = !isNoteSelectionMode && searchQuery.isNotEmpty()) {
        viewModel.setSearchQuery("")
    }

    val pinnedNotes = remember(notes, searchQuery) {
        notes.filter { it.isPinned && viewModel.matchesSearch(it.title, it.content, searchQuery) }
    }
    val otherNotes = remember(notes, searchQuery) {
        notes.filter { !it.isPinned && viewModel.matchesSearch(it.title, it.content, searchQuery) }
    }
    val activeTodos = remember(todos, searchQuery) {
        todos.filter { !it.isCompleted && viewModel.matchesSearch(it.title, "", searchQuery) }
    }
    val completedTodos = remember(todos, searchQuery) {
        todos.filter { it.isCompleted && viewModel.matchesSearch(it.title, "", searchQuery) }
    }

    val isNoteEditorOpen by viewModel.isNoteEditorOpen.collectAsState()
    val editingNote by viewModel.editingNote.collectAsState()

    val isTodoEditorOpen by viewModel.isTodoEditorOpen.collectAsState()
    val editingTodo by viewModel.editingTodo.collectAsState()

    val isSettingsOpen by viewModel.isSettingsOpen.collectAsState()

    val destinationTitle = if (currentDestination == NavDestination.NOTES) "Notes" else "To-do"

    NotelyTheme(themeSetting = themeSetting) {
        if (isNoteEditorOpen) {
            NoteEditScreen(
                note = editingNote,
                onSave = { id, title, content, colorIndex, isPinned, hasReminder, reminderText, reminderTimestamp, isBold, isItalic, isUnderline, isStrikethrough, headingLevel, isChecklist, checkedLines ->
                    viewModel.saveNote(
                        id = id,
                        title = title,
                        content = content,
                        colorIndex = colorIndex,
                        isPinned = isPinned,
                        hasReminder = hasReminder,
                        reminderText = reminderText,
                        reminderTimestamp = reminderTimestamp,
                        isBold = isBold,
                        isItalic = isItalic,
                        isUnderline = isUnderline,
                        isStrikethrough = isStrikethrough,
                        headingLevel = headingLevel,
                        isChecklist = isChecklist,
                        checkedLines = checkedLines
                    )
                },
                onDelete = viewModel::deleteNote,
                onClose = viewModel::closeNoteEditor
            )
        } else if (isSettingsOpen) {
            com.operatsi.notely.ui.screens.SettingsScreen(
                currentTheme = themeSetting,
                onThemeChanged = viewModel::setThemeSetting,
                onClose = viewModel::closeSettings
            )
        } else {
            BoxWithConstraints(
                modifier = modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
            ) {
                val isExpandedScreen = maxWidth >= 720.dp

                if (isExpandedScreen) {
                    // Large Tablet / Chromebook / Desktop Layout with Side Nav Rail
                    Row(modifier = Modifier.fillMaxSize()) {
                        NotelyNavRail(
                            currentDestination = currentDestination,
                            onDestinationSelected = viewModel::setDestination,
                            onOpenSettings = viewModel::openSettings,
                            onFabClicked = viewModel::onFabClicked
                        )

                        Scaffold(
                            topBar = {
                                NotelyHeader(
                                    title = destinationTitle,
                                    searchQuery = searchQuery,
                                    onSearchQueryChanged = viewModel::setSearchQuery,
                                    isGridView = isGridView,
                                    onToggleGridView = viewModel::toggleGridView,
                                    onOpenSettings = viewModel::openSettings,
                                    isSelectionMode = isNoteSelectionMode,
                                    selectedCount = selectedNoteIds.size,
                                    isAllSelected = selectedNoteIds.size == notes.size && notes.isNotEmpty(),
                                    onClearSelection = viewModel::clearNoteSelection,
                                    onSelectAll = viewModel::selectAllNotes,
                                    showBrandLogo = false
                                )
                            },
                            containerColor = MaterialTheme.colorScheme.background
                        ) { innerPadding ->
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .widthIn(max = Dimens.MaxContentWidth),
                                contentAlignment = Alignment.TopCenter
                            ) {
                                Crossfade(
                                    targetState = currentDestination,
                                    animationSpec = tween(durationMillis = 200),
                                    label = "destination_crossfade"
                                ) { destination ->
                                    when (destination) {
                                        NavDestination.NOTES -> {
                                            NotesScreen(
                                                pinnedNotes = pinnedNotes,
                                                otherNotes = otherNotes,
                                                isGridView = isGridView,
                                                searchQuery = searchQuery,
                                                onNoteClick = viewModel::openEditNote,
                                                onTogglePin = viewModel::toggleNotePin,
                                                contentPadding = innerPadding,
                                                isSelectionMode = isNoteSelectionMode,
                                                selectedNoteIds = selectedNoteIds,
                                                onStartSelection = viewModel::startNoteSelection,
                                                onToggleSelection = viewModel::toggleNoteSelection
                                            )
                                        }
                                        NavDestination.TODO -> {
                                            TodoScreen(
                                                activeTodos = activeTodos,
                                                completedTodos = completedTodos,
                                                searchQuery = searchQuery,
                                                onToggleCompleted = viewModel::toggleTodoCompleted,
                                                onTodoClick = viewModel::openEditTodo,
                                                onDeleteTodo = viewModel::deleteTodo,
                                                contentPadding = innerPadding
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else {
                    // Mobile Phone / Small Tablet Layout with Fixed Bottom Bar & Floating FAB
                    Scaffold(
                        topBar = {
                            NotelyHeader(
                                title = destinationTitle,
                                searchQuery = searchQuery,
                                onSearchQueryChanged = viewModel::setSearchQuery,
                                isGridView = isGridView,
                                onToggleGridView = viewModel::toggleGridView,
                                onOpenSettings = viewModel::openSettings,
                                isSelectionMode = isNoteSelectionMode,
                                selectedCount = selectedNoteIds.size,
                                isAllSelected = selectedNoteIds.size == notes.size && notes.isNotEmpty(),
                                onClearSelection = viewModel::clearNoteSelection,
                                onSelectAll = viewModel::selectAllNotes,
                                showBrandLogo = true
                            )
                        },
                        bottomBar = {
                            if (isNoteSelectionMode) {
                                NotelySelectionBottomBar(
                                    selectedCount = selectedNoteIds.size,
                                    onTogglePin = viewModel::togglePinSelectedNotes,
                                    onDelete = viewModel::deleteSelectedNotes
                                )
                            } else {
                                NotelyBottomBar(
                                    currentDestination = currentDestination,
                                    onDestinationSelected = viewModel::setDestination
                                )
                            }
                        },
                        floatingActionButton = {
                            if (!isNoteSelectionMode) {
                                NotelyFab(
                                    currentDestination = currentDestination,
                                    onClick = viewModel::onFabClicked,
                                    modifier = Modifier.padding(bottom = 8.dp, end = 4.dp)
                                )
                            }
                        },
                        contentWindowInsets = WindowInsets(0, 0, 0, 0),
                        containerColor = MaterialTheme.colorScheme.background
                    ) { innerPadding ->
                        Crossfade(
                            targetState = currentDestination,
                            animationSpec = tween(durationMillis = 200),
                            label = "mobile_destination_crossfade"
                        ) { destination ->
                            when (destination) {
                                NavDestination.NOTES -> {
                                    NotesScreen(
                                        pinnedNotes = pinnedNotes,
                                        otherNotes = otherNotes,
                                        isGridView = isGridView,
                                        searchQuery = searchQuery,
                                        onNoteClick = viewModel::openEditNote,
                                        onTogglePin = viewModel::toggleNotePin,
                                        contentPadding = innerPadding,
                                        isSelectionMode = isNoteSelectionMode,
                                        selectedNoteIds = selectedNoteIds,
                                        onStartSelection = viewModel::startNoteSelection,
                                        onToggleSelection = viewModel::toggleNoteSelection
                                    )
                                }
                                NavDestination.TODO -> {
                                    TodoScreen(
                                        activeTodos = activeTodos,
                                        completedTodos = completedTodos,
                                        searchQuery = searchQuery,
                                        onToggleCompleted = viewModel::toggleTodoCompleted,
                                        onTodoClick = viewModel::openEditTodo,
                                        onDeleteTodo = viewModel::deleteTodo,
                                        contentPadding = innerPadding
                                    )
                                }
                            }
                        }
                    }
                }

                // Dialogs
                if (isTodoEditorOpen) {
                    TodoEditorDialog(
                        todo = editingTodo,
                        onDismiss = viewModel::closeTodoEditor,
                        onSave = viewModel::saveTodo,
                        onDelete = viewModel::deleteTodo
                    )
                }
            }
        }
    }
}
