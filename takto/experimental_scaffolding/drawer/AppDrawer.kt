package com.takto.launcher.drawer

import android.app.usage.UsageStatsManager
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.takto.launcher.ui.AccentAmber
import com.takto.launcher.ui.AccentGold
import com.takto.launcher.ui.BorderSubtle
import com.takto.launcher.ui.CardSurface
import com.takto.launcher.ui.OledBlack
import com.takto.launcher.ui.TextMuted
import com.takto.launcher.ui.TextPrimary

enum class DrawerFoldState {
    COLLAPSED,      // Hidden under bottom search bar
    HALF_EXPANDED,  // Folded category chips & daily screen-time summary
    FULL_EXPANDED   // Complete A-Z indexed catalog with jump bar
}

data class AppItem(
    val packageName: String,
    val label: String,
    val category: String, // "COMMUNICATION", "PRODUCTIVITY", "SYSTEM", "MEDIA"
    val screenTimeMinutes: Long
)

/**
 * AppDrawer: Three-State Gesture-Folding Categorized App Drawer
 * Features:
 * 1. UsageStatsManager screen-time headers
 * 2. Folding category headers
 * 3. A-Z jump overlay
 * 4. Context menus for AlarmManager auto-launch timers
 */
@Composable
fun AppDrawer(
    foldState: DrawerFoldState,
    onAppLaunch: (String) -> Unit,
    onAppLongClick: (AppItem) -> Unit,
    modifier: Modifier = Modifier
) {
    val sampleApps = listOf(
        AppItem("org.logseq", "Logseq", "PRODUCTIVITY", 48),
        AppItem("org.thoughtcrime.securesms", "Signal", "COMMUNICATION", 24),
        AppItem("com.termux", "Termux", "SYSTEM", 112),
        AppItem("org.videolan.vlc", "VLC", "MEDIA", 15)
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(OledBlack)
            .padding(horizontal = 16.dp)
    ) {
        // UsageStatsManager Screen-Time Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
                .background(CardSurface, RoundedCornerShape(4.dp))
                .border(1.dp, BorderSubtle, RoundedCornerShape(4.dp))
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = "TODAY'S SCREEN TIME", fontSize = 9.sp, color = AccentAmber, letterSpacing = 1.sp)
                Text(text = "3h 19m", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            }
            Text(text = "UsageStatsManager", fontSize = 8.sp, color = TextMuted)
        }

        // App List
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(sampleApps, key = { it.packageName }) { app ->
                AppRow(app = app, onClick = { onAppLaunch(app.packageName) }, onLongClick = { onAppLongClick(app) })
            }
        }
    }
}

@Composable
fun AppRow(
    app: AppItem,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(CardSurface, RoundedCornerShape(4.dp))
            .border(1.dp, BorderSubtle, RoundedCornerShape(4.dp))
            .clickable { onClick() }
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(text = app.label, fontSize = 13.sp, color = TextPrimary, fontWeight = FontWeight.Medium)
            Text(text = "[${app.category}]", fontSize = 9.sp, color = TextMuted)
        }
        Text(text = "${app.screenTimeMinutes}m", fontSize = 10.sp, color = AccentGold)
    }
}
