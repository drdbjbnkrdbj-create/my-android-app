package com.example.ui.screens
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.item
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.StudentProfile
import com.example.ui.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentProfileScreen(viewModel: MainViewModel) {
    BackHandler {
        viewModel.navigateBack()
    }

    val context = LocalContext.current
    val profile by viewModel.studentProfile.collectAsStateWithLifecycle()
    val isDarkMode by viewModel.isDarkMode.collectAsStateWithLifecycle()

    var isEditing by remember { mutableStateOf(false) }

    // Form fields
    val currentProfile = profile ?: StudentProfile()
    var fullName by remember(currentProfile) { mutableStateOf(currentProfile.fullName) }
    var universityName by remember(currentProfile) { mutableStateOf(currentProfile.universityName) }
    var collegeName by remember(currentProfile) { mutableStateOf(currentProfile.collegeName) }
    var majorName by remember(currentProfile) { mutableStateOf(currentProfile.majorName) }
    var studentIdNumber by remember(currentProfile) { mutableStateOf(currentProfile.studentIdNumber) }
    var academicLevel by remember(currentProfile) { mutableStateOf(currentProfile.academicLevel) }
    var currentGpa by remember(currentProfile) { mutableStateOf(currentProfile.currentGpa.toString()) }
    var targetGpa by remember(currentProfile) { mutableStateOf(currentProfile.targetGpa.toString()) }
    var advisorName by remember(currentProfile) { mutableStateOf(currentProfile.advisorName) }
    var academicEmail by remember(currentProfile) { mutableStateOf(currentProfile.academicEmail) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "الملف الجامعي والمعلومات الشخصية",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = { viewModel.navigateBack() },
                        modifier = Modifier.testTag("profile_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "رجوع"
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            if (isEditing) {
                                // Save
                                val parsedGpa = currentGpa.toDoubleOrNull() ?: 4.5
                                val parsedTarget = targetGpa.toDoubleOrNull() ?: 5.0
                                viewModel.updateProfile(
                                    currentProfile.copy(
                                        fullName = fullName.trim(),
                                        universityName = universityName.trim(),
                                        collegeName = collegeName.trim(),
                                        majorName = majorName.trim(),
                                        studentIdNumber = studentIdNumber.trim(),
                                        academicLevel = academicLevel.trim(),
                                        currentGpa = parsedGpa,
                                        targetGpa = parsedTarget,
                                        advisorName = advisorName.trim(),
                                        academicEmail = academicEmail.trim()
                                    )
                                )
                                isEditing = false
                                Toast.makeText(context, "تم حفظ بياناتك بنجاح", Toast.LENGTH_SHORT).show()
                            } else {
                                isEditing = true
                            }
                        },
                        modifier = Modifier.testTag("edit_save_profile_button")
                    ) {
                        Icon(
                            imageVector = if (isEditing) Icons.Default.Check else Icons.Default.Edit,
                            contentDescription = if (isEditing) "حفظ" else "تعديل",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { Spacer(modifier = Modifier.height(2.dp)) }

            // Theme Mode Selector Card (مود داكن أزرق/رمادي - مود فاتح أبيض/رمادي)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "مظهر التطبيق (الألوان)",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            // Dark Mode Option (Navy & Slate Gray)
                            Surface(
                                onClick = { viewModel.setDarkMode(true) },
                                shape = RoundedCornerShape(12.dp),
                                color = if (isDarkMode) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                                border = if (isDarkMode) androidx.compose.foundation.BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null,
                                modifier = Modifier.weight(1f)
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.DarkMode,
                                        contentDescription = null,
                                        tint = if (isDarkMode) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(
                                            text = "مود داكن",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp,
                                            color = if (isDarkMode) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Text(
                                            text = "أزرق نيلي ورمادي",
                                            fontSize = 10.sp,
                                            color = MaterialTheme.colorScheme.outline
                                        )
                                    }
                                }
                            }

                            // Light Mode Option (White & Slate Gray)
                            Surface(
                                onClick = { viewModel.setDarkMode(false) },
                                shape = RoundedCornerShape(12.dp),
                                color = if (!isDarkMode) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                                border = if (!isDarkMode) androidx.compose.foundation.BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null,
                                modifier = Modifier.weight(1f)
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.LightMode,
                                        contentDescription = null,
                                        tint = if (!isDarkMode) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(
                                            text = "مود فاتح",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp,
                                            color = if (!isDarkMode) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Text(
                                            text = "أبيض ورمادي أنيق",
                                            fontSize = 10.sp,
                                            color = MaterialTheme.colorScheme.outline
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Student ID Card Preview
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(56.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primaryContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.School,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(30.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(14.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = fullName.ifBlank { "اسم الطالب" },
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "${studentIdNumber.ifBlank { "الرقم الجامعي" }} • ${universityName.ifBlank { "الجامعة" }}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "${collegeName.ifBlank { "الكلية" }} - ${majorName.ifBlank { "التخصص" }}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // GPA Progress Indicator
                        val gpaVal = currentGpa.toFloatOrNull() ?: 4.0f
                        val progress = (gpaVal / currentProfile.maxGpaScale.toFloat()).coerceIn(0f, 1f)
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "المعدل التراكمي الحالي: $currentGpa / ${currentProfile.maxGpaScale}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "المستهدف: $targetGpa",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            LinearProgressIndicator(
                                progress = { progress },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp)
                                    .clip(RoundedCornerShape(4.dp)),
                                color = MaterialTheme.colorScheme.primary,
                                trackColor = MaterialTheme.colorScheme.outlineVariant
                            )
                        }
                    }
                }
            }

            // Editable Form Fields
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = if (isEditing) "تعديل البيانات الجامعية" else "البيانات المسجلة",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        OutlinedTextField(
                            value = fullName,
                            onValueChange = { fullName = it },
                            label = { Text("الاسم الكامل للطالب") },
                            enabled = isEditing,
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = studentIdNumber,
                            onValueChange = { studentIdNumber = it },
                            label = { Text("الرقم الجامعي") },
                            enabled = isEditing,
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = universityName,
                            onValueChange = { universityName = it },
                            label = { Text("اسم الجامعة") },
                            enabled = isEditing,
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = collegeName,
                            onValueChange = { collegeName = it },
                            label = { Text("الكلية") },
                            enabled = isEditing,
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = majorName,
                            onValueChange = { majorName = it },
                            label = { Text("التخصص الدراسي") },
                            enabled = isEditing,
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = academicLevel,
                            onValueChange = { academicLevel = it },
                            label = { Text("المستوى / السنة الدراسية") },
                            enabled = isEditing,
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedTextField(
                                value = currentGpa,
                                onValueChange = { currentGpa = it },
                                label = { Text("المعدل الحالي") },
                                enabled = isEditing,
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = targetGpa,
                                onValueChange = { targetGpa = it },
                                label = { Text("المعدل المستهدف") },
                                enabled = isEditing,
                                modifier = Modifier.weight(1f)
                            )
                        }

                        OutlinedTextField(
                            value = advisorName,
                            onValueChange = { advisorName = it },
                            label = { Text("اسم المرشد الأكاديمي") },
                            enabled = isEditing,
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = academicEmail,
                            onValueChange = { academicEmail = it },
                            label = { Text("البريد الإلكتروني الجامعي") },
                            enabled = isEditing,
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        if (isEditing) {
                            Button(
                                onClick = {
                                    val parsedGpa = currentGpa.toDoubleOrNull() ?: 4.5
                                    val parsedTarget = targetGpa.toDoubleOrNull() ?: 5.0
                                    viewModel.updateProfile(
                                        currentProfile.copy(
                                            fullName = fullName.trim(),
                                            universityName = universityName.trim(),
                                            collegeName = collegeName.trim(),
                                            majorName = majorName.trim(),
                                            studentIdNumber = studentIdNumber.trim(),
                                            academicLevel = academicLevel.trim(),
                                            currentGpa = parsedGpa,
                                            targetGpa = parsedTarget,
                                            advisorName = advisorName.trim(),
                                            academicEmail = academicEmail.trim()
                                        )
                                    )
                                    isEditing = false
                                    Toast.makeText(context, "تم حفظ البيانات", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("حفظ التغييرات")
                            }
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(40.dp)) }
        }
    }
    item {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "منصة جيم",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Developed by the Greatest Developer, Khattab",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "جميع الحقوق محفوظة لصالح المطور، التعديل قد يعرضك للملاحقة القانونية © 2026",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

