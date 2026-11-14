package com.operatsi.notely.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import com.operatsi.notely.data.PreferencesManager
import com.operatsi.notely.model.NoteItem
import com.operatsi.notely.model.TodoItem
import com.operatsi.notely.model.TodoPriority
import com.operatsi.notely.ui.theme.ThemeSetting
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

enum class NavDestination {
    NOTES,
    TODO
}

class NotelyViewModel(context: Context) : ViewModel() {

    private val appContext: Context = context.applicationContext
    private val preferencesManager = PreferencesManager(context)

    val themeSetting: StateFlow<ThemeSetting> = preferencesManager.themeSetting
    val isGridView: StateFlow<Boolean> = preferencesManager.isGridView

    private val _currentDestination = MutableStateFlow(NavDestination.NOTES)
    val currentDestination: StateFlow<NavDestination> = _currentDestination.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _isSearchActive = MutableStateFlow(false)
    val isSearchActive: StateFlow<Boolean> = _isSearchActive.asStateFlow()

    // Dialog & Editor states
    private val _editingNote = MutableStateFlow<NoteItem?>(null)
    val editingNote: StateFlow<NoteItem?> = _editingNote.asStateFlow()

    private val _isNoteEditorOpen = MutableStateFlow(false)
    val isNoteEditorOpen: StateFlow<Boolean> = _isNoteEditorOpen.asStateFlow()

    private val _editingTodo = MutableStateFlow<TodoItem?>(null)
    val editingTodo: StateFlow<TodoItem?> = _editingTodo.asStateFlow()

    private val _isTodoEditorOpen = MutableStateFlow(false)
    val isTodoEditorOpen: StateFlow<Boolean> = _isTodoEditorOpen.asStateFlow()

    private val _isSettingsOpen = MutableStateFlow(false)
    val isSettingsOpen: StateFlow<Boolean> = _isSettingsOpen.asStateFlow()

    // Multi-Selection & Batch Deletion (Transsion style)
    private val _isNoteSelectionMode = MutableStateFlow(false)
    val isNoteSelectionMode: StateFlow<Boolean> = _isNoteSelectionMode.asStateFlow()

    private val _selectedNoteIds = MutableStateFlow<Set<String>>(emptySet())
    val selectedNoteIds: StateFlow<Set<String>> = _selectedNoteIds.asStateFlow()

    // Data Source with curated, executive sample items persisted in local storage
    private val _notes = MutableStateFlow(preferencesManager.getNotes() ?: createInitialNotes())
    val notes: StateFlow<List<NoteItem>> = _notes.asStateFlow()

    private val _todos = MutableStateFlow(preferencesManager.getTodos() ?: createInitialTodos())
    val todos: StateFlow<List<TodoItem>> = _todos.asStateFlow()

    fun matchesSearch(title: String, content: String, query: String): Boolean {
        if (query.isBlank()) return true
        val lower = query.trim().lowercase()
        return title.lowercase().contains(lower) || content.lowercase().contains(lower)
    }

    // Navigation
    fun setDestination(destination: NavDestination) {
        _currentDestination.value = destination
    }

    // Search
    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setSearchActive(active: Boolean) {
        _isSearchActive.value = active
        if (!active) {
            _searchQuery.value = ""
        }
    }

    // Layout
    fun toggleGridView() {
        preferencesManager.setGridView(!isGridView.value)
    }

    // Theme
    fun setThemeSetting(setting: ThemeSetting) {
        preferencesManager.setThemeSetting(setting)
    }

    // Note Operations
    fun openCreateNote() {
        _editingNote.value = null
        _isNoteEditorOpen.value = true
    }

    fun openEditNote(note: NoteItem) {
        _editingNote.value = note
        _isNoteEditorOpen.value = true
    }

    fun closeNoteEditor() {
        _isNoteEditorOpen.value = false
        _editingNote.value = null
    }

