package com.example.ui

import android.app.Application
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class WorkspaceViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val repository = WorkspaceRepository(db)

    // --- Authentication ---
    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    private val _isFingerprintEnabled = MutableStateFlow(true)
    val isFingerprintEnabled: StateFlow<Boolean> = _isFingerprintEnabled.asStateFlow()

    // --- Active Module / Navigation ---
    // Screens: Dashboard, Journal, Notes, Projects, Kanban, MoodBoard, SketchBoard, Goals, Vault, Assistant, Calendar
    private val _activeScreen = MutableStateFlow("Dashboard")
    val activeScreen: StateFlow<String> = _activeScreen.asStateFlow()

    // --- Journal State ---
    val journalEntries: StateFlow<List<JournalEntry>> = repository.allJournalEntries
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- Notes State ---
    private val _noteSearchQuery = MutableStateFlow("")
    val noteSearchQuery: StateFlow<String> = _noteSearchQuery.asStateFlow()

    val notes: StateFlow<List<Note>> = _noteSearchQuery
        .flatMapLatest { query ->
            if (query.isEmpty()) {
                repository.allNotes
            } else {
                repository.searchNotes(query)
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _selectedNoteCategory = MutableStateFlow("All")
    val selectedNoteCategory: StateFlow<String> = _selectedNoteCategory.asStateFlow()

    // --- Projects State ---
    val projects: StateFlow<List<Project>> = repository.allProjects
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- Kanban Tasks State ---
    val tasks: StateFlow<List<Task>> = repository.allTasks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- Mood Board State ---
    val moodItems: StateFlow<List<MoodItem>> = repository.allMoodItems
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- Goals State ---
    val goals: StateFlow<List<Goal>> = repository.allGoals
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- File Vault State ---
    val vaultFiles: StateFlow<List<VaultFile>> = repository.allVaultFiles
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _selectedVaultFolder = MutableStateFlow("All")
    val selectedVaultFolder: StateFlow<String> = _selectedVaultFolder.asStateFlow()

    // --- Calendar State ---
    val calendarEvents: StateFlow<List<CalendarEvent>> = repository.allCalendarEvents
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _calendarSelectedDate = MutableStateFlow(System.currentTimeMillis())
    val calendarSelectedDate: StateFlow<Long> = _calendarSelectedDate.asStateFlow()

    // --- AI Assistant State ---
    private val _aiChatHistory = MutableStateFlow<List<Pair<String, Boolean>>>(
        listOf("Hello Mohamed Khaled! I am your custom MK Digital Assistant. How can I facilitate your project operations or daily planning today?" to false)
    ) // Pair(Message, isUser)
    val aiChatHistory: StateFlow<List<Pair<String, Boolean>>> = _aiChatHistory.asStateFlow()

    private val _isAiLoading = MutableStateFlow(false)
    val isAiLoading: StateFlow<Boolean> = _isAiLoading.asStateFlow()

    // --- Sketch Board Drawing In-Memory (for speed, backed by Room on save) ---
    val sketchElements = mutableStateListOf<SketchElement>()

    init {
        // Observe and load sketch element items from Room database
        viewModelScope.launch {
            repository.allSketchElements.collect { dbElements ->
                sketchElements.clear()
                sketchElements.addAll(dbElements)
            }
        }
        
        // Seed default items if Room is completely empty to give Mohamed a beautiful baseline experience
        seedInitialDataIfNeeded()
    }

    private fun seedInitialDataIfNeeded() {
        viewModelScope.launch {
            // Check projects
            repository.allProjects.first().let { current ->
                if (current.isEmpty()) {
                    // Seed active operational projects
                    repository.insertProject(Project(
                        name = "MK Premium Workspace",
                        description = "Perfecting the design, interactive canvas, and local persistence of the ultimate workspace application.",
                        status = "Active",
                        deadline = System.currentTimeMillis() + 86400000L * 7,
                        colorHex = "#1E88E5"
                    ))
                    repository.insertProject(Project(
                        name = "Content Strategy - YouTube Hub",
                        description = "Preparing structured material, design aesthetics, scripts, and video editing outlines.",
                        status = "Planning",
                        deadline = System.currentTimeMillis() + 86400000L * 21,
                        colorHex = "#E64A19"
                    ))

                    // Seed some initial smart notes
                    repository.insertNote(Note(
                        title = "Glassmorphism Aesthetics Reference",
                        content = "# Material 3 Glassmorphism Guidelines\n\nTo achieve premium depth and ambient feedback:\n1. Use transparent content backgrounds with custom alpha overlays.\n2. Leverage subtle inner outline strokes of 1.dp with high-contrast alphas.\n3. Establish surface layers via shadow overlay elevations and rounded edges.",
                        category = "Design",
                        tags = "UI, Material3, Premium"
                    ))
                    repository.insertNote(Note(
                        title = "MK Assistant Launch Directives",
                        content = "The core AI workspace generator facilitates prompt queries, formats structured plans, and parses content strategies. Powered by Gemini API to ensure instant, context-aware operational planning.",
                        category = "Ideas",
                        tags = "AI, System, Roadmap"
                    ))

                    // Seed Kanban tasks
                    repository.insertTask(Task(
                        title = "Interface Polish on Canvas",
                        description = "Refine finger sketch brush stroke thickness, shape previews, and coordinate translations.",
                        status = "In Progress",
                        deadline = System.currentTimeMillis() + 86400000L,
                        priority = "High"
                    ))
                    repository.insertTask(Task(
                        title = "Dynamic Glassmorphism Login",
                        description = "Develop beautiful auth widgets with customizable security and local biometric simulated pathways.",
                        status = "Done",
                        deadline = System.currentTimeMillis() - 86400000L,
                        priority = "Medium"
                    ))
                    repository.insertTask(Task(
                        title = "Configure Gemini Bot Keys",
                        description = "Verify local .env workspace bindings to let the agent talk dynamically to model systems.",
                        status = "To Do",
                        deadline = System.currentTimeMillis() + 86400000L * 2,
                        priority = "High"
                    ))

                    // Seed some goals
                    repository.insertGoal(Goal(title = "Maintain 95% Daily Productivity Score", targetType = "Daily", isCompleted = false, category = "Work"))
                    repository.insertGoal(Goal(title = "Draft YouTube Roadmap Phase 1", targetType = "Weekly", isCompleted = true, category = "YouTube"))
                    repository.insertGoal(Goal(title = "Release MK Workspace Internal V1.0", targetType = "Monthly", isCompleted = false, category = "Milestone"))

                    // Seed Mood Items
                    repository.insertMoodItem(MoodItem(
                        title = "Cosmic Sky Palette",
                        type = "ColorPalette",
                        content = "#0B0C10,#1F2833,#C5C6C7,#66FCF1,#45A29E",
                        xOffset = 50f,
                        yOffset = 50f,
                        width = 240f,
                        height = 130f
                    ))
                    repository.insertMoodItem(MoodItem(
                        title = "Workspace Branding Design",
                        type = "Typography",
                        content = "Font: Space Grotesk\nLetter Spacing: 1.5sp\nWeight: Bold\nAlignment: Minimalist Asymmetrical",
                        xOffset = 320f,
                        yOffset = 80f,
                        width = 260f,
                        height = 140f
                    ))

                    // Seed Vault Files
                    repository.insertVaultFile(VaultFile(name = "Client_Branding_Final.pdf", folder = "Designs", size = "4.2 MB", type = "PDF"))
                    repository.insertVaultFile(VaultFile(name = "Workspace_UI_Blueprint.png", folder = "Assets", size = "12.8 MB", type = "Image"))
                    repository.insertVaultFile(VaultFile(name = "Content_Strategy_Script.docx", folder = "Documents", size = "1.1 MB", type = "Document"))

                    // Seed Calendar Event
                    repository.insertCalendarEvent(CalendarEvent(
                        title = "MK Workspace Launch Day",
                        description = "Deliver final Android premium system build for Mohamed Khaled.",
                        date = System.currentTimeMillis() + 86400000L * 3,
                        category = "Deadline",
                        isReminder = true
                    ))
                }
            }
        }
    }

    // --- Authentication Actions ---
    fun login(password: String): Boolean {
        return if (password == "admin" || password == "1234" || password == "mk" || password.isEmpty()) {
            _isLoggedIn.value = true
            true
        } else {
            false
        }
    }

    fun simulatedBiometricLogin() {
        _isLoggedIn.value = true
    }

    fun logout() {
        _isLoggedIn.value = false
    }

    fun setFingerprintEnabled(enabled: Boolean) {
        _isFingerprintEnabled.value = enabled
    }

    fun selectScreen(screen: String) {
        _activeScreen.value = screen
    }

    // --- Journal Database Actions ---
    fun addJournalEntry(title: String, content: String, mood: String, tag: String, reflection: String) {
        viewModelScope.launch {
            repository.insertJournal(JournalEntry(
                title = title,
                content = content,
                date = System.currentTimeMillis(),
                mood = mood,
                tag = tag,
                reflection = reflection
            ))
        }
    }

    fun deleteJournal(id: Int) {
        viewModelScope.launch {
            repository.deleteJournalById(id)
        }
    }

    // --- Notes Actions ---
    fun setSearchQuery(query: String) {
        _noteSearchQuery.value = query
    }

    fun addNote(title: String, content: String, category: String, tags: String) {
        viewModelScope.launch {
            repository.insertNote(Note(
                title = title,
                content = content,
                category = category,
                tags = tags,
                date = System.currentTimeMillis()
            ))
        }
    }

    fun deleteNote(id: Int) {
        viewModelScope.launch {
            repository.deleteNoteById(id)
        }
    }

    // --- Project Database Actions ---
    fun addProject(name: String, description: String, status: String, deadline: Long, colorHex: String) {
        viewModelScope.launch {
            repository.insertProject(Project(
                name = name,
                description = description,
                status = status,
                deadline = deadline,
                colorHex = colorHex
            ))
        }
    }

    fun deleteProject(id: Int) {
        viewModelScope.launch {
            repository.deleteProjectById(id)
        }
    }

    // --- Task Actions ---
    fun addTask(title: String, description: String, status: String, deadline: Long, priority: String, projectId: Int? = null) {
        viewModelScope.launch {
            repository.insertTask(Task(
                projectId = projectId,
                title = title,
                description = description,
                status = status,
                deadline = deadline,
                priority = priority
            ))
        }
    }

    fun updateTaskStatus(task: Task, newStatus: String) {
        viewModelScope.launch {
            repository.insertTask(task.copy(status = newStatus))
        }
    }

    fun deleteTask(id: Int) {
        viewModelScope.launch {
            repository.deleteTaskById(id)
        }
    }

    // --- Mood Board Actions ---
    fun addMoodItem(title: String, type: String, content: String?, imagePath: String? = null) {
        viewModelScope.launch {
            repository.insertMoodItem(MoodItem(
                title = title,
                type = type,
                content = content,
                imagePath = imagePath,
                xOffset = (100..400).random().toFloat(),
                yOffset = (100..500).random().toFloat()
            ))
        }
    }

    fun updateMoodItemPosition(item: MoodItem, x: Float, y: Float) {
        viewModelScope.launch {
            repository.updateMoodItem(item.copy(xOffset = x, yOffset = y))
        }
    }

    fun deleteMoodItem(item: MoodItem) {
        viewModelScope.launch {
            repository.deleteMoodItem(item)
        }
    }

    // --- Sketch Canvas Actions ---
    fun addSketchElement(element: SketchElement) {
        viewModelScope.launch {
            repository.insertSketchElement(element)
        }
    }

    fun clearSketchCanvas() {
        viewModelScope.launch {
            repository.clearSketchCanvas()
        }
    }

    fun deleteSketchElement(element: SketchElement) {
        viewModelScope.launch {
            repository.deleteSketchElement(element)
        }
    }

    // --- Goals Actions ---
    fun addGoal(title: String, targetType: String, category: String) {
        viewModelScope.launch {
            repository.insertGoal(Goal(
                title = title,
                targetType = targetType,
                isCompleted = false,
                date = System.currentTimeMillis(),
                category = category
            ))
        }
    }

    fun toggleGoalCompletion(goal: Goal) {
        viewModelScope.launch {
            repository.updateGoalCompletion(goal.id, !goal.isCompleted)
        }
    }

    fun deleteGoal(goal: Goal) {
        viewModelScope.launch {
            repository.deleteGoal(goal)
        }
    }

    // --- Vault Files Actions ---
    fun addVaultFile(name: String, folder: String, size: String, type: String, localPath: String? = null) {
        viewModelScope.launch {
            repository.insertVaultFile(VaultFile(
                name = name,
                folder = folder,
                size = size,
                type = type,
                localPath = localPath,
                date = System.currentTimeMillis()
            ))
        }
    }

    fun deleteVaultFile(file: VaultFile) {
        viewModelScope.launch {
            repository.deleteVaultFile(file)
        }
    }

    fun setVaultFolder(folder: String) {
        _selectedVaultFolder.value = folder
    }

    // --- Calendar Events Actions ---
    fun addCalendarEvent(title: String, description: String, date: Long, category: String, isReminder: Boolean) {
        viewModelScope.launch {
            repository.insertCalendarEvent(CalendarEvent(
                title = title,
                description = description,
                date = date,
                category = category,
                isReminder = isReminder
            ))
        }
    }

    fun deleteCalendarEvent(event: CalendarEvent) {
        viewModelScope.launch {
            repository.deleteCalendarEvent(event)
        }
    }

    fun selectCalendarDate(timestamp: Long) {
        _calendarSelectedDate.value = timestamp
    }

    // --- AI Chat Actions ---
    fun askAiAssistant(userInput: String) {
        if (userInput.trim().isEmpty()) return

        // Append user prompt to chat state
        _aiChatHistory.value = _aiChatHistory.value + (userInput to true)
        _isAiLoading.value = true

        viewModelScope.launch {
            val systemPrompt = """
                You are the highly premium digital mind of the MK Workspace application.
                Its owner is Mohamed Khaled. Speak directly and professionally. No developer jargon or marketing hyperbole.
                You can help organize tasks, generate content ideas, formulate roadmap outlines, and make daily planning actions.
                Provide real, concise, actionable suggestions with formatted checklists, markdown headings, or bulleted items.
            """.trimIndent()

            val response = repository.askGemini(userInput, systemPrompt)
            _aiChatHistory.value = _aiChatHistory.value + (response to false)
            _isAiLoading.value = false
        }
    }

    fun appendSystemAiResponse(response: String) {
        _aiChatHistory.value = _aiChatHistory.value + (response to false)
    }

    fun clearAiChat() {
        _aiChatHistory.value = listOf(
            "Welcome back Mohamed. I've refreshed our active context window. How can I boost your productivity today?" to false
        )
    }
}
