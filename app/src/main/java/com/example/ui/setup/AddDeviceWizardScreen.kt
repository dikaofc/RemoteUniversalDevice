package com.example.ui.setup

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.database.SeedDatabase
import com.example.domain.model.ConnectionType
import com.example.domain.model.DeviceType
import com.example.ui.components.*
import com.example.ui.home.getDeviceIcon

import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddDeviceWizardScreen(
    viewModel: SetupViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToHome: () -> Unit = {},
    onNavigateToRemote: (String) -> Unit = {},
    onNavigateToAutomation: () -> Unit = {},
    onNavigateToSettings: () -> Unit = {},
    onSetupComplete: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    var brandSearchQuery by remember { mutableStateOf("") }

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
                // Floating Glass Header
                GlassSurface(
                    shape = RoundedCornerShape(24.dp),
                    elevation = 8.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = {
                                if (state.setupStep > 1) {
                                    viewModel.setStep(state.setupStep - 1)
                                } else {
                                    onNavigateBack()
                                }
                            },
                            modifier = Modifier.testTag("wizard_back_btn")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "Device Setup Wizard",
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Step ${state.setupStep} of 4",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Progress indicator bar
                LinearProgressIndicator(
                    progress = { state.setupStep / 4f },
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp))
                )

                Spacer(modifier = Modifier.height(16.dp))

                when (state.setupStep) {
                    1 -> {
                        // Step 1: Device Type
                        Text(
                            text = "Choose Device Type",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Select the hardware you wish to pair with Dika Remote",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            contentPadding = PaddingValues(bottom = 100.dp)
                        ) {
                            items(DeviceType.entries) { type ->
                                GlassCard(
                                    onClick = { viewModel.selectType(type) },
                                    shape = RoundedCornerShape(22.dp),
                                    elevation = 6.dp,
                                    modifier = Modifier.testTag("type_card_${type.name}")
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .padding(18.dp)
                                            .fillMaxWidth(),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        GlassSurface(
                                            shape = RoundedCornerShape(14.dp),
                                            elevation = 0.dp,
                                            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.20f),
                                            modifier = Modifier.size(48.dp)
                                        ) {
                                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                                Icon(
                                                    imageVector = getDeviceIcon(type),
                                                    contentDescription = type.displayName,
                                                    tint = MaterialTheme.colorScheme.primary,
                                                    modifier = Modifier.size(28.dp)
                                                )
                                            }
                                        }
                                        Spacer(modifier = Modifier.height(10.dp))
                                        Text(
                                            text = type.displayName,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }
                            }
                        }
                    }

                    2 -> {
                        // Step 2: Choose Brand
                        Text(
                            text = "Choose Manufacturer",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Select brand for your ${state.selectedType.displayName}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        GlassSurface(
                            shape = RoundedCornerShape(16.dp),
                            elevation = 0.dp,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            OutlinedTextField(
                                value = brandSearchQuery,
                                onValueChange = { brandSearchQuery = it },
                                placeholder = { Text("Search brand (Samsung, LG, Sony...)") },
                                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                                colors = OutlinedTextFieldDefaults.colors(
                                    unfocusedBorderColor = Color.Transparent,
                                    focusedBorderColor = Color.Transparent
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        val filteredBrands = SeedDatabase.supportedBrands.filter {
                            it.contains(brandSearchQuery, ignoreCase = true)
                        }

                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            contentPadding = PaddingValues(bottom = 100.dp)
                        ) {
                            items(filteredBrands) { brand ->
                                GlassCard(
                                    onClick = { viewModel.selectBrand(brand) },
                                    shape = RoundedCornerShape(16.dp),
                                    elevation = 2.dp,
                                    modifier = Modifier.fillMaxWidth().testTag("brand_card_$brand")
                                ) {
                                    Row(
                                        modifier = Modifier.padding(16.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(Icons.Default.Business, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                        Spacer(modifier = Modifier.width(14.dp))
                                        Text(brand, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = MaterialTheme.colorScheme.onSurface)
                                    }
                                }
                            }
                        }
                    }

                    3 -> {
                        // Step 3: Connection Protocol
                        Text(
                            text = "Choose Connection Protocol",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "How should Dika Remote communicate with your ${state.selectedBrand}?",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        ConnectionType.entries.forEach { conn ->
                            GlassCard(
                                onClick = { viewModel.selectConnection(conn) },
                                shape = RoundedCornerShape(20.dp),
                                elevation = 6.dp,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp)
                                    .testTag("conn_card_${conn.name}")
                            ) {
                                Row(
                                    modifier = Modifier.padding(18.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    val icon = when (conn) {
                                        ConnectionType.IR -> Icons.Default.Sensors
                                        ConnectionType.WIFI -> Icons.Default.Wifi
                                        ConnectionType.BLUETOOTH -> Icons.Default.Bluetooth
                                        ConnectionType.HYBRID -> Icons.Default.Phonelink
                                    }
                                    GlassSurface(
                                        shape = CircleShape,
                                        elevation = 0.dp,
                                        tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.20f),
                                        modifier = Modifier.size(48.dp)
                                    ) {
                                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(26.dp))
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(16.dp))
                                    Column {
                                        Text(conn.displayName, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurface)
                                        Text(
                                            text = when (conn) {
                                                ConnectionType.IR -> "Direct IR blaster commands (No network needed)"
                                                ConnectionType.WIFI -> "Smart TV / IP Control over Wi-Fi network"
                                                ConnectionType.BLUETOOTH -> "Bluetooth Classic / BLE HID device control"
                                                ConnectionType.HYBRID -> "Wi-Fi IP control with IR fallback"
                                            },
                                            fontSize = 12.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }
                    }

                    4 -> {
                        if (state.selectedConnection == ConnectionType.WIFI) {
                            // Wi-Fi Discovery Screen with Central Glowing Orb Animation
                            Text(
                                text = "Scanning Network...",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Discovering ${state.selectedBrand} devices via mDNS / UPnP",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(20.dp))

                            if (state.isDiscovering) {
                                GlassDiscoveryOrb(statusText = "Searching for ${state.selectedBrand} devices...")
                            } else if (state.discoveredDevices.isEmpty()) {
                                GlassCard(
                                    shape = RoundedCornerShape(24.dp),
                                    elevation = 6.dp,
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(20.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Icon(Icons.Default.WifiOff, contentDescription = null, modifier = Modifier.size(48.dp), tint = MaterialTheme.colorScheme.error)
                                        Spacer(modifier = Modifier.height(12.dp))
                                        Text("No Wi-Fi devices automatically discovered.", fontWeight = FontWeight.Bold)
                                        Spacer(modifier = Modifier.height(12.dp))
                                        GlassButton(onClick = { viewModel.selectConnection(ConnectionType.IR) }) {
                                            Text("Switch to IR Mode", fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            } else {
                                LazyColumn(
                                    verticalArrangement = Arrangement.spacedBy(10.dp),
                                    contentPadding = PaddingValues(bottom = 100.dp)
                                ) {
                                    items(state.discoveredDevices) { disc ->
                                        GlassCard(
                                            onClick = { viewModel.saveDiscoveredDevice(disc, onSetupComplete) },
                                            shape = RoundedCornerShape(18.dp),
                                            elevation = 4.dp,
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(16.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Icon(Icons.Default.Tv, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(32.dp))
                                                Spacer(modifier = Modifier.width(14.dp))
                                                Column {
                                                    Text(disc.name, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurface)
                                                    Text("IP: ${disc.ipAddress} • ${disc.serviceType}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        } else {
                            // IR Code Testing Wizard
                            GlassCodeTestingContent(
                                state = state,
                                onTestCode = { viewModel.testCurrentCode() },
                                onNextCode = { viewModel.nextCodeSet() },
                                onPrevCode = { viewModel.prevCodeSet() },
                                onConfirmCode = { viewModel.saveTestedIrDevice(onSetupComplete) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun GlassScanningOrb() {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse_orb")
    val pulseRadius by infiniteTransition.animateFloat(
        initialValue = 40f,
        targetValue = 120f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulse_radius"
    )
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulse_alpha"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val centerOffset = Offset(size.width / 2f, size.height / 2f)

            // Sonar pulse rings
            drawCircle(
                color = Color(0xFF38BDF8).copy(alpha = pulseAlpha),
                radius = pulseRadius,
                center = centerOffset
            )
            drawCircle(
                color = Color(0xFF818CF8).copy(alpha = pulseAlpha * 0.5f),
                radius = pulseRadius * 1.4f,
                center = centerOffset
            )
        }

        // Central glowing orb
        GlassSurface(
            shape = CircleShape,
            elevation = 12.dp,
            tint = Color(0xFF38BDF8).copy(alpha = 0.35f),
            modifier = Modifier.size(80.dp)
        ) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Default.Radar,
                    contentDescription = "Scanning",
                    tint = Color.White,
                    modifier = Modifier.size(40.dp)
                )
            }
        }
    }
}

@Composable
fun GlassCodeTestingContent(
    state: SetupWizardState,
    onTestCode: () -> Unit,
    onNextCode: () -> Unit,
    onPrevCode: () -> Unit,
    onConfirmCode: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Testing IR Code Set",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = "Testing code set ${state.currentCodeIndex} of ${state.totalCodes} for ${state.selectedBrand}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Large Power Button Test
        GlassRemoteButton(
            icon = Icons.Default.PowerSettingsNew,
            label = "TEST POWER",
            isPowerButton = true,
            size = 90.dp,
            onClick = onTestCode,
            modifier = Modifier.testTag("test_power_btn")
        )

        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "Tap Power to emit IR command to device",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(28.dp))

        GlassCard(
            shape = RoundedCornerShape(24.dp),
            elevation = 8.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Did your device respond?",
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    GlassButton(
                        onClick = onNextCode,
                        modifier = Modifier.testTag("code_no_btn")
                    ) {
                        Text("NO (Next Code)", fontWeight = FontWeight.Bold)
                    }

                    GlassButton(
                        onClick = onConfirmCode,
                        containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.40f),
                        modifier = Modifier.testTag("code_yes_btn")
                    ) {
                        Text("YES (Save Remote)", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TextButton(onClick = onPrevCode, enabled = state.currentCodeIndex > 1) {
                Text("< Previous Code", color = MaterialTheme.colorScheme.primary)
            }
            TextButton(onClick = onNextCode, enabled = state.currentCodeIndex < state.totalCodes) {
                Text("Next Code >", color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}
