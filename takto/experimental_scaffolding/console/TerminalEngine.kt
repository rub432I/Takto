package com.takto.launcher.console

import java.io.BufferedReader
import java.io.InputStreamReader

enum class PrivilegeTier {
    ROOT_LIBSU,      // UID 0: Full hardware / AOSP HAL access
    SHIZUKU_ADB,     // UID 2000: Android framework privileged permissions
    SHELL_FALLBACK   // App UID: Standard unprivileged sandbox
}

/**
 * TerminalEngine: Three-tier escalating privilege router
 * Dynamically probes:
 * 1. libsu (Root / UID 0)
 * 2. Shizuku (ADB / UID 2000)
 * 3. Standard Shell fallback
 */
class TerminalEngine {

    fun detectActiveTier(): PrivilegeTier {
        // Probe Root
        try {
            val process = Runtime.getRuntime().exec(arrayOf("su", "-c", "id"))
            val output = BufferedReader(InputStreamReader(process.inputStream)).readLine() ?: ""
            if (output.contains("uid=0")) {
                return PrivilegeTier.ROOT_LIBSU
            }
        } catch (_: Exception) {}

        // Probe Shizuku / ADB
        try {
            val process = Runtime.getRuntime().exec(arrayOf("shizuku", "id"))
            if (process.waitFor() == 0) {
                return PrivilegeTier.SHIZUKU_ADB
            }
        } catch (_: Exception) {}

        return PrivilegeTier.SHELL_FALLBACK
    }

    fun executeCommand(command: String): String {
        val tier = detectActiveTier()
        return try {
            val process = when (tier) {
                PrivilegeTier.ROOT_LIBSU -> Runtime.getRuntime().exec(arrayOf("su", "-c", command))
                PrivilegeTier.SHIZUKU_ADB -> Runtime.getRuntime().exec(arrayOf("shizuku", "sh", "-c", command))
                PrivilegeTier.SHELL_FALLBACK -> Runtime.getRuntime().exec(arrayOf("sh", "-c", command))
            }
            val reader = BufferedReader(InputStreamReader(process.inputStream))
            val output = StringBuilder()
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                output.appendLine(line)
            }
            process.waitFor()
            output.toString().trim()
        } catch (e: Exception) {
            "[ERROR] Execution failed: ${e.message}"
        }
    }
}
