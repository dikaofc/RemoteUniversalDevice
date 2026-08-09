package com.example.ui.remote

import android.content.res.Configuration
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.DeviceType
import com.example.ui.components.*
import com.example.ui.exportimport.ProfileImportExportHelper

@Composable
fun UniversalRemoteScreen(
    viewModel: RemoteViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToHome: () -> Unit = {},
    onNavigateToAddDevice: () -> Unit = {},
    onNavigateToAutomation: () -> Unit = {},
    onNavigateToSettings: () -> Unit = {}
) {
    val state by viewModel.uiState.collectAsState()
    val device = state.device
    val orientation = LocalConfiguration.current.orientation
    var showExportDialog by remember { mutableStateOf(false) }

    val glowType = when (device?.deviceType) {
        DeviceType.AC -> AmbientGlowType.AC
        DeviceType.SOUNDBAR -> AmbientGlowType.AUDIO
        else -> AmbientGlowType.TV
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

    AmbientBackground(glowType = glowType) {
        Box(modifier = Modifier.fillMaxSize().nestedScroll(nestedScrollConnection)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
            ) {
                // Floating Glass Top Navigation Header
                GlassSurface(
                    shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp),
                    elevation = 8.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = onNavigateBack,
                                modifier = Modifier.testTag("remote_back_button")
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Back",
                                    tint = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            Spacer(modifier = Modifier.width(4.dp))
                            Column {
                                Text(
                                    text = device?.name ?: "Universal Remote",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    GlassSurface(
                                        shape = RoundedCornerShape(8.dp),
                                        elevation = 0.dp,
                                        tint = when (state.connectionStatus) {
                                            com.example.domain.transport.ConnectionStatus.CONNECTED_WIFI -> Color(0xFF22C55E).copy(alpha = 0.2f)
                                            com.example.domain.transport.ConnectionStatus.CONNECTED_BLUETOOTH -> Color(0xFF3B82F6).copy(alpha = 0.2f)
                                            com.example.domain.transport.ConnectionStatus.READY_IR -> Color(0xFFEAB308).copy(alpha = 0.2f)
                                            com.example.domain.transport.ConnectionStatus.READY_EXTERNAL_IR -> Color(0xFF8B5CF6).copy(alpha = 0.2f)
                                            else -> Color.Red.copy(alpha = 0.2f)
                                        },
                                        modifier = Modifier.wrapContentSize()
                                    ) {
                                        Text(
                                            text = state.connectionStatus.label,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = when (state.connectionStatus) {
                                                com.example.domain.transport.ConnectionStatus.CONNECTED_WIFI -> Color(0xFF22C55E)
                                                com.example.domain.transport.ConnectionStatus.CONNECTED_BLUETOOTH -> Color(0xFF3B82F6)
                                                com.example.domain.transport.ConnectionStatus.READY_IR -> Color(0xFFEAB308)
                                                com.example.domain.transport.ConnectionStatus.READY_EXTERNAL_IR -> Color(0xFF8B5CF6)
                                                else -> Color.Red
                                            },
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "• ${device?.brand ?: "Generic"}",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }

                        IconButton(onClick = { showExportDialog = true }) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "Export Profile",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                if (device == null) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    }
                } else {
                    val scrollState = rememberScrollState()

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp)
                    ) {
                        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                            Row(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(vertical = 12.dp)
                                    .verticalScroll(scrollState),
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Box(modifier = Modifier.weight(1f)) {
                                    if (device.deviceType == DeviceType.AC) {
                                        AcRemoteContent(
                                            state = state.acState,
                                            onTogglePower = { viewModel.togglePower() },
                                            onChangeTemp = { viewModel.changeTemperature(it) },
                                            onSetMode = { viewModel.setAcMode(it) },
                                            onSetFan = { viewModel.setFanSpeed(it) },
                                            onToggleSwing = { viewModel.toggleSwing() },
                                            onToggleTurbo = { viewModel.toggleTurbo() },
                                            onToggleEco = { viewModel.toggleEco() }
                                        )
                                    } else {
                                        TvRemoteContent(
                                            onSend = { cmd, isPwr -> viewModel.sendCommand(cmd, isPwr) },
                                            customButtons = state.customButtons
                                        )
                                    }
                                }
                            }
                        } else {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(top = 12.dp, bottom = 90.dp)
                                    .verticalScroll(scrollState)
                            ) {
                                // Connection Status Glass Card
                                GlassCard(
                                    shape = RoundedCornerShape(20.dp),
                                    elevation = 4.dp,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(bottom = 14.dp)
                                ) {
                                    Column(modifier = Modifier.padding(14.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(
                                                    text = state.connectionStatus.label,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 13.sp,
                                                    color = MaterialTheme.colorScheme.primary
                                                )
                                                Text(
                                                    text = state.statusMessage,
                                                    fontSize = 11.sp,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                            GlassButton(
                                                onClick = { viewModel.testConnection() },
                                                modifier = Modifier.testTag("test_connection_btn")
                                            ) {
                                                Text("Test Link", fontSize = 11.sp)
                                            }
                                        }

                                        state.testConnectionResult?.let { testResult ->
                                            Spacer(modifier = Modifier.height(6.dp))
                                            Text(
                                                text = testResult,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = if (testResult.startsWith("SUCCESS")) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                                            )
                                        }

                                        state.errorMessage?.let { err ->
                                            Spacer(modifier = Modifier.height(6.dp))
                                            Text(
                                                text = err,
                                                fontSize = 11.sp,
                                                color = MaterialTheme.colorScheme.error
                                            )
                                        }
                                    }
                                }

                                if (device.deviceType == DeviceType.AC) {
                                    AcRemoteContent(
                                        state = state.acState,
                                        onTogglePower = { viewModel.togglePower() },
                                        onChangeTemp = { viewModel.changeTemperature(it) },
                                        onSetMode = { viewModel.setAcMode(it) },
                                        onSetFan = { viewModel.setFanSpeed(it) },
                                        onToggleSwing = { viewModel.toggleSwing() },
                                        onToggleTurbo = { viewModel.toggleTurbo() },
                                        onToggleEco = { viewModel.toggleEco() }
                                    )
                                } else {
                                    TvRemoteContent(
                                        onSend = { cmd, isPwr -> viewModel.sendCommand(cmd, isPwr) },
                                        customButtons = state.customButtons
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Bottom Floating Glass Navigation Bar
            GlassNavigationBar(
                currentTab = NavTab.REMOTE,
                visible = isNavVisible,
                onTabSelected = { tab ->
                    if (tab != NavTab.REMOTE) {
                        when (tab) {
                            NavTab.HOME -> onNavigateToHome()
                            NavTab.REMOTE -> { /* Current */ }
                            NavTab.ADD -> onNavigateToAddDevice()
                            NavTab.AUTOMATION -> onNavigateToAutomation()
                            NavTab.SETTINGS -> onNavigateToSettings()
                        }
                    }
                },
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }

        if (showExportDialog && device != null) {
            val exportedJson = ProfileImportExportHelper.exportProfileToJson(device)
            GlassDialog(
                onDismissRequest = { showExportDialog = false },
                title = "Export Profile (.dikaremote)"
            ) {
                Text(
                    text = "Profile JSON configuration:",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                GlassSurface(
                    shape = RoundedCornerShape(16.dp),
                    elevation = 0.dp,
                    modifier = Modifier.fillMaxWidth().height(160.dp)
                ) {
                    OutlinedTextField(
                        value = exportedJson,
                        onValueChange = {},
                        readOnly = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = Color.Transparent,
                            focusedBorderColor = Color.Transparent
                        ),
                        modifier = Modifier.fillMaxSize()
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    GlassButton(onClick = { showExportDialog = false }) {
                        Text("Done", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
