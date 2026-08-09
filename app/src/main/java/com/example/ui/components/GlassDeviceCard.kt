package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SettingsRemote
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.ConnectionType
import com.example.domain.model.DeviceType
import com.example.domain.model.RemoteDevice
import com.example.ui.theme.DikaSpacing
import com.example.ui.animation.DikaSpring
import com.example.ui.animation.dikaPressable
import com.example.ui.home.getDeviceIcon

@Composable
fun GlassDeviceCard(
    device: RemoteDevice,
    onClick: () -> Unit,
    onToggleFavorite: () -> Unit,
    modifier: Modifier = Modifier
) {
    val starScale by animateFloatAsState(
        targetValue = if (device.isFavorite) 1.25f else 1.0f,
        animationSpec = DikaSpring.Bouncy,
        label = "star_scale"
    )

    val starRotation by animateFloatAsState(
        targetValue = if (device.isFavorite) 15f else 0f,
        animationSpec = DikaSpring.Bouncy,
        label = "star_rotation"
    )

    BoxWithConstraints(modifier = modifier) {
        val availableWidth = maxWidth
        val isWide = availableWidth >= 220.dp
        val spacing = DikaSpacing

        GlassCard(
            onClick = onClick,
            shape = RoundedCornerShape(22.dp),
            elevation = 6.dp,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("device_card_${device.id}")
                .dikaPressable(pressedScale = 0.96f)
        ) {
            Column(
                modifier = Modifier
                    .padding(spacing.lg)
                    .fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Glass icon container
                    GlassSurface(
                        shape = RoundedCornerShape(14.dp),
                        elevation = 0.dp,
                        tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.18f),
                        modifier = Modifier.size(44.dp)
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = getDeviceIcon(device.deviceType),
                                contentDescription = device.deviceType.displayName,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = onToggleFavorite,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = if (device.isFavorite) Icons.Default.Star else Icons.Outlined.StarBorder,
                                contentDescription = "Favorite",
                                tint = if (device.isFavorite) Color(0xFFFFB800) else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.graphicsLayer {
                                    scaleX = starScale
                                    scaleY = starScale
                                    rotationZ = starRotation
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = device.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = "${device.brand} • ${device.connectionType.displayName}",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Glass status tag bar
                    GlassSurface(
                        shape = RoundedCornerShape(10.dp),
                        elevation = 0.dp,
                        alpha = 0.10f,
                        modifier = Modifier.weight(1f)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = device.deviceType.displayName,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(
                                            when (device.connectionType) {
                                                ConnectionType.WIFI -> Color(0xFF10B981)
                                                ConnectionType.BLUETOOTH -> Color(0xFF38BDF8)
                                                ConnectionType.IR -> Color(0xFFF97316)
                                                ConnectionType.HYBRID -> Color(0xFFA855F7)
                                            }
                                        )
                                )
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    // Adaptive Action Button
                    GlassButton(
                        onClick = onClick,
                        shape = RoundedCornerShape(10.dp),
                        containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.25f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.SettingsRemote,
                            contentDescription = "Open Remote",
                            modifier = Modifier.size(14.dp)
                        )
                        if (isWide) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Open", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
