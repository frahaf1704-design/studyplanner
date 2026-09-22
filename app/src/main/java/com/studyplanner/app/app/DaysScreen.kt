package com.studyplanner.app

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.Alignment
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.background
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.text.style.TextDecoration
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import androidx.compose.runtime.livedata.observeAsState
import androidx.lifecycle.MutableLiveData
import androidx.compose.ui.platform.LocalContext
import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

// ViewModel برای مدیریت داده‌ها
class StudyViewModel(context: Context) : ViewModel() {
    private val dao = AppDatabase.getInstance(context).studyDao()
    val days = MutableLiveData<List<Day>>(emptyList())
    val lessons = MutableLiveData<List<Lesson>>(emptyList())

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            days.value = dao.getAllDays()
            lessons.value = dao.getAllLessons()
        }
    }

    fun addDay(name: String) {
        viewModelScope.launch {
            dao.insertDay(Day(name = name))
            loadData()
        }
    }

    fun addLesson(dayId: Long, name: String, color: Long, minutes: Int, start: String, end: String) {
        viewModelScope.launch {
            dao.insertLesson(Lesson(dayId = dayId, name = name, color = color, plannedMinutes = minutes, startTime = start, endTime = end))
            loadData()
        }
    }

    fun toggleDone(lesson: Lesson) {
        viewModelScope.launch {
            dao.updateDone(lesson.id, !lesson.done)
            loadData()
        }
    }

    fun toggleReview(lesson: Lesson) {
        viewModelScope.launch {
            dao.updateNeedsReview(lesson.id, !lesson.needsReview)
            loadData()
        }
    }

    fun updateNote(lesson: Lesson, note: String) {
        viewModelScope.launch {
            dao.updateNote(lesson.id, note)
            loadData()
        }
    }

    fun updateActualMinutes(lesson: Lesson, minutes: Int) {
        viewModelScope.launch {
            dao.updateActualMinutes(lesson.id, minutes)
            loadData()
        }
    }

    fun deleteLesson(lesson: Lesson) {
        viewModelScope.launch {
            dao.deleteLesson(lesson.id)
            loadData()
        }
    }
}

// Factory برای ViewModel
class StudyViewModelFactory(private val context: Context) : androidx.lifecycle.ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return StudyViewModel(context) as T
    }
}

// رنگ‌های هر درس
val lessonColors = listOf(
    Color(0xFF4CAF50), Color(0xFF2196F3), Color(0xFFFF9800),
    Color(0xFF9C27B0), Color(0xFFF44336), Color(0xFF009688),
    Color(0xFF3F51B5), Color(0xFFFF5722)
)

@Composable
fun DaysScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val viewModel: StudyViewModel = viewModel(factory = StudyViewModelFactory(context))
    val days by viewModel.days.observeAsState(emptyList())
    val lessons by viewModel.lessons.observeAsState(emptyList())
    var showAddDay by remember { mutableStateOf(false) }
    var selectedDay by remember { mutableStateOf<Day?>(null) }

    if (showAddDay) {
        AddDayDialog(viewModel) { showAddDay = false }
    }

    if (selectedDay != null) {
        DayDetailScreen(
            day = selectedDay!!,
