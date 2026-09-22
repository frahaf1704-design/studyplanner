package com.studyplanner.app

import android.content.Context
import androidx.room.*

// جدول روزها
@Entity(tableName = "days")
data class Day(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,  // "روز اول", "روز دوم", ...
    val date: Long = System.currentTimeMillis()
)

// جدول درس‌ها
@Entity(tableName = "lessons")
data class Lesson(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val dayId: Long,
    val name: String,
    val color: Long,  // رنگ درس
    val plannedMinutes: Int,  // زمان برنامه‌ریزی‌شده
    val actualMinutes: Int = 0,  // زمان واقعی مطالعه
    val done: Boolean = false,  // تیک انجام
    val needsReview: Boolean = false,  // نیاز به مرور
    val note: String = "",  // یادداشت
    val startTime: String = "",  // ساعت شروع
    val endTime: String = ""  // ساعت پایان
)

// جدول تست‌ها
@Entity(tableName = "tests")
data class Test(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val lessonId: Long,
    val score: Int = 0,
    val percent: Int = 0,
    val note: String = "",
    val date: Long = System.currentTimeMillis()
)

// جدول جلسات مطالعه (برای تاریخچه)
@Entity(tableName = "study_sessions")
data class StudySession(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val lessonId: Long,
    val startTime: Long,
    val endTime: Long,
    val durationMinutes: Int
)

// DAO — دسترسی به دیتابیس
@Dao
interface StudyDao {
    @Insert suspend fun insertDay(day: Day): Long
    @Insert suspend fun insertLesson(lesson: Lesson): Long
    @Insert suspend fun insertTest(test: Test)
    @Insert suspend fun insertSession(session: StudySession)

    @Query("SELECT * FROM days ORDER BY id")
    suspend fun getAllDays(): List<Day>

    @Query("SELECT * FROM lessons WHERE dayId = :dayId ORDER BY id")
    suspend fun getLessonsForDay(dayId: Long): List<Lesson>

    @Query("SELECT * FROM lessons ORDER BY id")
    suspend fun getAllLessons(): List<Lesson>

    @Query("SELECT * FROM tests WHERE lessonId = :lessonId ORDER BY date DESC")
    suspend fun getTestsForLesson(lessonId: Long): List<Test>

    @Query("SELECT * FROM study_sessions ORDER BY startTime DESC")
    suspend fun getAllSessions(): List<StudySession>

    @Query("UPDATE lessons SET done = :done WHERE id = :id")
    suspend fun updateDone(id: Long, done: Boolean)

    @Query("UPDATE lessons SET actualMinutes = :minutes WHERE id = :id")
    suspend fun updateActualMinutes(id: Long, minutes: Int)

    @Query("UPDATE lessons SET needsReview = :review WHERE id = :id")
    suspend fun updateNeedsReview(id: Long, review: Boolean)

    @Query("UPDATE lessons SET note = :note WHERE id = :id")
    suspend fun updateNote(id: Long, note: String)

    @Query("UPDATE lessons SET plannedMinutes = :minutes, startTime = :start, endTime = :end WHERE id = :id")
    suspend fun updateSchedule(id: Long, minutes: Int, start: String, end: String)

    @Query("DELETE FROM lessons WHERE id = :id")
    suspend fun deleteLesson(id: Long)
}

@Database(entities = [Day::class, Lesson::class, Test::class, StudySession::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun studyDao(): StudyDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null
        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "study_db"
                ).build().also { INSTANCE = it }
            }
        }
    }
}
