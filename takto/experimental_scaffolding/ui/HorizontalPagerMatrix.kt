package com.takto.launcher.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * Three-Page HorizontalPager Matrix:
 * Page 0 (Left): Windows Phone-inspired Metro Widget Dashboard
 * Page 1 (Center): "Internal Thread" reverse-scrolling SMS-style self-chat capture canvas
 * Page 2 (Right): Isolated Unified Communication Hub
 */
@Composable
fun HorizontalPagerMatrix(
    onTaskToggle: (Long, Boolean) -> Unit,
    onQuickLog: (String) -> Unit,
    onNotificationClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    // Initial page = 1 (Center: Internal Thread)
    val pagerState = rememberPagerState(initialPage = 1, pageCount = { 3 })

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(OledBlack)
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            when (page) {
                0 -> MetroDashboard(onTaskToggle = onTaskToggle)
                1 -> InternalThread(onSend = onQuickLog)
                2 -> UnifiedCommHub(onItemClick = onNotificationClick)
            }
        }
    }
}
