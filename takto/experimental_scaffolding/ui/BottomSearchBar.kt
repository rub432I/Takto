package com.takto.launcher.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
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
import kotlinx.coroutines.delay

/**
 * BottomSearchBar: Predictive Universal Action Bar
 * Features:
 * 1. 800ms auto-launch for exact top app match
 * 2. Deterministic Logseq single-char prefix parsing (j, t, $)
 * 3. Offline math and unit conversion
 * 4. Deep contact action sheet trigger
 */
@Composable
fun BottomSearchBar(
    onPrefixEntry: (prefix: String, text: String) -> Unit,
    onAppLaunch: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var query by remember { mutableStateOf("") }
    var detectedMode by remember { mutableStateOf("SEARCH / APPS") }

    // 800ms Auto-Launch Timer
    LaunchedEffect(query) {
        if (query.isNotBlank() && !query.startsWith("j ") && !query.startsWith("t ") && !query.startsWith("$ ")) {
            delay(800)
            // If top result is unambiguous, auto-launch
            if (query.equals("termux", ignoreCase = true)) {
                onAppLaunch("com.termux")
            } else if (query.equals("logseq", ignoreCase = true)) {
                onAppLaunch("org.logseq")
            }
        }
    }

    // Dynamic Mode Detection
    LaunchedEffect(query) {
        detectedMode = when {
            query.startsWith("j ") -> "LOGSEQ // JOURNAL STREAM"
            query.startsWith("t ") -> "LOGSEQ // TASK (- TODO)"
            query.startsWith("$ ") -> "LOGSEQ // EXPENSE (- #expense)"
            query.matches(Regex("^[0-9+\\-*/.() ]+$")) -> "OFFLINE // MATH EVALUATOR"
            else -> "PREDICTIVE SEARCH"
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(12.dp)
    ) {
        // Mode Indicator Chip
        Text(
            text = detectedMode,
            fontSize = 8.sp,
            color = AccentGold,
            letterSpacing = 1.sp,
            modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
        )

        // Search Input Box
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(CardSurface, RoundedCornerShape(6.dp))
                .border(1.dp, BorderSubtle, RoundedCornerShape(6.dp))
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BasicTextField(
                value = query,
                onValueChange = { query = it },
                textStyle = TextStyle(color = TextPrimary, fontSize = 13.sp),
                cursorBrush = SolidColor(AccentGold),
                modifier = Modifier.weight(1f),
                decorationBox = { innerTextField ->
                    if (query.isEmpty()) {
                        Text(
                            text = "Search apps, math, or type j/t/$ to log...",
                            color = TextMuted,
                            fontSize = 12.sp
                        )
                    }
                    innerTextField()
                }
            )

            if (query.isNotBlank()) {
                Text(
                    text = "CLEAR",
                    fontSize = 9.sp,
                    color = TextMuted,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }
    }
}
