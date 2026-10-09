package com.example
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import com.google.firebase.auth.FirebaseAuth
import com.example.ui.screens.LoginScreen
import androidx.compose.runtime.setValue
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.MainViewModel
import com.example.ui.ScreenDestination
import com.example.ui.screens.CourseDetailScreen
import com.example.ui.screens.DeadlinesScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.StudentProfileScreen
import com.example.ui.screens.TimetableScreen
import com.example.ui.theme.JamiAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
       setContent {
        val viewModel: MainViewModel = viewModel()
        val isDarkMode by viewModel.darkMode.collectAsStateWithLifecycle()
        val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()

        // حالة التحقق من تسجيل الدخول بواسطة Firebase
        var isLoggedIn by remember { 
            mutableStateOf(FirebaseAuth.getInstance().currentUser != null) 
        }

        JamiAppTheme(darkTheme = isDarkMode) {
            if (!isLoggedIn) {
                // عرض شاشة تسجيل الدخول إذا لم يكن المستخدم مسجلاً
                LoginScreen(
                    onLoginSuccess = {
                        isLoggedIn = true
                    }
                )
            } else {
                // واجهة التطبيق الرئيسية (الـ Scaffold القديم)
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = MaterialTheme.colorScheme.background,
                    bottomBar = {
                        if (currentScreen !is ScreenDestination.CourseDetail) {
                            UniversityBottomNavigation(
                                currentScreen = currentScreen,
                                onSelect = { dest -> viewModel.navigateTo(dest) }
                            )
                        }
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        when (val dest = currentScreen) {
                            is ScreenDestination.Home -> {
                                HomeScreen(
                                    viewModel = viewModel,
                                    onNavigateToTimetable = { viewModel.navigateTo(ScreenDestination.Timetable) },
                                    onNavigateToProfile = { viewModel.navigateTo(ScreenDestination.Profile) },
                                    onNavigateToDeadlines = { viewModel.navigateTo(ScreenDestination.AllDeadlines) }
                                )
                            }
                            is ScreenDestination.CourseDetail -> {
                                CourseDetailScreen(
                                    viewModel = viewModel,
                                    initialTab = dest.initialTab
                                )
                            }
                            is ScreenDestination.Timetable -> {
                                TimetableScreen(viewModel = viewModel)
                            }
                            is ScreenDestination.Profile -> {
                                StudentProfileScreen(viewModel = viewModel)
                            }
                            is ScreenDestination.AllDeadlines -> {
                                DeadlinesScreen(viewModel = viewModel)
                            }
                        }
                    }
                }
            }
        }
    }

@Composable
fun UniversityBottomNavigation(
    currentScreen: ScreenDestination,
    onSelect: (ScreenDestination) -> Unit
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = Modifier.testTag("bottom_nav_bar")
    ) {
        NavigationBarItem(
            selected = currentScreen is ScreenDestination.Home,
            onClick = { onSelect(ScreenDestination.Home) },
            icon = { Icon(Icons.Default.Folder, contentDescription = "المواد") },
            label = { Text("المواد", fontSize = 11.sp) },
            modifier = Modifier.testTag("nav_item_courses")
        )

        NavigationBarItem(
            selected = currentScreen is ScreenDestination.Timetable,
            onClick = { onSelect(ScreenDestination.Timetable) },
            icon = { Icon(Icons.Default.CalendarMonth, contentDescription = "الجدول") },
            label = { Text("الجدول", fontSize = 11.sp) },
            modifier = Modifier.testTag("nav_item_timetable")
        )

        NavigationBarItem(
            selected = currentScreen is ScreenDestination.AllDeadlines,
            onClick = { onSelect(ScreenDestination.AllDeadlines) },
            icon = { Icon(Icons.Default.Schedule, contentDescription = "التذكيرات") },
            label = { Text("التذكيرات", fontSize = 11.sp) },
            modifier = Modifier.testTag("nav_item_deadlines")
        )

        NavigationBarItem(
            selected = currentScreen is ScreenDestination.Profile,
            onClick = { onSelect(ScreenDestination.Profile) },
            icon = { Icon(Icons.Default.Person, contentDescription = "الملف الجامعي") },
            label = { Text("الملف", fontSize = 11.sp) },
            modifier = Modifier.testTag("nav_item_profile")
        )
    }
}   }