    fun saveNote(
        id: String?,
        title: String,
        content: String,
        colorIndex: Int,
        isPinned: Boolean,
        hasReminder: Boolean,
        reminderText: String?,
        reminderTimestamp: Long? = null,
        isBold: Boolean = false,
        isItalic: Boolean = false,
        isUnderline: Boolean = false,
        isStrikethrough: Boolean = false,
        headingLevel: String = "Body",
        isChecklist: Boolean = false,
        checkedLines: List<Int> = emptyList()
    ) {
        val currentList = _notes.value.toMutableList()
        val finalId: String
        if (id != null) {
            finalId = id
            val index = currentList.indexOfFirst { it.id == id }
            if (index != -1) {
                currentList[index] = currentList[index].copy(
                    title = title,
                    content = content,
                    colorIndex = colorIndex,
                    isPinned = isPinned,
                    hasReminder = hasReminder,
                    reminderText = reminderText,
                    dateModified = System.currentTimeMillis(),
                    isBold = isBold,
                    isItalic = isItalic,
                    isUnderline = isUnderline,
                    isStrikethrough = isStrikethrough,
                    headingLevel = headingLevel,
                    isChecklist = isChecklist,
                    checkedLines = checkedLines
                )
            }
        } else {
            val newNote = NoteItem(
                title = title,
                content = content,
                colorIndex = colorIndex,
                isPinned = isPinned,
                hasReminder = hasReminder,
                reminderText = reminderText,
                dateModified = System.currentTimeMillis(),
                isBold = isBold,
                isItalic = isItalic,
                isUnderline = isUnderline,
                isStrikethrough = isStrikethrough,
                headingLevel = headingLevel,
                isChecklist = isChecklist,
                checkedLines = checkedLines
            )
            finalId = newNote.id
            currentList.add(0, newNote)
        }
        _notes.value = currentList
        preferencesManager.saveNotes(currentList)

        if (hasReminder && reminderTimestamp != null) {
            try {
                com.operatsi.notely.util.NotificationHelper.scheduleReminder(
                    appContext,
                    finalId,
                    title,
                    content,
                    reminderTimestamp
                )
            } catch (_: Throwable) {}
        } else if (!hasReminder) {
            try {
                com.operatsi.notely.util.NotificationHelper.cancelReminder(appContext, finalId)
            } catch (_: Throwable) {}
        }

        closeNoteEditor()
    }

    fun toggleNotePin(noteId: String) {
        val updated = _notes.value.map {
            if (it.id == noteId) it.copy(isPinned = !it.isPinned) else it
        }
        _notes.value = updated
        preferencesManager.saveNotes(updated)
    }

    fun deleteNote(noteId: String) {
        val updated = _notes.value.filter { it.id != noteId }
        _notes.value = updated
        preferencesManager.saveNotes(updated)
        try {
            com.operatsi.notely.util.NotificationHelper.cancelReminder(appContext, noteId)
        } catch (_: Throwable) {}
        if (_editingNote.value?.id == noteId) {
            closeNoteEditor()
        }
    }

    // Selection & Batch Delete Operations (Transsion style)
    fun startNoteSelection(noteId: String) {
        _isNoteSelectionMode.value = true
        _selectedNoteIds.value = setOf(noteId)
    }

    fun toggleNoteSelection(noteId: String) {
        val current = _selectedNoteIds.value.toMutableSet()
        if (current.contains(noteId)) {
            current.remove(noteId)
        } else {
            current.add(noteId)
        }
        _selectedNoteIds.value = current
        if (current.isEmpty()) {
            _isNoteSelectionMode.value = false
        }
    }

    fun selectAllNotes() {
        if (_selectedNoteIds.value.size == _notes.value.size) {
            _selectedNoteIds.value = emptySet()
            _isNoteSelectionMode.value = false
        } else {
            _selectedNoteIds.value = _notes.value.map { it.id }.toSet()
        }
    }

