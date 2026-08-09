package com.example.ui.debug

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize

val LocalDebugMode = compositionLocalOf { false }

@Composable
fun DebugSizeOverlay(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val isDebug = LocalDebugMode.current
    var sizeText = ""
    
    if (isDebug) {
        val density = LocalDensity.current
        Box(
            modifier = modifier.onGloballyPositioned { coordinates ->
                val size = coordinates.size.toSize()
                val widthDp = with(density) { size.width.toDp() }
                val heightDp = with(density) { size.height.toDp() }
                sizeText = "${widthDp.value.toInt()}x${heightDp.value.toInt()}dp"
            }
        ) {
            content()
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(2.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color.Black.copy(alpha = 0.7f))
                    .padding(horizontal = 4.dp, vertical = 2.dp)
            ) {
                Text(
                    text = sizeText,
                    color = Color.White,
                    fontSize = 8.sp,
                    lineHeight = 8.sp
                )
            }
        }
    } else {
        content()
    }
}
