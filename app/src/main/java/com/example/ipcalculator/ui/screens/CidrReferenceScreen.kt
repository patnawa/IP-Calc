package com.example.ipcalculator.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
fun CidrReferenceScreen() {
    var searchQuery by rememberSaveable { mutableStateOf("") }
    val cidrTable = remember { IPCalculator.getCidrReferenceTable() }
    
    val filteredTable = remember(searchQuery) {
        cidrTable.filter {
            it.prefix.toString().contains(searchQuery) ||
            it.mask.contains(searchQuery) ||
            it.example.contains(searchQuery, ignoreCase = true)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Search by prefix, mask, or class...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            shape = RoundedCornerShape(12.dp),
            singleLine = true
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(filteredTable, key = { it.prefix }) { entry ->
                CidrRowCard(entry)
            }
        }
    }
}

@Composable
fun CidrRowCard(entry: IPCalculator.CidrEntry) {
    // Custom color coding based on prefix ranges
    val badgeColor = when (entry.prefix) {
        0 -> MaterialTheme.colorScheme.error
        in 1..8 -> MaterialTheme.colorScheme.primary // Class A
        in 9..16 -> MaterialTheme.colorScheme.secondary // Class B
        in 17..24 -> MaterialTheme.colorScheme.tertiary // Class C
        else -> MaterialTheme.colorScheme.outline // Classless / host subnets
    }

    GlowingCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Large Prefix Badge
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(badgeColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "/${entry.prefix}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = badgeColor
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = entry.mask,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
                Text(
                    text = "Wildcard: ${entry.wildcard}",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontFamily = FontFamily.Monospace
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = if (entry.usableHosts >= 1_000_000) {
                        "${entry.usableHosts / 1_000_000}M Hosts"
                    } else if (entry.usableHosts >= 1_000) {
                        "${entry.usableHosts / 1_000}K Hosts"
                    } else {
                        "${entry.usableHosts} Hosts"
                    },
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                if (entry.example.isNotEmpty()) {
                    Text(
                        text = entry.example,
                        fontSize = 11.sp,
                        color = badgeColor,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}
