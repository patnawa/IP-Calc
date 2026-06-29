package com.example.ipcalculator.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ipcalculator.IPCalculator
import com.example.ipcalculator.ui.components.SharedComponents.GlowingCard
import com.example.ipcalculator.ui.components.SharedComponents.SectionHeader

@Composable
fun SubnetCompareScreen(modifier: Modifier = Modifier) {
    var ipA by rememberSaveable { mutableStateOf("") }
    var prefixA by rememberSaveable { mutableStateOf("24") }
    var ipB by rememberSaveable { mutableStateOf("") }
    var prefixB by rememberSaveable { mutableStateOf("24") }

    var hasCompared by rememberSaveable { mutableStateOf(false) }
    var errorMessage by rememberSaveable { mutableStateOf<String?>(null) }

    // We can't store complex objects in rememberSaveable, so we store the inputs and recompute
    var comparedIpA by rememberSaveable { mutableStateOf("") }
    var comparedPrefixA by rememberSaveable { mutableStateOf(0) }
    var comparedIpB by rememberSaveable { mutableStateOf("") }
    var comparedPrefixB by rememberSaveable { mutableStateOf(0) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Subnet A Input
        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Subnet A",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = ipA,
                        onValueChange = {
                            ipA = it
                            hasCompared = false
                            errorMessage = null
                        },
                        label = { Text("IP Address") },
                        placeholder = { Text("e.g. 192.168.1.0") },
                        modifier = Modifier.weight(2f),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                        shape = RoundedCornerShape(12.dp)
                    )

                    OutlinedTextField(
                        value = prefixA,
                        onValueChange = {
                            val v = it.toIntOrNull()
                            if (it.isEmpty() || (v != null && v in 0..32)) {
                                prefixA = it
                                hasCompared = false
                                errorMessage = null
                            }
                        },
                        label = { Text("/ Prefix") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }
        }

        // Subnet B Input
        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Subnet B",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.secondary
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = ipB,
                        onValueChange = {
                            ipB = it
                            hasCompared = false
                            errorMessage = null
                        },
                        label = { Text("IP Address") },
                        placeholder = { Text("e.g. 10.0.0.0") },
                        modifier = Modifier.weight(2f),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                        shape = RoundedCornerShape(12.dp)
                    )

                    OutlinedTextField(
                        value = prefixB,
                        onValueChange = {
                            val v = it.toIntOrNull()
                            if (it.isEmpty() || (v != null && v in 0..32)) {
                                prefixB = it
                                hasCompared = false
                                errorMessage = null
                            }
                        },
                        label = { Text("/ Prefix") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }
        }

        // Compare Button
        Button(
            onClick = {
                errorMessage = null
                hasCompared = false

                val trimA = ipA.trim()
                val trimB = ipB.trim()
                val pA = prefixA.toIntOrNull()
                val pB = prefixB.toIntOrNull()

                if (!IPCalculator.isValidIPv4(trimA)) {
                    errorMessage = "Invalid IP address for Subnet A."
                    hasCompared = true
                    return@Button
                }
                if (!IPCalculator.isValidIPv4(trimB)) {
                    errorMessage = "Invalid IP address for Subnet B."
                    hasCompared = true
                    return@Button
                }
                if (pA == null || pA !in 0..32) {
                    errorMessage = "Invalid prefix for Subnet A (0-32)."
                    hasCompared = true
                    return@Button
                }
                if (pB == null || pB !in 0..32) {
                    errorMessage = "Invalid prefix for Subnet B (0-32)."
                    hasCompared = true
                    return@Button
                }

                comparedIpA = trimA
                comparedPrefixA = pA
                comparedIpB = trimB
                comparedPrefixB = pB
                hasCompared = true
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Compare", fontWeight = FontWeight.Bold)
        }

        // Error
        AnimatedVisibility(
            visible = hasCompared && errorMessage != null,
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

        // Results
        AnimatedVisibility(
            visible = hasCompared && errorMessage == null,
            enter = fadeIn() + slideInVertically(initialOffsetY = { it / 3 }),
            exit = fadeOut()
        ) {
            val comparison = IPCalculator.compareSubnets(
                comparedIpA, comparedPrefixA,
                comparedIpB, comparedPrefixB
            )

            if (comparison != null) {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    // Overlap Analysis Card
                    val overlapText = when {
                        comparison.aContainsB && comparison.bContainsA -> "🟰  Identical Subnets"
                        comparison.aContainsB -> "🔵  Subnet A contains Subnet B"
                        comparison.bContainsA -> "🟣  Subnet B contains Subnet A"
                        comparison.overlaps -> "⚠️  Overlapping Subnets"
                        else -> "✅  No Overlap"
                    }

                    val overlapColor = when {
                        comparison.aContainsB || comparison.bContainsA -> MaterialTheme.colorScheme.secondary
                        comparison.overlaps -> MaterialTheme.colorScheme.tertiary
                        else -> MaterialTheme.colorScheme.primary
                    }

                    ElevatedCard(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.elevatedCardColors(
                            containerColor = overlapColor.copy(alpha = 0.1f)
                        )
                    ) {
                        Text(
                            text = overlapText,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = overlapColor,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            textAlign = TextAlign.Center
                        )
                    }

                    // Side-by-Side Comparison
                    GlowingCard {
                        SectionHeader(title = "Comparison Details")
                        Spacer(modifier = Modifier.height(4.dp))

                        ComparisonRow(
                            label = "Network Address",
                            valueA = "${comparison.subnetA.networkAddress}/${comparison.subnetA.prefix}",
                            valueB = "${comparison.subnetB.networkAddress}/${comparison.subnetB.prefix}"
                        )
                        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))

                        ComparisonRow(
                            label = "Subnet Mask",
                            valueA = comparison.subnetA.subnetMask,
                            valueB = comparison.subnetB.subnetMask
                        )
                        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))

                        ComparisonRow(
                            label = "Broadcast",
                            valueA = comparison.subnetA.broadcastAddress,
                            valueB = comparison.subnetB.broadcastAddress
                        )
                        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))

                        ComparisonRow(
                            label = "Usable Range",
                            valueA = "${comparison.subnetA.usableRangeStart}\n– ${comparison.subnetA.usableRangeEnd}",
                            valueB = "${comparison.subnetB.usableRangeStart}\n– ${comparison.subnetB.usableRangeEnd}"
                        )
                        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))

                        ComparisonRow(
                            label = "Total Hosts",
                            valueA = comparison.subnetA.totalHosts.toString(),
                            valueB = comparison.subnetB.totalHosts.toString()
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ComparisonRow(label: String, valueA: String, valueB: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Subnet A value
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.08f))
                    .padding(8.dp)
            ) {
                Column {
                    Text(
                        text = "A",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = valueA,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            // Subnet B value
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.08f))
                    .padding(8.dp)
            ) {
                Column {
                    Text(
                        text = "B",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Text(
                        text = valueB,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}
