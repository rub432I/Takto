package com.takto.launcher.core

import android.content.Context
import android.net.Uri
import androidx.documentfile.provider.DocumentFile
import java.io.OutputStreamWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * LogseqFileManager: Storage Access Framework (SAF) Document Engine for Takto
 * Enforces the "OS for Life" philosophy:
 * 1. Appends daily thoughts, tasks, and expenses directly into today's Logseq Markdown journal
 * 2. Operates 100% offline with zero cloud telemetry
 * 3. Never alters historical notes; append-only stream pattern
 */
class LogseqFileManager(private val context: Context) {

    private val journalDateFormat = SimpleDateFormat("yyyy_MM_dd", Locale.US)
    private val timeFormat = SimpleDateFormat("HH:mm", Locale.US)

    /**
     * Resolves or creates today's Logseq journal file (yyyy_MM_dd.md)
     */
    fun getOrCreateTodayJournal(treeUri: Uri): DocumentFile? {
        val rootDir = DocumentFile.fromTreeUri(context, treeUri) ?: return null
        val journalsDir = rootDir.findFile("journals") ?: rootDir.createDirectory("journals") ?: return null
        val todayFileName = "${journalDateFormat.format(Date())}.md"

        return journalsDir.findFile(todayFileName) ?: journalsDir.createFile("text/markdown", todayFileName)
    }

    /**
     * Appends an entry into today's Logseq Markdown file
     */
    fun appendEntry(treeUri: Uri, prefixType: String, content: String): Boolean {
        val file = getOrCreateTodayJournal(treeUri) ?: return false
        val timestamp = timeFormat.format(Date())

        val bulletText = when (prefixType.lowercase()) {
            "t", "task" -> "- TODO $content"
            "$", "expense" -> "- #expense $content"
            else -> "- $timestamp $content"
        }

        return try {
            context.contentResolver.openOutputStream(file.uri, "wa")?.use { stream ->
                OutputStreamWriter(stream).use { writer ->
                    writer.appendLine(bulletText)
                }
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * Reads all active TODO tasks from today's journal
     */
    fun readActiveTasks(treeUri: Uri): List<String> {
        val file = getOrCreateTodayJournal(treeUri) ?: return emptyList()
        val tasks = mutableListOf<String>()

        try {
            context.contentResolver.openInputStream(file.uri)?.bufferedReader()?.useLines { lines ->
                lines.forEach { line ->
                    val trimmed = line.trim()
                    if (trimmed.startsWith("- TODO ")) {
                        tasks.add(trimmed.removePrefix("- TODO ").trim())
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return tasks
    }
}
