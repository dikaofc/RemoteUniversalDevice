package com.example.ui.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import com.example.domain.model.DeviceType
import com.example.domain.model.RemoteDevice
import com.example.ui.components.*
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onNavigateToAddDevice: () -> Unit,
    onNavigateToRemote: (String) -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToAutomation: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    val greeting = remember {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        when (hour) {
            in 5..11 -> "Good morning"
            in 12..17 -> "Good afternoon"
            else -> "Good evening"
        }
    }

    var isNavVisible by remember { mutableStateOf(true) }
    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                if (available.y < -15) isNavVisible = false
                if (available.y > 15) isNavVisible = true
                return Offset.Zero
            }
        }
    }

    AmbientBackground(glowType = AmbientGlowType.DEFAULT) {
        Box(modifier = Modifier.fillMaxSize().nestedScroll(nestedScrollConnection)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp)
            ) {
                Spacer(modifier = Modifier.height(12.dp))

                // Floating Glass Top Header
                GlassSurface(
                    shape = RoundedCornerShape(24.dp),
                    elevation = 8.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            GlassSurface(
                                shape = CircleShape,
                                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.20f),
                                elevation = 0.dp,
                                modifier = Modifier.size(42.dp)
                            ) {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.SettingsRemote,
                                        contentDescription = "Dika Remote Logo",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = greeting,
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "Dika Remote",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 20.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }

                        // Header actions removed as requested for a cleaner look
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // No IR phone banner
                AnimatedVisibility(visible = !state.hardware.hasIrEmitter) {
                    GlassCard(
                        shape = RoundedCornerShape(20.dp),
                        elevation = 4.dp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                GlassSurface(
                                    shape = CircleShape,
                                    tint = MaterialTheme.colorScheme.error.copy(alpha = 0.1f),
                                    elevation = 0.dp,
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Default.SensorsOff,
                                            contentDescription = "No IR",
                                            tint = MaterialTheme.colorScheme.error,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "Your phone has no IR Blaster",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "Use modern Smart Remote mode instead",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(14.dp))
                            Text(
                                text = "Available Control Methods:",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(bottom = 6.dp)
                            )
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                listOf(
                                    Triple(Icons.Default.Wifi, "Wi-Fi", Color(0xFF22C55E)),
                                    Triple(Icons.Default.Bluetooth, "Bluetooth", Color(0xFF3B82F6)),
                                    Triple(Icons.Default.Usb, "IR Hub", Color(0xFF8B5CF6))
                                ).forEach { (icon, label, color) ->
                                    GlassSurface(
                                        shape = RoundedCornerShape(12.dp),
                                        tint = color.copy(alpha = 0.1f),
                                        elevation = 0.dp,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.Center
                                        ) {
                                            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(12.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(label, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = color)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Room filter chips
                Text(
                    text = "Rooms",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(6.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    item {
                        GlassChipItem(
                            selected = state.selectedRoomId == "all",
                            label = "All Rooms",
                            onClick = { viewModel.selectRoom("all") }
                        )
                    }
                    items(state.rooms) { room ->
                        GlassChipItem(
                            selected = state.selectedRoomId == room.id,
                            label = room.name,
                            onClick = { viewModel.selectRoom(room.id) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Devices section header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "My Devices",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "${state.devices.size} Devices",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                if (state.devices.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        GlassCard(
                            shape = RoundedCornerShape(28.dp),
                            elevation = 8.dp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Default.DevicesOther,
                                    contentDescription = "Empty",
                                    modifier = Modifier.size(64.dp),
                                    tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = "No Devices Added",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Connect your first TV, AC, or smart device easily.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                GlassButton(
                                    onClick = onNavigateToAddDevice,
                                    modifier = Modifier.testTag("empty_add_button")
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = null)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Add Remote Device", fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Adaptive(minSize = 150.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(bottom = 100.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(state.devices, key = { it.id }) { device ->
                            GlassDeviceCard(
                                device = device,
                                onClick = { onNavigateToRemote(device.id) },
                                onToggleFavorite = { viewModel.toggleFavorite(device.id, !device.isFavorite) }
                            )
                        }
                    }
                }
            }

            // Bottom Floating Glass Navigation Bar
            GlassNavigationBar(
                currentTab = NavTab.HOME,
                visible = isNavVisible,
                onTabSelected = { tab ->
                    if (tab != NavTab.HOME) {
                        when (tab) {
                            NavTab.HOME -> { /* Already home */ }
                            NavTab.REMOTE -> {
                                if (state.devices.isNotEmpty()) {
                                    onNavigateToRemote(state.devices.first().id)
                                } else {
                                    onNavigateToAddDevice()
                                }
                            }
                            NavTab.ADD -> onNavigateToAddDevice()
                            NavTab.AUTOMATION -> onNavigateToAutomation()
                            NavTab.SETTINGS -> onNavigateToSettings()
                        }
                    }
                },
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }
}

@Composable
private fun GlassChipItem(
    selected: Boolean,
    label: String,
    onClick: () -> Unit
) {
    GlassSurface(
        shape = RoundedCornerShape(16.dp),
        tint = if (selected) MaterialTheme.colorScheme.primary.copy(alpha = 0.35f) else Color.White.copy(alpha = 0.10f),
        elevation = if (selected) 4.dp else 0.dp,
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 14.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (selected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
            }
            Text(
                text = label,
                fontSize = 13.sp,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

fun getDeviceIcon(type: DeviceType) = when (type) {
    DeviceType.TV -> Icons.Default.Tv
    DeviceType.AC -> Icons.Default.AcUnit
    DeviceType.STB -> Icons.Default.Router
    DeviceType.MEDIA_PLAYER -> Icons.Default.Cast
    DeviceType.PROJECTOR -> Icons.Default.Videocam
    DeviceType.SOUNDBAR -> Icons.Default.Speaker
    DeviceType.FAN -> Icons.Default.Air
    DeviceType.CUSTOM -> Icons.Default.SettingsRemote
}
