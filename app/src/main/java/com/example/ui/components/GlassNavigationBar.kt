package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.animation.DikaSpring
import com.example.ui.common.HapticFeedbackHelper
import com.example.ui.theme.AmoledBackground
import com.example.ui.theme.LocalGlassTokens

enum class NavTab {
    HOME, REMOTE, ADD, AUTOMATION, SETTINGS
}

@Composable
fun GlassNavigationBar(
    currentTab: NavTab,
    onTabSelected: (NavTab) -> Unit,
    modifier: Modifier = Modifier,
    visible: Boolean = true
) {
    val tokens = LocalGlassTokens.current
    val isDark = isSystemInDarkTheme() || MaterialTheme.colorScheme.background == AmoledBackground
    val context = LocalContext.current
    val hapticHelper = remember(context) { HapticFeedbackHelper(context) }

    val navBgColor = if (isDark) {
        Color(0xFF0F172A).copy(alpha = tokens.navAlpha)
    } else {
        Color.White.copy(alpha = tokens.navAlpha.coerceAtLeast(0.70f))
    }

    val offset by animateDpAsState(
        targetValue = if (visible) 0.dp else 120.dp,
        animationSpec = spring(stiffness = Spring.StiffnessLow, dampingRatio = Spring.DampingRatioLowBouncy),
        label = "nav_hide_offset"
    )

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .offset(y = offset)
            .navigationBarsPadding()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        val availableWidth = maxWidth
        val isCompact = availableWidth < 380.dp

        GlassSurface(
            shape = RoundedCornerShape(32.dp),
            tint = navBgColor,
            elevation = 12.dp,
            blurRadius = tokens.blurRadius,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val items = listOf(
                    NavigationTabItem(NavTab.HOME, Icons.Default.Home, Icons.Outlined.Home, "Home", "nav_tab_home"),
                    NavigationTabItem(NavTab.REMOTE, Icons.Default.SettingsRemote, Icons.Outlined.SettingsRemote, "Remote", "nav_tab_remote"),
                    NavigationTabItem(NavTab.ADD, Icons.Default.AddCircle, Icons.Outlined.AddCircleOutline, "Add", "nav_tab_add"),
                    NavigationTabItem(NavTab.AUTOMATION, Icons.Default.Bolt, Icons.Outlined.Bolt, "Macros", "nav_tab_macros"),
                    NavigationTabItem(NavTab.SETTINGS, Icons.Default.Settings, Icons.Outlined.Settings, "Settings", "nav_tab_settings")
                )

                items.forEach { item ->
                    val selected = currentTab == item.tab
                    Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                        GlassNavItem(
                            tab = item.tab,
                            selected = selected,
                            activeIcon = item.activeIcon,
                            inactiveIcon = item.inactiveIcon,
                            label = item.label,
                            testTag = item.testTag,
                            showLabel = !isCompact && selected
                        ) {
                            if (!selected) {
                                hapticHelper.performHaptic()
                                onTabSelected(item.tab)
                            }
                        }
                    }
                }
            }
        }
    }
}

private data class NavigationTabItem(
    val tab: NavTab,
    val activeIcon: ImageVector,
    val inactiveIcon: ImageVector,
    val label: String,
    val testTag: String
)

@Composable
private fun GlassNavItem(
    tab: NavTab,
    selected: Boolean,
    activeIcon: ImageVector,
    inactiveIcon: ImageVector,
    label: String,
    testTag: String,
    showLabel: Boolean = true,
    onClick: () -> Unit
) {
    val pillAlpha by animateFloatAsState(
        targetValue = if (selected) 1.0f else 0f,
        animationSpec = DikaSpring.Fast,
        label = "pill_alpha"
    )

    val iconScale by animateFloatAsState(
        targetValue = if (selected) 1.12f else 1.0f,
        animationSpec = DikaSpring.Bouncy,
        label = "icon_scale"
    )

    val contentColor by animateColorAsState(
        targetValue = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
        animationSpec = tween(300),
        label = "icon_color"
    )

    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = Modifier
            .testTag(testTag)
            .defaultMinSize(minWidth = 48.dp, minHeight = 48.dp)
            .clip(CircleShape)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 4.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        if (selected) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .graphicsLayer { alpha = pillAlpha }
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.18f))
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = if (selected) activeIcon else inactiveIcon,
                contentDescription = label,
                tint = contentColor,
                modifier = Modifier
                    .size(24.dp)
                    .graphicsLayer {
                        scaleX = iconScale
                        scaleY = iconScale
                    }
            )
            if (showLabel) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = label,
                    fontSize = 9.sp,
                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                    color = contentColor,
                    maxLines = 1,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                )
            }
        }
    }
}
