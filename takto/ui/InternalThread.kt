package com.takto.launcher.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class ThreadMessage(
    val id: Long,
    val text: String,
    val type: String, // "JOURNAL", "TASK", "EXPENSE"
    val time: String
)

/**
 * InternalThread: Center Page Capture Canvas
 * Reverse-scrolling SMS-style self-chat. Solves classification paralysis
 * by allowing continuous capture straight into today's stream.
 */
@Composable
fun InternalThread(
    onSend: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var inputText by remember { mutableStateOf("") }
    val messages = remember {
        mutableStateListOf(
            ThreadMessage(1, "Launched Takto // OS for Life", "JOURNAL", "09:12"),
            ThreadMessage(2, "- TODO Build QuakeConsole pull-down", "TASK", "10:30"),
            ThreadMessage(3, "- #expense 8.50 Coffee and croissant", "EXPENSE", "11:45")
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(OledBlack)
            .padding(16.dp)
    ) {
        // Stream Header
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "INTERNAL THREAD",
                fontSize = 12.sp,
                letterSpacing = 2.sp,
                color = AccentGold
            )
            Text(
                text = "TODAY'S STREAM",
                fontSize = 9.sp,
                color = TextMuted
            )
        }

        // Reverse-scrolling Message List
        LazyColumn(
            modifier = Modifier.weight(1f).fillMaxWidth(),
            reverseLayout = true,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(messages.reversed(), key = { it.id }) { msg ->
                SelfChatBubble(msg)
            }
        }

        // Bottom Capture Input
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp)
                .background(CardSurface, RoundedCornerShape(6.dp))
                .border(1.dp, BorderSubtle, RoundedCornerShape(6.dp))
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BasicTextField(
                value = inputText,
                onValueChange = { inputText = it },
                textStyle = TextStyle(color = TextPrimary, fontSize = 12.sp),
                cursorBrush = SolidColor(AccentGold),
                modifier = Modifier.weight(1f),
                decorationBox = { innerTextField ->
                    if (inputText.isEmpty()) {
                        Text(
                            text = "Log thought (j), task (t), or expense ($)...",
                            color = TextMuted,
                            fontSize = 12.sp
                        )
                    }
                    innerTextField()
                }
            )

            Text(
                text = "SEND",
                fontSize = 10.sp,
                color = if (inputText.isNotBlank()) AccentGold else TextMuted,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    }
}

@Composable
fun SelfChatBubble(msg: ThreadMessage) {
    val borderColor = when (msg.type) {
        "TASK" -> AccentGold
        "EXPENSE" -> AccentAmber
        else -> BorderSubtle
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(CardSurface, RoundedCornerShape(4.dp))
            .border(1.dp, borderColor, RoundedCornerShape(4.dp))
            .padding(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = msg.type, fontSize = 8.sp, color = AccentAmber, letterSpacing = 1.sp)
            Text(text = msg.time, fontSize = 8.sp, color = TextMuted)
        }
        Text(
            text = msg.text,
            fontSize = 12.sp,
            color = TextPrimary,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}
