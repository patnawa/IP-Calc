package com.example.ipcalculator.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ipcalculator.IPCalculator
import com.example.ipcalculator.Translator
import com.example.ipcalculator.ui.components.ActionButtonRow
import com.example.ipcalculator.ui.components.GlowingCard
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.BorderStroke
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import com.example.ipcalculator.ui.components.ResultRowWithCopy
import com.example.ipcalculator.ui.components.SectionHeader

data class DesignTemplate(
    val title: String,
    val description: String,
    val requirements: List<Pair<String, Int>>
)

@Composable
fun DesignWizardScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    var baseIp by rememberSaveable { mutableStateOf("192.168.0.0") }
    var basePrefix by rememberSaveable { mutableStateOf("23") }
    
    var selectedTemplateIndex by rememberSaveable { mutableStateOf(0) }
    var wizardResult by remember { mutableStateOf<List<IPCalculator.VlsmSubnet>?>(null) }
    var errorMsg by remember { mutableStateOf<String?>(null) }
    var calculated by rememberSaveable { mutableStateOf(false) }

    val templates = listOf(
        DesignTemplate(
            title = "Corporate VLANs Office",
            description = "Standard corporate office network design allocating specific VLAN ranges for departments and guests.",
            requirements = listOf(
                Pair("Staff & Sales", 120),
                Pair("IT & Server Admin", 25),
                Pair("Human Resources", 12),
                Pair("Management", 6),
                Pair("Guest Wi-Fi Net", 250)
            )
        ),
        DesignTemplate(
            title = "Startup Tech Office",
            description = "Lightweight startup layout with heavy engineering subnet sizes and public dmz segments.",
            requirements = listOf(
                Pair("Engineering Devs", 45),
                Pair("Ops & Servers Lab", 15),
                Pair("Finance & Marketing", 10),
                Pair("Office IoT Devices", 30),
                Pair("Public DMZ", 2)
            )
        ),
        DesignTemplate(
            title = "Smart Home IoT Grid",
            description = "Home network subnetting segmenting IoT devices from personal PCs and guest usage.",
            requirements = listOf(
                Pair("IoT Smart Devices", 60),
                Pair("Personal computers", 15),
                Pair("Guest Wi-Fi network", 10),
                Pair("Home NAS Storage", 4)
            )
        )
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Base Network Info
        ElevatedCard(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Subnet Design Template Wizard",
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
                        placeholder = { Text("e.g. 192.168.0.0") },
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

                // Slider
                val sliderVal = basePrefix.toIntOrNull()?.toFloat() ?: 23f
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Base Network Prefix", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("/${sliderVal.toInt()}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    }
                    Slider(
                        value = sliderVal,
                        onValueChange = { basePrefix = it.toInt().toString() },
                        valueRange = 0f..32f,
                        steps = 31
                    )
                }
            }
        }

        // Template selector cards
        Text(
            text = "Select Design Template",
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary
        )

        templates.forEachIndexed { index, template ->
            val isSelected = selectedTemplateIndex == index
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { 
                        selectedTemplateIndex = index 
                        calculated = false
                        wizardResult = null
                        errorMsg = null
                    },
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                ),
                shape = RoundedCornerShape(12.dp)
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
                            text = template.title,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                        )
                        RadioButton(
                            selected = isSelected,
                            onClick = { 
                                selectedTemplateIndex = index 
                                calculated = false
                                wizardResult = null
                                errorMsg = null
                            }
                        )
                    }
                    Text(
                        text = template.description,
                        fontSize = 12.sp,
                        color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f) else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    Text(
                        text = "VLAN segments required: " + template.requirements.joinToString(", ") { "${it.first} (${it.second} hosts)" },
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        Button(
            onClick = {
                calculated = true
                wizardResult = null
                errorMsg = null
                val prefixInt = basePrefix.toIntOrNull() ?: 23
                if (!IPCalculator.isValidIPv4(baseIp.trim())) {
                    errorMsg = Translator.t("error_invalid_ipv4")
                    return@Button
                }
                
                val result = IPCalculator.calculateVLSM(
                    baseIpStr = baseIp.trim(),
                    basePrefix = prefixInt,
                    requirements = templates[selectedTemplateIndex].requirements
                )
                if (result == null) {
                    errorMsg = "Design Allocation Failed! The selected base network /$prefixInt is too small to fit the requirements of the template."
                } else {
                    wizardResult = result
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Generate Subnet Design Layout")
        }

        // Show error message
        AnimatedVisibility(visible = calculated && errorMsg != null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.errorContainer)
                    .padding(16.dp)
            ) {
                Text(
                    text = errorMsg ?: "",
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Show Results
        AnimatedVisibility(visible = calculated && wizardResult != null) {
            val prefixInt = basePrefix.toIntOrNull() ?: 23
            
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                VlsmVisualMap(basePrefix = prefixInt, subnets = wizardResult!!)
                
                Text(
                    text = "Generated Network Allocations",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )

                var shareContent = "Subnet Design Report - ${templates[selectedTemplateIndex].title}\nBase Network: $baseIp/$basePrefix\n\n"

                wizardResult!!.forEach { subnet ->
                    val subnetText = """
                        Department: ${subnet.name} (/${subnet.prefix})
                        Subnet IP Address: ${subnet.subnetAddress}
                        Subnet Mask: ${subnet.mask}
                        Usable Range: ${subnet.rangeStart} - ${subnet.rangeEnd}
                        Broadcast Address: ${subnet.broadcast}
                        Hosts: Required: ${subnet.requestedHosts} | Allocated: ${subnet.allocatedHosts}
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
                        
                        SectionHeader(title = "Network Boundaries")

                        ResultRowWithCopy(Translator.t("network_address"), subnet.subnetAddress)
                        ResultRowWithCopy(Translator.t("subnet_mask"), subnet.mask)
                        ResultRowWithCopy(Translator.t("usable_range"), "${subnet.rangeStart} - ${subnet.rangeEnd}")
                        ResultRowWithCopy(Translator.t("broadcast_address"), subnet.broadcast)
                        ResultRowWithCopy(Translator.t("usable_hosts"), "${subnet.allocatedHosts} / ${subnet.requestedHosts}")
                    }
                }
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = {
                            val uri = IPCalculator.exportVlsmAsImage(context, baseIp, prefixInt, wizardResult!!)
                            if (uri != null) {
                                val intent = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
                                    type = "image/png"
                                    putExtra(android.content.Intent.EXTRA_STREAM, uri)
                                    addFlags(android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                }
                                context.startActivity(android.content.Intent.createChooser(intent, "Share Subnet Plan Image"))
                            } else {
                                Toast.makeText(context, "Failed to export plan", Toast.LENGTH_SHORT).show()
                            }
                        },
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.tertiary.copy(alpha = 0.5f)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Export Image", fontSize = 13.sp)
                    }
                    ActionButtonRow(allResultsText = shareContent.trim())
                }
            }
        }
    }
}
