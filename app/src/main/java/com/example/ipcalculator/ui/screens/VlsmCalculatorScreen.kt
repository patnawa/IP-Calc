package com.example.ipcalculator.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ipcalculator.IPCalculator

data class SubnetRequirement(
    val id: Int,
    val name: String,
    val hosts: String
)

@Composable
fun VlsmCalculatorScreen(modifier: Modifier = Modifier) {
    var isVlsm by remember { mutableStateOf(true) }
    var baseIp by remember { mutableStateOf("192.168.1.0") }
    var basePrefix by remember { mutableStateOf("24") }
    
    // VLSM specific states
    val requirements = remember { 
        mutableStateListOf(
            SubnetRequirement(1, "Subnet 1", "50"),
            SubnetRequirement(2, "Subnet 2", "20"),
            SubnetRequirement(3, "Subnet 3", "10")
        )
    }
    var nextId by remember { mutableStateOf(4) }
    
    // FLSM specific states
    var flsmSubnetsCount by remember { mutableStateOf("4") }

    // Calculation result states
    var vlsmResult by remember { mutableStateOf<List<IPCalculator.VlsmSubnet>?>(null) }
    var flsmResult by remember { mutableStateOf<List<IPCalculator.FlsmSubnet>?>(null) }
    var calculationPerformed by remember { mutableStateOf(false) }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    val context = LocalContext.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Mode Selector: VLSM vs FLSM
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { 
                        isVlsm = true 
                        calculationPerformed = false
                        vlsmResult = null
                        flsmResult = null
                        errorMsg = null
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isVlsm) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Text(
                        "VLSM",
                        color = if (isVlsm) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                    )
                }

                Button(
                    onClick = { 
                        isVlsm = false 
                        calculationPerformed = false
                        vlsmResult = null
                        flsmResult = null
                        errorMsg = null
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (!isVlsm) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Text(
                        "FLSM",
                        color = if (!isVlsm) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        // Base network info card
        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Base Network Configuration",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    OutlinedTextField(
                        value = baseIp,
                        onValueChange = { baseIp = it },
                        label = { Text("Base Network IP") },
                        placeholder = { Text("e.g. 192.168.1.0") },
                        modifier = Modifier.weight(2f),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = basePrefix,
                        onValueChange = {
                            val v = it.toIntOrNull()
                            if (it.isEmpty() || (v != null && v in 0..32)) {
                                basePrefix = it
                            }
                        },
                        label = { Text("Prefix (0-32)") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }
            }
        }

        // Requirements list or subnets count input
        if (isVlsm) {
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Subnet Requirements (VLSM)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    requirements.forEachIndexed { index, req ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = req.name,
                                onValueChange = { newName ->
                                    requirements[index] = req.copy(name = newName)
                                },
                                label = { Text("Subnet Name") },
                                modifier = Modifier.weight(2f),
                                singleLine = true
                            )

                            OutlinedTextField(
                                value = req.hosts,
                                onValueChange = { newHosts ->
                                    if (newHosts.isEmpty() || newHosts.toIntOrNull() != null) {
                                        requirements[index] = req.copy(hosts = newHosts)
                                    }
                                },
                                label = { Text("Hosts Needed") },
                                modifier = Modifier.weight(1.5f),
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                            )

                            IconButton(
                                onClick = {
                                    if (requirements.size > 1) {
                                        requirements.removeAt(index)
                                    } else {
                                        Toast.makeText(context, "Must keep at least one subnet requirement.", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            ) {
                                // Fallback icon instead of loading vector resource to ensure compilation
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
                                requirements.add(SubnetRequirement(nextId, "Subnet $nextId", "10"))
                                nextId++
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Add Subnet")
                        }

                        Button(
                            onClick = {
                                errorMsg = null
                                vlsmResult = null
                                calculationPerformed = true
                                val prefixInt = basePrefix.toIntOrNull() ?: 24
                                if (!IPCalculator.isValidIPv4(baseIp.trim())) {
                                    errorMsg = "Invalid Base IP address."
                                    return@Button
                                }
                                val reqList = requirements.mapNotNull {
                                    val h = it.hosts.toIntOrNull()
                                    if (h != null && h > 0) Pair(it.name, h) else null
                                }
                                if (reqList.isEmpty()) {
                                    errorMsg = "No valid host requirements specified."
                                    return@Button
                                }
                                val out = IPCalculator.calculateVLSM(baseIp.trim(), prefixInt, reqList)
                                if (out == null) {
                                    errorMsg = "Allocation failed! The base network is too small to fit the requested subnets."
                                } else {
                                    vlsmResult = out
                                }
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Calculate")
                        }
                    }
                }
            }
        } else {
            // FLSM Input
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Subnet Requirements (FLSM)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    OutlinedTextField(
                        value = flsmSubnetsCount,
                        onValueChange = {
                            if (it.isEmpty() || it.toIntOrNull() != null) {
                                flsmSubnetsCount = it
                            }
                        },
                        label = { Text("Number of Subnets") },
                        placeholder = { Text("e.g. 4") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )

                    Button(
                        onClick = {
                            errorMsg = null
                            flsmResult = null
                            calculationPerformed = true
                            val prefixInt = basePrefix.toIntOrNull() ?: 24
                            val subnetsCount = flsmSubnetsCount.toIntOrNull() ?: 0
                            if (!IPCalculator.isValidIPv4(baseIp.trim())) {
                                errorMsg = "Invalid Base IP address."
                                return@Button
                            }
                            if (subnetsCount <= 0) {
                                errorMsg = "Please enter a valid number of subnets."
                                return@Button
                            }
                            val out = IPCalculator.calculateFLSM(baseIp.trim(), prefixInt, subnetsCount)
                            if (out == null) {
                                errorMsg = "Failed to divide. The requested subnets count exceeds the host capacity of the base network prefix."
                            } else {
                                flsmResult = out
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Calculate FLSM")
                    }
                }
            }
        }

        // Show Errors
        if (calculationPerformed && errorMsg != null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.errorContainer)
                    .padding(16.dp)
            ) {
                Text(
                    text = errorMsg!!,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }

        // Show Results
        if (calculationPerformed && errorMsg == null) {
            if (isVlsm && vlsmResult != null) {
                Text(
                    text = "Allocated Subnets (VLSM)",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )

                vlsmResult!!.forEach { subnet ->
                    ElevatedCard(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = subnet.name,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = "/${subnet.prefix}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                            }
                            
                            HorizontalDivider()

                            SubnetDetailRow("Subnet IP", subnet.subnetAddress)
                            SubnetDetailRow("Subnet Mask", subnet.mask)
                            SubnetDetailRow("Usable Range", "${subnet.rangeStart} - ${subnet.rangeEnd}")
                            SubnetDetailRow("Broadcast Address", subnet.broadcast)
                            SubnetDetailRow("Hosts Info", "Requested: ${subnet.requestedHosts} | Usable Allocated: ${subnet.allocatedHosts}")
                        }
                    }
                }
            } else if (!isVlsm && flsmResult != null) {
                Text(
                    text = "Allocated Subnets (FLSM)",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )

                flsmResult!!.forEach { subnet ->
                    ElevatedCard(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Subnet #${subnet.id}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = "/${subnet.prefix}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                            }
                            
                            HorizontalDivider()

                            SubnetDetailRow("Subnet IP", subnet.subnetAddress)
                            SubnetDetailRow("Subnet Mask", subnet.mask)
                            SubnetDetailRow("Usable Range", "${subnet.rangeStart} - ${subnet.rangeEnd}")
                            SubnetDetailRow("Broadcast Address", subnet.broadcast)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SubnetDetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
