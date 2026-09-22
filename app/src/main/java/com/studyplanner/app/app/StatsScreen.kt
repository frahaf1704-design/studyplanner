Column(Modifier.weight(1f)) {
                Text(
                    lesson.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    textDecoration = if (lesson.done) TextDecoration.LineThrough else null,
                    color = if (lesson.done) Color.Gray else Color.Black
                )
                Text(
                    "${lesson.startTime} - ${lesson.endTime}  |  ${lesson.plannedMinutes} دقیقه",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
                if (lesson.actualMinutes > 0) {
                    Text(
                        "مطالعه‌شده: ${lesson.actualMinutes} دقیقه",
                        fontSize = 12.sp,
                        color = color
                    )
                }
                if (lesson.needsReview) {
                    Text(
                        "⚠️ نیاز به مرور",
                        fontSize = 12.sp,
                        color = Color(0xFFFF9800)
                    )
                }
            }
            if (lesson.done) {
                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = color)
            }
        }
    }
}

@Composable
fun AddLessonDialog(viewModel: StudyViewModel, dayId: Long, onClose: () -> Unit) {
    var name by remember { mutableStateOf("") }
    var minutes by remember { mutableStateOf("") }
    var start by remember { mutableStateOf("") }
    var end by remember { mutableStateOf("") }
    var colorIndex by remember { mutableStateOf(0) }

    AlertDialog(
        onDismissRequest = onClose,
        title = { Text("درس جدید") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("نام درس") }
                )
                OutlinedTextField(
                    value = minutes,
                    onValueChange = { minutes = it },
                    label = { Text("مدت برنامه‌ریزی (دقیقه)") },
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number)
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = start,
                        onValueChange = { start = it },
                        label = { Text("شروع (مثل 08:00)") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = end,
                        onValueChange = { end = it },
                        label = { Text("پایان (مثل 09:30)") },
                        modifier = Modifier.weight(1f)
                    )
                }
                Text("انتخاب رنگ:", fontSize = 14.sp)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    lessonColors.forEachIndexed { index, color ->
                        Box(
                            Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(color)
                                .clickable { colorIndex = index }
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                if (name.isNotBlank() && minutes.isNotBlank()) {
                    viewModel.addLesson(
                        dayId = dayId,
                        name = name,
                        color = lessonColors[colorIndex].value.toLong(),
                        minutes = minutes.toIntOrNull() ?: 0,
                        start = start,
                        end = end
                    )
                    onClose()
                }
            }) { Text("افزودن") }
