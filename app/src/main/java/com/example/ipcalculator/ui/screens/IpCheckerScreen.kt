package com.example.ipcalculator.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ipcalculator.IPCalculator
import com.example.ipcalculator.ui.components.SharedComponents.GlowingCard
import com.example.ipcalculator.ui.components.SharedComponents.ResultRowWithCopy
import com.example.ipcalculator.ui.components.SharedComponents.SectionHeader

@Composable
fun IpCheckerScreen(modifier: Modifier = Modifier) {
    var ipInput by rememberSaveable { mutableStateOf("") }
    var networkInput by rememberSaveable { mutableStateOf("") }
    var hasChecked by rememberSaveable { mutableStateOf(false) }
    var isInSubnet by rememberSaveable { mutableStateOf(false) }
    var errorMessage by rememberSaveable { mutableStateOf<String?>(null) }

    // Parsed values for display
    var parsedNetwork by rememberSaveable { mutableStateOf("") }
    var parsedPrefix by rememberSaveable { mutableStateOf(0) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Input Card
        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Is IP in Subnet?",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                OutlinedTextField(
                    value = ipInput,
                    onValueChange = {
                        ipInput = it
                        hasChecked = false
                        errorMessage = null
                    },
                    label = { Text("IP Address") },
                    placeholder = { Text("e.g. 192.168.1.50") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                    shape = RoundedCornerShape(12.dp)
                )

                OutlinedTextField(
                    value = networkInput,
                    onValueChange = {
                        networkInput = it
                        hasChecked = false
                        errorMessage = null
                    },
                    label = { Text("Network / CIDR") },
                    placeholder = { Text("e.g. 192.168.1.0/24") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                    shape = RoundedCornerShape(12.dp)
                )

                Button(
                    onClick = {
                        errorMessage = null
                        hasChecked = false

                        val trimmedIp = ipInput.trim()
                        val trimmedNetwork = networkInput.trim()

                        // Parse network/CIDR
                        val parts = trimmedNetwork.split("/")
                        if (parts.size != 2) {
                            errorMessage = "Invalid format. Use network/prefix (e.g. 192.168.1.0/24)"
                            hasChecked = true
                            return@Button
                        }

                        val netAddr = parts[0].trim()
                        val prefix = parts[1].trim().toIntOrNull()

                        if (!IPCalculator.isValidIPv4(trimmedIp)) {
                            errorMessage = "Invalid IP address."
                            hasChecked = true
                            return@Button
                        }

                        if (!IPCalculator.isValidIPv4(netAddr) || prefix == null || prefix !in 0..32) {
                            errorMessage = "Invalid network address or prefix."
                            hasChecked = true
                            return@Button
                        }

                        parsedNetwork = netAddr
                        parsedPrefix = prefix
                        isInSubnet = IPCalculator.isIpInSubnet(trimmedIp, netAddr, prefix)
                        hasChecked = true
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Check", fontWeight = FontWeight.Bold)
                }
            }
        }

        // Error display
        AnimatedVisibility(
            visible = hasChecked && errorMessage != null,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.elevatedCardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Text(
                    text = errorMessage ?: "",
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }

        // Result display
        AnimatedVisibility(
            visible = hasChecked && errorMessage == null,
            enter = fadeIn() + slideInVertically(initialOffsetY = { it / 3 }),
            exit = fadeOut()
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                // Status card
                ElevatedCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.elevatedCardColors(
                        containerColor = if (isInSubnet)
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                        else
                            MaterialTheme.colorScheme.tertiary.copy(alpha = 0.1f)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = if (isInSubnet) "✅" else "❌",
                            fontSize = 28.sp
                        )
                        Spacer(modifier = Modifier.padding(horizontal = 8.dp))
                        Column {
                            Text(
                                text = if (isInSubnet) "Yes" else "No",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = if (isInSubnet)
                                    MaterialTheme.colorScheme.primary
                                else
                                    MaterialTheme.colorScheme.tertiary
                            )
                            Text(
                                text = if (isInSubnet)
                                    "IP is within the subnet"
                                else
                                    "IP is NOT within the subnet",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // Subnet details when IP is in subnet
                if (isInSubnet) {
                    val subnetResult = IPCalculator.calculateIPv4(parsedNetwork, parsedPrefix)
                    if (subnetResult != null) {
                        GlowingCard {
                            SectionHeader(title = "Subnet Details")

                            Spacer(modifier = Modifier.height(4.dp))

                            ResultRowWithCopy(
                                label = "Network Address",
                                value = "${subnetResult.networkAddress}/${subnetResult.prefix}"
                            )
                            ResultRowWithCopy(
                                label = "Subnet Mask",
                                value = subnetResult.subnetMask
                            )
                            ResultRowWithCopy(
                                label = "Broadcast Address",
                                value = subnetResult.broadcastAddress
                            )
                            ResultRowWithCopy(
                                label = "Usable Range",
                                value = "${subnetResult.usableRangeStart} – ${subnetResult.usableRangeEnd}"
                            )
                            ResultRowWithCopy(
                                label = "Total Hosts",
                                value = "${subnetResult.totalHosts} (${subnetResult.usableHosts} usable)"
                            )
                            ResultRowWithCopy(
                                label = "IP Class",
                                value = subnetResult.ipClass
                            )
                        }
                    }
                }
            }
        }
    }
}
