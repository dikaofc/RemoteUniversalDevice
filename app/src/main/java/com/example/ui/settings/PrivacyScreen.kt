package com.example.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrivacyScreen(onNavigateBack: () -> Unit) {
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Privacy & Security", fontWeight = FontWeight.Bold) },
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
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Security, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(36.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text("100% Offline-First Architecture", fontWeight = FontWeight.Bold)
                        Text("No remote tracking, no external server requirement", fontSize = 12.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Privacy Commitments",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "1. Local Data Storage: All your device profiles, macros, custom button layouts, and settings are saved locally on your Android device in a secure SQLite Room database.\n\n" +
                        "2. Infrared Transmit Safety: IR commands are transmitted purely through your device's built-in Consumer IR hardware blaster directly to your TV/AC hardware.\n\n" +
                        "3. Local Network Control: Wi-Fi commands (mDNS, UPnP, WebSocket) operate strictly within your local home LAN. No remote cloud connection or telemetry is required.\n\n" +
                        "4. Zero Analytics & Ads: Dika Remote contains no third-party tracking, profiling, or telemetry SDKs.",
                style = MaterialTheme.typography.bodyMedium,
                lineHeight = 22.sp
            )
        }
    }
}
