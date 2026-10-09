package com.example.ui

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.db.AppDatabase
import com.example.data.model.Course
import com.example.data.model.CourseDeadline
import com.example.data.model.CourseNote
import com.example.data.model.CourseResource
import com.example.data.model.ScheduleLecture
import com.example.data.model.StudentProfile
import com.example.data.repository.UniversityRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed class ScreenDestination {
    data object Login : ScreenDestination()
    data object Home : ScreenDestination()
    data class CourseDetail(val courseId: Long = 0L, val initialTab: Int = 0) : ScreenDestination()
    data object Timetable : ScreenDestination()
    data object Profile : ScreenDestination()
    data object AllDeadlines : ScreenDestination()
}

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: UniversityRepository
    private val prefs = application.getSharedPreferences("jami_prefs", Context.MODE_PRIVATE)

    // Dark mode state
    private val _isDarkMode = MutableStateFlow(prefs.getBoolean("is_dark_mode", true))
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    fun toggleDarkMode() {
        val newVal = !_isDarkMode.value
        _isDarkMode.value = newVal
        prefs.edit().putBoolean("is_dark_mode", newVal).apply()
    }

    fun setDarkMode(dark: Boolean) {
        _isDarkMode.value = dark
        prefs.edit().putBoolean("is_dark_mode", dark).apply()
    }

    // Navigation Stack
    private val _currentScreen = MutableStateFlow<ScreenDestination>(ScreenDestination.Login)
    val currentScreen: StateFlow<ScreenDestination> = _currentScreen.asStateFlow()

    private val navBackStack = mutableListOf<ScreenDestination>()

    fun navigateTo(dest: ScreenDestination) {
        navBackStack.add(_currentScreen.value)
        _currentScreen.value = dest
    }

    fun navigateBack(): Boolean {
        if (navBackStack.isNotEmpty()) {
            _currentScreen.value = navBackStack.removeAt(navBackStack.lastIndex)
            return true
        } else if (_currentScreen.value !is ScreenDestination.Home) {
            _currentScreen.value = ScreenDestination.Home
            return true
        }
        return false
    }

    init {
        val db = AppDatabase.getDatabase(application)
        repository = UniversityRepository(db)
    }

    // Courses
    val courses: StateFlow<List<Course>> = repository.allCourses
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Active Selected Course for Detail View
    private val _selectedCourseId = MutableStateFlow<Long?>(null)
    val selectedCourseId: StateFlow<Long?> = _selectedCourseId.asStateFlow()

    fun selectCourse(courseId: Long, initialTab: Int = 0) {
        _selectedCourseId.value = courseId
        navigateTo(ScreenDestination.CourseDetail(courseId, initialTab))
    }

    val selectedCourse: StateFlow<Course?> = _selectedCourseId.flatMapLatest { id ->
        if (id != null) repository.getCourseById(id) else flowOf(null)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val selectedCourseResources: StateFlow<List<CourseResource>> = _selectedCourseId.flatMapLatest { id ->
        if (id != null) repository.getResourcesForCourse(id) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val selectedCourseNotes: StateFlow<List<CourseNote>> = _selectedCourseId.flatMapLatest { id ->
        if (id != null) repository.getNotesForCourse(id) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val selectedCourseDeadlines: StateFlow<List<CourseDeadline>> = _selectedCourseId.flatMapLatest { id ->
        if (id != null) repository.getDeadlinesForCourse(id) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // All Deadlines
    val allDeadlines: StateFlow<List<CourseDeadline>> = repository.allDeadlines
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Student Profile
    val studentProfile: StateFlow<StudentProfile?> = repository.studentProfile
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Weekly Timetable
    val allLectures: StateFlow<List<ScheduleLecture>> = repository.allLectures
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Course actions
    fun addCourse(
        name: String,
        code: String,
        instructor: String,
        location: String,
        creditHours: Int,
        colorHex: String,
        iconName: String
    ) {
        viewModelScope.launch {
            repository.insertCourse(
                Course(
                    name = name.trim(),
                    code = code.trim().uppercase(),
                    instructor = instructor.trim(),
                    location = location.trim(),
                    creditHours = creditHours,
                    colorHex = colorHex,
                    iconName = iconName
                )
            )
        }
    }

    fun updateCourse(course: Course) {
        viewModelScope.launch {
            repository.updateCourse(course)
        }
    }

    fun deleteCourse(course: Course) {
        viewModelScope.launch {
            repository.deleteCourse(course)
            if (_selectedCourseId.value == course.id) {
                navigateBack()
            }
        }
    }

    // Resource actions
    fun addResource(
        courseId: Long,
        title: String,
        type: String,
        urlOrPath: String,
        description: String
    ) {
        viewModelScope.launch {
            repository.insertResource(
                CourseResource(
                    courseId = courseId,
                    title = title.trim(),
                    type = type,
                    urlOrPath = urlOrPath.trim(),
                    description = description.trim()
                )
            )
        }
    }

    fun deleteResource(resource: CourseResource) {
        viewModelScope.launch {
            repository.deleteResource(resource)
        }
    }

    // Note actions
    fun addNote(
        courseId: Long,
        title: String,
        content: String,
        drawingData: String? = null
    ) {
        viewModelScope.launch {
            repository.insertNote(
                CourseNote(
                    courseId = courseId,
                    title = title.trim(),
                    content = content.trim(),
                    drawingData = drawingData,
                    updatedAt = System.currentTimeMillis()
                )
            )
        }
    }

    fun updateNote(note: CourseNote) {
        viewModelScope.launch {
            repository.updateNote(note.copy(updatedAt = System.currentTimeMillis()))
        }
    }

    fun deleteNote(note: CourseNote) {
        viewModelScope.launch {
            repository.deleteNote(note)
        }
    }

    // Deadline actions
    fun addDeadline(
        courseId: Long,
        title: String,
        type: String,
        dueDate: Long,
        priority: String,
        notes: String
    ) {
        viewModelScope.launch {
            repository.insertDeadline(
                CourseDeadline(
                    courseId = courseId,
                    title = title.trim(),
                    type = type,
                    dueDate = dueDate,
                    priority = priority,
                    notes = notes.trim(),
                    isCompleted = false
                )
            )
        }
    }

    fun toggleDeadline(id: Long, isCompleted: Boolean) {
        viewModelScope.launch {
            repository.toggleDeadlineCompletion(id, isCompleted)
        }
    }

    fun deleteDeadline(deadline: CourseDeadline) {
        viewModelScope.launch {
            repository.deleteDeadline(deadline)
        }
    }

    // Profile actions
    fun updateProfile(profile: StudentProfile) {
        viewModelScope.launch {
            repository.updateProfile(profile)
        }
    }

    // Lecture actions
    fun addLecture(lecture: ScheduleLecture) {
        viewModelScope.launch {
            repository.insertLecture(lecture)
        }
    }

    fun deleteLecture(lecture: ScheduleLecture) {
        viewModelScope.launch {
            repository.deleteLecture(lecture)
        }
    }}