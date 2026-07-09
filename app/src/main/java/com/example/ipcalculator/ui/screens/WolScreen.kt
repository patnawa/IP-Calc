package com.example.ipcalculator.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ipcalculator.IPCalculator
import com.example.ipcalculator.Translator
import com.example.ipcalculator.ui.components.GlowingCard
import com.example.ipcalculator.ui.components.SectionHeader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WolScreen() {
    val context = LocalContext.current
    val clipboard = LocalClipboardManager.current
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    var macAddress by rememberSaveable {
        mutableStateOf(
            if (WolScreenState.prefilledMac.isNotEmpty()) {
                val mac = WolScreenState.prefilledMac
                WolScreenState.prefilledMac = ""
                mac
            } else ""
        )
    }
    var broadcastIp by rememberSaveable {
        mutableStateOf(
            if (WolScreenState.prefilledBroadcast.isNotEmpty()) {
                val bc = WolScreenState.prefilledBroadcast
                WolScreenState.prefilledBroadcast = ""
                bc
            } else "255.255.255.255"
        )
    }
    var portString by rememberSaveable { mutableStateOf("9") }
    var resultLog by rememberSaveable { mutableStateOf<String?>(null) }
    var isSending by remember { mutableStateOf(false) }

    // Pulsing animation for the send button
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        SectionHeader(title = "Wake-on-LAN (WOL)")

        Text(
            text = "Wake up remote servers or PCs on the local subnet by broadcasting a magic UDP packet containing the target device's 48-bit MAC address.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        GlowingCard {
            // MAC Address Input
            OutlinedTextField(
                value = macAddress,
                onValueChange = { macAddress = it },
                label = { Text("MAC Address (e.g. 00:11:22:AA:BB:CC)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Ascii),
                trailingIcon = {
                    IconButton(onClick = {
                        clipboard.getText()?.let {
                            macAddress = it.text
                            Toast.makeText(context, "Pasted MAC", Toast.LENGTH_SHORT).show()
                        }
                    }) {
                        Icon(Icons.Default.ContentPaste, contentDescription = "Paste")
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                )
            )

            // Broadcast IP Input
            OutlinedTextField(
                value = broadcastIp,
                onValueChange = { broadcastIp = it },
                label = { Text("Subnet Broadcast IP") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                )
            )

            // Port Input
            OutlinedTextField(
                value = portString,
                onValueChange = { portString = it },
                label = { Text("UDP Port") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Pulsing Neon Send Button
            Button(
                onClick = {
                    if (macAddress.isBlank()) {
                        Toast.makeText(context, "Please enter a MAC address", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    isSending = true
                    scope.launch {
                        val port = portString.toIntOrNull() ?: 9
                        val log = withContext(Dispatchers.IO) {
                            IPCalculator.sendWakeOnLan(macAddress, broadcastIp, port)
                        }
                        resultLog = log
                        isSending = false
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .scale(if (isSending) 0.95f else pulseScale),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                shape = RoundedCornerShape(14.dp),
                enabled = !isSending
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.FlashOn,
                        contentDescription = "Send packet"
                    )
                    Text(
                        text = if (isSending) "Broadcasting Magic Packet..." else "SEND WAKE PACKET",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }
        }

        // Result Log Display
        AnimatedVisibility(visible = resultLog != null) {
            resultLog?.let { log ->
                val isSuccess = log.startsWith("Success")
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSuccess) {
                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f)
                        } else {
                            MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.2f)
                        }
                    ),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isSuccess) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = if (isSuccess) "✅ Packet Broadcasted" else "❌ Broadcast Failed",
                            fontWeight = FontWeight.Bold,
                            color = if (isSuccess) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                        )
                        Text(
                            text = log,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}

object WolScreenState {
    var prefilledMac = ""
    var prefilledBroadcast = ""
}
