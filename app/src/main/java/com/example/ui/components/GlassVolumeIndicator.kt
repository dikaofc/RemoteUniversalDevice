package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeDown
import androidx.compose.material.icons.automirrored.filled.VolumeMute
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.animation.DikaSpring

/**
 * Glass-styled Volume Progress Indicator component.
 * Uses animateFloatAsState with DikaSpring physics to naturally animate volume changes on the GPU without blocking the UI thread.
 */
@Composable
fun GlassVolumeIndicator(
    volume: Int,
    modifier: Modifier = Modifier,
    maxVolume: Int = 100,
    isMuted: Boolean = false,
    onVolumeUp: () -> Unit = {},
    onVolumeDown: () -> Unit = {},
    onToggleMute: () -> Unit = {}
) {
    val progressFraction = (volume.coerceIn(0, maxVolume).toFloat() / maxVolume.toFloat())

    // GPU-accelerated smooth progress animation
    val animatedProgress by animateFloatAsState(
        targetValue = if (isMuted) 0f else progressFraction,
        animationSpec = DikaSpring.Fast,
        label = "volume_progress_anim"
    )

    GlassCard(
        shape = RoundedCornerShape(24.dp),
        elevation = 6.dp,
        modifier = modifier
            .fillMaxWidth()
            .testTag("glass_volume_indicator")
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 18.dp, vertical = 14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = when {
                            isMuted -> Icons.Default.VolumeOff
                            volume == 0 -> Icons.AutoMirrored.Filled.VolumeMute
                            volume < 50 -> Icons.AutoMirrored.Filled.VolumeDown
                            else -> Icons.AutoMirrored.Filled.VolumeUp
                        },
                        contentDescription = "Volume State",
                        tint = if (isMuted) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "VOLUME",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Text(
                    text = if (isMuted) "MUTED" else "${(animatedProgress * 100).toInt()}%",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (isMuted) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Glass Volume Track
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(12.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.10f))
            ) {
                // Animated Progress Fill
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(animatedProgress.coerceIn(0f, 1f))
                        .clip(CircleShape)
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.primary,
                                    MaterialTheme.colorScheme.secondary
                                )
                            )
                        )
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Controls Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                GlassRemoteButton(
                    icon = Icons.AutoMirrored.Filled.VolumeDown,
                    label = "VOL -",
                    onClick = onVolumeDown,
                    size = 46.dp
                )

                GlassRemoteButton(
                    icon = if (isMuted) Icons.Default.VolumeOff else Icons.AutoMirrored.Filled.VolumeMute,
                    label = if (isMuted) "UNMUTE" else "MUTE",
                    onClick = onToggleMute,
                    accentColor = if (isMuted) MaterialTheme.colorScheme.error else null,
                    size = 46.dp
                )

                GlassRemoteButton(
                    icon = Icons.AutoMirrored.Filled.VolumeUp,
                    label = "VOL +",
                    onClick = onVolumeUp,
                    size = 46.dp
                )
            }
        }
    }
}
