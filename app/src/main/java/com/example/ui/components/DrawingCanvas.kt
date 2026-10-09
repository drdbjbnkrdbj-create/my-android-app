package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Create
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.json.JSONArray
import org.json.JSONObject

data class DrawPoint(val x: Float, val y: Float)

data class DrawStroke(
    val points: List<DrawPoint>,
    val colorHex: String,
    val strokeWidth: Float
)

object DrawingSerializer {
    fun serialize(strokes: List<DrawStroke>): String {
        val jsonArray = JSONArray()
        strokes.forEach { stroke ->
            val strokeObj = JSONObject()
            strokeObj.put("color", stroke.colorHex)
            strokeObj.put("strokeWidth", stroke.strokeWidth.toDouble())

            val pointsArray = JSONArray()
            stroke.points.forEach { pt ->
                val ptObj = JSONObject()
                ptObj.put("x", pt.x.toDouble())
                ptObj.put("y", pt.y.toDouble())
                pointsArray.put(ptObj)
            }
            strokeObj.put("points", pointsArray)
            jsonArray.put(strokeObj)
        }
        return jsonArray.toString()
    }

    fun deserialize(json: String?): List<DrawStroke> {
        if (json.isNullOrBlank()) return emptyList()
        val result = mutableListOf<DrawStroke>()
        try {
            val jsonArray = JSONArray(json)
            for (i in 0 until jsonArray.length()) {
                val strokeObj = jsonArray.getJSONObject(i)
                val color = strokeObj.optString("color", "#0284C7")
                val width = strokeObj.optDouble("strokeWidth", 6.0).toFloat()

                val points = mutableListOf<DrawPoint>()
                val pointsArray = strokeObj.getJSONArray("points")
                for (j in 0 until pointsArray.length()) {
                    val ptObj = pointsArray.getJSONObject(j)
                    val x = ptObj.getDouble("x").toFloat()
                    val y = ptObj.getDouble("y").toFloat()
                    points.add(DrawPoint(x, y))
                }
                if (points.isNotEmpty()) {
                    result.add(DrawStroke(points, color, width))
                }
            }
        } catch (_: Exception) {
            return emptyList()
        }
        return result
    }
}

fun parseColor(hex: String, fallback: Color = Color(0xFF0284C7)): Color {
    return try {
        Color(android.graphics.Color.parseColor(hex))
    } catch (_: Exception) {
        fallback
    }
}

