package com.example.ipcalculator.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ipcalculator.IPCalculator

@Composable
fun SupernettingScreen(modifier: Modifier = Modifier) {
    val subnets = remember {
        mutableStateListOf(
            "192.168.0.0/24",
            "192.168.1.0/24",
            "192.168.2.0/24",
            "192.168.3.0/24"
        )
    }

    var resultNetwork by remember { mutableStateOf<String?>(null) }
    var resultPrefix by remember { mutableStateOf<Int?>(null) }
    var calculationPerformed by remember { mutableStateOf(false) }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Subnets to Summarize",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                subnets.forEachIndexed { index, subnet ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = subnet,
                            onValueChange = { newVal ->
                                subnets[index] = newVal
                            },
                            label = { Text("Subnet (IP/Prefix)") },
                            placeholder = { Text("e.g. 192.168.1.0/24") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )

                        IconButton(
                            onClick = {
                                if (subnets.size > 1) {
                                    subnets.removeAt(index)
                                } else {
                                    Toast.makeText(context, "Must keep at least one subnet.", Toast.LENGTH_SHORT).show()
                                }
                            }
                        ) {
                            Text("✕", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            subnets.add("192.168.0.0/24")
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Add Subnet")
                    }

                    Button(
                        onClick = {
                            errorMsg = null
                            resultNetwork = null
                            resultPrefix = null
                            calculationPerformed = true

                            // Validate inputs
                            val trimmedSubnets = subnets.map { it.trim() }
                            for (sub in trimmedSubnets) {
                                val parts = sub.split("/")
                                if (parts.isEmpty()) {
                                    errorMsg = "Invalid subnet format. Use IP/Prefix (e.g. 192.168.1.0/24)"
                                    return@Button
                                }
                                val ip = parts[0]
                                if (!IPCalculator.isValidIPv4(ip)) {
                                    errorMsg = "Invalid IP address: $ip"
                                    return@Button
                                }
                                if (parts.size > 1) {
                                    val prefix = parts[1].toIntOrNull()
                                    if (prefix == null || prefix !in 0..32) {
                                        errorMsg = "Invalid prefix for subnet: $sub"
                                        return@Button
                                    }
                                }
                            }

                            val res = IPCalculator.summarizeRoutes(trimmedSubnets)
                            if (res == null) {
                                errorMsg = "Failed to summarize routes. Check your subnet addresses."
                            } else {
                                resultNetwork = res.first
                                resultPrefix = res.second
                            }
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Summarize")
                    }
                }
            }
        }

        if (calculationPerformed && errorMsg != null) {
            ElevatedCard(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = errorMsg!!,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    modifier = Modifier.padding(16.dp),
                    fontWeight = FontWeight.Bold
                )
            }
        }

        if (calculationPerformed && errorMsg == null && resultNetwork != null && resultPrefix != null) {
            val details = IPCalculator.calculateIPv4(resultNetwork!!, resultPrefix!!)
            
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Summarized Route (Supernet)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    HorizontalDivider()

                    ResultRow(
                        label = "Summarized Route / CIDR",
                        value = "$resultNetwork/$resultPrefix",
                        clipboardManager = clipboardManager
                    )

                    if (details != null) {
                        ResultRow(
                            label = "Subnet Mask",
                            value = details.subnetMask,
                            clipboardManager = clipboardManager
                        )
                        ResultRow(
                            label = "Range Start Address",
                            value = details.usableRangeStart,
                            clipboardManager = clipboardManager
                        )
                        ResultRow(
                            label = "Range End Address",
                            value = details.usableRangeEnd,
                            clipboardManager = clipboardManager
                        )
                        ResultRow(
                            label = "Total Covered IP Addresses",
                            value = details.totalHosts.toString(),
                            clipboardManager = clipboardManager
                        )
                    }
                }
            }
        }
    }
}
