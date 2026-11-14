package com.operatsi.notely.model

import java.util.UUID

enum class TodoPriority {
    LOW,
    MEDIUM,
    HIGH
}

data class TodoItem(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val isCompleted: Boolean = false,
    val dueDateText: String? = null,
    val priority: TodoPriority = TodoPriority.MEDIUM,
    val createdAt: Long = System.currentTimeMillis(),
    val hasReminder: Boolean = false,
    val reminderTimestamp: Long? = null
)
