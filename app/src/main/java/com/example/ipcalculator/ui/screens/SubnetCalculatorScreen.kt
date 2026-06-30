package com.example.ipcalculator.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ipcalculator.IPCalculator
import com.example.ipcalculator.ui.components.ActionButtonRow
import com.example.ipcalculator.ui.components.GlowingCard
import com.example.ipcalculator.ui.components.ResultRowWithCopy
import com.example.ipcalculator.ui.components.SectionHeader

@Composable
fun SubnetCalculatorScreen(modifier: Modifier = Modifier) {
    var isIPv4 by rememberSaveable { mutableStateOf(true) }
    
    // IPv4 states
    var ipInput by rememberSaveable { mutableStateOf("192.168.1.1") }
    var prefixInput by rememberSaveable { mutableStateOf("24") }
    var maskInput by rememberSaveable { mutableStateOf("255.255.255.0") }
    
    // IPv6 states
    var ipv6Input by rememberSaveable { mutableStateOf("2001:db8::") }
    var ipv6PrefixInput by rememberSaveable { mutableStateOf("64") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Mode Selector: IPv4 vs IPv6
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
                    onClick = { isIPv4 = true },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isIPv4) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Text(
                        "IPv4",
                        color = if (isIPv4) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                    )
                }

                Button(
                    onClick = { isIPv4 = false },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (!isIPv4) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Text(
                        "IPv6",
                        color = if (!isIPv4) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        // Inputs Card
        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = if (isIPv4) "IPv4 Subnet Calculator" else "IPv6 Subnet Calculator",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                if (isIPv4) {
                    OutlinedTextField(
                        value = ipInput,
                        onValueChange = { ipInput = it },
                        label = { Text("IP Address") },
                        placeholder = { Text("e.g. 192.168.1.1") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        OutlinedTextField(
                            value = prefixInput,
                            onValueChange = {
                                prefixInput = it
                                val cidr = it.toIntOrNull()
                                if (cidr != null && cidr in 0..32) {
                                    val maskLong = IPCalculator.cidrToMask(cidr)
                                    maskInput = IPCalculator.longToIPv4(maskLong)
                                }
                            },
                            label = { Text("Prefix (0-32)") },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )

                        OutlinedTextField(
                            value = maskInput,
                            onValueChange = {
                                maskInput = it
                                val cidr = IPCalculator.maskToCidr(it.trim())
                                if (cidr != null) {
                                    prefixInput = cidr.toString()
                                }
                            },
                            label = { Text("Subnet Mask") },
                            modifier = Modifier.weight(2f),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                        )
                    }

                    // Prefix Slider
                    val sliderVal = prefixInput.toIntOrNull()?.toFloat() ?: 24f
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("CIDR Prefix", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("/${sliderVal.toInt()}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        }
                        Slider(
                            value = sliderVal,
                            onValueChange = {
                                val cidr = it.toInt()
                                prefixInput = cidr.toString()
                                val maskLong = IPCalculator.cidrToMask(cidr)
                                maskInput = IPCalculator.longToIPv4(maskLong)
                            },
                            valueRange = 0f..32f,
                            steps = 31
                        )
                    }
                } else {
                    // IPv6 Inputs
                    OutlinedTextField(
                        value = ipv6Input,
                        onValueChange = { ipv6Input = it },
                        label = { Text("IPv6 Address") },
                        placeholder = { Text("e.g. 2001:db8::") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = ipv6PrefixInput,
                        onValueChange = {
                            val v = it.toIntOrNull()
                            if (it.isEmpty() || (v != null && v in 0..128)) {
                                ipv6PrefixInput = it
                            }
                        },
                        label = { Text("Prefix / CIDR (0-128)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )

                    val sliderVal = ipv6PrefixInput.toIntOrNull()?.toFloat() ?: 64f
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("IPv6 Prefix Length", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("/${sliderVal.toInt()}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        }
                        Slider(
                            value = sliderVal,
                            onValueChange = {
                                ipv6PrefixInput = it.toInt().toString()
                            },
                            valueRange = 0f..128f,
                            steps = 127
                        )
                    }
                }
            }
        }

        // Calculation Outputs
        if (isIPv4) {
            val prefix = prefixInput.toIntOrNull() ?: 24
            val result = IPCalculator.calculateIPv4(ipInput.trim(), prefix)

            if (result != null) {
                // Main results card
                GlowingCard {
                    SectionHeader(title = "Calculated Subnet Details")

                    ResultRowWithCopy("IP Class", result.ipClass)
                    ResultRowWithCopy("IP Type", result.ipType)
                    ResultRowWithCopy("Network Address", "${result.networkAddress}/${result.prefix}")
                    ResultRowWithCopy("Subnet Mask", result.subnetMask)
                    ResultRowWithCopy("Wildcard Mask", result.wildcardMask)
                    ResultRowWithCopy("Broadcast Address", result.broadcastAddress)
                    ResultRowWithCopy("Usable Host Range", "${result.usableRangeStart} - ${result.usableRangeEnd}")
                    ResultRowWithCopy("Usable Hosts", "${result.usableHosts} (Total: ${result.totalHosts})")
                    
                    val shareText = """
                        IPv4 Subnet Calculation:
                        IP Address: ${result.ip}
                        Prefix: /${result.prefix}
                        Class: ${result.ipClass}
                        Type: ${result.ipType}
                        Network: ${result.networkAddress}
                        Netmask: ${result.subnetMask}
                        Wildcard: ${result.wildcardMask}
                        Broadcast: ${result.broadcastAddress}
                        Range: ${result.usableRangeStart} - ${result.usableRangeEnd}
                        Usable Hosts: ${result.usableHosts}
                    """.trimIndent()
                    
                    ActionButtonRow(allResultsText = shareText)
                }

                // Binary Visualization Card
                GlowingCard {
                    SectionHeader(title = "Binary Representation (Network vs Host)")

                    BinaryRow("IP Address", result.ipBinary, result.prefix)
                    BinaryRow("Subnet Mask", result.maskBinary, result.prefix)
                    BinaryRow("Network Addr", result.networkBinary, result.prefix)
                    BinaryRow("Broadcast", result.broadcastBinary, result.prefix)
                }
            } else {
                Text(
                    "Invalid IPv4 Address format.",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
        } else {
            val prefix = ipv6PrefixInput.toIntOrNull() ?: 64
            val result = IPCalculator.calculateIPv6(ipv6Input.trim(), prefix)

            if (result != null) {
                GlowingCard {
                    SectionHeader(title = "Calculated IPv6 Details")

                    ResultRowWithCopy("IP Type", result.type)
                    ResultRowWithCopy("Compressed IP", result.compressed)
                    ResultRowWithCopy("Expanded IP", result.expanded)
                    ResultRowWithCopy("Network Address", "${result.networkAddress}/${result.prefix}")
                    ResultRowWithCopy("Range Start", result.rangeStart)
                    ResultRowWithCopy("Range End", result.rangeEnd)
                    ResultRowWithCopy("Total Hosts", result.totalHosts)
                    
                    val shareText = """
                        IPv6 Subnet Calculation:
                        IP Address: ${result.ip}
                        Prefix: /${result.prefix}
                        Type: ${result.type}
                        Network: ${result.networkAddress}
                        Range: ${result.rangeStart} - ${result.rangeEnd}
                        Total Hosts: ${result.totalHosts}
                    """.trimIndent()
                    
                    ActionButtonRow(allResultsText = shareText)
                }
            } else {
                Text(
                    "Invalid IPv6 Address format.",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
        }
    }
}

@Composable
fun BinaryRow(label: String, binaryValue: String, prefix: Int) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.background)
                .border(BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)), RoundedCornerShape(8.dp))
                .padding(12.dp)
        ) {
            val coloredText = buildColoredBinaryString(binaryValue, prefix)
            Text(
                text = coloredText,
                fontFamily = FontFamily.Monospace,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
fun buildColoredBinaryString(binaryStr: String, prefix: Int): AnnotatedString {
    val builder = AnnotatedString.Builder()
    var bitCount = 0
    val primaryColor = MaterialTheme.colorScheme.primary // Neon Cyan for Network
    val hostColor = MaterialTheme.colorScheme.tertiary // Bright Pink for Host
    val dotColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
    
    for (char in binaryStr) {
        if (char == '.') {
            builder.pushStyle(SpanStyle(color = dotColor))
            builder.append(char)
            builder.pop()
        } else {
            val color = if (bitCount < prefix) primaryColor else hostColor
            builder.pushStyle(SpanStyle(color = color, fontWeight = FontWeight.Bold))
            builder.append(char)
            builder.pop()
            bitCount++
        }
    }
    return builder.toAnnotatedString()
}
