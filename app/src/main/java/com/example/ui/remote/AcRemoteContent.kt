package com.example.ui.remote

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.AcMode
import com.example.domain.model.AirConditionerState
import com.example.domain.model.FanSpeed
import com.example.ui.theme.DikaSpacing
import com.example.ui.theme.DikaResponsiveSize
import com.example.ui.animation.DikaSpring
import com.example.ui.common.HapticFeedbackHelper
import com.example.ui.components.GlassButton
import com.example.ui.components.GlassCard
import com.example.ui.components.GlassRemoteButton
import com.example.ui.components.GlassSurface

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AcRemoteContent(
    state: AirConditionerState,
    onTogglePower: () -> Unit,
    onChangeTemp: (Int) -> Unit,
    onSetMode: (AcMode) -> Unit,
    onSetFan: (FanSpeed) -> Unit,
    onToggleSwing: () -> Unit,
    onToggleTurbo: () -> Unit,
    onToggleEco: () -> Unit
) {
    val context = LocalContext.current
    val hapticHelper = remember(context) { HapticFeedbackHelper(context) }
    val spacing = DikaSpacing

    BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
        val availableWidth = maxWidth
        val isWide = availableWidth >= 600.dp
        val sidePadding = spacing.responsivePadding()

        if (isWide) {
            // Adaptive Two-Column Layout for Tablets / Foldables / Landscape
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(sidePadding),
                horizontalArrangement = Arrangement.spacedBy(spacing.xl),
                verticalAlignment = Alignment.Top
            ) {
                // Left Column: Digital Display + Status Badges
                Column(
                    modifier = Modifier.weight(1.2f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    AcDisplayPanel(state = state, isWide = true)
                }

                // Right Column: Controls (Power, Temp, Mode, Fan, Extra)
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    AcControls(
                        state = state,
                        onTogglePower = onTogglePower,
                        onChangeTemp = onChangeTemp,
                        onSetMode = onSetMode,
                        onSetFan = onSetFan,
                        onToggleSwing = onToggleSwing,
                        onToggleTurbo = onToggleTurbo,
                        onToggleEco = onToggleEco
                    )
                }
            }
        } else {
            // Standard Compact Portrait Layout
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(sidePadding),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AcDisplayPanel(state = state, isWide = false)
                Spacer(modifier = Modifier.height(spacing.xl))
                AcControls(
                    state = state,
                    onTogglePower = onTogglePower,
                    onChangeTemp = onChangeTemp,
                    onSetMode = onSetMode,
                    onSetFan = onSetFan,
                    onToggleSwing = onToggleSwing,
                    onToggleTurbo = onToggleTurbo,
                    onToggleEco = onToggleEco
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun AcDisplayPanel(state: AirConditionerState, isWide: Boolean) {
    val spacing = DikaSpacing
    
    GlassCard(
        shape = RoundedCornerShape(28.dp),
        elevation = 8.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(if (isWide) 32.dp else 22.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(if (state.power) Color(0xFF10B981) else Color(0xFFEF4444))
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (state.power) "AC POWER ON" else "AC OFF",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = if (state.power) Color(0xFF10B981) else Color(0xFFEF4444)
                    )
                }

                GlassSurface(
                    shape = RoundedCornerShape(12.dp),
                    elevation = 0.dp,
                    tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.20f)
                ) {
                    Text(
                        text = state.mode.label.uppercase(),
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Circular Glass Temperature Reading Display
            val tempDisplaySize = DikaResponsiveSize.responsiveValue(
                min = 140.dp,
                max = 200.dp,
                compact = 160.dp,
                medium = 180.dp,
                expanded = 200.dp
            )

            GlassSurface(
                shape = CircleShape,
                elevation = 6.dp,
                tint = if (state.power) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f) else Color.White.copy(alpha = 0.05f),
                modifier = Modifier.size(tempDisplaySize)
            ) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        AnimatedContent(
                            targetState = state.temperature,
                            transitionSpec = {
                                if (targetState > initialState) {
                                    (slideInVertically { height -> height / 2 } + fadeIn(DikaSpring.Fast))
                                        .togetherWith(slideOutVertically { height -> -height / 2 } + fadeOut(DikaSpring.Fast))
                                } else {
                                    (slideInVertically { height -> -height / 2 } + fadeIn(DikaSpring.Fast))
                                        .togetherWith(slideOutVertically { height -> height / 2 } + fadeOut(DikaSpring.Fast))
                                }.using(SizeTransform(clip = false))
                            },
                            label = "ac_temp_anim"
                        ) { temp ->
                            val fontSizeValue = tempDisplaySize.value * 0.4f
                            Text(
                                text = "$temp",
                                fontSize = fontSizeValue.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.onSurface,
                                letterSpacing = (-2).sp
                            )
                        }
                        val unitFontSizeValue = tempDisplaySize.value * 0.18f
                        Text(
                            text = "°C",
                            fontSize = unitFontSizeValue.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Status Badges (Fan, Swing, Eco)
            FlowRow(
                horizontalArrangement = Arrangement.Center,
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                GlassSurface(shape = RoundedCornerShape(12.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Icon(Icons.Default.Air, contentDescription = null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Fan: ${state.fanSpeed.label}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface)
                    }
                }

                if (state.swing) {
                    Spacer(modifier = Modifier.width(8.dp))
                    GlassSurface(shape = RoundedCornerShape(12.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Icon(Icons.Default.SwapVert, contentDescription = null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Swing ON", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface)
                        }
                    }
                }

                if (state.eco) {
                    Spacer(modifier = Modifier.width(8.dp))
                    GlassSurface(shape = RoundedCornerShape(12.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Icon(Icons.Default.Eco, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color(0xFF10B981))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Eco", fontSize = 11.sp, color = Color(0xFF10B981))
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun AcControls(
    state: AirConditionerState,
    onTogglePower: () -> Unit,
    onChangeTemp: (Int) -> Unit,
    onSetMode: (AcMode) -> Unit,
    onSetFan: (FanSpeed) -> Unit,
    onToggleSwing: () -> Unit,
    onToggleTurbo: () -> Unit,
    onToggleEco: () -> Unit
) {
    val spacing = DikaSpacing
    
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        // Power & Temp Adjustment Glass Controls
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Temp Down Button
            GlassRemoteButton(
                icon = Icons.Default.Remove,
                enabled = state.power,
                onClick = { onChangeTemp(-1) },
                modifier = Modifier.testTag("temp_down_btn")
            )

            // AC Power Button Focal Point
            GlassRemoteButton(
                icon = Icons.Default.PowerSettingsNew,
                isPowerButton = true,
                size = DikaResponsiveSize.responsiveValue(min = 64.dp, max = 84.dp, medium = 76.dp),
                onClick = onTogglePower,
                modifier = Modifier.testTag("ac_power_btn")
            )

            // Temp Up Button
            GlassRemoteButton(
                icon = Icons.Default.Add,
                enabled = state.power,
                onClick = { onChangeTemp(1) },
                modifier = Modifier.testTag("temp_up_btn")
            )
        }

        Spacer(modifier = Modifier.height(spacing.xl))

        // Mode Selector Section
        Text(
            text = "Operation Mode",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(spacing.sm))
        FlowRow(
            horizontalArrangement = Arrangement.Center,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            AcMode.entries.forEach { mode ->
                val selected = state.mode == mode
                GlassButton(
                    onClick = { onSetMode(mode) },
                    enabled = state.power,
                    containerColor = if (selected) MaterialTheme.colorScheme.primary.copy(alpha = 0.40f) else null
                ) {
                    val icon = when (mode) {
                        AcMode.COOL -> Icons.Default.AcUnit
                        AcMode.HEAT -> Icons.Default.WbSunny
                        AcMode.DRY -> Icons.Default.WaterDrop
                        AcMode.FAN -> Icons.Default.Air
                        AcMode.AUTO -> Icons.Default.Autorenew
                    }
                    Icon(icon, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(mode.label, fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium)
                }
            }
        }

        Spacer(modifier = Modifier.height(spacing.lg))

        // Fan Speed Selector Section
        Text(
            text = "Fan Speed",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(spacing.sm))
        FlowRow(
            horizontalArrangement = Arrangement.Center,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            FanSpeed.entries.forEach { speed ->
                val selected = state.fanSpeed == speed
                GlassButton(
                    onClick = { onSetFan(speed) },
                    enabled = state.power,
                    containerColor = if (selected) MaterialTheme.colorScheme.primary.copy(alpha = 0.40f) else null
                ) {
                    Text(speed.label, fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium)
                }
            }
        }

        Spacer(modifier = Modifier.height(spacing.lg))

        // Extra Functions Row (Swing, Turbo, Eco)
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            GlassButton(
                onClick = onToggleSwing,
                enabled = state.power,
                containerColor = if (state.swing) MaterialTheme.colorScheme.primary.copy(alpha = 0.40f) else null
            ) {
                Icon(Icons.Default.SwapVert, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Swing")
            }
            GlassButton(
                onClick = onToggleTurbo,
                enabled = state.power,
                containerColor = if (state.turbo) MaterialTheme.colorScheme.primary.copy(alpha = 0.40f) else null
            ) {
                Icon(Icons.Default.FlashOn, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Turbo")
            }
            GlassButton(
                onClick = onToggleEco,
                enabled = state.power,
                containerColor = if (state.eco) Color(0xFF10B981).copy(alpha = 0.40f) else null
            ) {
                Icon(Icons.Default.Eco, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Eco")
            }
        }
    }
}
