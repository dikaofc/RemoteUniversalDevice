package com.example.ui.remote

import androidx.compose.animation.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Input
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.VolumeOff
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.CustomButton
import com.example.ui.animation.DikaMotion
import com.example.ui.animation.DikaSpring
import com.example.ui.common.HapticFeedbackHelper
import com.example.ui.components.GlassButton
import com.example.ui.components.GlassCard
import com.example.ui.components.GlassRemoteButton
import com.example.ui.components.GlassSurface
import com.example.ui.theme.DikaSpacing
import com.example.ui.theme.DikaResponsiveSize

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TvRemoteContent(
    onSend: (String, Boolean) -> Unit,
    customButtons: List<CustomButton>
) {
    var showNumpad by remember { mutableStateOf(false) }
    val spacing = DikaSpacing

    BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
        val availableWidth = maxWidth
        val isWideScreen = availableWidth >= 580.dp
        
        // Calculate adaptive button sizes
        val columns = if (isWideScreen) 6 else 4
        val gridSpacing = spacing.responsiveGridSpacing()
        val sidePadding = spacing.responsivePadding()
        val buttonSize = DikaResponsiveSize.calculateButtonSize(
            availableWidth = availableWidth - (sidePadding * 2),
            columns = columns,
            spacing = gridSpacing
        )

        if (isWideScreen) {
            // Adaptive Two-Column Layout for Tablets / Foldables / Landscape
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(sidePadding),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.Top
            ) {
                // Left Column: Top Actions + DPAD + Nav
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(gridSpacing, Alignment.CenterHorizontally)
                    ) {
                        GlassRemoteButton(
                            icon = Icons.Default.PowerSettingsNew,
                            label = "POWER",
                            isPowerButton = true,
                            size = buttonSize,
                            onClick = { onSend("power", true) },
                            modifier = Modifier.testTag("power_btn")
                        )
                        GlassRemoteButton(
                            icon = Icons.AutoMirrored.Filled.Input,
                            label = "SOURCE",
                            size = buttonSize,
                            onClick = { onSend("source", false) },
                            modifier = Modifier.testTag("source_btn")
                        )
                        GlassRemoteButton(
                            icon = Icons.AutoMirrored.Filled.VolumeOff,
                            label = "MUTE",
                            size = buttonSize,
                            onClick = { onSend("mute", false) },
                            modifier = Modifier.testTag("mute_btn")
                        )
                    }

                    Spacer(modifier = Modifier.height(spacing.xl))
                    GlassDPadController(
                        onSend = { onSend(it, false) },
                        modifier = Modifier.widthIn(max = 260.dp)
                    )
                    Spacer(modifier = Modifier.height(spacing.xl))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(gridSpacing, Alignment.CenterHorizontally)
                    ) {
                        GlassRemoteButton(
                            icon = Icons.AutoMirrored.Filled.ArrowBack,
                            label = "BACK",
                            size = buttonSize,
                            onClick = { onSend("back", false) },
                            modifier = Modifier.testTag("back_btn")
                        )
                        GlassRemoteButton(
                            icon = Icons.Default.Home,
                            label = "HOME",
                            size = buttonSize,
                            accentColor = MaterialTheme.colorScheme.primary,
                            onClick = { onSend("home", false) },
                            modifier = Modifier.testTag("home_btn")
                        )
                        GlassRemoteButton(
                            icon = Icons.Default.Menu,
                            label = "MENU",
                            size = buttonSize,
                            onClick = { onSend("menu", false) },
                            modifier = Modifier.testTag("menu_btn")
                        )
                    }
                }

                Spacer(modifier = Modifier.width(spacing.xl))

                // Right Column: Rockers + Numpad Toggle + Custom Shortcuts
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(gridSpacing, Alignment.CenterHorizontally)
                    ) {
                        GlassRockerControl(
                            topLabel = "+",
                            bottomLabel = "-",
                            title = "VOL",
                            onTopClick = { onSend("volume_up", false) },
                            onBottomClick = { onSend("volume_down", false) }
                        )

                        GlassRockerControl(
                            topLabel = "▲",
                            bottomLabel = "▼",
                            title = "CH",
                            onTopClick = { onSend("channel_up", false) },
                            onBottomClick = { onSend("channel_down", false) }
                        )

                        GlassRemoteButton(
                            icon = Icons.Default.Tag,
                            label = "NUMPAD",
                            size = buttonSize,
                            onClick = { showNumpad = !showNumpad },
                            modifier = Modifier.testTag("numpad_toggle_btn")
                        )
                    }

                    AnimatedVisibility(
                        visible = showNumpad,
                        enter = expandVertically(DikaSpring.dpSpring()) + fadeIn(DikaSpring.Fast),
                        exit = shrinkVertically(DikaSpring.dpSpring()) + fadeOut(DikaSpring.Fast)
                    ) {
                        Column {
                            Spacer(modifier = Modifier.height(spacing.lg))
                            GlassNumpadGrid(onSend = { onSend("num_$it", false) })
                        }
                    }

                    if (customButtons.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(spacing.xl))
                        Text(
                            text = "Custom Shortcuts",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(spacing.sm))
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(gridSpacing, Alignment.CenterHorizontally),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            customButtons.forEach { btn ->
                                GlassButton(onClick = { onSend(btn.commandKey, false) }) {
                                    Text(
                                        text = btn.label,
                                        fontWeight = FontWeight.SemiBold,
                                        maxLines = 1,
                                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }
                    }
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
                // Top Action Row (Power, Source, Mute, Menu)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    GlassRemoteButton(
                        icon = Icons.Default.PowerSettingsNew,
                        label = "POWER",
                        isPowerButton = true,
                        size = buttonSize,
                        onClick = { onSend("power", true) },
                        modifier = Modifier.testTag("power_btn")
                    )
                    GlassRemoteButton(
                        icon = Icons.AutoMirrored.Filled.Input,
                        label = "SOURCE",
                        size = buttonSize,
                        onClick = { onSend("source", false) },
                        modifier = Modifier.testTag("source_btn")
                    )
                    GlassRemoteButton(
                        icon = Icons.AutoMirrored.Filled.VolumeOff,
                        label = "MUTE",
                        size = buttonSize,
                        onClick = { onSend("mute", false) },
                        modifier = Modifier.testTag("mute_btn")
                    )
                    GlassRemoteButton(
                        icon = Icons.Default.Menu,
                        label = "MENU",
                        size = buttonSize,
                        onClick = { onSend("menu", false) },
                        modifier = Modifier.testTag("menu_btn")
                    )
                }

                Spacer(modifier = Modifier.height(spacing.xl))

                // Center Glass D-Pad Wheel
                GlassDPadController(onSend = { onSend(it, false) })

                Spacer(modifier = Modifier.height(spacing.xl))

                // Secondary Navigation (Back, Home, Numpad Toggle)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    GlassRemoteButton(
                        icon = Icons.AutoMirrored.Filled.ArrowBack,
                        label = "BACK",
                        size = buttonSize,
                        onClick = { onSend("back", false) },
                        modifier = Modifier.testTag("back_btn")
                    )
                    GlassRemoteButton(
                        icon = Icons.Default.Home,
                        label = "HOME",
                        size = buttonSize,
                        accentColor = MaterialTheme.colorScheme.primary,
                        onClick = { onSend("home", false) },
                        modifier = Modifier.testTag("home_btn")
                    )
                    GlassRemoteButton(
                        icon = Icons.Default.Tag,
                        label = "NUMPAD",
                        size = buttonSize,
                        onClick = { showNumpad = !showNumpad },
                        modifier = Modifier.testTag("numpad_toggle_btn")
                    )
                }

                Spacer(modifier = Modifier.height(spacing.xl))

                // Volume & Channel Glass Rockers
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    GlassRockerControl(
                        topLabel = "+",
                        bottomLabel = "-",
                        title = "VOL",
                        onTopClick = { onSend("volume_up", false) },
                        onBottomClick = { onSend("volume_down", false) }
                    )

                    GlassRockerControl(
                        topLabel = "▲",
                        bottomLabel = "▼",
                        title = "CH",
                        onTopClick = { onSend("channel_up", false) },
                        onBottomClick = { onSend("channel_down", false) }
                    )
                }

                AnimatedVisibility(
                    visible = showNumpad,
                    enter = expandVertically(DikaSpring.dpSpring()) + fadeIn(DikaSpring.Fast),
                    exit = shrinkVertically(DikaSpring.dpSpring()) + fadeOut(DikaSpring.Fast)
                ) {
                    Column {
                        Spacer(modifier = Modifier.height(spacing.xl))
                        GlassNumpadGrid(onSend = { onSend("num_$it", false) })
                    }
                }

                if (customButtons.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(spacing.xl))
                    Text(
                        text = "Custom Shortcuts",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(spacing.sm))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(gridSpacing, Alignment.CenterHorizontally),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        customButtons.forEach { btn ->
                            GlassButton(onClick = { onSend(btn.commandKey, false) }) {
                                Text(
                                    text = btn.label,
                                    fontWeight = FontWeight.SemiBold,
                                    maxLines = 1,
                                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun GlassDPadController(
    onSend: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val hapticHelper = remember(context) { HapticFeedbackHelper(context) }

    val interactionSource = remember { MutableInteractionSource() }
    val isOkPressed by interactionSource.collectIsPressedAsState()

    val okScale by animateFloatAsState(
        targetValue = if (isOkPressed) 0.90f else 1.0f,
        animationSpec = DikaSpring.Snappy,
        label = "ok_scale"
    )

    BoxWithConstraints(
        modifier = modifier
            .widthIn(max = 280.dp)
            .fillMaxWidth()
            .aspectRatio(1f),
        contentAlignment = Alignment.Center
    ) {
        val availableSize = minOf(maxWidth, maxHeight)
        val centerSize = availableSize * 0.38f
        val iconSize = availableSize * 0.16f
        val paddingSize = availableSize * 0.08f

        GlassSurface(
            shape = CircleShape,
            elevation = 10.dp,
            modifier = Modifier.size(availableSize)
        ) {
            Box(contentAlignment = Alignment.Center) {
                // UP
                IconButton(
                    onClick = {
                        hapticHelper.performHaptic()
                        onSend("up")
                    },
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = paddingSize)
                        .size(iconSize * 1.5f)
                        .testTag("dpad_up")
                ) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowUp,
                        contentDescription = "Up",
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(iconSize)
                    )
                }
                // DOWN
                IconButton(
                    onClick = {
                        hapticHelper.performHaptic()
                        onSend("down")
                    },
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = paddingSize)
                        .size(iconSize * 1.5f)
                        .testTag("dpad_down")
                ) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = "Down",
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(iconSize)
                    )
                }
                // LEFT
                IconButton(
                    onClick = {
                        hapticHelper.performHaptic()
                        onSend("left")
                    },
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(start = paddingSize)
                        .size(iconSize * 1.5f)
                        .testTag("dpad_left")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                        contentDescription = "Left",
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(iconSize)
                    )
                }
                // RIGHT
                IconButton(
                    onClick = {
                        hapticHelper.performHaptic()
                        onSend("right")
                    },
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = paddingSize)
                        .size(iconSize * 1.5f)
                        .testTag("dpad_right")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = "Right",
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(iconSize)
                    )
                }
                // OK Center Glass Button
                GlassSurface(
                    shape = CircleShape,
                    elevation = 6.dp,
                    tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.40f),
                    modifier = Modifier
                        .size(centerSize)
                        .graphicsLayer {
                            scaleX = okScale
                            scaleY = okScale
                        }
                        .clip(CircleShape)
                        .clickable(
                            interactionSource = interactionSource,
                            indication = null,
                            onClick = {
                                hapticHelper.performHaptic()
                                onSend("ok")
                            }
                        )
                        .testTag("dpad_ok")
                ) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            "OK",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = (centerSize.value * 0.3f).sp,
                            color = Color.White,
                            letterSpacing = 0.5.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun GlassRockerControl(
    topLabel: String,
    bottomLabel: String,
    title: String,
    onTopClick: () -> Unit,
    onBottomClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val hapticHelper = remember(context) { HapticFeedbackHelper(context) }

    BoxWithConstraints(
        modifier = modifier
            .widthIn(min = 60.dp, max = 80.dp)
            .aspectRatio(0.42f) // Normalized aspect ratio for consistent geometry
    ) {
        val rockerWidth = maxWidth
        
        GlassSurface(
            shape = RoundedCornerShape(rockerWidth / 2), // Circular ends for rocker
            elevation = 6.dp,
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.padding(vertical = 16.dp)
            ) {
                IconButton(
                    onClick = {
                        hapticHelper.performHaptic()
                        onTopClick()
                    },
                    modifier = Modifier.size(rockerWidth * 0.85f)
                ) {
                    Text(
                        topLabel, 
                        fontSize = (rockerWidth.value * 0.4f).sp, 
                        fontWeight = FontWeight.Bold, 
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                
                Text(
                    title,
                    fontSize = (rockerWidth.value * 0.18f).sp,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.9f),
                    letterSpacing = 0.5.sp
                )
                
                IconButton(
                    onClick = {
                        hapticHelper.performHaptic()
                        onBottomClick()
                    },
                    modifier = Modifier.size(rockerWidth * 0.85f)
                ) {
                    Text(
                        bottomLabel, 
                        fontSize = (rockerWidth.value * 0.4f).sp, 
                        fontWeight = FontWeight.Bold, 
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

@Composable
fun GlassNumpadGrid(onSend: (String) -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        val keys = listOf(
            listOf("1", "2", "3"),
            listOf("4", "5", "6"),
            listOf("7", "8", "9"),
            listOf(".", "0", "CLEAR")
        )
        keys.forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                row.forEach { num ->
                    GlassButton(
                        onClick = { onSend(num) },
                        modifier = Modifier
                            .width(72.dp)
                            .height(48.dp),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text(num, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}
