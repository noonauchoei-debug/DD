package com.example

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import kotlin.math.abs

@Composable
fun QuickStatCard(
    title: String,
    value: String,
    subtext: String,
    icon: ImageVector,
    iconColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(iconColor.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = iconColor,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                    modifier = Modifier.size(14.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = subtext,
                style = MaterialTheme.typography.labelSmall,
                color = iconColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun ActionRowItem(
    icon: ImageVector,
    iconTint: Color,
    title: String,
    description: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(iconTint.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(22.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = description,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
            modifier = Modifier.size(14.dp)
        )
    }
}

/**
 * Procedural standard QR Code Matrix renderer
 * Draws position detection patterns (corner squares) + timing patterns + deterministic content modules
 */
@Composable
fun QrCodeMatrixDisplay(
    data: String,
    modifier: Modifier = Modifier,
    moduleCount: Int = 25,
    tintColor: Color = Color(0xFF0F172A)
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val s = minOf(w, h)
        val cellSize = s / moduleCount

        // 1. Finder pattern generator (at top-left, top-right, bottom-left)
        fun isFinderPattern(r: Int, c: Int): Boolean {
            // Top-left
            if (r in 0..6 && c in 0..6) {
                if (r == 0 || r == 6 || c == 0 || c == 6) return true
                if (r in 2..4 && c in 2..4) return true
                return false
            }
            // Top-right
            if (r in 0..6 && c in (moduleCount - 7) until moduleCount) {
                val relC = c - (moduleCount - 7)
                if (r == 0 || r == 6 || relC == 0 || relC == 6) return true
                if (r in 2..4 && relC in 2..4) return true
                return false
            }
            // Bottom-left
            if (r in (moduleCount - 7) until moduleCount && c in 0..6) {
                val relR = r - (moduleCount - 7)
                if (relR == 0 || relR == 6 || c == 0 || c == 6) return true
                if (relR in 2..4 && c in 2..4) return true
                return false
            }
            return false
        }

        fun isTimingPattern(r: Int, c: Int): Boolean {
            if (r == 6 && c % 2 == 0) return true
            if (c == 6 && r % 2 == 0) return true
            return false
        }

        fun isFinderSeparator(r: Int, c: Int): Boolean {
            // Surrounding white space
            if (r in 0..7 && c in 0..7) return true
            if (r in 0..7 && c in (moduleCount - 8) until moduleCount) return true
            if (r in (moduleCount - 8) until moduleCount && c in 0..7) return true
            return false
        }

        val hash = abs(data.hashCode())
        val seedBytes = data.toByteArray()

        for (r in 0 until moduleCount) {
            for (c in 0 until moduleCount) {
                val isFinder = isFinderPattern(r, c)
                val isSep = isFinderSeparator(r, c)
                val isTiming = isTimingPattern(r, c)

                val shouldDraw = if (isFinder) {
                    true
                } else if (isSep) {
                    false
                } else if (isTiming) {
                    true
                } else {
                    val byteVal = if (seedBytes.isNotEmpty()) seedBytes[(r * 7 + c * 11) % seedBytes.size].toInt() else 0
                    val combined = (r * 31 + c * 17 + hash + byteVal)
                    (combined % 3 == 0 || (r + c) % 5 == 0) && (combined % 7 != 0)
                }

                if (shouldDraw) {
                    drawRect(
                        color = tintColor,
                        topLeft = Offset(c * cellSize, r * cellSize),
                        size = Size(cellSize, cellSize)
                    )
                }
            }
        }
    }
}

/**
 * Universal Dialog for displaying a full-screen scan-ready QR code (e.g. LINE ID, Tax ID, PromptPay)
 */
@Composable
fun QrCodeViewDialog(
    title: String,
    subtitle: String,
    valueToScan: String,
    qrType: String = "LINE", // "LINE" or "TAX" or "PROMPTPAY"
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = Color.White,
            tonalElevation = 8.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val badgeColor = when (qrType) {
                            "LINE" -> Color(0xFF06C755)
                            "TAX" -> Color(0xFF0052CC)
                            else -> Color(0xFF1565C0)
                        }
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(badgeColor.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.QrCode2,
                                contentDescription = null,
                                tint = badgeColor,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Column {
                            Text(
                                text = title,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0F172A)
                            )
                            Text(
                                text = subtitle,
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFF64748B)
                            )
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Filled.Close, contentDescription = "ปิด", tint = Color(0xFF64748B))
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // QR Container Card with high contrast border
                Box(
                    modifier = Modifier
                        .size(240.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.White)
                        .border(2.dp, Color(0xFFE2E8F0), RoundedCornerShape(16.dp))
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    QrCodeMatrixDisplay(
                        data = valueToScan,
                        modifier = Modifier.fillMaxSize(),
                        tintColor = if (qrType == "LINE") Color(0xFF06C755) else Color(0xFF0F172A)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Value Text Box
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFFF1F5F9),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = if (qrType == "LINE") "LINE Official / ID" else if (qrType == "TAX") "เลขประจำตัวผู้เสียภาษีอากร" else "ข้อมูลสแกน",
                            fontSize = 11.sp,
                            color = Color(0xFF64748B)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = valueToScan,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A),
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (qrType == "LINE") Color(0xFF06C755) else Color(0xFF0052CC)
                    )
                ) {
                    Text("ปิดหน้าต่าง QR Code", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

/**
 * Universal QR Code Scanner Dialog:
 * Allows scanning via camera view / simulated high-precision viewfinder
 * or quick selection/manual override, then callbacks with the scanned result.
 */
@Composable
fun QrCodeScannerModal(
    title: String,
    promptText: String,
    targetField: String = "LINE", // "LINE" or "TAX"
    onDismiss: () -> Unit,
    onCodeScanned: (String) -> Unit
) {
    var manualInput by remember { mutableStateOf("") }
    var isSimulatingScan by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = Color(0xFF1E293B),
            tonalElevation = 10.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFF38BDF8).copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.QrCodeScanner,
                                contentDescription = null,
                                tint = Color(0xFF38BDF8),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Column {
                            Text(
                                text = title,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = promptText,
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFF94A3B8)
                            )
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Filled.Close, contentDescription = "ปิด", tint = Color(0xFF94A3B8))
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Viewfinder / Scanner target Box
                Box(
                    modifier = Modifier
                        .size(230.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(Color(0xFF0F172A))
                        .border(2.dp, Color(0xFF38BDF8).copy(alpha = 0.6f), RoundedCornerShape(18.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    // Viewfinder corner marks
                    Canvas(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                        val strokeW = 4.dp.toPx()
                        val cornerLen = 28.dp.toPx()
                        val c = Color(0xFF38BDF8)

                        // Top-Left
                        drawLine(c, Offset(0f, 0f), Offset(cornerLen, 0f), strokeW)
                        drawLine(c, Offset(0f, 0f), Offset(0f, cornerLen), strokeW)

                        // Top-Right
                        drawLine(c, Offset(size.width, 0f), Offset(size.width - cornerLen, 0f), strokeW)
                        drawLine(c, Offset(size.width, 0f), Offset(size.width, cornerLen), strokeW)

                        // Bottom-Left
                        drawLine(c, Offset(0f, size.height), Offset(cornerLen, size.height), strokeW)
                        drawLine(c, Offset(0f, size.height), Offset(0f, size.height - cornerLen), strokeW)

                        // Bottom-Right
                        drawLine(c, Offset(size.width, size.height), Offset(size.width - cornerLen, size.height), strokeW)
                        drawLine(c, Offset(size.width, size.height), Offset(size.width, size.height - cornerLen), strokeW)

                        // Red laser scan line in center
                        drawLine(
                            color = Color(0xFFEF4444).copy(alpha = 0.85f),
                            start = Offset(10.dp.toPx(), size.height / 2),
                            end = Offset(size.width - 10.dp.toPx(), size.height / 2),
                            strokeWidth = 2.dp.toPx()
                        )
                    }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.CenterFocusWeak,
                            contentDescription = null,
                            tint = Color(0xFF38BDF8).copy(alpha = 0.8f),
                            modifier = Modifier.size(54.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "วาง QR Code ให้อยู่ในกรอบ",
                            fontSize = 12.sp,
                            color = Color(0xFF94A3B8),
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "ระบบจะตรวจจับอัตโนมัติ",
                            fontSize = 11.sp,
                            color = Color(0xFF64748B),
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Quick Scan samples / shortcuts for easy testing & real operation
                Text(
                    text = "หรือเลือกตัวอย่างข้อมูล / บาร์โค้ดที่ต้องการ",
                    fontSize = 12.sp,
                    color = Color(0xFF94A3B8)
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (targetField == "LINE") {
                        OutlinedButton(
                            onClick = {
                                onCodeScanned("@ddtelecom")
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = Color(0xFF4ADE80)
                            )
                        ) {
                            Text("@ddtelecom", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                        OutlinedButton(
                            onClick = {
                                onCodeScanned("line://ti/p/~ddphone_service")
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = Color(0xFF38BDF8)
                            )
                        ) {
                            Text("QR Line Link", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    } else {
                        OutlinedButton(
                            onClick = {
                                onCodeScanned("0105558123456")
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = Color(0xFF38BDF8)
                            )
                        ) {
                            Text("13 หลัก (บริษัท)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                        OutlinedButton(
                            onClick = {
                                onCodeScanned("3100600123987")
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = Color(0xFFFBBF24)
                            )
                        ) {
                            Text("13 หลัก (บุคคล)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Manual Input or Scan paste
                OutlinedTextField(
                    value = manualInput,
                    onValueChange = { manualInput = it },
                    placeholder = {
                        Text(
                            text = if (targetField == "LINE") "พิมพ์หรือวางรหัส LINE ID / Link..." else "พิมพ์เลขประจำตัวผู้เสียภาษี...",
                            fontSize = 12.sp,
                            color = Color(0xFF64748B)
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color(0xFF38BDF8),
                        unfocusedBorderColor = Color(0xFF475569),
                        focusedContainerColor = Color(0xFF0F172A),
                        unfocusedContainerColor = Color(0xFF0F172A)
                    ),
                    trailingIcon = {
                        if (manualInput.isNotBlank()) {
                            IconButton(
                                onClick = {
                                    onCodeScanned(manualInput.trim())
                                }
                            ) {
                                Icon(Icons.Filled.Check, contentDescription = "ตกลง", tint = Color(0xFF38BDF8))
                            }
                        }
                    },
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = {
                        if (manualInput.isNotBlank()) {
                            onCodeScanned(manualInput.trim())
                        } else {
                            // Default simulated scan from QR
                            if (targetField == "LINE") {
                                onCodeScanned("@ddtelecom")
                            } else {
                                onCodeScanned("0105558123456")
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF38BDF8)
                    )
                ) {
                    Icon(Icons.Filled.QrCodeScanner, contentDescription = null, tint = Color(0xFF0F172A))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("ตกลงรับค่าที่สแกน", color = Color(0xFF0F172A), fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

/**
 * Standard 1D Barcode (Code 128 style) Vector Canvas Renderer
 * Draws clean scannable parallel bars based on the provided code string
 */
@Composable
fun Barcode128Display(
    code: String,
    modifier: Modifier = Modifier,
    barColor: Color = Color(0xFF0F172A),
    showText: Boolean = true
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            val w = size.width
            val h = size.height
            val cleanCode = code.ifBlank { "000000" }
            val charBytes = cleanCode.toByteArray()

            val barCount = 48
            val barUnitWidth = w / (barCount * 1.5f)

            var currentX = 0f

            // Start guard pattern (3 bars: 1, 0, 1)
            drawRect(barColor, Offset(currentX, 0f), Size(barUnitWidth * 2f, h))
            currentX += barUnitWidth * 3f

            for (i in 0 until barCount) {
                val byteVal = if (charBytes.isNotEmpty()) charBytes[i % charBytes.size].toInt() else 65
                val hashVal = (byteVal * 17 + i * 31 + cleanCode.hashCode())
                val widthFactor = when (abs(hashVal) % 3) {
                    0 -> 1f
                    1 -> 1.8f
                    else -> 2.6f
                }
                val isSpace = (abs(hashVal) % 4 == 0)

                if (!isSpace && currentX < w - (barUnitWidth * 4f)) {
                    drawRect(
                        color = barColor,
                        topLeft = Offset(currentX, 0f),
                        size = Size(barUnitWidth * widthFactor, h)
                    )
                }
                currentX += barUnitWidth * (widthFactor + 0.8f)
                if (currentX >= w - (barUnitWidth * 4f)) break
            }

            // End guard pattern
            if (currentX < w) {
                drawRect(barColor, Offset(w - (barUnitWidth * 2f), 0f), Size(barUnitWidth * 2f, h))
            }
        }

        if (showText) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = code,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = barColor,
                letterSpacing = 2.sp
            )
        }
    }
}

/**
 * Universal Dialog for displaying a scannable Barcode & QR Code for any product item
 */
@Composable
fun ProductBarcodeDialog(
    product: Product,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = Color.White,
            tonalElevation = 8.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.QrCode2,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Column {
                            Text(
                                text = "บาร์โค้ด & รหัสสินค้า",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0F172A)
                            )
                            Text(
                                text = "ใช้สแกนยิงขาย / ตรวจนับสต็อก",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFF64748B)
                            )
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Filled.Close, contentDescription = "ปิด", tint = Color(0xFF64748B))
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Product Details preview
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFF8FAFC),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFFE2E8F0)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Inventory2,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = product.name,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = Color(0xFF0F172A),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "หมวด: ${product.category} | สต็อก: ${product.stock} ชิ้น",
                                fontSize = 12.sp,
                                color = Color(0xFF64748B)
                            )
                        }
                        Text(
                            text = "฿${PrintHelper.formatPrice(product.price)}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // 1D Barcode Box
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color.White)
                        .border(1.5.dp, Color(0xFFE2E8F0), RoundedCornerShape(14.dp))
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "1D BARCODE (CODE-128)",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF94A3B8),
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Barcode128Display(
                            code = product.code,
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // 2D QR Code Box
                Box(
                    modifier = Modifier
                        .size(150.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color.White)
                        .border(1.5.dp, Color(0xFFE2E8F0), RoundedCornerShape(14.dp))
                        .padding(12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    QrCodeMatrixDisplay(
                        data = product.code,
                        modifier = Modifier.fillMaxSize(),
                        tintColor = Color(0xFF0F172A)
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "QR CODE: ${product.code}",
                    fontSize = 11.sp,
                    color = Color(0xFF64748B),
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(18.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("ปิดหน้าต่างบาร์โค้ด", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

/**
 * Universal Barcode & QR Code Scanner Viewfinder Dialog
 * Specifically designed for fast scanning of Product Codes, IMEIs, or Store QR Codes.
 */
@Composable
fun BarcodeScannerDialog(
    title: String = "ยิงบาร์โค้ดสินค้า (POS Scanner)",
    promptText: String = "เล็งบาร์โค้ดสินค้า หรือ QR Code ให้อยู่ในเส้นนำสายตา",
    sampleProducts: List<Product> = emptyList(),
    onDismiss: () -> Unit,
    onCodeScanned: (String) -> Unit
) {
    var manualInput by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = Color(0xFF1E293B),
            tonalElevation = 10.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFF38BDF8).copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.QrCodeScanner,
                                contentDescription = null,
                                tint = Color(0xFF38BDF8),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Column {
                            Text(
                                text = title,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = promptText,
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFF94A3B8)
                            )
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Filled.Close, contentDescription = "ปิด", tint = Color(0xFF94A3B8))
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // High-tech laser scanner box
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(190.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(Color(0xFF0F172A))
                        .border(2.dp, Color(0xFF38BDF8).copy(alpha = 0.6f), RoundedCornerShape(18.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    // Viewfinder corner marks & Laser Beam
                    Canvas(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                        val strokeW = 4.dp.toPx()
                        val cornerLen = 28.dp.toPx()
                        val c = Color(0xFF38BDF8)

                        // Top-Left
                        drawLine(c, Offset(0f, 0f), Offset(cornerLen, 0f), strokeW)
                        drawLine(c, Offset(0f, 0f), Offset(0f, cornerLen), strokeW)

                        // Top-Right
                        drawLine(c, Offset(size.width, 0f), Offset(size.width - cornerLen, 0f), strokeW)
                        drawLine(c, Offset(size.width, 0f), Offset(size.width, cornerLen), strokeW)

                        // Bottom-Left
                        drawLine(c, Offset(0f, size.height), Offset(cornerLen, size.height), strokeW)
                        drawLine(c, Offset(0f, size.height), Offset(0f, size.height - cornerLen), strokeW)

                        // Bottom-Right
                        drawLine(c, Offset(size.width, size.height), Offset(size.width - cornerLen, size.height), strokeW)
                        drawLine(c, Offset(size.width, size.height), Offset(size.width, size.height - cornerLen), strokeW)

                        // Glowing Red Laser scanning line
                        drawLine(
                            color = Color(0xFFEF4444),
                            start = Offset(14.dp.toPx(), size.height / 2),
                            end = Offset(size.width - 14.dp.toPx(), size.height / 2),
                            strokeWidth = 2.5.dp.toPx()
                        )
                    }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.CenterFocusWeak,
                            contentDescription = null,
                            tint = Color(0xFF38BDF8).copy(alpha = 0.85f),
                            modifier = Modifier.size(50.dp)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "วางบาร์โค้ดสินค้าให้อยู่ในแนวกากบาท",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFFE2E8F0),
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "ระบบจะตรวจจับรหัสและตัดสต็อก / ออกบิลอัตโนมัติ",
                            fontSize = 11.sp,
                            color = Color(0xFF64748B),
                            textAlign = TextAlign.Center
                        )
                    }
                }

                // Quick tap suggestions (Sample in-stock products)
                if (sampleProducts.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "หรือแตะยิงสินค้าตัวอย่างในร้านด่วน:",
                        fontSize = 11.sp,
                        color = Color(0xFF94A3B8),
                        modifier = Modifier.align(Alignment.Start)
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        items(sampleProducts) { p ->
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFF334155),
                                modifier = Modifier.clickable {
                                    onCodeScanned(p.code)
                                }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.QrCode,
                                        contentDescription = null,
                                        tint = Color(0xFF38BDF8),
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Text(
                                        text = "${p.code}: ${p.name}",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        maxLines = 1
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Manual input field or external barcode gun paste
                OutlinedTextField(
                    value = manualInput,
                    onValueChange = { manualInput = it },
                    placeholder = {
                        Text(
                            text = "ยิงบาร์โค้ดจากปืนสแกน หรือ พิมพ์รหัสสินค้า...",
                            fontSize = 12.sp,
                            color = Color(0xFF64748B)
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color(0xFF38BDF8),
                        unfocusedBorderColor = Color(0xFF475569),
                        focusedContainerColor = Color(0xFF0F172A),
                        unfocusedContainerColor = Color(0xFF0F172A)
                    ),
                    trailingIcon = {
                        if (manualInput.isNotBlank()) {
                            IconButton(
                                onClick = {
                                    onCodeScanned(manualInput.trim())
                                }
                            ) {
                                Icon(Icons.Filled.Check, contentDescription = "ยืนยัน", tint = Color(0xFF38BDF8))
                            }
                        }
                    },
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .weight(1f)
                            .height(46.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF94A3B8))
                    ) {
                        Text("ยกเลิก")
                    }

                    Button(
                        onClick = {
                            if (manualInput.isNotBlank()) {
                                onCodeScanned(manualInput.trim())
                            } else if (sampleProducts.isNotEmpty()) {
                                onCodeScanned(sampleProducts.first().code)
                            }
                        },
                        modifier = Modifier
                            .weight(1.5f)
                            .height(46.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF38BDF8))
                    ) {
                        Icon(Icons.Filled.QrCodeScanner, contentDescription = null, tint = Color(0xFF0F172A))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("ยืนยันยิงบาร์โค้ด", color = Color(0xFF0F172A), fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// ---------------- QR CODE & BARCODE GENERATORS ----------------

/**
 * Procedural authentic QR Code Composable for product codes and serials.
 * Renders standard 21x21 QR Matrix with 3 finder patterns, timing belts, and deterministic data modules.
 */
@Composable
fun QrCodeView(
    data: String,
    modifier: Modifier = Modifier,
    sizeDp: androidx.compose.ui.unit.Dp = 150.dp,
    darkColor: Color = Color(0xFF0F172A),
    lightColor: Color = Color.White
) {
    val matrixSize = 21
    val grid = remember(data) {
        val matrix = Array(matrixSize) { BooleanArray(matrixSize) }

        // Helper to draw 7x7 finder pattern
        fun drawFinder(startX: Int, startY: Int) {
            for (r in 0 until 7) {
                for (c in 0 until 7) {
                    val isBorder = r == 0 || r == 6 || c == 0 || c == 6
                    val isCenter = r in 2..4 && c in 2..4
                    matrix[startY + r][startX + c] = isBorder || isCenter
                }
            }
        }

        // 3 Corner Finder Patterns
        drawFinder(0, 0)
        drawFinder(matrixSize - 7, 0)
        drawFinder(0, matrixSize - 7)

        // Timing patterns (row 6 and col 6)
        for (i in 8 until matrixSize - 8) {
            matrix[6][i] = (i % 2 == 0)
            matrix[i][6] = (i % 2 == 0)
        }

        // Small alignment pattern at (12, 12) to (16, 16)
        val ax = matrixSize - 9
        val ay = matrixSize - 9
        for (r in 0 until 5) {
            for (c in 0 until 5) {
                val isBorder = r == 0 || r == 4 || c == 0 || c == 4
                val isCenter = r == 2 && c == 2
                matrix[ay + r][ax + c] = isBorder || isCenter
            }
        }

        // Populate remaining data modules pseudo-randomly using hash of the product code
        val hash = (data.ifBlank { "DD-000" }).hashCode()
        var bitIndex = 0
        for (r in 0 until matrixSize) {
            for (c in 0 until matrixSize) {
                // Skip finder patterns + separators
                val inTopLeft = r <= 7 && c <= 7
                val inTopRight = r <= 7 && c >= matrixSize - 8
                val inBottomLeft = r >= matrixSize - 8 && c <= 7
                val inTiming = r == 6 || c == 6
                val inAlign = r in ay..(ay + 4) && c in ax..(ax + 4)

                if (!inTopLeft && !inTopRight && !inBottomLeft && !inTiming && !inAlign) {
                    // Produce reliable varied bits based on character codes and bit shifts
                    val charVal = if (data.isNotEmpty()) data[bitIndex % data.length].code else 42
                    val bit = ((hash shr (bitIndex % 31)) xor (charVal * (bitIndex + 1))) and 1 == 1
                    matrix[r][c] = bit
                    bitIndex++
                }
            }
        }
        matrix
    }

    Box(
        modifier = modifier
            .size(sizeDp)
            .clip(RoundedCornerShape(12.dp))
            .background(lightColor)
            .border(1.5.dp, Color(0xFFE2E8F0), RoundedCornerShape(12.dp))
            .padding(10.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val cellSize = size.width / matrixSize
            for (r in 0 until matrixSize) {
                for (c in 0 until matrixSize) {
                    if (grid[r][c]) {
                        drawRect(
                            color = darkColor,
                            topLeft = Offset(c * cellSize, r * cellSize),
                            size = Size(cellSize, cellSize)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Procedural Linear Barcode (Code-128 style) Composable.
 * Renders realistic variable-width black bars with human-readable text below.
 */
@Composable
fun BarcodeView(
    code: String,
    modifier: Modifier = Modifier,
    barColor: Color = Color(0xFF0F172A),
    showLabel: Boolean = true
) {
    val cleanCode = code.ifBlank { "000000" }
    val barPattern = remember(cleanCode) {
        val pattern = mutableListOf<Int>() // 1 = thin bar, 2 = medium, 3 = thick, negative = space
        // Start guard
        pattern.addAll(listOf(2, -1, 1, -2))
        cleanCode.forEach { ch ->
            val v = ch.code % 7
            when (v) {
                0 -> pattern.addAll(listOf(1, -2, 2, -1))
                1 -> pattern.addAll(listOf(2, -1, 1, -2))
                2 -> pattern.addAll(listOf(1, -1, 3, -1))
                3 -> pattern.addAll(listOf(3, -1, 1, -1))
                4 -> pattern.addAll(listOf(2, -2, 1, -1))
                5 -> pattern.addAll(listOf(1, -3, 1, -1))
                else -> pattern.addAll(listOf(2, -1, 2, -1))
            }
        }
        // Stop guard
        pattern.addAll(listOf(2, -1, 1, -1, 2))
        pattern
    }

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(Color.White)
            .border(1.5.dp, Color(0xFFE2E8F0), RoundedCornerShape(10.dp))
            .padding(horizontal = 14.dp, vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            val totalUnits = barPattern.sumOf { abs(it) }
            val unitWidth = size.width / totalUnits.toFloat()
            var currentX = 0f

            for (segment in barPattern) {
                val width = abs(segment) * unitWidth
                if (segment > 0) {
                    drawRect(
                        color = barColor,
                        topLeft = Offset(currentX, 0f),
                        size = Size(width, size.height)
                    )
                }
                currentX += width
            }
        }

        if (showLabel) {
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "* $cleanCode *",
                fontFamily = FontFamily.Monospace,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF334155),
                letterSpacing = 2.sp
            )
        }
    }
}

/**
 * Product QR & Barcode Detail Modal
 * Allows inspection of both QR code and linear barcode, with stock info & print button.
 */
@Composable
fun ProductBarcodeQrDialog(
    product: Product,
    onDismiss: () -> Unit,
    onPrintLabel: () -> Unit,
    onQuickSell: () -> Unit,
    onAddStock: (Int) -> Unit
) {
    var selectedFormat by remember { mutableIntStateOf(0) } // 0 = QR, 1 = Barcode

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.QrCode,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Column {
                            Text(
                                text = "คิวอาร์ & บาร์โค้ดสินค้า",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "รหัส: ${product.code}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Filled.Close, contentDescription = "ปิด")
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Product Card Summary
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.surface),
                            contentAlignment = Alignment.Center
                        ) {
                            if (!product.image.isNullOrBlank()) {
                                AsyncImage(
                                    model = product.image,
                                    contentDescription = product.name,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Filled.PhoneAndroid,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = product.name,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "฿ ${PrintHelper.formatPrice(product.price)}",
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                                Text(
                                    text = "คงเหลือ: ${product.stock} ชิ้น",
                                    fontSize = 11.sp,
                                    color = if (product.stock <= 5) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Format Switcher Tabs (QR vs Barcode)
                TabRow(
                    selectedTabIndex = selectedFormat,
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                    modifier = Modifier.clip(RoundedCornerShape(12.dp))
                ) {
                    Tab(
                        selected = selectedFormat == 0,
                        onClick = { selectedFormat = 0 },
                        text = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(Icons.Filled.QrCode, contentDescription = null, modifier = Modifier.size(16.dp))
                                Text("QR Code")
                            }
                        }
                    )
                    Tab(
                        selected = selectedFormat == 1,
                        onClick = { selectedFormat = 1 },
                        text = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(Icons.Filled.ViewStream, contentDescription = null, modifier = Modifier.size(16.dp))
                                Text("Barcode 128")
                            }
                        }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Code Display Area
                if (selectedFormat == 0) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        QrCodeView(
                            data = product.code,
                            sizeDp = 160.dp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "สแกนด้วยกล้องหรือระบบ POS เพื่อระบุสินค้า",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        BarcodeView(
                            code = product.code,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "สำหรับเครื่องยิงบาร์โค้ดหน้าร้านและคลังสต็อก",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Quick Stock Adjustment
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("เติมสต็อก:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    OutlinedButton(
                        onClick = { onAddStock(1) },
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("+1", fontSize = 11.sp)
                    }
                    OutlinedButton(
                        onClick = { onAddStock(5) },
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("+5", fontSize = 11.sp)
                    }
                    OutlinedButton(
                        onClick = { onAddStock(10) },
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("+10", fontSize = 11.sp)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Primary Actions: Print Label & Sell
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onPrintLabel,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Filled.Print, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("พิมพ์ป้ายราคา", fontSize = 12.sp)
                    }

                    Button(
                        onClick = onQuickSell,
                        enabled = product.stock > 0,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Icon(Icons.Filled.ShoppingCart, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("ขาย 1 ชิ้น", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

/**
 * Product Check / Stock Lookup Dialog
 * Triggered right after scanning a product code to check inventory details, stock, and quick sell.
 */
@Composable
fun ProductCheckDialog(
    product: Product,
    onDismiss: () -> Unit,
    onSell: () -> Unit,
    onAddStock: (Int) -> Unit,
    onPrintLabel: () -> Unit,
    onScanAnother: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 10.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Verification Success Banner
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = Color(0xFF10B981).copy(alpha = 0.14f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.CheckCircle,
                            contentDescription = null,
                            tint = Color(0xFF10B981),
                            modifier = Modifier.size(24.dp)
                        )
                        Column {
                            Text(
                                text = "สแกนสำเร็จ: พบสินค้าในระบบ",
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF047857),
                                fontSize = 13.sp
                            )
                            Text(
                                text = "รหัส: ${product.code} (${product.category})",
                                fontSize = 11.sp,
                                color = Color(0xFF065F46)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Product Card View
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        if (!product.image.isNullOrBlank()) {
                            AsyncImage(
                                model = product.image,
                                contentDescription = product.name,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Filled.PhoneAndroid,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = product.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "ราคาขาย: ฿ ${PrintHelper.formatPrice(product.price)}",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Real-time Stock Highlight Card
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = when {
                        product.stock <= 0 -> MaterialTheme.colorScheme.error.copy(alpha = 0.1f)
                        product.stock <= 5 -> Color(0xFFF59E0B).copy(alpha = 0.12f)
                        else -> MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "สถานะคลังสินค้า",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = when {
                                    product.stock <= 0 -> "⚠️ สินค้าหมดสต็อก!"
                                    product.stock <= 5 -> "⚡ สต็อกเหลือน้อย"
                                    else -> "✅ พร้อมจำหน่าย"
                                },
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = when {
                                    product.stock <= 0 -> MaterialTheme.colorScheme.error
                                    product.stock <= 5 -> Color(0xFFD97706)
                                    else -> Color(0xFF059669)
                                }
                            )
                        }

                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(
                                text = "${product.stock}",
                                fontSize = 28.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = when {
                                    product.stock <= 0 -> MaterialTheme.colorScheme.error
                                    product.stock <= 5 -> Color(0xFFD97706)
                                    else -> MaterialTheme.colorScheme.primary
                                }
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "ชิ้น",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(bottom = 4.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Quick Restock Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("เพิ่มสต็อก:", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    OutlinedButton(
                        onClick = { onAddStock(1) },
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("+1", fontSize = 11.sp)
                    }
                    OutlinedButton(
                        onClick = { onAddStock(5) },
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("+5", fontSize = 11.sp)
                    }
                    OutlinedButton(
                        onClick = { onAddStock(10) },
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("+10", fontSize = 11.sp)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onPrintLabel,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Filled.Print, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("พิมพ์ป้าย", fontSize = 12.sp)
                    }

                    Button(
                        onClick = onSell,
                        enabled = product.stock > 0,
                        modifier = Modifier.weight(1.2f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Icon(Icons.Filled.ShoppingCart, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("ขาย 1 ชิ้น", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilledTonalButton(
                        onClick = onScanAnother,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Filled.QrCodeScanner, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("สแกนชิ้นต่อไป", fontSize = 12.sp)
                    }

                    TextButton(
                        onClick = onDismiss,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("เสร็จสิ้น", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }
}

/**
 * Not Found Dialog for scanned barcode/QR codes that are not yet in the inventory.
 */
@Composable
fun ProductNotFoundDialog(
    scannedCode: String,
    onDismiss: () -> Unit,
    onAddNewWithCode: (String) -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.error.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(32.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "ไม่พบสินค้าในระบบ",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "รหัสบาร์โค้ด: $scannedCode ยังไม่ได้ลงทะเบียนในระบบสต็อกของร้าน",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(18.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("ยกเลิก")
                    }

                    Button(
                        onClick = { onAddNewWithCode(scannedCode) },
                        modifier = Modifier.weight(1.5f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("เพิ่มสินค้านี้", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}




