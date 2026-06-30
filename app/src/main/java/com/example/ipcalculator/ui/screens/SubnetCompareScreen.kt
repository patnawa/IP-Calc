package com.example.ipcalculator.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
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
import com.example.ipcalculator.ui.components.SectionHeader
import com.example.ipcalculator.ui.components.ActionButtonRow

@Composable
fun SubnetCompareScreen() {
    var ipA by rememberSaveable { mutableStateOf("") }
    var prefixA by rememberSaveable { mutableStateOf("24") }
    var ipB by rememberSaveable { mutableStateOf("") }
    var prefixB by rememberSaveable { mutableStateOf("24") }

    var calculated by rememberSaveable { mutableStateOf(false) }
    var comparison by remember { mutableStateOf<IPCalculator.SubnetComparison?>(null) }
    
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
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Subnet Comparison Engine",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                // Subnet A inputs
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = ipA,
                        onValueChange = { ipA = it; calculated = false },
                        modifier = Modifier.weight(2f),
                        label = { Text("Subnet A IP") },
                        placeholder = { Text("e.g. 192.168.1.0") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = prefixA,
                        onValueChange = { 
                            if (it.isEmpty() || (it.toIntOrNull() != null && it.toInt() in 0..32)) {
                                prefixA = it
                            }
                            calculated = false
                        },
                        modifier = Modifier.weight(1f),
                        label = { Text("/CIDR") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true
                    )
                }

                // Divider Icon
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(28.dp)
                    )
                }

                // Subnet B inputs
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = ipB,
                        onValueChange = { ipB = it; calculated = false },
                        modifier = Modifier.weight(2f),
                        label = { Text("Subnet B IP") },
                        placeholder = { Text("e.g. 192.168.1.128") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = prefixB,
                        onValueChange = { 
                            if (it.isEmpty() || (it.toIntOrNull() != null && it.toInt() in 0..32)) {
                                prefixB = it
                            }
                            calculated = false
                        },
                        modifier = Modifier.weight(1f),
                        label = { Text("/CIDR") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true
                    )
                }

                Button(
                    onClick = {
                        val pA = prefixA.toIntOrNull() ?: 24
                        val pB = prefixB.toIntOrNull() ?: 24
                        if (IPCalculator.isValidIPv4(ipA.trim()) && IPCalculator.isValidIPv4(ipB.trim())) {
                            comparison = IPCalculator.compareSubnets(ipA.trim(), pA, ipB.trim(), pB)
                            calculated = comparison != null
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    enabled = ipA.isNotEmpty() && prefixA.isNotEmpty() && ipB.isNotEmpty() && prefixB.isNotEmpty()
                ) {
                    Text("Compare Subnets", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        AnimatedVisibility(
            visible = calculated,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            comparison?.let { comp ->
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    // Relationship Badge Box
                    val relationText = when {
                        comp.aContainsB && comp.bContainsA -> "Equal Subnets (Identical)"
                        comp.aContainsB -> "Subnet A CONTAINS Subnet B"
                        comp.bContainsA -> "Subnet B CONTAINS Subnet A"
                        comp.overlaps -> "Overlapping Subnets (Conflict!)"
                        else -> "Disjoint Subnets (No Overlap/Safe)"
                    }
                    val relationColor = when {
                        comp.aContainsB && comp.bContainsA -> MaterialTheme.colorScheme.primary
                        comp.overlaps && !comp.aContainsB && !comp.bContainsA -> MaterialTheme.colorScheme.error
                        else -> MaterialTheme.colorScheme.secondary
                    }

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = relationColor.copy(alpha = 0.15f)),
                        border = BorderStroke(1.dp, relationColor.copy(alpha = 0.4f))
                    ) {
                        Text(
                            text = relationText,
                            modifier = Modifier
                                .padding(16.dp)
                                .align(Alignment.CenterHorizontally),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = relationColor
                        )
                    }

                    // Comparison details card
                    GlowingCard {
                        SectionHeader(title = "Subnet Comparison Details")

                        CompareItemRow(label = "Network", valA = "${comp.subnetA.networkAddress}/${comp.subnetA.prefix}", valB = "${comp.subnetB.networkAddress}/${comp.subnetB.prefix}")
                        CompareItemRow(label = "Netmask", valA = comp.subnetA.subnetMask, valB = comp.subnetB.subnetMask)
                        CompareItemRow(label = "Broadcast", valA = comp.subnetA.broadcastAddress, valB = comp.subnetB.broadcastAddress)
                        CompareItemRow(label = "Host Range", valA = "${comp.subnetA.usableRangeStart} – ${comp.subnetA.usableRangeEnd}", valB = "${comp.subnetB.usableRangeStart} – ${comp.subnetB.usableRangeEnd}")
                        CompareItemRow(label = "Total Hosts", valA = comp.subnetA.totalHosts.toString(), valB = comp.subnetB.totalHosts.toString())
                        CompareItemRow(label = "Usable Hosts", valA = comp.subnetA.usableHosts.toString(), valB = comp.subnetB.usableHosts.toString())
                        CompareItemRow(label = "IP Class", valA = comp.subnetA.ipClass, valB = comp.subnetB.ipClass)
                        CompareItemRow(label = "IP Type", valA = comp.subnetA.ipType, valB = comp.subnetB.ipType)

                        val shareText = """
                            Subnet Comparison Report:
                            Subnet A: ${comp.subnetA.networkAddress}/${comp.subnetA.prefix}
                            Subnet B: ${comp.subnetB.networkAddress}/${comp.subnetB.prefix}
                            Relationship: $relationText
                            
                            Subnet A Details:
                            - Mask: ${comp.subnetA.subnetMask}
                            - Range: ${comp.subnetA.usableRangeStart} - ${comp.subnetA.usableRangeEnd}
                            - Usable Hosts: ${comp.subnetA.usableHosts}
                            
                            Subnet B Details:
                            - Mask: ${comp.subnetB.subnetMask}
                            - Range: ${comp.subnetB.usableRangeStart} - ${comp.subnetB.usableRangeEnd}
                            - Usable Hosts: ${comp.subnetB.usableHosts}
                        """.trimIndent()

                        ActionButtonRow(allResultsText = shareText)
                    }
                }
            }
        }
    }
}

@Composable
fun CompareItemRow(label: String, valA: String, valB: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.SemiBold
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(8.dp))
                    .padding(8.dp)
            ) {
                Text(text = "A", fontSize = 10.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                Text(text = valA, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface)
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(8.dp))
                    .padding(8.dp)
            ) {
                Text(text = "B", fontSize = 10.sp, color = MaterialTheme.colorScheme.secondary, fontWeight = FontWeight.Bold)
                Text(text = valB, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface)
            }
        }
    }
}