    fun clearNoteSelection() {
        _selectedNoteIds.value = emptySet()
        _isNoteSelectionMode.value = false
    }

    fun deleteSelectedNotes() {
        val toDelete = _selectedNoteIds.value
        val updated = _notes.value.filter { !toDelete.contains(it.id) }
        _notes.value = updated
        preferencesManager.saveNotes(updated)
        toDelete.forEach { noteId ->
            try {
                com.operatsi.notely.util.NotificationHelper.cancelReminder(appContext, noteId)
            } catch (_: Throwable) {}
        }
        clearNoteSelection()
    }

    fun togglePinSelectedNotes() {
        val selected = _selectedNoteIds.value
        val shouldPin = _notes.value.filter { selected.contains(it.id) }.any { !it.isPinned }
        val updated = _notes.value.map {
            if (selected.contains(it.id)) it.copy(isPinned = shouldPin) else it
        }
        _notes.value = updated
        preferencesManager.saveNotes(updated)
        clearNoteSelection()
    }

    // Todo Operations
    fun openCreateTodo() {
        _editingTodo.value = null
        _isTodoEditorOpen.value = true
    }

    fun openEditTodo(todo: TodoItem) {
        _editingTodo.value = todo
        _isTodoEditorOpen.value = true
    }

    fun closeTodoEditor() {
        _isTodoEditorOpen.value = false
        _editingTodo.value = null
    }

    fun saveTodo(
        id: String?,
        title: String,
        dueDateText: String?,
        priority: TodoPriority,
        hasReminder: Boolean = false,
        reminderTimestamp: Long? = null
    ) {
        val currentList = _todos.value.toMutableList()
        val finalId: String
        if (id != null) {
            finalId = id
            val index = currentList.indexOfFirst { it.id == id }
            if (index != -1) {
                currentList[index] = currentList[index].copy(
                    title = title,
                    dueDateText = dueDateText,
                    priority = priority,
                    hasReminder = hasReminder,
                    reminderTimestamp = reminderTimestamp
                )
            }
        } else {
            val newTodo = TodoItem(
                title = title,
                dueDateText = dueDateText,
                priority = priority,
                hasReminder = hasReminder,
                reminderTimestamp = reminderTimestamp
            )
            finalId = newTodo.id
            currentList.add(0, newTodo)
        }
        _todos.value = currentList
        preferencesManager.saveTodos(currentList)

        if (hasReminder && reminderTimestamp != null) {
            try {
                com.operatsi.notely.util.NotificationHelper.scheduleReminder(
                    appContext,
                    "todo_$finalId",
                    "To-do: $title",
                    dueDateText ?: "Task due",
                    reminderTimestamp
                )
            } catch (_: Throwable) {}
        } else {
            try {
                com.operatsi.notely.util.NotificationHelper.cancelReminder(appContext, "todo_$finalId")
            } catch (_: Throwable) {}
        }

        closeTodoEditor()
    }

    fun toggleTodoCompleted(todoId: String) {
        val updated = _todos.value.map {
            if (it.id == todoId) {
                val newCompleted = !it.isCompleted
                if (newCompleted) {
                    try {
                        com.operatsi.notely.util.NotificationHelper.cancelReminder(appContext, "todo_$todoId")
                    } catch (_: Throwable) {}
                }
                it.copy(isCompleted = newCompleted)
            } else it
        }
        _todos.value = updated
        preferencesManager.saveTodos(updated)
    }

    fun deleteTodo(todoId: String) {
        val updated = _todos.value.filter { it.id != todoId }
        _todos.value = updated
        preferencesManager.saveTodos(updated)
        try {
            com.operatsi.notely.util.NotificationHelper.cancelReminder(appContext, "todo_$todoId")
        } catch (_: Throwable) {}
        if (_editingTodo.value?.id == todoId) {
            closeTodoEditor()
        }
    }

