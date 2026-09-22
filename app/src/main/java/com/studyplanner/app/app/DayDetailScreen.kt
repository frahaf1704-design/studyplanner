lessons = lessons.filter { it.dayId == selectedDay!!.id },
            viewModel = viewModel,
            onBack = { selectedDay = null }
        )
    } else {
        LazyColumn(modifier = modifier.fillMaxSize().padding(16.dp)) {
            item {
                Button(
                    onClick = { showAddDay = true },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("افزودن روز جدید")
                }
            }
            items(days) { day ->
                val dayLessons = lessons.filter { it.dayId == day.id }
                val doneCount = dayLessons.count { it.done }
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp).clickable { selectedDay = day },
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(Modifier.weight(1f)) {
                            Text(day.name, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                            Text(
                                "تکمیل‌شده: $doneCount از ${dayLessons.size}",
                                fontSize = 13.sp,
                                color = Color.Gray
                            )
                        }
                        Icon(Icons.Default.ChevronRight, contentDescription = null)
                    }
                }
            }
        }
    }
}

@Composable
fun AddDayDialog(viewModel: StudyViewModel, onClose: () -> Unit) {
    var name by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onClose,
        title = { Text("روز جدید") },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("نام روز (مثلاً: روز اول)") }
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                if (name.isNotBlank()) {
                    viewModel.addDay(name)
                    onClose()
                }
            }) { Text("افزودن") }
        },
        dismissButton = {
            TextButton(onClick = onClose) { Text("انصراف") }
        }
    )
}
