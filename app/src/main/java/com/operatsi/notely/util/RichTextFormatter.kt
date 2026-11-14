package com.operatsi.notely.util

import androidx.compose.material3.ColorScheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.sp

object RichTextFormatter {

    /**
     * Parse text and apply visual SpanStyles (Headings, Bold, Italic, Underline, Strikethrough, Quotes, Bullets).
     * When stripSyntax is true, markdown delimiters like **, <u>, ~~ are omitted for clean display on note cards.
     */
    fun formatToAnnotatedString(
        rawText: String,
        colorScheme: ColorScheme,
        stripSyntax: Boolean = false
    ): AnnotatedString {
        if (rawText.isEmpty()) return AnnotatedString("")

        if (stripSyntax) {
            return buildCleanAnnotatedString(rawText, colorScheme)
        }

        return buildAnnotatedString {
            append(rawText)
            applySyntaxStyles(this, rawText, colorScheme)
        }
    }

    private fun applySyntaxStyles(
        builder: AnnotatedString.Builder,
        text: String,
        colorScheme: ColorScheme
    ) {
        val subtleColor = colorScheme.onSurface.copy(alpha = 0.28f)

        // 1. Line-based styles (H1-H5, Bullets, Quotes)
        var lineStart = 0
        while (lineStart < text.length) {
            val lineEnd = text.indexOf('\n', lineStart).let { if (it == -1) text.length else it }
            val line = text.substring(lineStart, lineEnd)

            when {
                line.startsWith("##### ") -> {
                    builder.addStyle(SpanStyle(fontSize = 15.sp, fontWeight = FontWeight.Bold), lineStart, lineEnd)
                    builder.addStyle(SpanStyle(color = subtleColor), lineStart, lineStart + 6)
                }
                line.startsWith("#### ") -> {
                    builder.addStyle(SpanStyle(fontSize = 17.sp, fontWeight = FontWeight.Bold), lineStart, lineEnd)
                    builder.addStyle(SpanStyle(color = subtleColor), lineStart, lineStart + 5)
                }
                line.startsWith("### ") -> {
                    builder.addStyle(SpanStyle(fontSize = 19.sp, fontWeight = FontWeight.Bold), lineStart, lineEnd)
                    builder.addStyle(SpanStyle(color = subtleColor), lineStart, lineStart + 4)
                }
                line.startsWith("## ") -> {
                    builder.addStyle(SpanStyle(fontSize = 21.sp, fontWeight = FontWeight.Bold), lineStart, lineEnd)
                    builder.addStyle(SpanStyle(color = subtleColor), lineStart, lineStart + 3)
                }
                line.startsWith("# ") -> {
                    builder.addStyle(SpanStyle(fontSize = 24.sp, fontWeight = FontWeight.Bold), lineStart, lineEnd)
                    builder.addStyle(SpanStyle(color = subtleColor), lineStart, lineStart + 2)
                }
                line.startsWith("> ") || line.startsWith("❝ ") -> {
                    builder.addStyle(
                        SpanStyle(
                            fontStyle = FontStyle.Italic,
                            color = colorScheme.primary
                        ),
                        lineStart,
                        lineEnd
                    )
                    builder.addStyle(SpanStyle(color = subtleColor), lineStart, lineStart + 2)
                }
                line.startsWith("• ") || line.startsWith("•— ") -> {
                    builder.addStyle(
                        SpanStyle(fontWeight = FontWeight.SemiBold, color = colorScheme.primary),
                        lineStart,
                        (lineStart + 2).coerceAtMost(lineEnd)
                    )
                }
                line.startsWith("☐ ") || line.startsWith("[ ] ") -> {
                    builder.addStyle(
                        SpanStyle(fontWeight = FontWeight.Bold, color = colorScheme.primary),
                        lineStart,
                        (lineStart + 2).coerceAtMost(lineEnd)
                    )
                }
            }
            lineStart = lineEnd + 1
        }

        // 2. Inline Bold: **text**
        val boldRegex = Regex("""\*\*(.+?)\*\*""")
        boldRegex.findAll(text).forEach { match ->
            val range = match.range
            builder.addStyle(SpanStyle(color = subtleColor), range.first, range.first + 2)
            builder.addStyle(SpanStyle(fontWeight = FontWeight.Bold), range.first + 2, range.last - 1)
            builder.addStyle(SpanStyle(color = subtleColor), range.last - 1, range.last + 1)
        }

        // 3. Inline Italic: *text* (excluding **)
        val italicRegex = Regex("""(?<!\*)\*([^*]+?)\*(?!\*)""")
        italicRegex.findAll(text).forEach { match ->
            val range = match.range
            builder.addStyle(SpanStyle(color = subtleColor), range.first, range.first + 1)
            builder.addStyle(SpanStyle(fontStyle = FontStyle.Italic), range.first + 1, range.last)
            builder.addStyle(SpanStyle(color = subtleColor), range.last, range.last + 1)
        }

        // 4. Inline Underline: <u>text</u>
        val underlineRegex = Regex("""<u>(.+?)</u>""", RegexOption.IGNORE_CASE)
        underlineRegex.findAll(text).forEach { match ->
            val range = match.range
            builder.addStyle(SpanStyle(color = subtleColor), range.first, range.first + 3)
            builder.addStyle(SpanStyle(textDecoration = TextDecoration.Underline), range.first + 3, range.last - 3)
            builder.addStyle(SpanStyle(color = subtleColor), range.last - 3, range.last + 1)
        }

        // 5. Inline Strikethrough: ~~text~~
        val strikeRegex = Regex("""~~(.+?)~~""")
        strikeRegex.findAll(text).forEach { match ->
            val range = match.range
            builder.addStyle(SpanStyle(color = subtleColor), range.first, range.first + 2)
            builder.addStyle(SpanStyle(textDecoration = TextDecoration.LineThrough), range.first + 2, range.last - 1)
            builder.addStyle(SpanStyle(color = subtleColor), range.last - 1, range.last + 1)
        }
    }

