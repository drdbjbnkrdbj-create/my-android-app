package com.example.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.Course
import com.example.data.model.CourseDeadline
import com.example.data.model.CourseNote
import com.example.data.model.CourseResource
import com.example.data.model.ScheduleLecture
import com.example.data.model.StudentProfile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        Course::class,
        CourseResource::class,
        CourseNote::class,
        CourseDeadline::class,
        StudentProfile::class,
        ScheduleLecture::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun courseDao(): CourseDao
    abstract fun resourceDao(): ResourceDao
    abstract fun noteDao(): NoteDao
    abstract fun deadlineDao(): DeadlineDao
    abstract fun profileDao(): ProfileDao
    abstract fun scheduleDao(): ScheduleDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "jami_university_database"
                )
                    .addCallback(DatabaseCallback())
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        populateInitialData(database)
                    }
                }
            }

            private suspend fun populateInitialData(database: AppDatabase) {
                // Populate Default Student Profile
                database.profileDao().insertOrUpdateProfile(
                    StudentProfile(
                        id = 1,
                        fullName = "عبدالرحمن الشمري",
                        universityName = "جامعة الملك سعود",
                        collegeName = "كلية علوم الحاسب والمعلومات",
                        majorName = "هندسة البرمجيات",
                        studentIdNumber = "442109823",
                        academicLevel = "السنة الثالثة - الفصل الأول",
                        currentGpa = 4.82,
                        targetGpa = 4.95,
                        maxGpaScale = 5.0,
                        advisorName = "د. سلطان القحطاني",
                        academicEmail = "a.alshammary@student.ksu.edu.sa"
                    )
                )

                // Populate Default Courses
                val c1Id = database.courseDao().insertCourse(
                    Course(
                        name = "هندسة البرمجيات المتقدمة",
                        code = "SWE 312",
                        instructor = "د. فهد الدوسري",
                        location = "مبنى 31 - قاعة 204",
                        creditHours = 3,
                        colorHex = "#0284C7",
                        iconName = "code"
                    )
                )

                val c2Id = database.courseDao().insertCourse(
                    Course(
                        name = "مقدمة في الذكاء الاصطناعي",
                        code = "AI 301",
                        instructor = "د. نورة السالم",
                        location = "مبنى 31 - مدرج 1",
                        creditHours = 4,
                        colorHex = "#4F46E5",
                        iconName = "psychology"
                    )
                )

                val c3Id = database.courseDao().insertCourse(
                    Course(
                        name = "قواعد البيانات الحديثة",
                        code = "CS 340",
                        instructor = "د. خالد المنصور",
                        location = "معمل الحاسب 4",
                        creditHours = 3,
                        colorHex = "#0D9488",
                        iconName = "database"
                    )
                )

                // Populate Resources for SWE 312
                database.resourceDao().insertResource(
                    CourseResource(
                        courseId = c1Id,
                        title = "سلايدات المحاضرة الأولى: أنماط التصميم المعماري",
                        type = "SLIDES",
                        urlOrPath = "https://lms.university.edu/swe312/lecture1.pdf",
                        description = "تغطي معمارية Microservices والأنماط المعمارية الشائعة"
                    )
                )
                database.resourceDao().insertResource(
                    CourseResource(
                        courseId = c1Id,
                        title = "رابط مستودع مشروع الفصل على GitHub",
                        type = "LINK",
                        urlOrPath = "https://github.com/swe312-team-project",
                        description = "المستودع الرسمي للمشروع المشترك مع الفريق"
                    )
                )
                database.resourceDao().insertResource(
                    CourseResource(
                        courseId = c1Id,
                        title = "كتاب Clean Architecture - مرجع المادة الأساسي",
                        type = "BOOK",
                        urlOrPath = "https://openlibrary.org/clean-architecture",
                        description = "الفصول 3 و 4 و 7 مطلوبة للاختبار النصفي"
                    )
                )

                // Populate Notes for SWE 312 (including sample drawing points JSON!)
                database.noteDao().insertNote(
                    CourseNote(
                        courseId = c1Id,
                        title = "ملاحظات وتخطيط هيكل المشروع (Architecture Diagram)",
                        content = "تم الاتفاق على تقسيم النظام إلى Controller و Service و Repository مع عزل الـ Domain Models.\n\nتنبيه: يجب مراعاة مبادئ SOLID خصوصاً الـ Dependency Inversion.",
                        drawingData = """[{"color":"#0284C7","strokeWidth":6.0,"points":[{"x":60.0,"y":80.0},{"x":140.0,"y":80.0},{"x":140.0,"y":130.0},{"x":60.0,"y":130.0},{"x":60.0,"y":80.0}]},{"color":"#4F46E5","strokeWidth":4.0,"points":[{"x":100.0,"y":130.0},{"x":100.0,"y":180.0}]},{"color":"#0D9488","strokeWidth":6.0,"points":[{"x":50.0,"y":180.0},{"x":150.0,"y":180.0},{"x":150.0,"y":230.0},{"x":50.0,"y":230.0},{"x":50.0,"y":180.0}]}]""",
                        updatedAt = System.currentTimeMillis() - 86400000L
                    )
                )

                // Populate Deadlines for SWE 312 & AI
                val now = System.currentTimeMillis()
                database.deadlineDao().insertDeadline(
                    CourseDeadline(
                        courseId = c1Id,
                        title = "تسليم متطلبات المشروع (SRS Document)",
                        type = "PROJECT",
                        dueDate = now + (2L * 86400000L),
                        priority = "HIGH",
                        notes = "تسليم ملف PDF يشمل الـ Use Case Diagrams",
                        isCompleted = false
                    )
                )
                database.deadlineDao().insertDeadline(
                    CourseDeadline(
                        courseId = c1Id,
                        title = "كويز قصير في مبادئ الـ Refactoring",
                        type = "QUIZ",
                        dueDate = now + (5L * 86400000L),
                        priority = "MEDIUM",
                        notes = "يشمل المحاضرات من 1 إلى 3",
                        isCompleted = false
                    )
                )
                database.deadlineDao().insertDeadline(
                    CourseDeadline(
                        courseId = c2Id,
                        title = "واجب خورازميات البحث A* Search",
                        type = "ASSIGNMENT",
                        dueDate = now + (3L * 86400000L),
                        priority = "HIGH",
                        notes = "كود Python وحساب التعقيد الزمني",
                        isCompleted = false
                    )
                )

                // Populate Weekly Schedule (Sunday to Thursday)
                // Sunday (1)
                database.scheduleDao().insertLecture(
                    ScheduleLecture(
                        courseId = c1Id,
                        courseName = "هندسة البرمجيات المتقدمة",
                        dayOfWeek = 1,
                        startTime = "08:00",
                        endTime = "09:30",
                        room = "مبنى 31 - قاعة 204",
                        type = "LECTURE",
                        colorHex = "#0284C7"
                    )
                )
                database.scheduleDao().insertLecture(
                    ScheduleLecture(
                        courseId = c2Id,
                        courseName = "مقدمة في الذكاء الاصطناعي",
                        dayOfWeek = 1,
                        startTime = "10:00",
                        endTime = "11:30",
                        room = "مبنى 31 - مدرج 1",
                        type = "LECTURE",
                        colorHex = "#4F46E5"
                    )
                )
                // Monday (2)
                database.scheduleDao().insertLecture(
                    ScheduleLecture(
                        courseId = c3Id,
                        courseName = "قواعد البيانات الحديثة",
                        dayOfWeek = 2,
                        startTime = "09:00",
                        endTime = "11:00",
                        room = "معمل الحاسب 4",
                        type = "LAB",
                        colorHex = "#0D9488"
                    )
                )
                // Tuesday (3)
                database.scheduleDao().insertLecture(
                    ScheduleLecture(
                        courseId = c1Id,
                        courseName = "هندسة البرمجيات المتقدمة",
                        dayOfWeek = 3,
                        startTime = "08:00",
                        endTime = "09:30",
                        room = "مبنى 31 - قاعة 204",
                        type = "LECTURE",
                        colorHex = "#0284C7"
                    )
                )
                database.scheduleDao().insertLecture(
                    ScheduleLecture(
                        courseId = c2Id,
                        courseName = "مقدمة في الذكاء الاصطناعي",
                        dayOfWeek = 3,
                        startTime = "10:00",
                        endTime = "11:30",
                        room = "مبنى 31 - مدرج 1",
                        type = "LECTURE",
                        colorHex = "#4F46E5"
                    )
                )
                // Wednesday (4)
                database.scheduleDao().insertLecture(
                    ScheduleLecture(
                        courseId = c3Id,
                        courseName = "قواعد البيانات الحديثة",
                        dayOfWeek = 4,
                        startTime = "11:00",
                        endTime = "12:30",
                        room = "مبنى 31 - قاعة 105",
                        type = "LECTURE",
                        colorHex = "#0D9488"
                    )
                )
            }
        }
    }
}
