package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "courses")
data class Course(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val code: String,
    val instructor: String = "",
    val location: String = "",
    val creditHours: Int = 3,
    val colorHex: String = "#0284C7",
    val iconName: String = "book",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "course_resources")
data class CourseResource(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val courseId: Long,
    val title: String,
    val type: String, // SLIDES, LINK, BOOK, REFERENCE, SUMMARY, FILE
    val urlOrPath: String,
    val description: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "course_notes")
data class CourseNote(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val courseId: Long,
    val title: String,
    val content: String,
    val drawingData: String? = null, // JSON representation of sketch paths
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "course_deadlines")
data class CourseDeadline(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val courseId: Long,
    val title: String,
    val type: String, // PROJECT, ASSIGNMENT, QUIZ, EXAM, REMINDER
    val dueDate: Long,
    val priority: String = "MEDIUM", // HIGH, MEDIUM, LOW
    val notes: String = "",
    val isCompleted: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "student_profile")
data class StudentProfile(
    @PrimaryKey
    val id: Int = 1,
    val fullName: String = "عبدالرحمن الشمري",
    val universityName: String = "جامعة الملك سعود",
    val collegeName: String = "كلية علوم الحاسب والمعلومات",
    val majorName: String = "هندسة البرمجيات",
    val studentIdNumber: String = "441203984",
    val academicLevel: String = "السنة الثالثة - المستوى السادس",
    val currentGpa: Double = 4.75,
    val targetGpa: Double = 4.90,
    val maxGpaScale: Double = 5.0,
    val advisorName: String = "د. سلطان القحطاني",
    val academicEmail: String = "s441203984@student.ksu.edu.sa"
)

@Entity(tableName = "schedule_lectures")
data class ScheduleLecture(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val courseId: Long? = null,
    val courseName: String,
    val dayOfWeek: Int, // 1 = Sunday (الأحد), 2 = Monday (الإثنين), 3 = Tuesday (الثلاثاء), 4 = Wednesday (الأربعاء), 5 = Thursday (الخميس)
    val startTime: String, // "08:00"
    val endTime: String, // "09:30"
    val room: String, // "قاعة 204"
    val type: String = "LECTURE", // LECTURE, LAB, TUTORIAL
    val colorHex: String = "#0284C7"
)
