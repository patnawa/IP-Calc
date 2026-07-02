package com.example.ipcalculator.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ipcalculator.IPCalculator
import com.example.ipcalculator.Translator
import com.example.ipcalculator.ui.components.GlowingCard
import com.example.ipcalculator.ui.components.SectionHeader
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.platform.LocalContext
import com.example.ipcalculator.HistoryManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.regex.Pattern

@Composable
fun DnsLookupScreen(modifier: Modifier = Modifier) {
    var isDnsTab by rememberSaveable { mutableStateOf(true) }
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        TabRow(selectedTabIndex = if (isDnsTab) 0 else 1) {
            Tab(
                selected = isDnsTab,
                onClick = { isDnsTab = true },
                text = { Text("DNS Resolver") }
            )
            Tab(
                selected = !isDnsTab,
                onClick = { isDnsTab = false },
                text = { Text("RDAP Whois") }
            )
        }

        if (isDnsTab) {
            DnsResolverTab()
        } else {
            WhoisTab()
        }
    }
}

@Composable
fun DnsResolverTab() {
    var domainInput by rememberSaveable { mutableStateOf("google.com") }
    var selectedType by rememberSaveable { mutableStateOf("A") }
    var queryResult by remember { mutableStateOf<List<String>?>(null) }
    var isQuerying by remember { mutableStateOf(false) }
    var errorMsg by remember { mutableStateOf<String?>(null) }
    
    val recordTypes = listOf("A", "AAAA", "MX", "TXT", "NS", "CNAME")
    var typeExpanded by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
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
                    text = "DNS Query Tool (Google DoH API)",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                val context = LocalContext.current
                var isStarred by remember(domainInput) {
                    mutableStateOf(HistoryManager.isFavorite(context, domainInput))
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = domainInput,
                        onValueChange = { domainInput = it },
                        label = { Text("Domain Name") },
                        placeholder = { Text("e.g. cloudflare.com") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    IconButton(
                        onClick = {
                            HistoryManager.toggleFavorite(context, domainInput)
                            isStarred = !isStarred
                        }
                    ) {
                        Icon(
                            imageVector = if (isStarred) Icons.Default.Star else Icons.Default.StarBorder,
                            contentDescription = "Star",
                            tint = if (isStarred) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // History & Favorites Chips
                val history = remember(domainInput, isStarred) {
                    HistoryManager.getHistory(context)
                }
                val favorites = remember(domainInput, isStarred) {
                    HistoryManager.getFavorites(context)
                }

                if (history.isNotEmpty() || favorites.isNotEmpty()) {
                    Text(
                        text = "Quick Access & Favorites",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                    androidx.compose.foundation.lazy.LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(favorites) { fav ->
                            SuggestionChip(
                                onClick = { domainInput = fav },
                                label = { Text("⭐ $fav", fontSize = 11.sp) }
                            )
                        }
                        items(history) { hist ->
                            if (!favorites.contains(hist)) {
                                SuggestionChip(
                                    onClick = { domainInput = hist },
                                    label = { Text(hist, fontSize = 11.sp) }
                                )
                            }
                        }
                    }
                }

                @OptIn(ExperimentalMaterial3Api::class)
                ExposedDropdownMenuBox(
                    expanded = typeExpanded,
                    onExpandedChange = { typeExpanded = it },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = selectedType,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("DNS Record Type") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = typeExpanded) },
                        modifier = Modifier.fillMaxWidth().menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = typeExpanded,
                        onDismissRequest = { typeExpanded = false }
                    ) {
                        recordTypes.forEach { type ->
                            DropdownMenuItem(
                                text = { Text(type) },
                                onClick = {
                                    selectedType = type
                                    typeExpanded = false
                                }
                            )
                        }
                    }
                }

                Button(
                    onClick = {
                        if (domainInput.trim().isEmpty()) return@Button
                        HistoryManager.addHistory(context, domainInput.trim())
                        isStarred = HistoryManager.isFavorite(context, domainInput.trim())
                        isQuerying = true
                        queryResult = null
                        errorMsg = null
                        scope.launch {
                            val rawJson = withContext(Dispatchers.IO) {
                                IPCalculator.queryDnsOverHttps(domainInput.trim(), selectedType)
                            }
                            if (rawJson.startsWith("Error")) {
                                errorMsg = rawJson
                            } else {
                                val records = parseDnsGoogleJson(rawJson)
                                if (records.isEmpty()) {
                                    errorMsg = "No records found for type $selectedType."
                                } else {
                                    queryResult = records
                                }
                            }
                            isQuerying = false
                        }
                    },
                    enabled = !isQuerying,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (isQuerying) "Resolving..." else "Resolve Record")
                }
            }
        }

        // Output Result list
        AnimatedVisibility(visible = queryResult != null || errorMsg != null) {
            GlowingCard {
                SectionHeader(title = "Resolved Records ($selectedType)")

                if (errorMsg != null) {
                    Text(
                        text = errorMsg!!,
                        color = MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(8.dp)
                    )
                } else if (queryResult != null) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.background)
                            .padding(12.dp)
                    ) {
                        queryResult!!.forEachIndexed { index, record ->
                            Text(
                                text = "[${index + 1}] $record",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 14.sp,
                                modifier = Modifier.padding(vertical = 4.dp),
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun WhoisTab() {
    var domainInput by rememberSaveable { mutableStateOf("apple.com") }
    var whoisData by remember { mutableStateOf<String?>(null) }
    var isQuerying by remember { mutableStateOf(false) }
    var errorMsg by remember { mutableStateOf<String?>(null) }
    
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
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
                    text = "RDAP Domain WHOIS Query",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                OutlinedTextField(
                    value = domainInput,
                    onValueChange = { domainInput = it },
                    label = { Text("Domain Name") },
                    placeholder = { Text("e.g. apple.com") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Button(
                    onClick = {
                        if (domainInput.trim().isEmpty()) return@Button
                        isQuerying = true
                        whoisData = null
                        errorMsg = null
                        scope.launch {
                            val rawJson = withContext(Dispatchers.IO) {
                                IPCalculator.queryWhoisRdap(domainInput.trim())
                            }
                            if (rawJson.startsWith("Error")) {
                                errorMsg = rawJson
                            } else {
                                whoisData = formatRdapJson(rawJson)
                            }
                            isQuerying = false
                        }
                    },
                    enabled = !isQuerying,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (isQuerying) "Querying RDAP..." else "Query WHOIS")
                }
            }
        }

        // Whois Results
        AnimatedVisibility(visible = whoisData != null || errorMsg != null) {
            GlowingCard {
                SectionHeader(title = "WHOIS / RDAP Registry Summary")

                if (errorMsg != null) {
                    Text(
                        text = errorMsg!!,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(8.dp)
                    )
                } else if (whoisData != null) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.background)
                            .padding(12.dp)
                    ) {
                        Text(
                            text = whoisData!!,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurface,
                            lineHeight = 20.sp
                        )
                    }
                }
            }
        }
    }
}

