package com.example.ipcalculator.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ipcalculator.IPCalculator
import com.example.ipcalculator.ui.components.GlowingCard
import com.example.ipcalculator.ui.components.ResultRowWithCopy
import com.example.ipcalculator.ui.components.SectionHeader
import com.example.ipcalculator.ui.components.ActionButtonRow

@Composable
fun MacLookupScreen() {
    var macInput by rememberSaveable { mutableStateOf("") }
    
    var calculated by rememberSaveable { mutableStateOf(false) }
    var vendor by rememberSaveable { mutableStateOf("") }
    var formattedMacs by remember { mutableStateOf<Map<String, String>>(emptyMap()) }
    var errorMsg by rememberSaveable { mutableStateOf<String?>(null) }
    
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
                    text = "MAC Vendor OUI Lookup",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Find the manufacturer (vendor) of a network device and convert the MAC address to standard notation formats.",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                OutlinedTextField(
                    value = macInput,
                    onValueChange = { 
                        macInput = it
                        calculated = false
                        errorMsg = null
                    },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("MAC Address") },
                    placeholder = { Text("e.g. 00-1A-11-F0-AA-11 or 080027112233") },
                    singleLine = true
                )

                Button(
                    onClick = {
                        val cleaned = macInput.replace(Regex("[^0-9a-fA-F]"), "")
                        if (cleaned.length >= 6) {
                            vendor = IPCalculator.lookupMacOui(cleaned)
                            val forms = IPCalculator.formatMacAddress(cleaned)
                            if (forms.isNotEmpty()) {
                                formattedMacs = forms
                                calculated = true
                                errorMsg = null
                            } else {
                                calculated = true
                                formattedMacs = emptyMap() // only OUI lookup
                            }
                        } else {
                            errorMsg = "MAC Address must be at least 6 hex characters (for OUI)."
                            calculated = false
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    enabled = macInput.isNotEmpty()
                ) {
                    Text("Lookup & Format", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
                
                errorMsg?.let { msg ->
                    Text(msg, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(vertical = 4.dp))
                }
            }
        }

        AnimatedVisibility(
            visible = calculated,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                // Vendor Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.12f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Vendor (OUI Registry)", fontSize = 11.sp, color = MaterialTheme.colorScheme.secondary, fontWeight = FontWeight.Bold)
                        Text(vendor, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.onSurface)
                    }
                }

                // Format Conversions Card
                if (formattedMacs.isNotEmpty()) {
                    GlowingCard {
                        SectionHeader(title = "MAC Address Formats")
                        
                        ResultRowWithCopy("Standard Colon Notation", formattedMacs["colon"] ?: "")
                        ResultRowWithCopy("Windows Hyphen Notation", formattedMacs["hyphen"] ?: "")
                        ResultRowWithCopy("Cisco Dot Notation", formattedMacs["dot"] ?: "")
                        ResultRowWithCopy("Raw Hexadecimal", formattedMacs["raw"] ?: "")
                        
                        val shareText = """
                            MAC Address Vendor Report:
                            MAC: ${formattedMacs["colon"]}
                            Vendor: $vendor
                            Formats:
                            - Hyphen: ${formattedMacs["hyphen"]}
                            - Cisco: ${formattedMacs["dot"]}
                            - Raw: ${formattedMacs["raw"]}
                        """.trimIndent()
                        
                        ActionButtonRow(allResultsText = shareText)
                    }
                }
            }
        }
    }
}