    private fun buildCleanAnnotatedString(
        text: String,
        colorScheme: ColorScheme
    ): AnnotatedString {
        val lines = text.split('\n')
        return buildAnnotatedString {
            lines.forEachIndexed { lineIdx, rawLine ->
                if (lineIdx > 0) append("\n")

                var line = rawLine
                var headingStyle: SpanStyle? = null

                when {
                    line.startsWith("##### ") -> {
                        headingStyle = SpanStyle(fontSize = 15.sp, fontWeight = FontWeight.Bold)
                        line = line.removePrefix("##### ")
                    }
                    line.startsWith("#### ") -> {
                        headingStyle = SpanStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        line = line.removePrefix("#### ")
                    }
                    line.startsWith("### ") -> {
                        headingStyle = SpanStyle(fontSize = 17.sp, fontWeight = FontWeight.Bold)
                        line = line.removePrefix("### ")
                    }
                    line.startsWith("## ") -> {
                        headingStyle = SpanStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        line = line.removePrefix("## ")
                    }
                    line.startsWith("# ") -> {
                        headingStyle = SpanStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        line = line.removePrefix("# ")
                    }
                    line.startsWith("> ") || line.startsWith("❝ ") -> {
                        headingStyle = SpanStyle(fontStyle = FontStyle.Italic, color = colorScheme.primary)
                        line = line.substring(2)
                    }
                }

                val lineStartOffset = this.length

                // Parse and strip inline tokens
                val cleaned = line
                    .replace(Regex("""<u>(.+?)</u>"""), "$1")
                    .replace(Regex("""\*\*(.+?)\*\*"""), "$1")
                    .replace(Regex("""(?<!\*)\*([^*]+?)\*(?!\*)"""), "$1")
                    .replace(Regex("""~~(.+?)~~"""), "$1")

                append(cleaned)
                val lineEndOffset = this.length

                if (headingStyle != null) {
                    addStyle(headingStyle, lineStartOffset, lineEndOffset)
                }
            }
        }
    }
}

class MarkdownVisualTransformation(
    private val colorScheme: ColorScheme
) : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val formatted = RichTextFormatter.formatToAnnotatedString(
            rawText = text.text,
            colorScheme = colorScheme,
            stripSyntax = false
        )
        return TransformedText(formatted, OffsetMapping.Identity)
    }
}
