package com.example.ipcalculator.ui.screens

import android.widget.Toast
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ipcalculator.IPCalculator

@Composable
fun SubnetCalculatorScreen(modifier: Modifier = Modifier) {
    var isIPv4 by remember { mutableStateOf(true) }
    var ipInput by remember { mutableStateOf("192.168.1.1") }
    var prefixInput by remember { mutableStateOf("24") }
    
    // IPv6 states
    var ipv6Input by remember { mutableStateOf("2001:db8::") }
    var ipv6PrefixInput by remember { mutableStateOf("64") }

    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

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
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = prefixInput,
                            onValueChange = {
                                val intVal = it.toIntOrNull()
                                if (it.isEmpty() || (intVal != null && intVal in 0..32)) {
                                    prefixInput = it
                                }
                            },
                            label = { Text("CIDR Prefix (0-32)") },
                            modifier = Modifier.width(120.dp),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )

                        val currentSliderVal = prefixInput.toFloatOrNull() ?: 24f
                        Slider(
                            value = currentSliderVal,
                            onValueChange = { prefixInput = it.toInt().toString() },
                            valueRange = 0f..32f,
                            modifier = Modifier.weight(1f)
                        )
                    }
                } else {
                    OutlinedTextField(
                        value = ipv6Input,
                        onValueChange = { ipv6Input = it },
                        label = { Text("IPv6 Address") },
                        placeholder = { Text("e.g. 2001:db8::") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = ipv6PrefixInput,
                            onValueChange = {
                                val intVal = it.toIntOrNull()
                                if (it.isEmpty() || (intVal != null && intVal in 0..128)) {
                                    ipv6PrefixInput = it
                                }
                            },
                            label = { Text("Prefix (0-128)") },
                            modifier = Modifier.width(120.dp),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )

                        val currentSliderVal = ipv6PrefixInput.toFloatOrNull() ?: 64f
                        Slider(
                            value = currentSliderVal,
                            onValueChange = { ipv6PrefixInput = it.toInt().toString() },
                            valueRange = 0f..128f,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // Calculation Results
        if (isIPv4) {
            val prefix = prefixInput.toIntOrNull() ?: 24
            val result = IPCalculator.calculateIPv4(ipInput.trim(), prefix)

            if (result != null) {
                // Results Card
                ElevatedCard(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            "Subnet Details",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )

                        HorizontalDivider()

                        ResultRow("IP Class", result.ipClass, clipboardManager)
                        ResultRow("IP Type", result.ipType, clipboardManager)
                        ResultRow("Network Address", "${result.networkAddress}/${result.prefix}", clipboardManager)
                        ResultRow("Subnet Mask", result.subnetMask, clipboardManager)
                        ResultRow("Wildcard Mask", result.wildcardMask, clipboardManager)
                        ResultRow("Broadcast Address", result.broadcastAddress, clipboardManager)
                        ResultRow("Usable Host Range", "${result.usableRangeStart} - ${result.usableRangeEnd}", clipboardManager)
                        ResultRow("Usable Hosts", "${result.usableHosts} (Total: ${result.totalHosts})", clipboardManager)
                    }
                }

                // Binary Visualization Card
                ElevatedCard(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            "Binary Representation (Network vs Host)",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )

                        HorizontalDivider()

                        BinaryRow("IP Address", result.ipBinary, result.prefix)
                        BinaryRow("Subnet Mask", result.maskBinary, result.prefix)
                        BinaryRow("Network Addr", result.networkBinary, result.prefix)
                        BinaryRow("Broadcast", result.broadcastBinary, result.prefix)
                    }
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
                ElevatedCard(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            "Subnet Details",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )

                        HorizontalDivider()

                        ResultRow("IP Type", result.type, clipboardManager)
                        ResultRow("Compressed IP", result.compressed, clipboardManager)
                        ResultRow("Expanded IP", result.expanded, clipboardManager)
                        ResultRow("Network Address", "${result.networkAddress}/${result.prefix}", clipboardManager)
                        ResultRow("Range Start", result.rangeStart, clipboardManager)
                        ResultRow("Range End", result.rangeEnd, clipboardManager)
                        ResultRow("Total Hosts", result.totalHosts, clipboardManager)
                    }
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
fun ResultRow(
    label: String,
    value: String,
    clipboardManager: androidx.compose.ui.platform.ClipboardManager
) {
    val context = LocalContext.current
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        
        TextButton(
            onClick = {
                clipboardManager.setText(AnnotatedString(value))
                Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_SHORT).show()
            }
        ) {
            Text("Copy", fontSize = 12.sp)
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
