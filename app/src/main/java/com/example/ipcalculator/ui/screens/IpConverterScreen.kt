package com.example.ipcalculator.ui.screens

import androidx.compose.animation.*
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ipcalculator.IPCalculator
import com.example.ipcalculator.ui.components.ActionButtonRow
import com.example.ipcalculator.ui.components.GlowingCard
import com.example.ipcalculator.ui.components.ResultRowWithCopy
import com.example.ipcalculator.ui.components.SectionHeader

enum class IpFormat {
    DOT_DECIMAL,
    BINARY,
    HEXADECIMAL,
    DECIMAL_INT
}

@Composable
fun IpConverterScreen(modifier: Modifier = Modifier) {
    var inputFormat by rememberSaveable { mutableStateOf(IpFormat.DOT_DECIMAL) }
    var rawInput by rememberSaveable { mutableStateOf("192.168.1.1") }

    val scrollState = rememberScrollState()

    // Conversions calculated reactively
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
                val formattedBin = if (cleanedBin.length == 32) {
                    (0..3).joinToString(".") { cleanedBin.substring(it * 8, it * 8 + 8) }
                } else {
                    trimmedInput
                }
                val ipStr = IPCalculator.binaryToIPv4(formattedBin)
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
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Selector for input format
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Source Input Format",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                // 2x2 Grid of Format Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FormatSelectorButton(
                        text = "Dot-Decimal",
                        selected = inputFormat == IpFormat.DOT_DECIMAL,
                        onClick = {
                            if (isValid) rawInput = dotDecimalOut
                            inputFormat = IpFormat.DOT_DECIMAL
                        },
                        modifier = Modifier.weight(1f)
                    )
                    FormatSelectorButton(
                        text = "Binary",
                        selected = inputFormat == IpFormat.BINARY,
                        onClick = {
                            if (isValid) rawInput = binaryOut
                            inputFormat = IpFormat.BINARY
                        },
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FormatSelectorButton(
                        text = "Hexadecimal",
                        selected = inputFormat == IpFormat.HEXADECIMAL,
                        onClick = {
                            if (isValid) rawInput = hexOut.replace("0x", "")
                            inputFormat = IpFormat.HEXADECIMAL
                        },
                        modifier = Modifier.weight(1f)
                    )
                    FormatSelectorButton(
                        text = "Decimal Int",
                        selected = inputFormat == IpFormat.DECIMAL_INT,
                        onClick = {
                            if (isValid) rawInput = decimalIntOut
                            inputFormat = IpFormat.DECIMAL_INT
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Input Card
        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Enter IP Address Value",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                OutlinedTextField(
                    value = rawInput,
                    onValueChange = { rawInput = it },
                    placeholder = {
                        Text(
                            when (inputFormat) {
                                IpFormat.DOT_DECIMAL -> "e.g. 192.168.1.1"
                                IpFormat.BINARY -> "e.g. 1100000010101000..."
                                IpFormat.HEXADECIMAL -> "e.g. C0A80101"
                                IpFormat.DECIMAL_INT -> "e.g. 3232235777"
                            }
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = when (inputFormat) {
                            IpFormat.DOT_DECIMAL -> KeyboardType.Phone
                            IpFormat.BINARY -> KeyboardType.Number
                            IpFormat.HEXADECIMAL -> KeyboardType.Text
                            IpFormat.DECIMAL_INT -> KeyboardType.Number
                        }
                    )
                )

                if (rawInput.isNotEmpty() && !isValid) {
                    Text(
                        text = errorMsg,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        // Results Card
        AnimatedVisibility(
            visible = isValid,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            GlowingCard {
                SectionHeader(title = "Converted IP Formats")

                ResultRowWithCopy("Dot-Decimal Format", dotDecimalOut)
                ResultRowWithCopy("Binary Representation", formatBinaryOutput(binaryOut))
                ResultRowWithCopy("Hexadecimal Format", hexOut.uppercase())
                ResultRowWithCopy("Decimal Integer Format", decimalIntOut)

                val shareText = """
                    IP Format Conversion Report:
                    - Dot-Decimal: $dotDecimalOut
                    - Binary: ${formatBinaryOutput(binaryOut)}
                    - Hex: ${hexOut.uppercase()}
                    - Dec Integer: $decimalIntOut
                """.trimIndent()

                ActionButtonRow(allResultsText = shareText)
            }
        }
    }
}

@Composable
fun FormatSelectorButton(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
        ),
        modifier = modifier,
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(
            text = text,
            color = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

fun formatBinaryOutput(binary: String): String {
    if (binary.length != 32) return binary
    return "${binary.substring(0, 8)}.${binary.substring(8, 16)}.${binary.substring(16, 24)}.${binary.substring(24, 32)}"
}
