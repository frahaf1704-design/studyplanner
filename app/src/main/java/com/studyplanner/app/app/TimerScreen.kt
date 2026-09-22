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
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope
import java.util.concurrent.TimeUnit
import android.widget.Toast

@Composable
fun DayDetailScreen(
    day: Day,
    lessons: List<Lesson>,
    viewModel: StudyViewModel,
    onBack: () -> Unit
) {
    var showAddLesson by remember { mutableStateOf(false) }
    var selectedLesson by remember { mutableStateOf<Lesson?>(null) }
    var showTimer by remember { mutableStateOf<Lesson?>(null) }

    if (showAddLesson) {
        AddLessonDialog(viewModel, day.id) { showAddLesson = false }
    }

    if (selectedLesson != null) {
        LessonDetailDialog(
            lesson = selectedLesson!!,
            viewModel = viewModel,
            onClose = { selectedLesson = null },
            onStartTimer = {
                showTimer = selectedLesson
                selectedLesson = null
            }
        )
    }

    if (showTimer != null) {
        TimerScreen(
            lesson = showTimer!!,
            viewModel = viewModel,
            onClose = { showTimer = null }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(day.name, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "بازگشت")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF4CAF50),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddLesson = true }) {
                Icon(Icons.Default.Add, contentDescription = "افزودن درس")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)
        ) {
            items(lessons) { lesson ->
                LessonCard(
                    lesson = lesson,
                    onClick = { selectedLesson = lesson },
                    onToggleDone = { viewModel.toggleDone(lesson) }
                )
            }
        }
    }
}

@Composable
fun LessonCard(
    lesson: Lesson,
    onClick: () -> Unit,
    onToggleDone: () -> Unit
) {
    val color = lessonColors[(lesson.id % lessonColors.size).toInt()]
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp).clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // دکمه تیک
            Checkbox(
                checked = lesson.done,
                onCheckedChange = { onToggleDone() },
                colors = CheckboxDefaults.colors(checkedColor = color)
            )
            // نوار رنگی درس
            Box(
                Modifier.width(4.dp).height(50.dp).clip(RoundedCornerShape(4.dp)).background(color)
            )
            Spacer(Modifier.width(12.dp))
