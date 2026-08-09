package com.example.ui.compatibility

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Help
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class CompatibilityItem(
    val deviceName: String,
    val irSupport: String, // "YES", "NO", "DEPENDS"
    val wifiSupport: String,
    val btSupport: String,
    val autoDetectSupport: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompatibilityScreen(
    onNavigateBack: () -> Unit
) {
    val items = listOf(
        CompatibilityItem("Samsung Smart TV", "YES", "YES", "DEPENDS", "YES"),
        CompatibilityItem("LG webOS TV", "YES", "YES", "DEPENDS", "YES"),
        CompatibilityItem("Android TV / Google TV", "YES", "YES", "DEPENDS", "YES"),
        CompatibilityItem("Roku TV & Streaming", "YES", "YES", "DEPENDS", "YES"),
        CompatibilityItem("Generic Non-Smart TV", "YES", "NO", "NO", "IR required"),
        CompatibilityItem("Air Conditioner (AC)", "YES", "DEPENDS", "DEPENDS", "DEPENDS"),
        CompatibilityItem("Set-Top Box (STB)", "YES", "DEPENDS", "NO", "DEPENDS"),
        CompatibilityItem("Bluetooth Soundbar", "YES", "NO", "YES", "YES")
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Device Compatibility Matrix", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)
                ),
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "Transport Support Overview",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                    Text(
                        text = "Actual implementation capabilities for phone hardware and remote control transports.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f)
                    )
                }
            }

            // Table Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .padding(vertical = 10.dp, horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Device", modifier = Modifier.weight(2.2f), fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium)
                Text("IR", modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium)
                Text("Wi-Fi", modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium)
                Text("BT", modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium)
                Text("Auto", modifier = Modifier.weight(1.2f), fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium)
            }

            Spacer(modifier = Modifier.height(4.dp))

            LazyColumn {
                items(items) { item ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp, horizontal = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(item.deviceName, modifier = Modifier.weight(2.2f), style = MaterialTheme.typography.bodyMedium)
                        BoxCell(item.irSupport, Modifier.weight(1f))
                        BoxCell(item.wifiSupport, Modifier.weight(1f))
                        BoxCell(item.btSupport, Modifier.weight(1f))
                        BoxCell(item.autoDetectSupport, Modifier.weight(1.2f))
                    }
                }
            }
        }
    }
}

@Composable
private fun BoxCell(value: String, modifier: Modifier) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        when (value) {
            "YES" -> Icon(Icons.Default.Check, contentDescription = "Supported", tint = Color(0xFF2E7D32), modifier = Modifier.height(18.dp))
            "NO" -> Icon(Icons.Default.Close, contentDescription = "Unsupported", tint = MaterialTheme.colorScheme.error, modifier = Modifier.height(18.dp))
            else -> Text(value, style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp), color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
