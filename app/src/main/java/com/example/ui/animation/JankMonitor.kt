package com.example.ui.animation

import androidx.compose.runtime.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class PerformanceMetrics(
    val fps: Int = 60,
    val frameTimeMs: Float = 16.6f,
    val droppedFrames: Long = 0L,
    val refreshRateHz: Int = 60,
    val isJanky: Boolean = false
)

/**
 * Real-time Jank & FPS Monitor for Dika Remote Developer Mode.
 * Tracks display refresh rate and frame delivery latencies without artificial delays.
 */
object JankMonitor {
    private val _metrics = MutableStateFlow(PerformanceMetrics())
    val metrics: StateFlow<PerformanceMetrics> = _metrics.asStateFlow()

    private var frameCount = 0
    private var lastFpsTimestamp = 0L
    private var lastFrameTimeNanos = 0L
    private var totalDroppedFrames = 0L

    fun recordFrame(frameNanos: Long) {
        if (lastFrameTimeNanos == 0L) {
            lastFrameTimeNanos = frameNanos
            lastFpsTimestamp = frameNanos
            return
        }

        val frameDeltaNanos = frameNanos - lastFrameTimeNanos
        lastFrameTimeNanos = frameNanos
        val frameTimeMs = frameDeltaNanos / 1_000_000f

        frameCount++

        // Expected frame duration for 60Hz (~16.6ms), 90Hz (~11.1ms), 120Hz (~8.3ms)
        val targetFrameTimeMs = 16.66f
        if (frameTimeMs > targetFrameTimeMs * 1.5f) {
            val missed = ((frameTimeMs - targetFrameTimeMs) / targetFrameTimeMs).toLong().coerceAtLeast(1)
            totalDroppedFrames += missed
        }

        val timeSinceLastFps = frameNanos - lastFpsTimestamp
        if (timeSinceLastFps >= 1_000_000_000L) { // 1 second interval
            val currentFps = (frameCount * 1_000_000_000L / timeSinceLastFps).toInt()
            val isJanky = currentFps < 50

            _metrics.value = PerformanceMetrics(
                fps = currentFps,
                frameTimeMs = frameTimeMs,
                droppedFrames = totalDroppedFrames,
                refreshRateHz = if (currentFps > 90) 120 else if (currentFps > 70) 90 else 60,
                isJanky = isJanky
            )

            frameCount = 0
            lastFpsTimestamp = frameNanos
        }
    }
}

/**
 * Composable effect to track real-time frame rates via Compose FrameClock.
 */
@Composable
fun TrackFramePerformanceEffect(enabled: Boolean = true) {
    if (!enabled) return
    LaunchedEffect(Unit) {
        while (true) {
            withFrameNanos { frameTimeNanos ->
                JankMonitor.recordFrame(frameTimeNanos)
            }
        }
    }
}
