package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.Course
import com.example.data.model.CourseDeadline
import com.example.data.model.CourseNote
import com.example.data.model.CourseResource
import com.example.ui.MainViewModel
import com.example.ui.components.DrawingThumbnail
import com.example.ui.components.EmptyPlaceholder
import com.example.ui.components.InteractiveDrawingPad
import com.example.ui.components.PriorityBadge
import com.example.ui.components.TypeTag
import com.example.ui.components.formatTimestamp
import com.example.ui.components.getCourseIcon
import com.example.ui.components.getDaysRemainingText
import com.example.ui.components.parseColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourseDetailScreen(
    viewModel: MainViewModel,
    initialTab: Int = 0
) {
    BackHandler {
        viewModel.navigateBack()
    }

    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val course by viewModel.selectedCourse.collectAsStateWithLifecycle()
    val resources by viewModel.selectedCourseResources.collectAsStateWithLifecycle()
    val notes by viewModel.selectedCourseNotes.collectAsStateWithLifecycle()
    val deadlines by viewModel.selectedCourseDeadlines.collectAsStateWithLifecycle()

    var selectedTabIndex by remember { mutableIntStateOf(initialTab) }

    // Dialog States
    var showAddResourceDialog by remember { mutableStateOf(false) }
    var showAddNoteDialog by remember { mutableStateOf(false) }
    var showAddDeadlineDialog by remember { mutableStateOf(false) }
    var showDeleteCourseDialog by remember { mutableStateOf(false) }
    var viewingNote by remember { mutableStateOf<CourseNote?>(null) }

    if (course == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("جاري تحميل بيانات المادة...")
        }
        return
    }

    val currentCourse = course!!
    val courseAccent = parseColor(currentCourse.colorHex)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = currentCourse.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = currentCourse.code,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = { viewModel.navigateBack() },
                        modifier = Modifier.testTag("back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "الرجوع"
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { showDeleteCourseDialog = true },
                        modifier = Modifier.testTag("delete_course_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "حذف المادة",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    when (selectedTabIndex) {
                        0 -> showAddResourceDialog = true
                        1 -> showAddNoteDialog = true
                        2 -> showAddDeadlineDialog = true
                    }
                },
                containerColor = courseAccent,
                contentColor = Color.White,
                modifier = Modifier.testTag("add_item_fab")
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "إضافة جديد"
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Course Info Header Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(courseAccent),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = getCourseIcon(currentCourse.iconName),
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        if (currentCourse.instructor.isNotBlank()) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(15.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = currentCourse.instructor,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        if (currentCourse.location.isNotBlank()) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Place,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(15.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = currentCourse.location,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(courseAccent.copy(alpha = 0.15f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "${currentCourse.creditHours} ساعات",
                            color = courseAccent,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // 3 Required Tabs
            TabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = courseAccent
            ) {
                Tab(
                    selected = selectedTabIndex == 0,
                    onClick = { selectedTabIndex = 0 },
                    text = {
                        Text(
                            text = "الملازم والمراجع (${resources.size})",
                            fontSize = 12.sp,
                            fontWeight = if (selectedTabIndex == 0) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    icon = { Icon(Icons.Default.MenuBook, contentDescription = null, modifier = Modifier.size(18.dp)) },
                    modifier = Modifier.testTag("tab_resources")
                )
                Tab(
                    selected = selectedTabIndex == 1,
                    onClick = { selectedTabIndex = 1 },
                    text = {
                        Text(
                            text = "الملاحظات والرسم (${notes.size})",
                            fontSize = 12.sp,
                            fontWeight = if (selectedTabIndex == 1) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    icon = { Icon(Icons.Default.Description, contentDescription = null, modifier = Modifier.size(18.dp)) },
                    modifier = Modifier.testTag("tab_notes")
                )
                Tab(
                    selected = selectedTabIndex == 2,
                    onClick = { selectedTabIndex = 2 },
                    text = {
                        Text(
                            text = "المشاريع والتذكيرات (${deadlines.size})",
                            fontSize = 12.sp,
                            fontWeight = if (selectedTabIndex == 2) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    icon = { Icon(Icons.Default.Event, contentDescription = null, modifier = Modifier.size(18.dp)) },
                    modifier = Modifier.testTag("tab_deadlines")
                )
            }

            // Tab Content
            when (selectedTabIndex) {
                0 -> {
                    // TAB 1: Resources & Links & Slides
                    if (resources.isEmpty()) {
                        EmptyPlaceholder(
                            icon = Icons.Default.MenuBook,
                            title = "لا توجد ملازم أو مراجع مضافة",
                            subtitle = "اضغط على زر (+) لإضافة سلايدات، ملفات PDF، روابط مواقع، أو مراجع للمادة"
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            items(resources, key = { it.id }) { res ->
                                ResourceItemCard(
                                    resource = res,
                                    onOpenUrl = {
                                        if (res.urlOrPath.startsWith("http://") || res.urlOrPath.startsWith("https://")) {
                                            try {
                                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(res.urlOrPath))
                                                context.startActivity(intent)
                                            } catch (_: Exception) {
                                                Toast.makeText(context, "تعذر فتح الرابط", Toast.LENGTH_SHORT).show()
                                            }
                                        } else {
                                            Toast.makeText(context, res.urlOrPath, Toast.LENGTH_SHORT).show()
                                        }
                                    },
                                    onCopy = {
                                        clipboardManager.setText(AnnotatedString(res.urlOrPath))
                                        Toast.makeText(context, "تم نسخ الرابط للحافظة", Toast.LENGTH_SHORT).show()
                                    },
                                    onDelete = { viewModel.deleteResource(res) }
                                )
                            }
                            item { Spacer(modifier = Modifier.height(72.dp)) }
                        }
                    }
                }
                1 -> {
                    // TAB 2: Notes & Drawing
                    if (notes.isEmpty()) {
                        EmptyPlaceholder(
                            icon = Icons.Default.Description,
                            title = "لا توجد ملاحظات أو رسومات بعد",
                            subtitle = "اضغط على زر (+) لتدوين ملاحظات المحاضرة، ورسم المخططات والمعادلات بيدك!"
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(notes, key = { it.id }) { note ->
                                NoteItemCard(
                                    note = note,
                                    onClick = { viewingNote = note },
                                    onDelete = { viewModel.deleteNote(note) }
                                )
                            }
                            item { Spacer(modifier = Modifier.height(72.dp)) }
                        }
                    }
                }
                2 -> {
                    // TAB 3: Projects & Deadlines
                    if (deadlines.isEmpty()) {
                        EmptyPlaceholder(
                            icon = Icons.Default.Schedule,
                            title = "لا توجد مواعيد مشاريع أو تذكيرات",
                            subtitle = "اضغط على زر (+) لتسجيل موعد تسليم مشروع، كويز، أو اختبار تود التذكير به"
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            items(deadlines, key = { it.id }) { deadline ->
                                DeadlineItemCard(
                                    deadline = deadline,
                                    onToggle = { isDone ->
                                        viewModel.toggleDeadline(deadline.id, isDone)
                                    },
                                    onDelete = { viewModel.deleteDeadline(deadline) }
                                )
                            }
                            item { Spacer(modifier = Modifier.height(72.dp)) }
                        }
                    }
                }
            }
        }
    }

    // Add Resource Dialog
    if (showAddResourceDialog) {
        AddResourceDialog(
            courseId = currentCourse.id,
            onDismiss = { showAddResourceDialog = false },
            onSave = { title, type, url, desc ->
                viewModel.addResource(currentCourse.id, title, type, url, desc)
                showAddResourceDialog = false
            }
        )
    }

    // Add Note Dialog (with Drawing Pad)
    if (showAddNoteDialog) {
        AddNoteWithDrawingDialog(
            courseId = currentCourse.id,
            onDismiss = { showAddNoteDialog = false },
            onSave = { title, content, drawingJson ->
                viewModel.addNote(currentCourse.id, title, content, drawingJson)
                showAddNoteDialog = false
            }
        )
    }

    // View Note Dialog
    if (viewingNote != null) {
        ViewNoteDetailDialog(
            note = viewingNote!!,
            onDismiss = { viewingNote = null },
            onDelete = {
                viewModel.deleteNote(viewingNote!!)
                viewingNote = null
            }
        )
    }

    // Add Deadline Dialog
    if (showAddDeadlineDialog) {
        AddDeadlineDialog(
            courseId = currentCourse.id,
            onDismiss = { showAddDeadlineDialog = false },
            onSave = { title, type, dueTimestamp, priority, notesText ->
                viewModel.addDeadline(currentCourse.id, title, type, dueTimestamp, priority, notesText)
                showAddDeadlineDialog = false
            }
        )
    }

    // Delete Course Confirmation Dialog
    if (showDeleteCourseDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteCourseDialog = false },
            title = { Text("حذف مادة ${currentCourse.name}") },
            text = { Text("هل أنت متأكد من حذف هذه المادة؟ سيتم حذف جميع الملازم والملاحظات والتذكيرات المرتبطة بها.") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteCourse(currentCourse)
                        showDeleteCourseDialog = false
                    }
                ) {
                    Text("نعم، حذف المادة")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteCourseDialog = false }) {
                    Text("إلغاء")
                }
            }
        )
    }
}

