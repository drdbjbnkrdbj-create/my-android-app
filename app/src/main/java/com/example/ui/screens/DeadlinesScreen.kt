package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.MainViewModel
import com.example.ui.components.EmptyPlaceholder

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeadlinesScreen(viewModel: MainViewModel) {
    BackHandler {
        viewModel.navigateBack()
    }

    val deadlines by viewModel.allDeadlines.collectAsStateWithLifecycle()
    val courses by viewModel.courses.collectAsStateWithLifecycle()

    var selectedFilter by remember { mutableIntStateOf(0) } // 0 = All, 1 = Pending, 2 = Completed, 3 = Projects
    var showAddDeadlineDialog by remember { mutableStateOf(false) }

    val filteredDeadlines = when (selectedFilter) {
        1 -> deadlines.filter { !it.isCompleted }
        2 -> deadlines.filter { it.isCompleted }
        3 -> deadlines.filter { it.type == "PROJECT" }
        else -> deadlines
    }

    val defaultCourseId = courses.firstOrNull()?.id ?: 1L

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "جميع المشاريع والتذكيرات",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = { viewModel.navigateBack() },
                        modifier = Modifier.testTag("deadlines_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "رجوع"
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
                onClick = { showAddDeadlineDialog = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.testTag("add_deadline_fab")
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "إضافة موعد أو تذكير"
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Filter chips
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val filters = listOf(
                    "الكل (${deadlines.size})",
                    "المعلقة (${deadlines.count { !it.isCompleted }})",
                    "المكتملة (${deadlines.count { it.isCompleted }})",
                    "المشاريع فقط (${deadlines.count { it.type == "PROJECT" }})"
                )
                items(filters.indices.toList()) { index ->
                    FilterChip(
                        selected = selectedFilter == index,
                        onClick = { selectedFilter = index },
                        label = { Text(filters[index], fontSize = 12.sp) }
                    )
                }
            }

            if (filteredDeadlines.isEmpty()) {
                EmptyPlaceholder(
                    icon = Icons.Default.Schedule,
                    title = "لا توجد مواعيد في هذا القسم",
                    subtitle = "اضغط على زر (+) لتسجيل موعد مشروع جديد أو تذكير لاختبار"
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(filteredDeadlines, key = { it.id }) { deadline ->
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

    if (showAddDeadlineDialog) {
        AddDeadlineDialog(
            courseId = defaultCourseId,
            onDismiss = { showAddDeadlineDialog = false },
            onSave = { title, type, dueTimestamp, priority, notesText ->
                viewModel.addDeadline(defaultCourseId, title, type, dueTimestamp, priority, notesText)
                showAddDeadlineDialog = false
            }
        )
    }
}