    // Settings
    fun openSettings() {
        _isSettingsOpen.value = true
    }

    fun closeSettings() {
        _isSettingsOpen.value = false
    }

    // FAB Action
    fun onFabClicked() {
        when (_currentDestination.value) {
            NavDestination.NOTES -> openCreateNote()
            NavDestination.TODO -> openCreateTodo()
        }
    }

    companion object {
        private fun createInitialNotes(): List<NoteItem> {
            val now = System.currentTimeMillis()
            return listOf(
                NoteItem(
                    title = "Welcome to Notely",
                    content = "Notely is crafted by OperatSI to deliver an ultra-clean, minimalist note-taking experience inspired by Google Keep. Organize your thoughts, pin key documents, and boost daily productivity.",
                    dateModified = now,
                    colorIndex = 4, // Sky tint
                    isPinned = true,
                    hasReminder = false
                ),
                NoteItem(
                    title = "Executive Q4 Strategy",
                    content = "1. Finalize product roadmap and design language.\n2. Verify cross-platform responsiveness on phones, tablets, and Chromebooks.\n3. Complete enterprise security compliance audit.",
                    dateModified = now - 3600_000L * 2,
                    colorIndex = 2, // Sand tint
                    isPinned = true,
                    hasReminder = true,
                    reminderText = "Tomorrow, 9:00 AM"
                ),
                NoteItem(
                    title = "Architecture Principles",
                    content = "Maintain clean separation of concerns. Adhere to Material 3 tokens, seamless edge-to-edge system insets, and zero unnecessary visual clutter.",
                    dateModified = now - 3600_000L * 18,
                    colorIndex = 3, // Mint tint
                    isPinned = false,
                    hasReminder = false
                ),
                NoteItem(
                    title = "Meeting Notes — OperatSI Team",
                    content = "Discussed user feedback on multi-column grid performance on Chromebooks. Refined typography hierarchy for enhanced readability and contrast.",
                    dateModified = now - 3600_000L * 30,
                    colorIndex = 5, // Lavender tint
                    isPinned = false,
                    hasReminder = true,
                    reminderText = "Friday, 2:00 PM"
                ),
                NoteItem(
                    title = "Design System Guidelines",
                    content = "Primary brand color: Deep Corporate Navy.\nCorner radius: 16dp.\nTouch targets: Minimum 48dp.\nElevation: Flat to subtle shadow.",
                    dateModified = now - 3600_000L * 48,
                    colorIndex = 1, // Peach tint
                    isPinned = false,
                    hasReminder = false
                )
            )
        }

        private fun createInitialTodos(): List<TodoItem> {
            val now = System.currentTimeMillis()
            return listOf(
                TodoItem(
                    title = "Review Notely responsive UI on tablet landscape",
                    isCompleted = false,
                    dueDateText = "Today",
                    priority = TodoPriority.HIGH,
                    createdAt = now
                ),
                TodoItem(
                    title = "Validate light and dark mode contrast ratios",
                    isCompleted = false,
                    dueDateText = "Today, 5:00 PM",
                    priority = TodoPriority.HIGH,
                    createdAt = now - 1800_000L
                ),
                TodoItem(
                    title = "Verify Chromebook window resizing and multi-column grid",
                    isCompleted = false,
                    dueDateText = "Tomorrow",
                    priority = TodoPriority.MEDIUM,
                    createdAt = now - 3600_000L
                ),
                TodoItem(
                    title = "Check edge-to-edge system bar insets and keyboard adjustResize",
                    isCompleted = true,
                    dueDateText = "Completed",
                    priority = TodoPriority.MEDIUM,
                    createdAt = now - 7200_000L
                ),
                TodoItem(
                    title = "Setup initial project architecture and Material 3 theme",
                    isCompleted = true,
                    dueDateText = "Completed",
                    priority = TodoPriority.LOW,
                    createdAt = now - 14400_000L
                )
            )
        }
    }
}
