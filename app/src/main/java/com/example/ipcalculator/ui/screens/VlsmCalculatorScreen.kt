package com.example.ipcalculator.ui.screens

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ipcalculator.IPCalculator
import com.example.ipcalculator.ui.components.ActionButtonRow
import com.example.ipcalculator.ui.components.GlowingCard
import com.example.ipcalculator.ui.components.ResultRowWithCopy
import com.example.ipcalculator.ui.components.SectionHeader

data class SubnetRequirement(
    val id: Int,
    val name: String,
    val hosts: String
)

@Composable
fun VlsmCalculatorScreen(modifier: Modifier = Modifier) {
    var isVlsm by rememberSaveable { mutableStateOf(true) }
    var baseIp by rememberSaveable { mutableStateOf("192.168.1.0") }
    var basePrefix by rememberSaveable { mutableStateOf("24") }
    
    // VLSM specific states
    val requirements = remember { 
        mutableStateListOf(
            SubnetRequirement(1, "Subnet 1", "50"),
            SubnetRequirement(2, "Subnet 2", "20"),
            SubnetRequirement(3, "Subnet 3", "10")
        )
    }
    var nextId by rememberSaveable { mutableStateOf(4) }
    
    // FLSM specific states
    var flsmSubnetsCount by rememberSaveable { mutableStateOf("4") }

    // Calculation result states
    var vlsmResult by remember { mutableStateOf<List<IPCalculator.VlsmSubnet>?>(null) }
    var flsmResult by remember { mutableStateOf<List<IPCalculator.FlsmSubnet>?>(null) }
    var calculationPerformed by rememberSaveable { mutableStateOf(false) }
    var errorMsg by rememberSaveable { mutableStateOf<String?>(null) }

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
                val prefixInt = basePrefix.toIntOrNull() ?: 24
                
                // Interactive IP Map Visualizer (NEW!)
                VlsmVisualMap(basePrefix = prefixInt, subnets = vlsmResult!!)
                
                Text(
                    text = "Allocated Subnets (VLSM)",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )

                var shareContent = "VLSM Allocation Report (Base Network: $baseIp/$basePrefix):\n\n"

                vlsmResult!!.forEach { subnet ->
                    val subnetText = """
                        Subnet: ${subnet.name} (/${subnet.prefix})
                        Network Address: ${subnet.subnetAddress}
                        Mask: ${subnet.mask}
                        Range: ${subnet.rangeStart} - ${subnet.rangeEnd}
                        Broadcast: ${subnet.broadcast}
                        Hosts: Requested: ${subnet.requestedHosts} | Allocated: ${subnet.allocatedHosts}
                    """.trimIndent()
                    
                    shareContent += "$subnetText\n\n"

                    GlowingCard {
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
                        
                        SectionHeader(title = "Allocation details")

                        ResultRowWithCopy("Subnet IP Address", subnet.subnetAddress)
                        ResultRowWithCopy("Subnet Mask", subnet.mask)
                        ResultRowWithCopy("Usable Host Range", "${subnet.rangeStart} - ${subnet.rangeEnd}")
                        ResultRowWithCopy("Broadcast Address", subnet.broadcast)
                        ResultRowWithCopy("Usable / Requested Hosts", "${subnet.allocatedHosts} / ${subnet.requestedHosts}")
                    }
                }
                
                // Copy all / Share row for the entire report
                ActionButtonRow(allResultsText = shareContent.trim())
                
            } else if (!isVlsm && flsmResult != null) {
                Text(
                    text = "Allocated Subnets (FLSM)",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )

                var shareContent = "FLSM Allocation Report (Base Network: $baseIp/$basePrefix):\n\n"

                flsmResult!!.forEach { subnet ->
                    val subnetText = """
                        Subnet: #${subnet.id} (/${subnet.prefix})
                        Network Address: ${subnet.subnetAddress}
                        Mask: ${subnet.mask}
                        Range: ${subnet.rangeStart} - ${subnet.rangeEnd}
                        Broadcast: ${subnet.broadcast}
                    """.trimIndent()
                    
                    shareContent += "$subnetText\n\n"

                    GlowingCard {
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
                        
                        SectionHeader(title = "Subnet boundary")

                        ResultRowWithCopy("Subnet IP Address", subnet.subnetAddress)
                        ResultRowWithCopy("Subnet Mask", subnet.mask)
                        ResultRowWithCopy("Usable Host Range", "${subnet.rangeStart} - ${subnet.rangeEnd}")
                        ResultRowWithCopy("Broadcast Address", subnet.broadcast)
                    }
                }

                ActionButtonRow(allResultsText = shareContent.trim())
            }
        }
    }
}

@Composable
fun VlsmVisualMap(basePrefix: Int, subnets: List<IPCalculator.VlsmSubnet>) {
    val totalSize = 1L shl (32 - basePrefix)
    val allocatedSize = subnets.sumOf { 1L shl (32 - it.prefix) }
    val freeSize = totalSize - allocatedSize
    
    val colors = listOf(
        MaterialTheme.colorScheme.primary,
        MaterialTheme.colorScheme.secondary,
        MaterialTheme.colorScheme.tertiary,
        MaterialTheme.colorScheme.error
    )
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Interactive IP Allocation Map",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.primary
            )
            
            // Horizontal Bar representing allocated vs free blocks
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(36.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
            ) {
                subnets.forEachIndexed { index, subnet ->
                    val size = 1L shl (32 - subnet.prefix)
                    val color = colors[index % colors.size]
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .weight(size.toFloat())
                            .background(color)
                            .padding(horizontal = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        if (size >= totalSize / 16) {
                            Text(
                                text = "${subnet.name} (/${subnet.prefix})",
                                color = MaterialTheme.colorScheme.onPrimary,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1
                            )
                        }
                    }
                }
                if (freeSize > 0) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .weight(freeSize.toFloat())
                            .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
                            .padding(horizontal = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        if (freeSize >= totalSize / 16) {
                            Text(
                                text = "Free ($freeSize)",
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1
                            )
                        }
                    }
                }
            }
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Utilization: ${"%.1f".format((allocatedSize.toDouble() / totalSize.toDouble()) * 100.0)}%",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Allocated: $allocatedSize / $totalSize IPs",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
