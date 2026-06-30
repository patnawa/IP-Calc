package com.example.ipcalculator.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ipcalculator.IPCalculator
import com.example.ipcalculator.Translator
import com.example.ipcalculator.ui.components.ActionButtonRow
import com.example.ipcalculator.ui.components.GlowingCard
import com.example.ipcalculator.ui.components.ResultRowWithCopy
import com.example.ipcalculator.ui.components.SectionHeader

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AclGeneratorScreen(modifier: Modifier = Modifier) {
    var networkIp by rememberSaveable { mutableStateOf("192.168.1.0") }
    var prefix by rememberSaveable { mutableStateOf("24") }
    
    var aclAction by rememberSaveable { mutableStateOf("permit") }
    var protocol by rememberSaveable { mutableStateOf("ip") }
    var destType by rememberSaveable { mutableStateOf("any") }
    var destIp by rememberSaveable { mutableStateOf("") }
    
    var aclActionExpanded by remember { mutableStateOf(false) }
    var protocolExpanded by remember { mutableStateOf(false) }
    var destTypeExpanded by remember { mutableStateOf(false) }
    
    val actionsList = listOf("permit", "deny")
    val protocolsList = listOf("ip", "tcp", "udp", "icmp")
    val destTypesList = listOf("any", "host")

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
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
                    text = "Cisco ACL & Wildcard Generator",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                OutlinedTextField(
                    value = networkIp,
                    onValueChange = { networkIp = it },
                    label = { Text("Source Network IP") },
                    placeholder = { Text("e.g. 192.168.1.0") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                )

                OutlinedTextField(
                    value = prefix,
                    onValueChange = {
                        val v = it.toIntOrNull()
                        if (it.isEmpty() || (v != null && v in 0..32)) {
                            prefix = it
                        }
                    },
                    label = { Text("Source Prefix / CIDR") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                // Slider for prefix
                val sliderVal = prefix.toIntOrNull()?.toFloat() ?: 24f
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Source Prefix Length", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("/${sliderVal.toInt()}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    }
                    Slider(
                        value = sliderVal,
                        onValueChange = { prefix = it.toInt().toString() },
                        valueRange = 0f..32f,
                        steps = 31
                    )
                }

                HorizontalDivider()

                // ACL Configurations
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Action dropdown
                    ExposedDropdownMenuBox(
                        expanded = aclActionExpanded,
                        onExpandedChange = { aclActionExpanded = it },
                        modifier = Modifier.weight(1f)
                    ) {
                        OutlinedTextField(
                            value = aclAction.uppercase(),
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("ACL Action") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = aclActionExpanded) },
                            modifier = Modifier.menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = aclActionExpanded,
                            onDismissRequest = { aclActionExpanded = false }
                        ) {
                            actionsList.forEach { action ->
                                DropdownMenuItem(
                                    text = { Text(action.uppercase()) },
                                    onClick = {
                                        aclAction = action
                                        aclActionExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    // Protocol dropdown
                    ExposedDropdownMenuBox(
                        expanded = protocolExpanded,
                        onExpandedChange = { protocolExpanded = it },
                        modifier = Modifier.weight(1f)
                    ) {
                        OutlinedTextField(
                            value = protocol.uppercase(),
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Protocol") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = protocolExpanded) },
                            modifier = Modifier.menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = protocolExpanded,
                            onDismissRequest = { protocolExpanded = false }
                        ) {
                            protocolsList.forEach { proto ->
                                DropdownMenuItem(
                                    text = { Text(proto.uppercase()) },
                                    onClick = {
                                        protocol = proto
                                        protocolExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Destination Type
                    ExposedDropdownMenuBox(
                        expanded = destTypeExpanded,
                        onExpandedChange = { destTypeExpanded = it },
                        modifier = Modifier.weight(1f)
                    ) {
                        OutlinedTextField(
                            value = destType.uppercase(),
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Dest Type") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = destTypeExpanded) },
                            modifier = Modifier.menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = destTypeExpanded,
                            onDismissRequest = { destTypeExpanded = false }
                        ) {
                            destTypesList.forEach { type ->
                                DropdownMenuItem(
                                    text = { Text(type.uppercase()) },
                                    onClick = {
                                        destType = type
                                        destTypeExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    // Destination Host IP
                    AnimatedVisibility(
                        visible = destType == "host",
                        modifier = Modifier.weight(1.5f)
                    ) {
                        OutlinedTextField(
                            value = destIp,
                            onValueChange = { destIp = it },
                            label = { Text("Dest Host IP") },
                            placeholder = { Text("e.g. 8.8.8.8") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                        )
                    }
                }
            }
        }

        // Output Card
        val pVal = prefix.toIntOrNull() ?: 24
        val isValid = IPCalculator.isValidIPv4(networkIp.trim())
        
        if (isValid) {
            val wildcard = IPCalculator.calculateWildcard(pVal)
            val aclCommand = IPCalculator.generateCiscoAcl(
                network = networkIp.trim(),
                prefix = pVal,
                action = aclAction,
                protocol = protocol,
                destType = destType,
                destIp = destIp.trim()
            )

            GlowingCard {
                SectionHeader(title = "Generated Cisco IOS ACL")

                ResultRowWithCopy("Wildcard Mask", wildcard)
                ResultRowWithCopy("Access List Rule", aclCommand)

                val shareText = """
                    Cisco Wildcard & ACL Configuration:
                    Network: ${networkIp.trim()}/$pVal
                    Wildcard Mask: $wildcard
                    Cisco ACL Command:
                    $aclCommand
                """.trimIndent()

                Spacer(modifier = Modifier.height(8.dp))
                ActionButtonRow(allResultsText = shareText)
            }
        } else {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = androidx.compose.ui.Alignment.Center
            ) {
                Text(
                    text = Translator.t("error_invalid_ipv4"),
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}
