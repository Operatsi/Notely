package com.operatsi.notely.model

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

data class NoteItem(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val content: String,
    val dateModified: Long = System.currentTimeMillis(),
    val colorIndex: Int = 0,
    val isPinned: Boolean = false,
    val hasReminder: Boolean = false,
    val reminderText: String? = null,
    val isBold: Boolean = false,
    val isItalic: Boolean = false,
    val isUnderline: Boolean = false,
    val isStrikethrough: Boolean = false,
    val headingLevel: String = "Body",
    val isChecklist: Boolean = false,
    val checkedLines: List<Int> = emptyList()
) {
    val cleanContent: String
        get() = content
            .replace(Regex("""\*\*|\*|<u>|</u>|~~|☐\s*"""), "")
            .replace(Regex("""^#{1,6}\s*""", RegexOption.MULTILINE), "")
            .replace(Regex("""^>\s*""", RegexOption.MULTILINE), "")
            .trim()

    val formattedDate: String
        get() {
            val now = System.currentTimeMillis()
            val diff = now - dateModified
            val oneDay = 24 * 60 * 60 * 1000L
            return when {
                diff < oneDay -> {
                    val sdf = SimpleDateFormat("h:mm a", Locale.getDefault())
                    "Today, ${sdf.format(Date(dateModified))}"
                }
                diff < 2 * oneDay -> "Yesterday"
                else -> {
                    val sdf = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
                    sdf.format(Date(dateModified))
                }
            }
        }
}
