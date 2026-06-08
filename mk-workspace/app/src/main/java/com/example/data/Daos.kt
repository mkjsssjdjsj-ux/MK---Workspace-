package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface JournalDao {
    @Query("SELECT * FROM journal_entries ORDER BY date DESC")
    fun getAllEntries(): Flow<List<JournalEntry>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEntry(entry: JournalEntry)

    @Delete
    suspend fun deleteEntry(entry: JournalEntry)

    @Query("DELETE FROM journal_entries WHERE id = :id")
    suspend fun deleteEntryById(id: Int)
}

@Dao
interface NoteDao {
    @Query("SELECT * FROM notes ORDER BY date DESC")
    fun getAllNotes(): Flow<List<Note>>

    @Query("SELECT * FROM notes WHERE title LIKE '%' || :query || '%' OR content LIKE '%' || :query || '%'")
    fun searchNotes(query: String): Flow<List<Note>>

    @Query("SELECT * FROM notes WHERE category = :category ORDER BY date DESC")
    fun getNotesByCategory(category: String): Flow<List<Note>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: Note)

    @Delete
    suspend fun deleteNote(note: Note)

    @Query("DELETE FROM notes WHERE id = :id")
    suspend fun deleteNoteById(id: Int)
}

@Dao
interface ProjectDao {
    @Query("SELECT * FROM projects ORDER BY deadline ASC")
    fun getAllProjects(): Flow<List<Project>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProject(project: Project)

    @Delete
    suspend fun deleteProject(project: Project)

    @Query("DELETE FROM projects WHERE id = :id")
    suspend fun deleteProjectById(id: Int)
}

@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks ORDER BY deadline ASC")
    fun getAllTasks(): Flow<List<Task>>

    @Query("SELECT * FROM tasks WHERE projectId = :projectId")
    fun getTasksForProject(projectId: Int): Flow<List<Task>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: Task)

    @Delete
    suspend fun deleteTask(task: Task)

    @Query("DELETE FROM tasks WHERE id = :id")
    suspend fun deleteTaskById(id: Int)
}

@Dao
interface MoodDao {
    @Query("SELECT * FROM mood_items ORDER BY date DESC")
    fun getAllMoodItems(): Flow<List<MoodItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMoodItem(item: MoodItem)

    @Update
    suspend fun updateMoodItem(item: MoodItem)

    @Delete
    suspend fun deleteMoodItem(item: MoodItem)
}

@Dao
interface SketchDao {
    @Query("SELECT * FROM sketch_elements")
    fun getAllElements(): Flow<List<SketchElement>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertElement(element: SketchElement)

    @Query("DELETE FROM sketch_elements")
    suspend fun clearCanvas()

    @Delete
    suspend fun deleteElement(element: SketchElement)
}

@Dao
interface GoalDao {
    @Query("SELECT * FROM goals ORDER BY date DESC")
    fun getAllGoals(): Flow<List<Goal>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGoal(goal: Goal)

    @Query("UPDATE goals SET isCompleted = :isCompleted WHERE id = :id")
    suspend fun updateGoalCompletion(id: Int, isCompleted: Boolean)

    @Delete
    suspend fun deleteGoal(goal: Goal)
}

@Dao
interface VaultFileDao {
    @Query("SELECT * FROM vault_files ORDER BY date DESC")
    fun getAllFiles(): Flow<List<VaultFile>>

    @Query("SELECT * FROM vault_files WHERE folder = :folder ORDER BY date DESC")
    fun getFilesByFolder(folder: String): Flow<List<VaultFile>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFile(file: VaultFile)

    @Delete
    suspend fun deleteFile(file: VaultFile)
}

@Dao
interface CalendarEventDao {
    @Query("SELECT * FROM calendar_events ORDER BY date ASC")
    fun getAllEvents(): Flow<List<CalendarEvent>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvent(event: CalendarEvent)

    @Delete
    suspend fun deleteEvent(event: CalendarEvent)
}
