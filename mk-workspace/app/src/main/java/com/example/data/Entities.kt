package com.example.data

import androidx.room.*

@Entity(tableName = "journal_entries")
data class JournalEntry(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val content: String,
    val date: Long = System.currentTimeMillis(),
    val mood: String, // Excellent, Good, Neutral, Low
    val tag: String, // Personal, Work, Creative
    val reflection: String
)

@Entity(tableName = "notes")
data class Note(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val content: String,
    val category: String, // Ideas, Business, Design, YouTube, Learning
    val tags: String, // Comma separated tags
    val date: Long = System.currentTimeMillis()
)

@Entity(tableName = "projects")
data class Project(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val description: String,
    val status: String, // Planning, Active, On Hold, Completed
    val deadline: Long,
    val colorHex: String = "#1E88E5"
)

@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val projectId: Int? = null,
    val title: String,
    val description: String,
    val status: String, // To Do, In Progress, Review, Done
    val deadline: Long = System.currentTimeMillis(),
    val priority: String = "Medium" // Low, Medium, High
)

@Entity(tableName = "mood_items")
data class MoodItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val imagePath: String? = null, // Path to loaded drawable or file
    val type: String, // Image, ColorPalette, Typography
    val content: String? = null, // CSV or metadata config (e.g. hex codes)
    val date: Long = System.currentTimeMillis(),
    val xOffset: Float = 0f,
    val yOffset: Float = 0f,
    val width: Float = 150f,
    val height: Float = 150f
)

@Entity(tableName = "sketch_elements")
data class SketchElement(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val type: String, // Path, Shape, Text
    val points: String, // Serialized floats "x,y;x,y;..."
    val colorHex: String,
    val thickness: Float = 5f,
    val text: String? = null,
    val x: Float = 0f,
    val y: Float = 0f
)

@Entity(tableName = "goals")
data class Goal(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val targetType: String, // Daily, Weekly, Monthly, Yearly
    val isCompleted: Boolean = false,
    val date: Long = System.currentTimeMillis(),
    val category: String = "General"
)

@Entity(tableName = "vault_files")
data class VaultFile(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val folder: String, // e.g. "Assets", "PDFs", "Designs"
    val size: String,
    val type: String, // Image, PDF, Document, Design Asset
    val date: Long = System.currentTimeMillis(),
    val localPath: String? = null
)

@Entity(tableName = "calendar_events")
data class CalendarEvent(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val description: String,
    val date: Long, // timestamp
    val category: String, // Personal, Work, Creative, Deadline
    val isReminder: Boolean = false
)
