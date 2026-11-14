package com.operatsi.notely.data

import android.content.Context
import android.content.SharedPreferences
import com.operatsi.notely.ui.theme.ThemeSetting
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class PreferencesManager(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("notely_preferences", Context.MODE_PRIVATE)

    private val _themeSetting = MutableStateFlow(getInitialTheme())
    val themeSetting: StateFlow<ThemeSetting> = _themeSetting.asStateFlow()

    private val _isGridView = MutableStateFlow(prefs.getBoolean(KEY_IS_GRID_VIEW, true))
    val isGridView: StateFlow<Boolean> = _isGridView.asStateFlow()

    private fun getInitialTheme(): ThemeSetting {
        val name = prefs.getString(KEY_THEME_SETTING, ThemeSetting.SYSTEM.name)
        return try {
            ThemeSetting.valueOf(name ?: ThemeSetting.SYSTEM.name)
        } catch (_: Exception) {
            ThemeSetting.SYSTEM
        }
    }

    fun setThemeSetting(setting: ThemeSetting) {
        try {
            val editor = prefs.edit()
            editor?.putString(KEY_THEME_SETTING, setting.name)
            editor?.apply()
        } catch (_: Throwable) {
            // Layoutlib / Preview fallback
        }
        _themeSetting.value = setting
    }

    fun setGridView(isGrid: Boolean) {
        try {
            val editor = prefs.edit()
            editor?.putBoolean(KEY_IS_GRID_VIEW, isGrid)
            editor?.apply()
        } catch (_: Throwable) {
            // Layoutlib / Preview fallback
        }
        _isGridView.value = isGrid
    }

    fun saveNotes(notes: List<com.operatsi.notely.model.NoteItem>) {
        try {
            val jsonArray = org.json.JSONArray()
            for (note in notes) {
                val obj = org.json.JSONObject().apply {
                    put("id", note.id)
                    put("title", note.title)
                    put("content", note.content)
                    put("dateModified", note.dateModified)
                    put("colorIndex", note.colorIndex)
                    put("isPinned", note.isPinned)
                    put("hasReminder", note.hasReminder)
                    put("reminderText", note.reminderText ?: "")
                    put("isBold", note.isBold)
                    put("isItalic", note.isItalic)
                    put("isUnderline", note.isUnderline)
                    put("isStrikethrough", note.isStrikethrough)
                    put("headingLevel", note.headingLevel)
                    put("isChecklist", note.isChecklist)
                    val checkedArray = org.json.JSONArray()
                    note.checkedLines.forEach { checkedArray.put(it) }
                    put("checkedLines", checkedArray)
                }
                jsonArray.put(obj)
            }
            val editor = prefs.edit()
            editor?.putString(KEY_NOTES_DATA, jsonArray.toString())
            editor?.apply()
        } catch (_: Throwable) {}
    }

    fun getNotes(): List<com.operatsi.notely.model.NoteItem>? {
        val jsonString = prefs.getString(KEY_NOTES_DATA, null) ?: return null
        return try {
            val jsonArray = org.json.JSONArray(jsonString)
            val list = mutableListOf<com.operatsi.notely.model.NoteItem>()
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                val rawContent = obj.getString("content")
                val cleanContent = rawContent
                    .replace(Regex("""\*\*|\*|<u>|</u>|~~|☐\s*"""), "")
                    .replace(Regex("""^#{1,6}\s*""", RegexOption.MULTILINE), "")
                    .replace(Regex("""^>\s*""", RegexOption.MULTILINE), "")
                    .trim()

                val checkedList = mutableListOf<Int>()
                val checkedArray = obj.optJSONArray("checkedLines")
                if (checkedArray != null) {
                    for (j in 0 until checkedArray.length()) {
                        checkedList.add(checkedArray.getInt(j))
                    }
                }

                list.add(
                    com.operatsi.notely.model.NoteItem(
                        id = obj.getString("id"),
                        title = obj.getString("title"),
                        content = cleanContent,
                        dateModified = obj.optLong("dateModified", System.currentTimeMillis()),
                        colorIndex = obj.optInt("colorIndex", 0),
                        isPinned = obj.optBoolean("isPinned", false),
                        hasReminder = obj.optBoolean("hasReminder", false),
                        reminderText = obj.optString("reminderText", "").takeIf { it.isNotEmpty() },
                        isBold = obj.optBoolean("isBold", rawContent.contains("**")),
                        isItalic = obj.optBoolean("isItalic", rawContent.contains("*")),
                        isUnderline = obj.optBoolean("isUnderline", rawContent.contains("<u>")),
                        isStrikethrough = obj.optBoolean("isStrikethrough", rawContent.contains("~~")),
                        headingLevel = obj.optString("headingLevel", if (rawContent.startsWith("#")) "H1" else "Body"),
                        isChecklist = obj.optBoolean("isChecklist", rawContent.contains("☐")),
                        checkedLines = checkedList
                    )
                )
            }
            list
        } catch (_: Throwable) {
            null
        }
    }

    fun saveTodos(todos: List<com.operatsi.notely.model.TodoItem>) {
        try {
            val jsonArray = org.json.JSONArray()
            for (todo in todos) {
                val obj = org.json.JSONObject().apply {
                    put("id", todo.id)
                    put("title", todo.title)
                    put("isCompleted", todo.isCompleted)
                    put("priority", todo.priority.name)
                    put("dueDateText", todo.dueDateText ?: "")
                    put("createdAt", todo.createdAt)
                    put("hasReminder", todo.hasReminder)
                    if (todo.reminderTimestamp != null) {
                        put("reminderTimestamp", todo.reminderTimestamp)
                    }
                }
                jsonArray.put(obj)
            }
            val editor = prefs.edit()
            editor?.putString(KEY_TODOS_DATA, jsonArray.toString())
            editor?.apply()
        } catch (_: Throwable) {}
    }

    fun getTodos(): List<com.operatsi.notely.model.TodoItem>? {
        val jsonString = prefs.getString(KEY_TODOS_DATA, null) ?: return null
        return try {
            val jsonArray = org.json.JSONArray(jsonString)
            val list = mutableListOf<com.operatsi.notely.model.TodoItem>()
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                val priorityName = obj.optString("priority", com.operatsi.notely.model.TodoPriority.MEDIUM.name)
                val priority = try {
                    com.operatsi.notely.model.TodoPriority.valueOf(priorityName)
                } catch (_: Exception) {
                    com.operatsi.notely.model.TodoPriority.MEDIUM
                }
                list.add(
                    com.operatsi.notely.model.TodoItem(
                        id = obj.getString("id"),
                        title = obj.getString("title"),
                        isCompleted = obj.optBoolean("isCompleted", false),
                        priority = priority,
                        dueDateText = obj.optString("dueDateText", "").takeIf { it.isNotEmpty() },
                        createdAt = obj.optLong("createdAt", System.currentTimeMillis()),
                        hasReminder = obj.optBoolean("hasReminder", false),
                        reminderTimestamp = if (obj.has("reminderTimestamp")) obj.getLong("reminderTimestamp") else null
                    )
                )
            }
            list
        } catch (_: Throwable) {
            null
        }
    }

    companion object {
        private const val KEY_THEME_SETTING = "key_theme_setting"
        private const val KEY_IS_GRID_VIEW = "key_is_grid_view"
        private const val KEY_NOTES_DATA = "key_notes_data"
        private const val KEY_TODOS_DATA = "key_todos_data"
    }
}