// Extract data records from Google DNS response JSON without Gson
fun parseDnsGoogleJson(json: String): List<String> {
    val list = mutableListOf<String>()
    // A simple regex matching "data":"..."
    val pattern = Pattern.compile("\"data\"\\s*:\\s*\"([^\"]+)\"")
    val matcher = pattern.matcher(json)
    while (matcher.find()) {
        list.add(matcher.group(1))
    }
    return list
}

// Clean up and extract important parameters from RDAP WHOIS response
fun formatRdapJson(json: String): String {
    val sb = java.lang.StringBuilder()
    
    fun extractField(patternStr: String, label: String) {
        val pattern = Pattern.compile(patternStr)
        val matcher = pattern.matcher(json)
        if (matcher.find()) {
            sb.append("$label: ${matcher.group(1)}\n")
        }
    }
    
    // Extract basic fields
    extractField("\"ldhName\"\\s*:\\s*\"([^\"]+)\"", "Domain Name")
    extractField("\"port43\"\\s*:\\s*\"([^\"]+)\"", "Whois Server")
    
    // Extract dates
    extractField("\"eventAction\"\\s*:\\s*\"registration\"\\s*,\\s*\"eventDate\"\\s*:\\s*\"([^\"]+)\"", "Registration Date")
    extractField("\"eventAction\"\\s*:\\s*\"expiration\"\\s*,\\s*\"eventDate\"\\s*:\\s*\"([^\"]+)\"", "Expiration Date")
    extractField("\"eventAction\"\\s*:\\s*\"last changed\"\\s*,\\s*\"eventDate\"\\s*:\\s*\"([^\"]+)\"", "Last Updated")
    
    // Extract registrar entity
    val registrarPattern = Pattern.compile("\"fn\"\\s*:\\s*\"([^\"]+)\"")
    val rMatcher = registrarPattern.matcher(json)
    if (rMatcher.find()) {
        sb.append("Registrar: ${rMatcher.group(1)}\n")
    }
    
    // Nameservers
    sb.append("\nNameservers:\n")
    val nsPattern = Pattern.compile("\"ldhName\"\\s*:\\s*\"([a-zA-Z0-9.-]+\\.[a-zA-Z]{2,})\"")
    val nsMatcher = nsPattern.matcher(json)
    var nsCount = 0
    val nsSet = mutableSetOf<String>()
    while (nsMatcher.find() && nsCount < 6) {
        val ns = nsMatcher.group(1)
        if (ns != null && nsSet.add(ns.lowercase())) {
            sb.append(" - ${ns.lowercase()}\n")
            nsCount++
        }
    }
    
    if (sb.trim().isEmpty()) {
        return "Failed to extract key fields. Raw RDAP JSON returned:\n\n${if (json.length > 500) json.substring(0, 500) + "..." else json}"
    }
    
    return sb.toString()
}
