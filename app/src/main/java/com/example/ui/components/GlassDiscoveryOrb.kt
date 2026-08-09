package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.animation.LocalAnimationQuality

/**
 * GPU-efficient Device Discovery animation using a central Glass Orb and subtle ring expansion.
 * Drawn via Canvas and GPU-accelerated graphicsLayer to maintain 120/90/60 FPS with zero layout passes.
 */
@Composable
fun GlassDiscoveryOrb(
    modifier: Modifier = Modifier,
    statusText: String = "Scanning for nearby devices..."
) {
    val quality = LocalAnimationQuality.current
    val primaryColor = MaterialTheme.colorScheme.primary

    val infiniteTransition = rememberInfiniteTransition(label = "discovery_orb")

    // Ring 1 expansion
    val progress1 by if (quality.isPhysicsEnabled) {
        infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(2400, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "ring_progress_1"
        )
    } else {
        remember { mutableStateOf(0.5f) }
    }

    // Ring 2 offset expansion (staggered phase)
    val progress2 by if (quality.isPhysicsEnabled) {
        infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(2400, delayMillis = 800, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "ring_progress_2"
        )
    } else {
        remember { mutableStateOf(0.2f) }
    }

    // Center Orb Pulse scale
    val orbScale by if (quality.isPhysicsEnabled) {
        infiniteTransition.animateFloat(
            initialValue = 0.96f,
            targetValue = 1.05f,
            animationSpec = infiniteRepeatable(
                animation = tween(1200, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "orb_pulse"
        )
    } else {
        remember { mutableStateOf(1.0f) }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(220.dp),
            contentAlignment = Alignment.Center
        ) {
            // GPU Canvas for Ring Expansion
            Canvas(modifier = Modifier.fillMaxSize()) {
                val centerOffset = Offset(size.width / 2f, size.height / 2f)
                val maxRadius = size.width / 2.2f
                val minRadius = 45.dp.toPx()

                // Draw Ring 1
                val radius1 = minRadius + (maxRadius - minRadius) * progress1
                val alpha1 = (1f - progress1).coerceIn(0f, 0.8f)
                drawCircle(
                    color = primaryColor.copy(alpha = alpha1),
                    radius = radius1,
                    center = centerOffset,
                    style = Stroke(width = 2.dp.toPx())
                )

                // Draw Ring 2
                val radius2 = minRadius + (maxRadius - minRadius) * progress2
                val alpha2 = (1f - progress2).coerceIn(0f, 0.6f)
                drawCircle(
                    color = primaryColor.copy(alpha = alpha2 * 0.7f),
                    radius = radius2,
                    center = centerOffset,
                    style = Stroke(width = 1.5.dp.toPx())
                )
            }

            // Central Glass Orb with GPU graphicsLayer pulse transform
            GlassSurface(
                shape = CircleShape,
                elevation = 12.dp,
                tint = primaryColor.copy(alpha = 0.30f),
                modifier = Modifier
                    .size(84.dp)
                    .graphicsLayer {
                        scaleX = orbScale
                        scaleY = orbScale
                    }
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Radar,
                        contentDescription = "Scanning",
                        tint = Color.White,
                        modifier = Modifier.size(42.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = statusText,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
