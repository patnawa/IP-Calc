package com.example.ipcalculator.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ipcalculator.IPCalculator
import com.example.ipcalculator.Translator
import com.example.ipcalculator.ui.components.GlowingCard
import com.example.ipcalculator.ui.components.SectionHeader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.InetSocketAddress
import java.net.Socket

@Composable
fun PingScannerScreen(modifier: Modifier = Modifier) {
    var isPingTab by rememberSaveable { mutableStateOf(true) }
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Tab Row
        TabRow(selectedTabIndex = if (isPingTab) 0 else 1) {
            Tab(
                selected = isPingTab,
                onClick = { isPingTab = true },
                text = { Text("Single Host Ping") }
            )
            Tab(
                selected = !isPingTab,
                onClick = { isPingTab = false },
                text = { Text("Subnet Scanner") }
            )
        }

        if (isPingTab) {
            PingTab()
        } else {
            ScannerTab()
        }
    }
}

@Composable
fun PingTab() {
    var hostInput by rememberSaveable { mutableStateOf("8.8.8.8") }
    var timeoutVal by rememberSaveable { mutableStateOf(1000f) }
    
    val logs = remember { mutableStateListOf<String>() }
    var isPinging by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        ElevatedCard(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "ICMP / TCP Ping Utility",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                OutlinedTextField(
                    value = hostInput,
                    onValueChange = { hostInput = it },
                    label = { Text("Target Hostname or IP") },
                    placeholder = { Text("e.g. google.com or 8.8.8.8") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Timeout", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("${timeoutVal.toInt()} ms", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    }
                    Slider(
                        value = timeoutVal,
                        onValueChange = { timeoutVal = it },
                        valueRange = 500f..5000f,
                        steps = 9
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            if (hostInput.trim().isEmpty()) return@Button
                            isPinging = true
                            logs.add("PING ${hostInput.trim()}...")
                            scope.launch {
                                val result = performPing(hostInput.trim(), timeoutVal.toInt())
                                logs.add(result)
                                isPinging = false
                            }
                        },
                        enabled = !isPinging,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(imageVector = Icons.Default.PlayArrow, contentDescription = "Ping")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Ping")
                    }

                    OutlinedButton(
                        onClick = { logs.clear() },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Clear Logs")
                    }
                }
            }
        }

        // Logs Display
        AnimatedVisibility(visible = logs.isNotEmpty()) {
            GlowingCard {
                SectionHeader(title = "Console Output")
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.background)
                        .padding(12.dp)
                ) {
                    logs.forEach { log ->
                        Text(
                            text = log,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 13.sp,
                            color = if (log.contains("Success")) MaterialTheme.colorScheme.primary else if (log.contains("fail") || log.contains("Timed out")) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(vertical = 2.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ScannerTab() {
    var startIp by rememberSaveable { mutableStateOf("192.168.1.1") }
    var endIp by rememberSaveable { mutableStateOf("192.168.1.30") }
    
    val activeHosts = remember { mutableStateListOf<Pair<String, Long>>() }
    var isScanning by remember { mutableStateOf(false) }
    var progress by remember { mutableStateOf(0f) }
    var currentScannedIp by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        ElevatedCard(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Subnet Active Range Sweep",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    OutlinedTextField(
                        value = startIp,
                        onValueChange = { startIp = it },
                        label = { Text("Start IP") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                    )

                    OutlinedTextField(
                        value = endIp,
                        onValueChange = { endIp = it },
                        label = { Text("End IP") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            if (!IPCalculator.isValidIPv4(startIp.trim()) || !IPCalculator.isValidIPv4(endIp.trim())) return@Button
                            isScanning = true
                            activeHosts.clear()
                            progress = 0f
                            
                            scope.launch {
                                performSubnetScan(
                                    startIp = startIp.trim(),
                                    endIp = endIp.trim(),
                                    onProgress = { scannedIp, currentProg ->
                                        currentScannedIp = scannedIp
                                        progress = currentProg
                                    },
                                    onHostFound = { ip, rtt ->
                                        activeHosts.add(Pair(ip, rtt))
                                    }
                                )
                                isScanning = false
                            }
                        },
                        enabled = !isScanning,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(imageVector = Icons.Default.Refresh, contentDescription = "Scan")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Sweep Scan")
                    }

                    OutlinedButton(
                        onClick = { activeHosts.clear() },
                        enabled = !isScanning,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Clear")
                    }
                }
            }
        }

        // Progress Bar
        AnimatedVisibility(visible = isScanning) {
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Scanning IP: $currentScannedIp", fontSize = 12.sp)
                        Text("${(progress * 100).toInt()}%", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        // Scan Results List
        GlowingCard {
            SectionHeader(title = "Online Host Devices found: (${activeHosts.size})")

            if (activeHosts.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (isScanning) "Scanning..." else "No active hosts discovered yet. Press Sweep Scan.",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                activeHosts.forEach { (ip, rtt) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "🟢  $ip",
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                        Text(
                            text = "RTT: ${rtt}ms",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}

// Network Helpers in IO thread scope
suspend fun performPing(host: String, timeoutMs: Int): String {
    return withContext(Dispatchers.IO) {
        val start = System.currentTimeMillis()
        try {
            val address = java.net.InetAddress.getByName(host)
            // isReachable attempts ICMP Echo or Echo service on port 7.
            // On android it requires internet permission.
            val reached = address.isReachable(timeoutMs)
            val elapsed = System.currentTimeMillis() - start
            if (reached) {
                "Success: Reply from ${address.hostAddress} in ${elapsed}ms"
            } else {
                "Timed out: Host ${address.hostName} is not reachable."
            }
        } catch (e: Exception) {
            "Ping failed: ${e.message}"
        }
    }
}

suspend fun performSubnetScan(
    startIp: String,
    endIp: String,
    onProgress: (String, Float) -> Unit,
    onHostFound: (String, Long) -> Unit
) {
    withContext(Dispatchers.IO) {
        val startLong = IPCalculator.ipv4ToLong(startIp)
        val endLong = IPCalculator.ipv4ToLong(endIp)
        if (startLong > endLong) return@withContext
        
        val totalIps = (endLong - startLong + 1).coerceAtLeast(1)
        val concurrencyLimit = 15
        
        var currentIndex = 0
        for (i in startLong..endLong step concurrencyLimit.toLong()) {
            val batch = (0 until concurrencyLimit).mapNotNull { offset ->
                val ipLong = i + offset
                if (ipLong <= endLong) {
                    val ipStr = IPCalculator.longToIPv4(ipLong)
                    async {
                        val start = System.currentTimeMillis()
                        val active = isPortOpen(ipStr, 80, 250) || isPortOpen(ipStr, 443, 250) || isPortOpen(ipStr, 22, 250) || isPortOpen(ipStr, 135, 250)
                        val rtt = System.currentTimeMillis() - start
                        Triple(ipStr, active, rtt)
                    }
                } else null
            }
            
            val results = batch.awaitAll()
            results.forEachIndexed { index, (ip, active, rtt) ->
                currentIndex++
                onProgress(ip, currentIndex.toFloat() / totalIps.toFloat())
                if (active) {
                    onHostFound(ip, rtt)
                }
            }
        }
    }
}

// Quick check using Socket connection on common TCP ports
private fun isPortOpen(ip: String, port: Int, timeoutMs: Int): Boolean {
    return try {
        val socket = Socket()
        socket.connect(InetSocketAddress(ip, port), timeoutMs)
        socket.close()
        true
    } catch (e: Exception) {
        false
    }
}
