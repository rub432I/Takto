package com.takto.launcher.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class NormalizedNotification(
    val id: String,
    val senderName: String,
    val appSource: String, // "Signal", "WhatsApp", "SMS", "Telegram"
    val previewText: String,
    val timeReceived: String
)

/**
 * UnifiedCommHub: Isolated Communication Hub on Page 2 (Right)
 * Employs offline NotificationListenerService and normalized contact identities.
 * Separates external social stress from personal capture.
 */
@Composable
fun UnifiedCommHub(
    onItemClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val sampleNotifications = listOf(
        NormalizedNotification("1", "Alice Vance", "Signal", "Sent you the cryptographic verification hash.", "11:42"),
        NormalizedNotification("2", "David Chen", "SMS", "Meeting moved to 14:00.", "11:15"),
        NormalizedNotification("3", "Server Alert", "Telegram", "Backup snapshot completed successfully.", "09:00")
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(OledBlack)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "COMMUNICATION HUB",
                fontSize = 12.sp,
                letterSpacing = 2.sp,
                color = AccentGold
            )
            Text(
                text = "ISOLATED // ZERO CLOUD",
                fontSize = 9.sp,
                color = TextMuted
            )
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(sampleNotifications, key = { it.id }) { item ->
                CommNotificationCard(item = item, onClick = { onItemClick(item.id) })
            }
        }
    }
}

@Composable
fun CommNotificationCard(
    item: NormalizedNotification,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(CardSurface, RoundedCornerShape(4.dp))
            .border(1.dp, BorderSubtle, RoundedCornerShape(4.dp))
            .clickable { onClick() }
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = item.senderName, fontSize = 12.sp, color = TextPrimary)
            Text(text = "[${item.appSource}] ${item.timeReceived}", fontSize = 9.sp, color = AccentAmber)
        }
        Text(
            text = item.previewText,
            fontSize = 11.sp,
            color = TextSecondary,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}
