package com.example.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeveloperModeScreen(
    viewModel: SettingsViewModel,
    onNavigateBack: () -> Unit
) {
    var rawFreq by remember { mutableStateOf("38000") }
    var rawPattern by remember { mutableStateOf("9000, 4500, 560, 560, 560, 1690") }
    var protocolResult by remember { mutableStateOf("") }
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Developer Mode & IR Debug", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState)
                .padding(16.dp)
        ) {
            Text("Raw IR Transmission Test", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text("Send raw carrier frequency and microsecond pattern directly to ConsumerIrManager", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = rawFreq,
                onValueChange = { rawFreq = it },
                label = { Text("Carrier Frequency (Hz)") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = rawPattern,
                onValueChange = { rawPattern = it },
                label = { Text("Pulse/Space Pattern (µs, comma separated)") },
                modifier = Modifier.fillMaxWidth().height(100.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = {
                    val freq = rawFreq.toIntOrNull() ?: 38000
                    viewModel.testIrRawTransmission(freq, rawPattern)
                },
                modifier = Modifier.fillMaxWidth().testTag("transmit_raw_ir_btn")
            ) {
                Text("Transmit Raw IR Signal")
            }

            val settings by viewModel.settings.collectAsState()

            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Layout & Touch Bounds Debug Overlay", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text("Shows component bounds, touch targets, screen insets & WindowSizeClass", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Switch(
                        checked = settings.layoutDebugEnabled,
                        onCheckedChange = { viewModel.setLayoutDebugEnabled(it) },
                        modifier = Modifier.testTag("layout_debug_switch")
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text("Protocol Encoder Test", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { protocolResult = viewModel.testProtocolEncoder("nec") },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Test NEC Protocol")
                }
                Button(
                    onClick = { protocolResult = viewModel.testProtocolEncoder("samsung") },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Test Samsung")
                }
            }

            if (protocolResult.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                    Text(protocolResult, modifier = Modifier.padding(12.dp), fontSize = 12.sp)
                }
            }
        }
    }
}
