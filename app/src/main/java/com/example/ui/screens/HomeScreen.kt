@Composable
fun AddCourseDialog(
    onDismiss: () -> Unit,
    onSave: (
        name: String,
        code: String,
        instructor: String,
        location: String,
        credits: Int,
        colorHex: String,
        iconName: String
    ) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var code by remember { mutableStateOf("") }
    var instructor by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var credits by remember { mutableIntStateOf(3) }
    var selectedColor by remember { mutableStateOf("#0284C7") }
    var selectedIcon by remember { mutableStateOf("book") }

    val colors = listOf(
        "#0284C7", "#4F46E5", "#0D9488", "#059669", "#D97706", "#E11D48", "#9333EA", "#3B82F6"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("إضافة مجلد مادة جديدة") },
        text = {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("اسم المادة الدراسية *") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = code,
                        onValueChange = { code = it },
                        label = { Text("رمز المادة (مثال: CS101)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = instructor,
                        onValueChange = { instructor = it },
                        label = { Text("اسم الأستاذ") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = location,
                        onValueChange = { location = it },
                        label = { Text("القاعة أو الموقع") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        onSave(name, code, instructor, location, credits, selectedColor, selectedIcon)
                    }
                }
            ) {
                Text("إضافة")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("إلغاء")
            }
        }
    )
}