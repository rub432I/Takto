package com.takto.launcher.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * MetroDashboard: Windows Phone-inspired 4-column live tile grid
 * Housing:
 * 1. Local Clock & Date (2x2)
 * 2. Hydration Tracker (1x1)
 * 3. Offline Pass Wallet (1x1)
 * 4. CameraX/ZXing QR Scanner (2x1)
 * 5. Native AppWidgetHost Bridge (2x1)
 * 6. 4x2 LogseqTaskTile: Active Markdown TODO items
 */
@Composable
fun MetroDashboard(
    onTaskToggle: (Long, Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    var waterCount by remember { mutableStateOf(4) }
    val timeFormat = remember { SimpleDateFormat("HH:mm", Locale.US) }
    val dateFormat = remember { SimpleDateFormat("EEE, MMM dd", Locale.US) }

    LazyVerticalGrid(
        columns = GridCells.Fixed(4),
        modifier = modifier
            .fillMaxSize()
            .padding(12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Tile 1: Clock & Date (Span 2x2)
        item(span = { GridItemSpan(2) }) {
            MetroTile(
                modifier = Modifier.aspectRatio(1f),
                title = "TIME"
            ) {
                Column(
                    modifier = Modifier.fillMaxSize().padding(12.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = timeFormat.format(Date()),
                        fontSize = 34.sp,
                        fontWeight = FontWeight.Bold,
                        color = AccentGold
                    )
                    Text(
                        text = dateFormat.format(Date()),
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                }
            }
        }

        // Tile 2: Hydration Tracker (Span 1x1)
        item(span = { GridItemSpan(1) }) {
            MetroTile(
                modifier = Modifier.aspectRatio(1f).clickable { waterCount++ },
                title = "WATER"
            ) {
                Column(
                    modifier = Modifier.fillMaxSize().padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(text = "$waterCount/8", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Text(text = "cups", fontSize = 9.sp, color = TextSecondary)
                }
            }
        }

        // Tile 3: Offline Pass Wallet (Span 1x1)
        item(span = { GridItemSpan(1) }) {
            MetroTile(
                modifier = Modifier.aspectRatio(1f),
                title = "WALLET"
            ) {
                Column(
                    modifier = Modifier.fillMaxSize().padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(text = "PASSES", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = AccentAmber)
                    Text(text = "Offline", fontSize = 9.sp, color = TextMuted)
                }
            }
        }

        // Tile 4: CameraX / ZXing QR Scanner (Span 2x1)
        item(span = { GridItemSpan(2) }) {
            MetroTile(
                modifier = Modifier.height(72.dp),
                title = "QR SCANNER"
            ) {
                Row(
                    modifier = Modifier.fillMaxSize().padding(horizontal = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Instant Lens", fontSize = 12.sp, color = TextPrimary)
                    Text(text = "[ SCAN ]", fontSize = 10.sp, color = AccentGold, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Tile 5: Native AppWidgetHost Bridge (Span 2x1)
        item(span = { GridItemSpan(2) }) {
            MetroTile(
                modifier = Modifier.height(72.dp),
                title = "WIDGET BRIDGE"
            ) {
                Box(
                    modifier = Modifier.fillMaxSize().padding(horizontal = 12.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Text(text = "Android AppWidgetHost", fontSize = 11.sp, color = TextSecondary)
                }
            }
        }

        // Tile 6: 4x2 LogseqTaskTile (Span 4 columns!)
        item(span = { GridItemSpan(4) }) {
            MetroTile(
                modifier = Modifier.fillMaxWidth().height(160.dp),
                title = "4x2 LOGSEQ ACTIVE TASKS"
            ) {
                Column(
                    modifier = Modifier.fillMaxSize().padding(12.dp)
                ) {
                    Text(
                        text = "Parsed directly from today's journal",
                        fontSize = 10.sp,
                        color = TextMuted,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                    // Sample active items
                    TaskRow(title = "Implement QuakeConsole terminal overlay", isDone = false)
                    TaskRow(title = "Dual-write Room SQLite cache", isDone = true)
                    TaskRow(title = "Purge telemetry & ad SDKs", isDone = true)
                }
            }
        }
    }
}

@Composable
fun TaskRow(title: String, isDone: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = if (isDone) "[X]" else "[ ]",
            color = if (isDone) TextMuted else AccentGold,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(end = 8.dp)
        )
        Text(
            text = title,
            color = if (isDone) TextMuted else TextPrimary,
            fontSize = 11.sp
        )
    }
}

@Composable
fun MetroTile(
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .background(CardSurface, RoundedCornerShape(4.dp))
            .border(1.dp, BorderSubtle, RoundedCornerShape(4.dp))
    ) {
        content()
        Text(
            text = title,
            fontSize = 8.sp,
            fontWeight = FontWeight.Bold,
            color = TextMuted,
            letterSpacing = 0.1.sp,
            modifier = Modifier.align(Alignment.TopEnd).padding(6.dp)
        )
    }
}