@Composable
fun InteractiveDrawingPad(
    initialStrokesJson: String?,
    onStrokesChanged: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val initialStrokes = remember(initialStrokesJson) {
        DrawingSerializer.deserialize(initialStrokesJson)
    }

    val strokes = remember { mutableStateListOf<DrawStroke>().apply { addAll(initialStrokes) } }
    var currentPoints by remember { mutableStateOf<List<DrawPoint>>(emptyList()) }

    val palette = listOf(
        "#0284C7", // Cyan / Sky Blue
        "#4F46E5", // Indigo
        "#059669", // Emerald Green
        "#DC2626", // Red
        "#D97706", // Amber
        "#9333EA", // Purple
        "#0F172A", // Dark Slate
        "#FFFFFF"  // White
    )

    var selectedColorHex by remember { mutableStateOf(palette[0]) }
    var selectedStrokeWidth by remember { mutableStateOf(6f) }
    var isEraser by remember { mutableStateOf(false) }

    val canvasBackground = MaterialTheme.colorScheme.surfaceVariant

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(16.dp))
            .padding(12.dp)
    ) {
        // Toolbar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Create,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "لوحة الرسم والمخططات",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                // Undo
                IconButton(
                    onClick = {
                        if (strokes.isNotEmpty()) {
                            strokes.removeAt(strokes.lastIndex)
                            onStrokesChanged(DrawingSerializer.serialize(strokes))
                        }
                    },
                    enabled = strokes.isNotEmpty()
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Undo,
                        contentDescription = "تراجع",
                        tint = if (strokes.isNotEmpty()) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                    )
                }

                // Clear
                IconButton(
                    onClick = {
                        strokes.clear()
                        currentPoints = emptyList()
                        onStrokesChanged("")
                    },
                    enabled = strokes.isNotEmpty()
                ) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "مسح الكل",
                        tint = if (strokes.isNotEmpty()) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.outline
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Colors & Stroke widths row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Color chips
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                palette.forEach { hex ->
                    val color = parseColor(hex)
                    val isSelected = !isEraser && selectedColorHex == hex
                    Box(
                        modifier = Modifier
                            .size(if (isSelected) 28.dp else 22.dp)
                            .clip(CircleShape)
                            .background(color)
                            .border(
                                width = if (isSelected) 2.5.dp else 1.dp,
                                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                                shape = CircleShape
                            )
                            .clickable {
                                isEraser = false
                                selectedColorHex = hex
                            }
                    )
                }
            }

            // Stroke size toggles
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                listOf(4f to "رفيع", 8f to "متوسط", 14f to "عريض").forEach { (width, label) ->
                    val isSelected = selectedStrokeWidth == width
                    Surface(
                        onClick = { selectedStrokeWidth = width },
                        shape = RoundedCornerShape(8.dp),
                        color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.height(28.dp)
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        ) {
                            Text(
                                text = label,
                                fontSize = 11.sp,
                                color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Canvas Area
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(260.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(canvasBackground)
                .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                .pointerInput(selectedColorHex, selectedStrokeWidth, isEraser) {
                    detectDragGestures(
                        onDragStart = { offset ->
                            currentPoints = listOf(DrawPoint(offset.x, offset.y))
                        },
                        onDrag = { change, _ ->
                            change.consume()
                            currentPoints = currentPoints + DrawPoint(change.position.x, change.position.y)
                        },
                        onDragEnd = {
                            if (currentPoints.isNotEmpty()) {
                                val activeHex = if (isEraser) {
                                    "#%06X".format(0xFFFFFF and canvasBackground.toArgb())
                                } else {
                                    selectedColorHex
                                }
                                strokes.add(
                                    DrawStroke(
                                        points = currentPoints,
                                        colorHex = activeHex,
                                        strokeWidth = selectedStrokeWidth
                                    )
                                )
                                currentPoints = emptyList()
                                onStrokesChanged(DrawingSerializer.serialize(strokes))
                            }
                        },
                        onDragCancel = {
                            currentPoints = emptyList()
                        }
                    )
                }
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                // Render completed strokes
                strokes.forEach { stroke ->
                    val strokeColor = parseColor(stroke.colorHex)
                    if (stroke.points.size > 1) {
                        val path = Path().apply {
                            moveTo(stroke.points[0].x, stroke.points[0].y)
                            for (i in 1 until stroke.points.size) {
                                lineTo(stroke.points[i].x, stroke.points[i].y)
                            }
                        }
                        drawPath(
                            path = path,
                            color = strokeColor,
                            style = Stroke(
                                width = stroke.strokeWidth,
                                cap = StrokeCap.Round,
                                join = StrokeJoin.Round
                            )
                        )
                    } else if (stroke.points.isNotEmpty()) {
                        drawCircle(
                            color = strokeColor,
                            radius = stroke.strokeWidth / 2f,
                            center = Offset(stroke.points[0].x, stroke.points[0].y)
                        )
                    }
                }

                // Render current stroke in progress
                if (currentPoints.size > 1) {
                    val activeColor = if (isEraser) canvasBackground else parseColor(selectedColorHex)
                    val path = Path().apply {
                        moveTo(currentPoints[0].x, currentPoints[0].y)
                        for (i in 1 until currentPoints.size) {
                            lineTo(currentPoints[i].x, currentPoints[i].y)
                        }
                    }
                    drawPath(
                        path = path,
                        color = activeColor,
                        style = Stroke(
                            width = selectedStrokeWidth,
                            cap = StrokeCap.Round,
                            join = StrokeJoin.Round
                        )
                    )
                }
            }

            if (strokes.isEmpty() && currentPoints.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "ارسم هنا (معادلات، رسومات بيانية، خرائط ذهنية)",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                }
            }
        }
    }
}

@Composable
fun DrawingThumbnail(
    drawingData: String?,
    modifier: Modifier = Modifier
) {
    if (drawingData.isNullOrBlank()) return

    val strokes = remember(drawingData) { DrawingSerializer.deserialize(drawingData) }
    if (strokes.isEmpty()) return

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f))
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(8.dp))
    ) {
        Canvas(modifier = Modifier.fillMaxSize().padding(6.dp)) {
            // Find bounds
            var minX = Float.MAX_VALUE
            var maxX = Float.MIN_VALUE
            var minY = Float.MAX_VALUE
            var maxY = Float.MIN_VALUE

            strokes.forEach { stroke ->
                stroke.points.forEach { pt ->
                    if (pt.x < minX) minX = pt.x
                    if (pt.x > maxX) maxX = pt.x
                    if (pt.y < minY) minY = pt.y
                    if (pt.y > maxY) maxY = pt.y
                }
            }

            val strokeW = if (maxX > minX) maxX - minX else 1f
            val strokeH = if (maxY > minY) maxY - minY else 1f

            val scaleX = size.width / (strokeW * 1.15f)
            val scaleY = size.height / (strokeH * 1.15f)
            val scale = minOf(scaleX, scaleY).coerceIn(0.1f, 3.0f)

            strokes.forEach { stroke ->
                val strokeColor = parseColor(stroke.colorHex)
                if (stroke.points.size > 1) {
                    val path = Path().apply {
                        val firstX = (stroke.points[0].x - minX) * scale + 4f
                        val firstY = (stroke.points[0].y - minY) * scale + 4f
                        moveTo(firstX, firstY)
                        for (i in 1 until stroke.points.size) {
                            val px = (stroke.points[i].x - minX) * scale + 4f
                            val py = (stroke.points[i].y - minY) * scale + 4f
                            lineTo(px, py)
                        }
                    }
                    drawPath(
                        path = path,
                        color = strokeColor,
                        style = Stroke(
                            width = (stroke.strokeWidth * scale * 0.75f).coerceAtLeast(1.5f),
                            cap = StrokeCap.Round,
                            join = StrokeJoin.Round
                        )
                    )
                }
            }
        }
    }
}
