package com.example.data

import android.content.Context
import androidx.room.*

@Database(
    entities = [
        JournalEntry::class,
        Note::class,
        Project::class,
        Task::class,
        MoodItem::class,
        SketchElement::class,
        Goal::class,
        VaultFile::class,
        CalendarEvent::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun journalDao(): JournalDao
    abstract fun noteDao(): NoteDao
    abstract fun projectDao(): ProjectDao
    abstract fun taskDao(): TaskDao
    abstract fun moodDao(): MoodDao
    abstract fun sketchDao(): SketchDao
    abstract fun goalDao(): GoalDao
    abstract fun vaultFileDao(): VaultFileDao
    abstract fun calendarEventDao(): CalendarEventDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "mk_workspace_db"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
