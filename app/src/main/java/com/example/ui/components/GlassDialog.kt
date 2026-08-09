package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.theme.AmoledBackground
import com.example.ui.theme.LocalGlassTokens

@Composable
fun GlassDialog(
    onDismissRequest: () -> Unit,
    title: String,
    modifier: Modifier = Modifier,
    properties: DialogProperties = DialogProperties(),
    content: @Composable ColumnScope.() -> Unit
) {
    val tokens = LocalGlassTokens.current
    val isDark = isSystemInDarkTheme() || MaterialTheme.colorScheme.background == AmoledBackground

    val dialogBgColor = if (isDark) {
        Color(0xFF0F172A).copy(alpha = tokens.modalAlpha)
    } else {
        Color.White.copy(alpha = tokens.modalAlpha)
    }

    Dialog(
        onDismissRequest = onDismissRequest,
        properties = properties
    ) {
        GlassSurface(
            shape = RoundedCornerShape(28.dp),
            tint = dialogBgColor,
            elevation = 16.dp,
            blurRadius = tokens.blurRadius,
            modifier = modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = title,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(16.dp))
                content()
            }
        }
    }
}
