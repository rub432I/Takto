package com.takto.launcher.console

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.takto.launcher.ui.AccentGold
import com.takto.launcher.ui.BorderSubtle
import com.takto.launcher.ui.TextMuted
import com.takto.launcher.ui.TextPrimary

/**
 * QuakeConsole: Translucent pull-down terminal overlay
 * Triggered by a two-finger swipe down gesture from anywhere on the screen.
 * Wired directly to TerminalEngine and bidirectional takto-cli.
 */
@Composable
fun QuakeConsole(
    isVisible: Boolean,
    terminalEngine: TerminalEngine,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    var inputCommand by remember { mutableStateOf("") }
    val consoleLog = remember {
        mutableStateListOf(
            "TAKTO QUAKE TERMINAL v1.0 [ESC/Swipe Up to Close]",
            "Type 'status', 'tasks', or any shell/root command:"
        )
    }

    AnimatedVisibility(
        visible = isVisible,
        enter = slideInVertically(initialOffsetY = { -it }),
        exit = slideOutVertically(targetOffsetY = { -it }),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.65f)
                .background(Color(0xEE05070A)) // Translucent dark terminal
                .border(1.dp, BorderSubtle)
                .padding(14.dp)
        ) {
            // Header Bar
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "QUAKE CONSOLE // ${terminalEngine.detectActiveTier()}",
                    fontSize = 10.sp,
                    color = AccentGold,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "[X]",
                    fontSize = 11.sp,
                    color = TextMuted,
                    modifier = Modifier.padding(4.dp)
                )
            }

            // Terminal Logs
            LazyColumn(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(consoleLog) { line ->
                    Text(
                        text = line,
                        fontSize = 11.sp,
                        color = if (line.startsWith("[ERROR]")) Color(0xFFFF5555) else TextPrimary,
                        lineHeight = 15.sp
                    )
                }
            }

            // Command Prompt
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "takto# ",
                    fontSize = 12.sp,
                    color = AccentGold
                )
                BasicTextField(
                    value = inputCommand,
                    onValueChange = { inputCommand = it },
                    textStyle = TextStyle(color = TextPrimary, fontSize = 12.sp),
                    cursorBrush = SolidColor(AccentGold),
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}
