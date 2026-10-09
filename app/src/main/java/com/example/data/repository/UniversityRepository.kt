package com.example.data.repository

import com.example.data.db.AppDatabase
import com.example.data.model.Course
import com.example.data.model.CourseDeadline
import com.example.data.model.CourseNote
import com.example.data.model.CourseResource
import com.example.data.model.ScheduleLecture
import com.example.data.model.StudentProfile
import kotlinx.coroutines.flow.Flow

class UniversityRepository(private val db: AppDatabase) {

    // Courses
    val allCourses: Flow<List<Course>> = db.courseDao().getAllCourses()

    fun getCourseById(id: Long): Flow<Course?> = db.courseDao().getCourseById(id)

    suspend fun insertCourse(course: Course): Long = db.courseDao().insertCourse(course)

    suspend fun updateCourse(course: Course) = db.courseDao().updateCourse(course)

    suspend fun deleteCourse(course: Course) {
        db.resourceDao().deleteResourcesForCourse(course.id)
        db.noteDao().deleteNotesForCourse(course.id)
        db.deadlineDao().deleteDeadlinesForCourse(course.id)
        db.courseDao().deleteCourse(course)
    }

    // Resources
    fun getResourcesForCourse(courseId: Long): Flow<List<CourseResource>> =
        db.resourceDao().getResourcesForCourse(courseId)

    suspend fun insertResource(resource: CourseResource): Long =
        db.resourceDao().insertResource(resource)

    suspend fun updateResource(resource: CourseResource) =
        db.resourceDao().updateResource(resource)

    suspend fun deleteResource(resource: CourseResource) =
        db.resourceDao().deleteResource(resource)

    // Notes
    fun getNotesForCourse(courseId: Long): Flow<List<CourseNote>> =
        db.noteDao().getNotesForCourse(courseId)

    fun getNoteById(id: Long): Flow<CourseNote?> =
        db.noteDao().getNoteById(id)

    suspend fun insertNote(note: CourseNote): Long =
        db.noteDao().insertNote(note)

    suspend fun updateNote(note: CourseNote) =
        db.noteDao().updateNote(note)

    suspend fun deleteNote(note: CourseNote) =
        db.noteDao().deleteNote(note)

    // Deadlines
    fun getDeadlinesForCourse(courseId: Long): Flow<List<CourseDeadline>> =
        db.deadlineDao().getDeadlinesForCourse(courseId)

    val allDeadlines: Flow<List<CourseDeadline>> =
        db.deadlineDao().getAllDeadlines()

    suspend fun insertDeadline(deadline: CourseDeadline): Long =
        db.deadlineDao().insertDeadline(deadline)

    suspend fun updateDeadline(deadline: CourseDeadline) =
        db.deadlineDao().updateDeadline(deadline)

    suspend fun deleteDeadline(deadline: CourseDeadline) =
        db.deadlineDao().deleteDeadline(deadline)

    suspend fun toggleDeadlineCompletion(id: Long, completed: Boolean) =
        db.deadlineDao().updateCompletionStatus(id, completed)

    // Profile
    val studentProfile: Flow<StudentProfile?> =
        db.profileDao().getProfile()

    suspend fun updateProfile(profile: StudentProfile) =
        db.profileDao().insertOrUpdateProfile(profile)

    // Schedule
    val allLectures: Flow<List<ScheduleLecture>> =
        db.scheduleDao().getAllLectures()

    fun getLecturesForDay(dayOfWeek: Int): Flow<List<ScheduleLecture>> =
        db.scheduleDao().getLecturesForDay(dayOfWeek)

    suspend fun insertLecture(lecture: ScheduleLecture): Long =
        db.scheduleDao().insertLecture(lecture)

    suspend fun updateLecture(lecture: ScheduleLecture) =
        db.scheduleDao().updateLecture(lecture)

    suspend fun deleteLecture(lecture: ScheduleLecture) =
        db.scheduleDao().deleteLecture(lecture)
}