@Composable
fun ResourceItemCard(
    resource: CourseResource,
    onOpenUrl: () -> Unit,
    onCopy: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TypeTag(resource.type)
                IconButton(onClick = onDelete, modifier = Modifier.size(28.dp)) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "حذف",
                        tint = MaterialTheme.colorScheme.outline,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = resource.title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            if (resource.description.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = resource.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Link,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = resource.urlOrPath,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = onCopy, modifier = Modifier.size(24.dp)) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "نسخ",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(15.dp)
                    )
                }
                if (resource.urlOrPath.startsWith("http")) {
                    Spacer(modifier = Modifier.width(4.dp))
                    IconButton(onClick = onOpenUrl, modifier = Modifier.size(24.dp)) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                            contentDescription = "فتح",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun NoteItemCard(
    note: CourseNote,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = note.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = onDelete, modifier = Modifier.size(28.dp)) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "حذف",
                        tint = MaterialTheme.colorScheme.outline,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            if (note.content.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = note.content,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Thumbnail if drawing exists!
            if (!note.drawingData.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(10.dp))
                DrawingThumbnail(
                    drawingData = note.drawingData,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "آخر تحديث: ${formatTimestamp(note.updatedAt)}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.outline,
                fontSize = 10.sp
            )
        }
    }
}

@Composable
fun DeadlineItemCard(
    deadline: CourseDeadline,
    onToggle: (Boolean) -> Unit,
    onDelete: () -> Unit
) {
    val (remainingText, remainingColor) = getDaysRemainingText(deadline.dueDate)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (deadline.isCompleted)
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
            else
                MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.Top
        ) {
            IconButton(
                onClick = { onToggle(!deadline.isCompleted) },
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = if (deadline.isCompleted) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                    contentDescription = null,
                    tint = if (deadline.isCompleted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TypeTag(deadline.type)
                    PriorityBadge(deadline.priority)
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = deadline.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = if (deadline.isCompleted) MaterialTheme.colorScheme.outline else MaterialTheme.colorScheme.onSurface,
                    textDecoration = if (deadline.isCompleted) TextDecoration.LineThrough else null
                )

                if (deadline.notes.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = deadline.notes,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "الموعد: ${formatTimestamp(deadline.dueDate)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline,
                        fontSize = 11.sp
                    )

                    Text(
                        text = if (deadline.isCompleted) "مكتمل ✓" else remainingText,
                        color = if (deadline.isCompleted) MaterialTheme.colorScheme.primary else remainingColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            IconButton(onClick = onDelete, modifier = Modifier.size(28.dp)) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "حذف",
                    tint = MaterialTheme.colorScheme.outline,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

// Dialogs

@Composable
fun AddResourceDialog(
    courseId: Long,
    onDismiss: () -> Unit,
    onSave: (title: String, type: String, url: String, desc: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var url by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf("SLIDES") }

    val types = listOf(
        "SLIDES" to "سلايد/ملزمة",
        "LINK" to "رابط موقع",
        "BOOK" to "كتاب",
        "REFERENCE" to "مرجع",
        "SUMMARY" to "ملخص"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("إضافة ملزمة أو مرجع أو رابط") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("عنوان المرجع أو الملف") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                // Type selector
                Text("نوع المرجع:", style = MaterialTheme.typography.bodySmall)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    types.forEach { (typeKey, label) ->
                        val isSelected = selectedType == typeKey
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant)
                                .clickable { selectedType = typeKey }
                                .padding(horizontal = 8.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = label,
                                fontSize = 11.sp,
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = url,
                    onValueChange = { url = it },
                    label = { Text("الرابط أو مسار الملف (URL/Drive/PDF)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = desc,
                    onValueChange = { desc = it },
                    label = { Text("وصف مختصر أو تنبيهات (اختياري)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        onSave(title, selectedType, url, desc)
                    }
                },
                enabled = title.isNotBlank()
            ) {
                Text("إضافة")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("إلغاء") }
        }
    )
}

@Composable
fun AddNoteWithDrawingDialog(
    courseId: Long,
    onDismiss: () -> Unit,
    onSave: (title: String, content: String, drawingJson: String?) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var drawingJson by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("إضافة ملاحظة جديدة مع لوحة رسم") },
        text = {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                item {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("عنوان الملاحظة") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item {
                    OutlinedTextField(
                        value = content,
                        onValueChange = { content = it },
                        label = { Text("محتوى الملاحظة المكتوب") },
                        minLines = 3,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item {
                    InteractiveDrawingPad(
                        initialStrokesJson = null,
                        onStrokesChanged = { json ->
                            drawingJson = json.ifBlank { null }
                        }
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        onSave(title, content, drawingJson)
                    }
                },
                enabled = title.isNotBlank()
            ) {
                Text("حفظ الملاحظة")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("إلغاء") }
        }
    )
}

@Composable
fun ViewNoteDetailDialog(
    note: CourseNote,
    onDismiss: () -> Unit,
    onDelete: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = note.title, fontWeight = FontWeight.Bold)
        },
        text = {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (note.content.isNotBlank()) {
                    item {
                        Text(
                            text = note.content,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                if (!note.drawingData.isNullOrBlank()) {
                    item {
                        Text(
                            text = "الرسم والمخطط المحفوظ:",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        DrawingThumbnail(
                            drawingData = note.drawingData,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(220.dp)
                        )
                    }
                }

                item {
                    Text(
                        text = "التاريخ: ${formatTimestamp(note.updatedAt)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("إغلاق") }
        },
        dismissButton = {
            TextButton(onClick = onDelete) {
                Text("حذف الملاحظة", color = MaterialTheme.colorScheme.error)
            }
        }
    )
}

@Composable
fun AddDeadlineDialog(
    courseId: Long,
    onDismiss: () -> Unit,
    onSave: (title: String, type: String, dueTimestamp: Long, priority: String, notes: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf("PROJECT") }
    var selectedPriority by remember { mutableStateOf("HIGH") }
    var daysFromNow by remember { mutableIntStateOf(3) }

    val types = listOf(
        "PROJECT" to "مشروع",
        "ASSIGNMENT" to "واجب",
        "QUIZ" to "كويز",
        "EXAM" to "اختبار",
        "REMINDER" to "تذكير"
    )

    val priorities = listOf(
        "HIGH" to "عاجل",
        "MEDIUM" to "متوسط",
        "LOW" to "عادي"
    )

    val dueOptions = listOf(
        1 to "غداً",
        3 to "بعد 3 أيام",
        7 to "بعد أسبوع",
        14 to "بعد أسبوعين",
        30 to "بعد شهر"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("تسجيل موعد مشروع أو تذكير") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("عنوان الموعد أو المشروع") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                // Type selector
                Text("النوع:", style = MaterialTheme.typography.bodySmall)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    types.forEach { (typeKey, label) ->
                        val isSelected = selectedType == typeKey
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant)
                                .clickable { selectedType = typeKey }
                                .padding(horizontal = 8.dp, vertical = 5.dp)
                        ) {
                            Text(
                                text = label,
                                fontSize = 11.sp,
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // Priority selector
                Text("الأهمية:", style = MaterialTheme.typography.bodySmall)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    priorities.forEach { (pKey, label) ->
                        val isSelected = selectedPriority == pKey
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant)
                                .clickable { selectedPriority = pKey }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = label,
                                fontSize = 12.sp,
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // Due date selector
                Text("الموعد النهائي:", style = MaterialTheme.typography.bodySmall)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    dueOptions.forEach { (days, label) ->
                        val isSelected = daysFromNow == days
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.surfaceVariant)
                                .clickable { daysFromNow = days }
                                .padding(horizontal = 8.dp, vertical = 5.dp)
                        ) {
                            Text(
                                text = label,
                                fontSize = 11.sp,
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("ملاحظات إضافية (اختياري)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        val dueTimestamp = System.currentTimeMillis() + (daysFromNow * 86400000L)
                        onSave(title, selectedType, dueTimestamp, selectedPriority, notes)
                    }
                },
                enabled = title.isNotBlank()
            ) {
                Text("تسجيل الموعد")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("إلغاء") }
        }
    )
}
