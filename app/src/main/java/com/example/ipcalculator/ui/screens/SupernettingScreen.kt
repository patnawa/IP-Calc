package com.example.ipcalculator.ui.screens

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ipcalculator.IPCalculator
import com.example.ipcalculator.ui.components.ActionButtonRow
import com.example.ipcalculator.ui.components.GlowingCard
import com.example.ipcalculator.ui.components.ResultRowWithCopy
import com.example.ipcalculator.ui.components.SectionHeader

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
    var calculationPerformed by rememberSaveable { mutableStateOf(false) }
    var errorMsg by rememberSaveable { mutableStateOf<String?>(null) }

    val context = LocalContext.current
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
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
                                calculationPerformed = false
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
                                    calculationPerformed = false
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
                                                val lastSubnet = subnets.lastOrNull() ?: "192.168.0.0/24"
                                                val parts = lastSubnet.split("/")
                                                val ip = parts.getOrNull(0) ?: "192.168.0.0"
                                                val prefix = parts.getOrNull(1) ?: "24"
                                                val ipParts = ip.split(".")
                                                if (ipParts.size == 4) {
                                                    val third = ipParts[2].toIntOrNull() ?: 0
                                                    if (third in 0..254) {
                                                        val nextSub = "${ipParts[0]}.${ipParts[1]}.${third + 1}.0/$prefix"
                                                        subnets.add(nextSub)
                                                    } else {
                                                        // Wrap around to next second-octet
                                                        val second = ipParts[1].toIntOrNull() ?: 0
                                                        val nextSub = "${ipParts[0]}.${if (second < 255) second + 1 else 0}.0.0/$prefix"
                                                        subnets.add(nextSub)
                                                    }
                                                } else {
                                                    subnets.add("192.168.0.0/24")
                                                }
                                                calculationPerformed = false
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
                            
                            val cleanedSubnets = subnets.map { it.trim() }.filter { it.isNotEmpty() }
                            if (cleanedSubnets.isEmpty()) {
                                errorMsg = "Please enter at least one subnet."
                                return@Button
                            }
                            
                            // Validate format
                            for (sub in cleanedSubnets) {
                                val parts = sub.split("/")
                                if (parts.isEmpty()) {
                                    errorMsg = "Invalid format: $sub (Expected IP/Prefix)"
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
                                        errorMsg = "Invalid prefix: ${parts[1]} (Must be 0-32)"
                                        return@Button
                                    }
                                }
                            }
                            
                            val result = IPCalculator.summarizeRoutes(cleanedSubnets)
                            if (result != null) {
                                resultNetwork = result.first
                                resultPrefix = result.second
                            } else {
                                errorMsg = "Failed to summarize routes. Check address formats."
                            }
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Summarize")
                    }
                }
            }
        }

        // Error display
        if (calculationPerformed && errorMsg != null) {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = errorMsg!!,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(16.dp),
                    fontSize = 14.sp
                )
            }
        }

        // Result displays
        AnimatedVisibility(
            visible = calculationPerformed && errorMsg == null && resultNetwork != null,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            val net = resultNetwork ?: ""
            val pref = resultPrefix ?: 24
            val details = IPCalculator.calculateIPv4(net, pref)
            
            details?.let { res ->
                GlowingCard {
                    SectionHeader(title = "Summarized Supernet Route")
                    
                    ResultRowWithCopy("Summarized Route", "$net/$pref")
                    ResultRowWithCopy("Subnet Mask", res.subnetMask)
                    ResultRowWithCopy("Wildcard Mask", res.wildcardMask)
                    ResultRowWithCopy("Usable Host Range", "${res.usableRangeStart} - ${res.usableRangeEnd}")
                    ResultRowWithCopy("Total Covered IPs", "${res.totalHosts}")
                    
                    val shareText = """
                        Supernet Summarization Report:
                        Input Subnets: ${subnets.joinToString(", ")}
                        Summarized Route: $net/$pref
                        Netmask: ${res.subnetMask}
                        Range: ${res.usableRangeStart} - ${res.usableRangeEnd}
                        Total Covered IPs: ${res.totalHosts}
                    """.trimIndent()
                    
                    ActionButtonRow(allResultsText = shareText)
                }
            }
        }
    }
}
