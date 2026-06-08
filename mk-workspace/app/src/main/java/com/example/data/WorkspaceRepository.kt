package com.example.data

import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class WorkspaceRepository(private val database: AppDatabase) {

    // --- Journal ---
    val allJournalEntries: Flow<List<JournalEntry>> = database.journalDao().getAllEntries()
    suspend fun insertJournal(entry: JournalEntry) = withContext(Dispatchers.IO) {
        database.journalDao().insertEntry(entry)
    }
    suspend fun deleteJournalById(id: Int) = withContext(Dispatchers.IO) {
        database.journalDao().deleteEntryById(id)
    }

    // --- Notes ---
    val allNotes: Flow<List<Note>> = database.noteDao().getAllNotes()
    fun searchNotes(query: String): Flow<List<Note>> = database.noteDao().searchNotes(query)
    fun getNotesByCategory(category: String): Flow<List<Note>> = database.noteDao().getNotesByCategory(category)
    suspend fun insertNote(note: Note) = withContext(Dispatchers.IO) {
        database.noteDao().insertNote(note)
    }
    suspend fun deleteNoteById(id: Int) = withContext(Dispatchers.IO) {
        database.noteDao().deleteNoteById(id)
    }

    // --- Projects ---
    val allProjects: Flow<List<Project>> = database.projectDao().getAllProjects()
    suspend fun insertProject(project: Project) = withContext(Dispatchers.IO) {
        database.projectDao().insertProject(project)
    }
    suspend fun deleteProjectById(id: Int) = withContext(Dispatchers.IO) {
        database.projectDao().deleteProjectById(id)
    }

    // --- Tasks ---
    val allTasks: Flow<List<Task>> = database.taskDao().getAllTasks()
    fun getTasksForProject(projectId: Int): Flow<List<Task>> = database.taskDao().getTasksForProject(projectId)
    suspend fun insertTask(task: Task) = withContext(Dispatchers.IO) {
        database.taskDao().insertTask(task)
    }
    suspend fun deleteTaskById(id: Int) = withContext(Dispatchers.IO) {
        database.taskDao().deleteTaskById(id)
    }

    // --- Mood Board ---
    val allMoodItems: Flow<List<MoodItem>> = database.moodDao().getAllMoodItems()
    suspend fun insertMoodItem(item: MoodItem) = withContext(Dispatchers.IO) {
        database.moodDao().insertMoodItem(item)
    }
    suspend fun updateMoodItem(item: MoodItem) = withContext(Dispatchers.IO) {
        database.moodDao().updateMoodItem(item)
    }
    suspend fun deleteMoodItem(item: MoodItem) = withContext(Dispatchers.IO) {
        database.moodDao().deleteMoodItem(item)
    }

    // --- Sketch Area ---
    val allSketchElements: Flow<List<SketchElement>> = database.sketchDao().getAllElements()
    suspend fun insertSketchElement(element: SketchElement) = withContext(Dispatchers.IO) {
        database.sketchDao().insertElement(element)
    }
    suspend fun clearSketchCanvas() = withContext(Dispatchers.IO) {
        database.sketchDao().clearCanvas()
    }
    suspend fun deleteSketchElement(element: SketchElement) = withContext(Dispatchers.IO) {
        database.sketchDao().deleteElement(element)
    }

    // --- Goals ---
    val allGoals: Flow<List<Goal>> = database.goalDao().getAllGoals()
    suspend fun insertGoal(goal: Goal) = withContext(Dispatchers.IO) {
        database.goalDao().insertGoal(goal)
    }
    suspend fun updateGoalCompletion(id: Int, isCompleted: Boolean) = withContext(Dispatchers.IO) {
        database.goalDao().updateGoalCompletion(id, isCompleted)
    }
    suspend fun deleteGoal(goal: Goal) = withContext(Dispatchers.IO) {
        database.goalDao().deleteGoal(goal)
    }

    // --- File Vault ---
    val allVaultFiles: Flow<List<VaultFile>> = database.vaultFileDao().getAllFiles()
    fun getVaultFilesByFolder(folder: String): Flow<List<VaultFile>> = database.vaultFileDao().getFilesByFolder(folder)
    suspend fun insertVaultFile(file: VaultFile) = withContext(Dispatchers.IO) {
        database.vaultFileDao().insertFile(file)
    }
    suspend fun deleteVaultFile(file: VaultFile) = withContext(Dispatchers.IO) {
        database.vaultFileDao().deleteFile(file)
    }

    // --- Calendar Events ---
    val allCalendarEvents: Flow<List<CalendarEvent>> = database.calendarEventDao().getAllEvents()
    suspend fun insertCalendarEvent(event: CalendarEvent) = withContext(Dispatchers.IO) {
        database.calendarEventDao().insertEvent(event)
    }
    suspend fun deleteCalendarEvent(event: CalendarEvent) = withContext(Dispatchers.IO) {
        database.calendarEventDao().deleteEvent(event)
    }

    // --- Gemini AI Assistant Integration ---
    suspend fun askGemini(prompt: String, systemInstruction: String? = null): String = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext "API Key is missing! Please configure the GEMINI_API_KEY secret in Google AI Studio to unlock the digital assistant."
        }

        val request = GeminiRequest(
            contents = listOf(
                GeminiContent(parts = listOf(GeminiPart(text = prompt)))
            ),
            systemInstruction = systemInstruction?.let {
                GeminiContent(parts = listOf(GeminiPart(text = it)))
            }
        )

        try {
            val response = RetrofitClient.service.generateContent(apiKey, request)
            response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                ?: "No valid response text returned from the AI assistant."
        } catch (e: Exception) {
            e.printStackTrace()
            "AI Assistant Error: ${e.localizedMessage ?: "Failed to reach the server. Make sure internet is active."}"
        }
    }
}
