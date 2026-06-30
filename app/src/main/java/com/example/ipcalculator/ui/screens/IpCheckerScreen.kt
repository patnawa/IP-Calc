package com.example.ipcalculator.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ipcalculator.IPCalculator
import com.example.ipcalculator.ui.components.GlowingCard
import com.example.ipcalculator.ui.components.ResultRowWithCopy
import com.example.ipcalculator.ui.components.SectionHeader
import com.example.ipcalculator.ui.components.ActionButtonRow

@Composable
fun IpCheckerScreen() {
    var targetIp by rememberSaveable { mutableStateOf("") }
    var networkIp by rememberSaveable { mutableStateOf("") }
    var prefixInput by rememberSaveable { mutableStateOf("24") }
    
    var calculated by rememberSaveable { mutableStateOf(false) }
    var isInSubnet by rememberSaveable { mutableStateOf(false) }
    var subnetResult by remember { mutableStateOf<IPCalculator.IPv4Result?>(null) }
    
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Verify IP Subnet Containment",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                
                OutlinedTextField(
                    value = targetIp,
                    onValueChange = { 
                        targetIp = it 
                        calculated = false
                    },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Target IP Address") },
                    placeholder = { Text("e.g. 192.168.1.50") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    singleLine = true
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = networkIp,
                        onValueChange = { 
                            networkIp = it 
                            calculated = false
                        },
                        modifier = Modifier.weight(2f),
                        label = { Text("Subnet Network IP") },
                        placeholder = { Text("e.g. 192.168.1.0") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        singleLine = true
                    )
                    
                    OutlinedTextField(
                        value = prefixInput,
                        onValueChange = { 
                            if (it.isEmpty() || (it.toIntOrNull() != null && it.toInt() in 0..32)) {
                                prefixInput = it
                            }
                            calculated = false
                        },
                        modifier = Modifier.weight(1f),
                        label = { Text("Prefix /CIDR") },
                        placeholder = { Text("24") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true
                    )
                }

                Button(
                    onClick = {
                        val prefix = prefixInput.toIntOrNull() ?: 24
                        if (IPCalculator.isValidIPv4(targetIp.trim()) && IPCalculator.isValidIPv4(networkIp.trim())) {
                            isInSubnet = IPCalculator.isIpInSubnet(targetIp.trim(), networkIp.trim(), prefix)
                            subnetResult = IPCalculator.calculateIPv4(networkIp.trim(), prefix)
                            calculated = true
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    enabled = targetIp.isNotEmpty() && networkIp.isNotEmpty() && prefixInput.isNotEmpty()
                ) {
                    Text("Check Containment", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        AnimatedVisibility(
            visible = calculated,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                // Status Box
                val containerColor = if (isInSubnet) {
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                } else {
                    MaterialTheme.colorScheme.error.copy(alpha = 0.1f)
                }
                val icon = if (isInSubnet) Icons.Default.Check else Icons.Default.Close
                val tint = if (isInSubnet) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                val statusText = if (isInSubnet) {
                    "YES — IP address $targetIp belongs to the $networkIp/$prefixInput network."
                } else {
                    "NO — IP address $targetIp is OUTSIDE the $networkIp/$prefixInput network."
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = containerColor)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(32.dp))
                        Text(text = statusText, fontSize = 15.sp, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Medium)
                    }
                }

                // Subnet Info Card
                subnetResult?.let { result ->
                    GlowingCard {
                        SectionHeader(title = "Subnet Boundary Reference")
                        
                        ResultRowWithCopy("Subnet Mask", result.subnetMask)
                        ResultRowWithCopy("Network Address", "${result.networkAddress}/${result.prefix}")
                        ResultRowWithCopy("Usable Host Range", "${result.usableRangeStart} – ${result.usableRangeEnd}")
                        ResultRowWithCopy("Broadcast Address", result.broadcastAddress)
                        ResultRowWithCopy("Total Usable Hosts", "${result.usableHosts}")
                        
                        val shareText = """
                            Subnet Boundary Check:
                            Target IP: $targetIp
                            In Subnet: ${if (isInSubnet) "Yes" else "No"}
                            Subnet Network: ${result.networkAddress}/${result.prefix}
                            Netmask: ${result.subnetMask}
                            Usable Range: ${result.usableRangeStart} - ${result.usableRangeEnd}
                            Broadcast: ${result.broadcastAddress}
                        """.trimIndent()
                        
                        ActionButtonRow(allResultsText = shareText)
                    }
                }
            }
        }
    }
}
