package com.example.ipcalculator.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.ipcalculator.IPCalculator

enum class IpFormat {
    DOT_DECIMAL,
    BINARY,
    HEXADECIMAL,
    DECIMAL_INT
}

@Composable
fun IpConverterScreen(modifier: Modifier = Modifier) {
    var inputFormat by remember { mutableStateOf(IpFormat.DOT_DECIMAL) }
    var rawInput by remember { mutableStateOf("192.168.1.1") }

    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current

    // Convert values reactively
    var dotDecimalOut = ""
    var binaryOut = ""
    var hexOut = ""
    var decimalIntOut = ""
    var isValid = false
    var errorMsg = ""

    val trimmedInput = rawInput.trim()

    if (trimmedInput.isNotEmpty()) {
        when (inputFormat) {
            IpFormat.DOT_DECIMAL -> {
                if (IPCalculator.isValidIPv4(trimmedInput)) {
                    val lVal = IPCalculator.ipv4ToLong(trimmedInput)
                    dotDecimalOut = trimmedInput
                    binaryOut = IPCalculator.longToBinaryString(lVal).replace(".", "")
                    hexOut = "0x" + IPCalculator.ipv4ToHex(trimmedInput)
                    decimalIntOut = lVal.toString()
                    isValid = true
                } else {
                    errorMsg = "Invalid Dot-Decimal format (e.g. 192.168.1.1)"
                }
            }
            IpFormat.BINARY -> {
                val cleanedBin = trimmedInput.replace(".", "")
                val ipStr = IPCalculator.binaryToIPv4(cleanedBin)
                if (ipStr != null) {
                    val lVal = IPCalculator.ipv4ToLong(ipStr)
                    dotDecimalOut = ipStr
                    binaryOut = cleanedBin
                    hexOut = "0x" + IPCalculator.ipv4ToHex(ipStr)
                    decimalIntOut = lVal.toString()
                    isValid = true
                } else {
                    errorMsg = "Invalid Binary format (must be 32 bits of 0s and 1s)"
                }
            }
            IpFormat.HEXADECIMAL -> {
                val ipStr = IPCalculator.hexToIPv4(trimmedInput)
                if (ipStr != null) {
                    val lVal = IPCalculator.ipv4ToLong(ipStr)
                    dotDecimalOut = ipStr
                    binaryOut = IPCalculator.longToBinaryString(lVal).replace(".", "")
                    hexOut = if (trimmedInput.startsWith("0x")) trimmedInput else "0x$trimmedInput"
                    decimalIntOut = lVal.toString()
                    isValid = true
                } else {
                    errorMsg = "Invalid Hexadecimal format (8 characters, e.g. C0A80101)"
                }
            }
            IpFormat.DECIMAL_INT -> {
                val ipStr = IPCalculator.decimalToIPv4(trimmedInput)
                if (ipStr != null) {
                    val lVal = trimmedInput.toLong()
                    dotDecimalOut = ipStr
                    binaryOut = IPCalculator.longToBinaryString(lVal).replace(".", "")
                    hexOut = "0x" + IPCalculator.ipv4ToHex(ipStr)
                    decimalIntOut = trimmedInput
                    isValid = true
                } else {
                    errorMsg = "Invalid Decimal Integer format (0 to 4294967295)"
                }
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Input Format Selection",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                // Grid or column of options
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FormatButton(
                            label = "Dot-Decimal",
                            selected = inputFormat == IpFormat.DOT_DECIMAL,
                            onClick = {
                                inputFormat = IpFormat.DOT_DECIMAL
                                rawInput = "192.168.1.1"
                            },
                            modifier = Modifier.weight(1f)
                        )
                        FormatButton(
                            label = "Binary",
                            selected = inputFormat == IpFormat.BINARY,
                            onClick = {
                                inputFormat = IpFormat.BINARY
                                rawInput = "11000000101010000000000100000001"
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FormatButton(
                            label = "Hexadecimal",
                            selected = inputFormat == IpFormat.HEXADECIMAL,
                            onClick = {
                                inputFormat = IpFormat.HEXADECIMAL
                                rawInput = "C0A80101"
                            },
                            modifier = Modifier.weight(1f)
                        )
                        FormatButton(
                            label = "Decimal Int",
                            selected = inputFormat == IpFormat.DECIMAL_INT,
                            onClick = {
                                inputFormat = IpFormat.DECIMAL_INT
                                rawInput = "3232235777"
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                HorizontalDivider()

                OutlinedTextField(
                    value = rawInput,
                    onValueChange = { rawInput = it },
                    label = { Text("Input Value") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = when (inputFormat) {
                            IpFormat.DECIMAL_INT -> KeyboardType.Number
                            else -> KeyboardType.Text
                        }
                    )
                )

                if (trimmedInput.isNotEmpty() && !isValid) {
                    Text(
                        text = errorMsg,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }

        if (isValid && trimmedInput.isNotEmpty()) {
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Conversion Results",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    HorizontalDivider()

                    ResultRow("Dot-Decimal Representation", dotDecimalOut, clipboardManager)
                    ResultRow("Binary Representation", formatBinaryOutput(binaryOut), clipboardManager)
                    ResultRow("Hexadecimal Representation", hexOut, clipboardManager)
                    ResultRow("Decimal Integer Representation", decimalIntOut, clipboardManager)
                }
            }
        }
    }
}

@Composable
fun FormatButton(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Text(
            text = label,
            color = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

// Format 32-bit binary into 4 octets separated by dots for readability
fun formatBinaryOutput(rawBin: String): String {
    if (rawBin.length != 32) return rawBin
    return "${rawBin.substring(0, 8)}.${rawBin.substring(8, 16)}.${rawBin.substring(16, 24)}.${rawBin.substring(24, 32)}"
}
