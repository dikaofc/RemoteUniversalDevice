package com.example.ui.automation

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MacroBuilderScreen(
    viewModel: MacroViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToHome: () -> Unit = {},
    onNavigateToRemote: () -> Unit = {},
    onNavigateToAddDevice: () -> Unit = {},
    onNavigateToSettings: () -> Unit = {}
) {
    val state by viewModel.uiState.collectAsState()

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
                        IconButton(onClick = onNavigateBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Macros & Automation",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "One-Tap Smart Routines",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Automate multi-device actions with custom timing",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(16.dp))

                if (state.isExecutingMacro) {
                    GlassCard(
                        shape = RoundedCornerShape(20.dp),
                        elevation = 8.dp,
                        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CircularProgressIndicator(
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Executing Routine...", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                                Text(state.currentRunningMacroName ?: "", fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
                            }
                            TextButton(onClick = { viewModel.cancelMacro() }) {
                                Text("Cancel", color = MaterialTheme.colorScheme.error)
                            }
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    GlassButton(
                        onClick = { viewModel.createPresetMacro("Watch TV Routine", "Power TV + HDMI 1 + Volume Down") },
                        modifier = Modifier.weight(1f).testTag("add_watch_tv_macro")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Watch TV", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    GlassButton(
                        onClick = { viewModel.createPresetMacro("AC Night Sleep", "Power AC + Cool 24°C + Fan Auto + Sleep ON") },
                        modifier = Modifier.weight(1f).testTag("add_ac_sleep_macro")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("AC Sleep", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                if (state.macros.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxWidth().weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No routines created yet. Tap buttons above to create preset routines.",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 13.sp
                        )
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        contentPadding = PaddingValues(bottom = 100.dp)
                    ) {
                        items(state.macros) { macro ->
                            GlassCard(
                                shape = RoundedCornerShape(20.dp),
                                elevation = 6.dp,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    GlassSurface(
                                        shape = CircleShape,
                                        elevation = 0.dp,
                                        tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.20f),
                                        modifier = Modifier.size(44.dp)
                                    ) {
                                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                            Icon(
                                                imageVector = Icons.Default.Bolt,
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.size(24.dp)
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(14.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(macro.name, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurface)
                                        Text(macro.description, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                    GlassButton(
                                        onClick = { viewModel.executeMacro(macro) },
                                        modifier = Modifier.testTag("run_macro_${macro.id}")
                                    ) {
                                        Icon(Icons.Default.PlayArrow, contentDescription = "Run", tint = MaterialTheme.colorScheme.primary)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Bottom Floating Glass Navigation Bar
            GlassNavigationBar(
                currentTab = NavTab.AUTOMATION,
                visible = isNavVisible,
                onTabSelected = { tab ->
                    if (tab != NavTab.AUTOMATION) {
                        when (tab) {
                            NavTab.HOME -> onNavigateToHome()
                            NavTab.REMOTE -> onNavigateToRemote()
                            NavTab.ADD -> onNavigateToAddDevice()
                            NavTab.AUTOMATION -> { /* Current */ }
                            NavTab.SETTINGS -> onNavigateToSettings()
                        }
                    }
                },
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }
}
