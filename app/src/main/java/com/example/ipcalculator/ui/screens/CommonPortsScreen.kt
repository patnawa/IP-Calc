package com.example.ipcalculator.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
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
import com.example.ipcalculator.ui.components.GlowingCard

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CommonPortsScreen() {
    var searchQuery by rememberSaveable { mutableStateOf("") }
    var selectedCategory by rememberSaveable { mutableStateOf("All") }
    
    val allPorts = remember { IPCalculator.getCommonPorts() }
    
    val categories = listOf("All", "Web", "Email", "File Transfer", "Database", "Remote Access", "DNS/DHCP", "Security", "Monitoring", "Other")

    val filteredPorts = remember(searchQuery, selectedCategory) {
        allPorts.filter { port ->
            val matchesSearch = port.port.toString().contains(searchQuery) ||
                    port.service.contains(searchQuery, ignoreCase = true) ||
                    port.description.contains(searchQuery, ignoreCase = true)
            
            val matchesCategory = selectedCategory == "All" || port.category == selectedCategory
            
            matchesSearch && matchesCategory
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Search ports, services, description...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            shape = RoundedCornerShape(12.dp),
            singleLine = true
        )

        // Scrollable category chips
        ScrollableTabRow(
            selectedTabIndex = categories.indexOf(selectedCategory).coerceAtLeast(0),
            edgePadding = 0.dp,
            divider = {},
            indicator = {}
        ) {
            categories.forEach { cat ->
                val selected = cat == selectedCategory
                Tab(
                    selected = selected,
                    onClick = { selectedCategory = cat },
                    modifier = Modifier.padding(vertical = 4.dp)
                ) {
                    SuggestionChip(
                        onClick = { selectedCategory = cat },
                        label = { Text(cat, fontSize = 12.sp) },
                        colors = if (selected) {
                            SuggestionChipDefaults.suggestionChipColors(
                                containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                                labelColor = MaterialTheme.colorScheme.primary
                            )
                        } else {
                            SuggestionChipDefaults.suggestionChipColors()
                        },
                        border = if (selected) {
                            BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
                        } else {
                            BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
                        },
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )
                }
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(filteredPorts, key = { "${it.port}_${it.protocol}" }) { port ->
                PortRowCard(port)
            }
        }
    }
}

@Composable
fun PortRowCard(port: IPCalculator.PortEntry) {
    val categoryColor = when (port.category) {
        "Web" -> MaterialTheme.colorScheme.primary
        "Email" -> MaterialTheme.colorScheme.secondary
        "File Transfer" -> MaterialTheme.colorScheme.tertiary
        "Database" -> MaterialTheme.colorScheme.error
        "Security" -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.outline
    }

    GlowingCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Port Number Badge
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(categoryColor.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = port.port.toString(),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = categoryColor,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = port.protocol,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = port.service,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = port.description,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Category Badge
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = port.category,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
