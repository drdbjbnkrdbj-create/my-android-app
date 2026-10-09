package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

fun getCourseIcon(iconName: String): ImageVector {
    return when (iconName.lowercase()) {
        "code" -> Icons.Default.Code
        "psychology", "ai" -> Icons.Default.Psychology
        "database", "storage" -> Icons.Default.Storage
        "science" -> Icons.Default.Science
        "math", "calc" -> Icons.Default.Calculate
        "globe" -> Icons.Default.Public
        else -> Icons.Default.MenuBook
    }
}

fun formatTimestamp(millis: Long): String {
    val sdf = SimpleDateFormat("dd MMMM yyyy", Locale("ar"))
    return sdf.format(Date(millis))
}

fun formatShortDate(millis: Long): String {
    val sdf = SimpleDateFormat("dd/MM", Locale("ar"))
    return sdf.format(Date(millis))
}

fun getDaysRemainingText(dueMillis: Long): Pair<String, Color> {
    val now = System.currentTimeMillis()
    val diff = dueMillis - now
    val days = TimeUnit.MILLISECONDS.toDays(diff)

    return when {
        diff < 0 -> Pair("فات الموعد", Color(0xFFEF4444))
        days == 0L -> Pair("الموعد اليوم!", Color(0xFFF97316))
        days == 1L -> Pair("باقي يوم واحد", Color(0xFFF59E0B))
        days == 2L -> Pair("باقي يومان", Color(0xFF3B82F6))
        days in 3..10 -> Pair("باقي $days أيام", Color(0xFF10B981))
        else -> Pair("باقي $days يوم", Color(0xFF64748B))
    }
}

@Composable
fun PriorityBadge(priority: String, modifier: Modifier = Modifier) {
    val (label, bg, fg) = when (priority.uppercase()) {
        "HIGH" -> Triple("عاجل جداً", Color(0xFFEF4444).copy(alpha = 0.15f), Color(0xFFEF4444))
        "MEDIUM" -> Triple("متوسط", Color(0xFFF59E0B).copy(alpha = 0.15f), Color(0xFFF59E0B))
        else -> Triple("عادي", Color(0xFF10B981).copy(alpha = 0.15f), Color(0xFF10B981))
    }

    Surface(
        color = bg,
        shape = RoundedCornerShape(8.dp),
        modifier = modifier
    ) {
        Text(
            text = label,
            color = fg,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
        )
    }
}

@Composable
fun TypeTag(type: String, modifier: Modifier = Modifier) {
    val label = when (type.uppercase()) {
        "SLIDES" -> "سلايدات وملازم"
        "FILE" -> "ملف PDF"
        "LINK" -> "رابط ويب"
        "BOOK" -> "كتاب ومصدر"
        "REFERENCE" -> "مرجع أكاديمي"
        "SUMMARY" -> "ملخص شامل"
        "PROJECT" -> "مشروع"
        "ASSIGNMENT" -> "واجب منزلي"
        "QUIZ" -> "كويز"
        "EXAM" -> "اختبار نهائي/نصفي"
        "REMINDER" -> "تذكير"
        else -> type
    }

    Surface(
        color = MaterialTheme.colorScheme.secondaryContainer,
        shape = RoundedCornerShape(6.dp),
        modifier = modifier
    ) {
        Text(
            text = label,
            color = MaterialTheme.colorScheme.onSecondaryContainer,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
        )
    }
}

@Composable
fun EmptyPlaceholder(
    icon: ImageVector,
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(32.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}
