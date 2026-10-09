package com.example.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.Course
import com.example.data.model.CourseDeadline
import com.example.data.model.CourseNote
import com.example.data.model.CourseResource
import com.example.data.model.ScheduleLecture
import com.example.data.model.StudentProfile
import kotlinx.coroutines.flow.Flow

@Dao
interface CourseDao {
    @Query("SELECT * FROM courses ORDER BY createdAt DESC")
    fun getAllCourses(): Flow<List<Course>>

    @Query("SELECT * FROM courses WHERE id = :id LIMIT 1")
    fun getCourseById(id: Long): Flow<Course?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCourse(course: Course): Long

    @Update
    suspend fun updateCourse(course: Course)

    @Delete
    suspend fun deleteCourse(course: Course)

    @Query("DELETE FROM courses WHERE id = :id")
    suspend fun deleteCourseById(id: Long)
}

@Dao
interface ResourceDao {
    @Query("SELECT * FROM course_resources WHERE courseId = :courseId ORDER BY createdAt DESC")
    fun getResourcesForCourse(courseId: Long): Flow<List<CourseResource>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertResource(resource: CourseResource): Long

    @Update
    suspend fun updateResource(resource: CourseResource)

    @Delete
    suspend fun deleteResource(resource: CourseResource)

    @Query("DELETE FROM course_resources WHERE courseId = :courseId")
    suspend fun deleteResourcesForCourse(courseId: Long)
}

@Dao
interface NoteDao {
    @Query("SELECT * FROM course_notes WHERE courseId = :courseId ORDER BY updatedAt DESC")
    fun getNotesForCourse(courseId: Long): Flow<List<CourseNote>>

    @Query("SELECT * FROM course_notes WHERE id = :id LIMIT 1")
    fun getNoteById(id: Long): Flow<CourseNote?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: CourseNote): Long

    @Update
    suspend fun updateNote(note: CourseNote)

    @Delete
    suspend fun deleteNote(note: CourseNote)

    @Query("DELETE FROM course_notes WHERE courseId = :courseId")
    suspend fun deleteNotesForCourse(courseId: Long)
}

@Dao
interface DeadlineDao {
    @Query("SELECT * FROM course_deadlines WHERE courseId = :courseId ORDER BY isCompleted ASC, dueDate ASC")
    fun getDeadlinesForCourse(courseId: Long): Flow<List<CourseDeadline>>

    @Query("SELECT * FROM course_deadlines ORDER BY isCompleted ASC, dueDate ASC")
    fun getAllDeadlines(): Flow<List<CourseDeadline>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDeadline(deadline: CourseDeadline): Long

    @Update
    suspend fun updateDeadline(deadline: CourseDeadline)

    @Delete
    suspend fun deleteDeadline(deadline: CourseDeadline)

    @Query("UPDATE course_deadlines SET isCompleted = :completed WHERE id = :id")
    suspend fun updateCompletionStatus(id: Long, completed: Boolean)

    @Query("DELETE FROM course_deadlines WHERE courseId = :courseId")
    suspend fun deleteDeadlinesForCourse(courseId: Long)
}

@Dao
interface ProfileDao {
    @Query("SELECT * FROM student_profile WHERE id = 1 LIMIT 1")
    fun getProfile(): Flow<StudentProfile?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateProfile(profile: StudentProfile)
}

@Dao
interface ScheduleDao {
    @Query("SELECT * FROM schedule_lectures ORDER BY dayOfWeek ASC, startTime ASC")
    fun getAllLectures(): Flow<List<ScheduleLecture>>

    @Query("SELECT * FROM schedule_lectures WHERE dayOfWeek = :dayOfWeek ORDER BY startTime ASC")
    fun getLecturesForDay(dayOfWeek: Int): Flow<List<ScheduleLecture>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLecture(lecture: ScheduleLecture): Long

    @Update
    suspend fun updateLecture(lecture: ScheduleLecture)

    @Delete
    suspend fun deleteLecture(lecture: ScheduleLecture)

    @Query("DELETE FROM schedule_lectures WHERE id = :id")
    suspend fun deleteLectureById(id: Long)
}
