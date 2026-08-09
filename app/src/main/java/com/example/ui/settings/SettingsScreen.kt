package com.example.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import com.example.ui.theme.ResponsiveDimension
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.GlassEffectsMode
import com.example.data.ThemeMode
import com.example.ui.components.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToHome: () -> Unit = {},
    onNavigateToRemote: () -> Unit = {},
    onNavigateToAddDevice: () -> Unit = {},
    onNavigateToAutomation: () -> Unit = {},
    onNavigateToDeveloperMode: () -> Unit,
    onNavigateToPrivacy: () -> Unit,
    onNavigateToCompatibility: () -> Unit = {}
) {
    val settings by viewModel.settings.collectAsState()

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
        BoxWithConstraints(modifier = Modifier.fillMaxSize().nestedScroll(nestedScrollConnection)) {
            val isTablet = maxWidth > 600.dp
            val horizontalPadding = if (isTablet) 0.dp else 16.dp
            val contentWidth = if (isTablet) 720.dp else maxWidth

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
            ) {
                // Header (Full Width on Phone, centered on Tablet)
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    GlassSurface(
                        shape = RoundedCornerShape(24.dp),
                        elevation = 8.dp,
                        modifier = Modifier
                            .widthIn(max = 720.dp)
                            .padding(horizontal = horizontalPadding)
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
                                text = "Settings",
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    contentPadding = PaddingValues(
                        start = horizontalPadding,
                        end = horizontalPadding,
                        top = 8.dp,
                        bottom = 120.dp
                    ),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    item {
                        Column(modifier = Modifier.widthIn(max = 720.dp)) {
                            SettingsSectionHeader("Appearance & Visuals")
                            GlassSettingsCard {
                                Column {
                                    SettingsItemHeader("Theme Mode")
                                    Row(
                                        modifier = Modifier
                                            .padding(horizontal = 16.dp, vertical = 8.dp)
                                            .fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        ThemeMode.entries.forEach { mode ->
                                            GlassChip(
                                                selected = settings.themeMode == mode,
                                                label = mode.name,
                                                onClick = { viewModel.setThemeMode(mode) },
                                                modifier = Modifier.weight(1f)
                                            )
                                        }
                                    }

                                    SettingsDivider()

                                    SettingsItemHeader(
                                        title = "Glass Effects",
                                        subtitle = "Adjust blur radius and GPU rendering level"
                                    )
                                    Row(
                                        modifier = Modifier
                                            .padding(horizontal = 16.dp, vertical = 8.dp)
                                            .fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        GlassEffectsMode.entries.forEach { mode ->
                                            GlassChip(
                                                selected = settings.glassEffectsMode == mode,
                                                label = mode.name,
                                                onClick = { viewModel.setGlassEffectsMode(mode) },
                                                modifier = Modifier.weight(1f)
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(24.dp))

                            SettingsSectionHeader("Feedback & Haptics")
                            GlassSettingsCard {
                                Column {
                                    GlassSettingsItem(
                                        icon = Icons.Default.Vibration,
                                        title = "Haptic Feedback",
                                        description = "Vibrate gently on remote key taps",
                                        trailing = {
                                            Switch(
                                                checked = settings.hapticEnabled,
                                                onCheckedChange = { viewModel.setHapticEnabled(it) },
                                                colors = SwitchDefaults.colors(checkedThumbColor = MaterialTheme.colorScheme.primary)
                                            )
                                        }
                                    )
                                    SettingsDivider()
                                    GlassSettingsItem(
                                        icon = Icons.Default.VolumeUp,
                                        title = "Sound Feedback",
                                        description = "Play key click sound on command transmit",
                                        trailing = {
                                            Switch(
                                                checked = settings.soundEnabled,
                                                onCheckedChange = { viewModel.setSoundEnabled(it) },
                                                colors = SwitchDefaults.colors(checkedThumbColor = MaterialTheme.colorScheme.primary)
                                            )
                                        }
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(24.dp))

                            SettingsSectionHeader("Developer & Advanced")
                            GlassSettingsCard {
                                Column {
                                    GlassSettingsItem(
                                        icon = Icons.Default.Code,
                                        title = "Developer Mode",
                                        description = "Raw IR pulse encoder & carrier debug tools",
                                        trailing = {
                                            Switch(
                                                checked = settings.developerModeEnabled,
                                                onCheckedChange = { viewModel.setDeveloperMode(it) },
                                                colors = SwitchDefaults.colors(checkedThumbColor = MaterialTheme.colorScheme.primary)
                                            )
                                        }
                                    )
                                    if (settings.developerModeEnabled) {
                                        SettingsDivider()
                                        GlassSettingsItem(
                                            icon = Icons.Default.BugReport,
                                            title = "Open Developer Tools",
                                            description = "Carrier frequency test, Raw IR & Protocol debug",
                                            onClick = onNavigateToDeveloperMode
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(24.dp))

                            SettingsSectionHeader("Privacy & About")
                            GlassSettingsCard {
                                Column {
                                    GlassSettingsItem(
                                        icon = Icons.Default.Devices,
                                        title = "Compatibility Matrix",
                                        description = "Supported devices & transport list",
                                        onClick = onNavigateToCompatibility
                                    )
                                    SettingsDivider()
                                    GlassSettingsItem(
                                        icon = Icons.Default.PrivacyTip,
                                        title = "Privacy Policy",
                                        description = "Offline-first guarantee",
                                        onClick = onNavigateToPrivacy
                                    )
                                    SettingsDivider()
                                    GlassSettingsItem(
                                        icon = Icons.Default.Info,
                                        title = "About Dika Remote",
                                        description = "Version 1.0.0 • Universal Controller"
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Bottom Navigation
            GlassNavigationBar(
                currentTab = NavTab.SETTINGS,
                visible = isNavVisible,
                onTabSelected = { tab ->
                    if (tab != NavTab.SETTINGS) {
                        when (tab) {
                            NavTab.HOME -> onNavigateToHome()
                            NavTab.REMOTE -> onNavigateToRemote()
                            NavTab.ADD -> onNavigateToAddDevice()
                            NavTab.AUTOMATION -> onNavigateToAutomation()
                            NavTab.SETTINGS -> { /* Current */ }
                        }
                    }
                },
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }
}

@Composable
fun SettingsSectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(start = 8.dp, bottom = 8.dp)
    )
}

@Composable
fun SettingsItemHeader(title: String, subtitle: String? = null) {
    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        if (subtitle != null) {
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun GlassSettingsCard(content: @Composable () -> Unit) {
    GlassCard(
        shape = RoundedCornerShape(22.dp),
        elevation = 6.dp,
        modifier = Modifier.fillMaxWidth(),
        content = { Box(modifier = Modifier.fillMaxWidth()) { content() } }
    )
}


@Composable
fun GlassSettingsItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    description: String? = null,
    trailing: @Composable (() -> Unit)? = null,
    onClick: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = ResponsiveDimension.buttonHeight)
            .clickable(enabled = onClick != null) { onClick?.invoke() }
            .padding(ResponsiveDimension.horizontalPadding),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(ResponsiveDimension.iconSize),
                tint = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
            )
            if (description != null) {
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 3,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                )
            }
        }

        if (trailing != null) {
            Box(modifier = Modifier.padding(start = 8.dp)) {
                trailing()
            }
        } else if (onClick != null) {
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
            )
        }
    }
}

@Composable
fun SettingsDivider() {
    HorizontalDivider(
        modifier = Modifier.padding(horizontal = 16.dp),
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f)
    )
}

@Composable
private fun GlassChip(
    selected: Boolean,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    GlassSurface(
        shape = RoundedCornerShape(12.dp),
        tint = if (selected) MaterialTheme.colorScheme.primary.copy(alpha = 0.35f) else androidx.compose.ui.graphics.Color.White.copy(alpha = 0.10f),
        elevation = if (selected) 2.dp else 0.dp,
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .padding(horizontal = 8.dp, vertical = 10.dp)
                .defaultMinSize(minWidth = 60.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = label,
                fontSize = 12.sp,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                maxLines = 1
            )
        }
    }
}
