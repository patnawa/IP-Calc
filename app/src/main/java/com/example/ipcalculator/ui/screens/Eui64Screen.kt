package com.example.ipcalculator.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ipcalculator.IPCalculator
import com.example.ipcalculator.ui.components.GlowingCard
import com.example.ipcalculator.ui.components.ResultRowWithCopy
import com.example.ipcalculator.ui.components.SectionHeader
import com.example.ipcalculator.ui.components.ActionButtonRow
import java.util.Locale

@Composable
fun Eui64Screen() {
    var macInput by rememberSaveable { mutableStateOf("") }
    var prefixInput by rememberSaveable { mutableStateOf("fe80::") }
    
    var calculated by rememberSaveable { mutableStateOf(false) }
    var resultEui by rememberSaveable { mutableStateOf("") }
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
                    text = "IPv6 EUI-64 Generator",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Generate IPv6 address from a MAC Address using the EUI-64 standard.",
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
                    placeholder = { Text("e.g. 00:11:22:33:44:55") },
                    singleLine = true
                )

                OutlinedTextField(
                    value = prefixInput,
                    onValueChange = { 
                        prefixInput = it
                        calculated = false
                        errorMsg = null
                    },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("IPv6 Prefix (/64)") },
                    placeholder = { Text("e.g. fe80:: or 2001:db8::") },
                    singleLine = true
                )

                Button(
                    onClick = {
                        val eui = IPCalculator.calculateEui64(macInput.trim(), prefixInput.trim())
                        if (eui != null) {
                            resultEui = eui
                            calculated = true
                            errorMsg = null
                        } else {
                            errorMsg = "Invalid MAC Address or IPv6 Prefix format."
                            calculated = false
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    enabled = macInput.isNotEmpty() && prefixInput.isNotEmpty()
                ) {
                    Text("Generate EUI-64", fontSize = 16.sp, fontWeight = FontWeight.Bold)
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
                // Result Card
                GlowingCard {
                    SectionHeader(title = "Calculated IPv6 Address")
                    
                    ResultRowWithCopy("IPv6 Address (EUI-64)", resultEui)
                    
                    val cleanMac = macInput.replace(Regex("[^0-9a-fA-F]"), "").uppercase(Locale.ROOT)
                    if (cleanMac.length == 12) {
                        ResultRowWithCopy("Interface ID", "${resultEui.split(":").takeLast(4).joinToString(":")}")
                    }
                    
                    val shareText = """
                        IPv6 EUI-64 Address:
                        MAC: $macInput
                        Prefix: $prefixInput
                        Result IPv6: $resultEui
                    """.trimIndent()
                    
                    ActionButtonRow(allResultsText = shareText)
                }

                // Educational Steps Card
                val cleanMac = macInput.replace(Regex("[^0-9a-fA-F]"), "").uppercase(Locale.ROOT)
                if (cleanMac.length == 12) {
                    val p1 = cleanMac.substring(0, 6)
                    val p2 = cleanMac.substring(6, 12)
                    val firstByte = p1.substring(0, 2)
                    val flipped = (firstByte.toInt(16) xor 2).toString(16).padStart(2, '0').uppercase(Locale.ROOT)
                    
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text("How EUI-64 is derived:", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.secondary)
                            
                            StepRow(step = "1. Split MAC Address", desc = "$p1 | $p2")
                            StepRow(step = "2. Insert FFFE in middle", desc = "${p1}FFFE$p2")
                            StepRow(step = "3. Flip 7th bit (U/L) of first byte", desc = "Byte $firstByte -> $flipped (${p1.substring(2)}FFFE$p2)")
                            StepRow(step = "4. Prepend network prefix", desc = resultEui)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StepRow(step: String, desc: String) {
    Column {
        Text(text = step, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Bold)
        Text(text = desc, fontSize = 14.sp, fontFamily = FontFamily.Monospace, color = MaterialTheme.colorScheme.onSurface)
    }
}
