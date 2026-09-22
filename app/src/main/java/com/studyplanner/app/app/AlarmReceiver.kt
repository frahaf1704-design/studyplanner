},
        dismissButton = {
            TextButton(onClick = onClose) { Text("انصراف") }
        }
    )
}

@Composable
fun LessonDetailDialog(
    lesson: Lesson,
    viewModel: StudyViewModel,
    onClose: () -> Unit,
    onStartTimer: () -> Unit
) {
    var note by remember { mutableStateOf(lesson.note) }
    var showDelete by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onClose,
        title = { Text(lesson.name, fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("⏱ برنامه: ${lesson.startTime} - ${lesson.endTime}")
                Text("📚 مدت برنامه‌ریزی: ${lesson.plannedMinutes} دقیقه")
                Text("✅ مطالعه‌شده: ${lesson.actualMinutes} دقیقه")

                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text("یادداشت") },
                    modifier = Modifier.fillMaxWidth()
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = {
                            viewModel.updateNote(lesson, note)
                            onClose()
                        },
                        modifier = Modifier.weight(1f)
                    ) { Text("ذخیره یادداشت") }
                    Button(
                        onClick = { viewModel.toggleReview(lesson) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (lesson.needsReview) Color(0xFFFF9800) else Color.Gray
                        )
                    ) {
                        Text(if (lesson.needsReview) "مرور لازم" else "نیاز به مرور")
                    }
                }

                Button(
                    onClick = onStartTimer,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("شروع تایمر مطالعه")
                }

                TextButton(
                    onClick = { showDelete = true },
                    colors = ButtonDefaults.textButtonColors(contentColor = Color.Red)
                ) {
                    Text("حذف درس")
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onClose) { Text("بستن") }
        }
    )

    if (showDelete) {
        AlertDialog(
            onDismissRequest = { showDelete = false },
            title = { Text("حذف درس") },
            text = { Text("مطمئنی می‌خوای این درس رو حذف کنی؟") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteLesson(lesson)
                    showDelete = false
                    onClose()
                }) { Text("حذف", color = Color.Red) }
            },
            dismissButton = {
                TextButton(onClick = { showDelete = false }) { Text("انصراف") }
            }
        )
    }
}
