package com.example.ipcalculator.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AboutScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // App Visual Header
        Box(
            modifier = Modifier
                .size(96.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "IP",
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }

        Text(
            text = "IP Calculator Suite",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Text(
            text = "Version 2.0.0 (Build 2)",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Button(
            onClick = {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/patnawa/IP-Calc"))
                context.startActivity(intent)
            },
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Visit GitHub Repository")
        }

        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Application Changelog",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                HorizontalDivider()

                ChangelogItem(
                    version = "v2.0.0 (2026-06-30)",
                    changes = listOf(
                        "Interactive Subnetting Quiz: CCNA/Network+ practice mode with score/streak tracking & answers explanation.",
                        "Interactive IP Allocation Map: Visual progress bar in VLSM displaying allocated subnets & free space.",
                        "New Localized Developer Tools: IPv6 EUI-64 generator, MAC OUI Registry Lookup, CIDR chart, Port cheat sheet, IP Subnet Checker & Comparator.",
                        "Global Multi-Language Switching: Instant translation switcher supporting EN, TH, ES, ZH, JA, DE, FR.",
                        "Visual Polish & Bug Fixes: Corrected screen overlapping padding, fixed IPv6 zero runs, validation logic, and VLSM integer overflow.",
                        "Premium App Icon: Sleek 3D isometric glowing network node grid icon."
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                ChangelogItem(
                    version = "v1.0.0 (2026-06-28)",
                    changes = listOf(
                        "Initial Release of the complete IP Suite.",
                        "Subnet Calculator: Configurable IPv4 & IPv6 network parameter computation.",
                        "Binary Viewer: Visual vertical alignment of subnet mask and IP address bits.",
                        "VLSM / FLSM Screen: Dynamic hosts size allocations and fixed subnet partitioning.",
                        "IP Converter: Formatted 4-way translator between dot-decimal, binary, hex, and long values.",
                        "Route Summarizer: Grouping multiple networks into a single supernet prefix.",
                        "Modern M3 Theme: Material 3 UI design with native Dark and Light mode support.",
                        "Custom Icons: Integrated modern glassmorphic icons across all Android resolutions."
                    )
                )
            }
        }

        Text(
            text = "Developed by AI Coding Assistant Antigravity",
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.outline,
            modifier = Modifier.padding(top = 16.dp),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun ChangelogItem(version: String, changes: List<String>) {
    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        Text(
            text = version,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.secondary
        )
        changes.forEach { change ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(text = "•", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(
                    text = change,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
