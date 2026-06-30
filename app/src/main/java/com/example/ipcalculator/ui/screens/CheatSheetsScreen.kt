package com.example.ipcalculator.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ipcalculator.ui.components.GlowingCard
import com.example.ipcalculator.ui.components.SectionHeader

@Composable
fun CheatSheetsScreen(modifier: Modifier = Modifier) {
    var selectedTabIndex by rememberSaveable { mutableStateOf(0) }
    val tabs = listOf("OSI Model", "RJ-45 Wiring", "IP Headers", "Fiber Colors")

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        TabRow(selectedTabIndex = selectedTabIndex) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    text = { Text(title, fontSize = 12.sp) }
                )
            }
        }

        when (selectedTabIndex) {
            0 -> OsiModelTab()
            1 -> Rj45WiringTab()
            2 -> IpHeadersTab()
            3 -> FiberColorsTab()
        }
    }
}

@Composable
fun OsiModelTab() {
    val layers = listOf(
        OsiLayer("7. Application", "Data", "HTTP, HTTPS, DNS, DHCP, FTP, SSH", "Provides network services directly to end-user applications."),
        OsiLayer("6. Presentation", "Data", "SSL, TLS, JPEG, ASCII, GIF", "Formats, encrypts, and compresses data for transmission."),
        OsiLayer("5. Session", "Data", "NetBIOS, RPC, Sockets", "Establishes, manages, and terminates connection sessions between hosts."),
        OsiLayer("4. Transport", "Segments/Datagrams", "TCP, UDP", "Handles host-to-host flow control, error recovery, and multiplexing."),
        OsiLayer("3. Network", "Packets", "IPv4, IPv6, ICMP, IPsec, OSPF", "Determines logical routing paths and handles logical IP addressing."),
        OsiLayer("2. Data Link", "Frames", "Ethernet, PPP, ARP, Switch", "Handles MAC physical addressing, framing, and error detection on local links."),
        OsiLayer("1. Physical", "Bits", "Ethernet Cables, Fiber, Hubs, Wi-Fi", "Transmits raw bit stream over electrical, optical, or radio mediums.")
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        layers.forEach { layer ->
            GlowingCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = layer.name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Badge(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                    ) {
                        Text(layer.unit, fontSize = 10.sp, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                    }
                }
                Text(
                    text = "Protocols: ${layer.protocols}",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.tertiary
                )
                Text(
                    text = layer.description,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

data class OsiLayer(
    val name: String,
    val unit: String,
    val protocols: String,
    val description: String
)

@Composable
fun Rj45WiringTab() {
    val t568a = listOf(
        Pair("1. White/Green", Color(0xFF81C784)),
        Pair("2. Green", Color(0xFF2E7D32)),
        Pair("3. White/Orange", Color(0xFFFFB74D)),
        Pair("4. Blue", Color(0xFF1E88E5)),
        Pair("5. White/Blue", Color(0xFF64B5F6)),
        Pair("6. Orange", Color(0xFFF57C00)),
        Pair("7. White/Brown", Color(0xFFD7CCC8)),
        Pair("8. Brown", Color(0xFF8D6E63))
    )

    val t568b = listOf(
        Pair("1. White/Orange", Color(0xFFFFB74D)),
        Pair("2. Orange", Color(0xFFF57C00)),
        Pair("3. White/Green", Color(0xFF81C784)),
        Pair("4. Blue", Color(0xFF1E88E5)),
        Pair("5. White/Blue", Color(0xFF64B5F6)),
        Pair("6. Green", Color(0xFF2E7D32)),
        Pair("7. White/Brown", Color(0xFFD7CCC8)),
        Pair("8. Brown", Color(0xFF8D6E63))
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // T568B (Most Common)
        GlowingCard {
            SectionHeader(title = "T568B Standard Wiring (Standard Patch)")
            
            t568b.forEach { (pin, color) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp, 16.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(color)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(text = pin, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                }
            }
        }

        // T568A
        GlowingCard {
            SectionHeader(title = "T568A Standard Wiring (Crossover/Legacy)")
            
            t568a.forEach { (pin, color) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp, 16.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(color)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(text = pin, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                }
            }
        }
    }
}

@Composable
fun IpHeadersTab() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        GlowingCard {
            SectionHeader(title = "IPv4 Packet Header Fields")
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.background)
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("• Version (4 bits): Identifies IP version (value 4).", fontSize = 12.sp, fontFamily = FontFamily.Monospace)
                Text("• IHL (4 bits): Internet Header Length (header size).", fontSize = 12.sp, fontFamily = FontFamily.Monospace)
                Text("• Type of Service (8 bits): Quality of Service (QoS).", fontSize = 12.sp, fontFamily = FontFamily.Monospace)
                Text("• Total Length (16 bits): Entire packet size in bytes.", fontSize = 12.sp, fontFamily = FontFamily.Monospace)
                Text("• TTL (8 bits): Time To Live (maximum router hops).", fontSize = 12.sp, fontFamily = FontFamily.Monospace)
                Text("• Protocol (8 bits): Next layer protocol (TCP=6, UDP=17, ICMP=1).", fontSize = 12.sp, fontFamily = FontFamily.Monospace)
                Text("• Source Address (32 bits): Sender IPv4.", fontSize = 12.sp, fontFamily = FontFamily.Monospace, color = MaterialTheme.colorScheme.primary)
                Text("• Destination Address (32 bits): Target IPv4.", fontSize = 12.sp, fontFamily = FontFamily.Monospace, color = MaterialTheme.colorScheme.primary)
            }
        }

        GlowingCard {
            SectionHeader(title = "IPv6 Packet Header Fields")
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.background)
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("• Version (4 bits): Identifies IP version (value 6).", fontSize = 12.sp, fontFamily = FontFamily.Monospace)
                Text("• Traffic Class (8 bits): QoS priority level.", fontSize = 12.sp, fontFamily = FontFamily.Monospace)
                Text("• Flow Label (20 bits): Identifies packets needing same routing.", fontSize = 12.sp, fontFamily = FontFamily.Monospace)
                Text("• Payload Length (16 bits): Data size in bytes.", fontSize = 12.sp, fontFamily = FontFamily.Monospace)
                Text("• Next Header (8 bits): Specifies protocol in extension header.", fontSize = 12.sp, fontFamily = FontFamily.Monospace)
                Text("• Hop Limit (8 bits): Maximum router hops (TTL equivalent).", fontSize = 12.sp, fontFamily = FontFamily.Monospace)
                Text("• Source Address (128 bits): Sender IPv6.", fontSize = 12.sp, fontFamily = FontFamily.Monospace, color = MaterialTheme.colorScheme.primary)
                Text("• Destination Address (128 bits): Target IPv6.", fontSize = 12.sp, fontFamily = FontFamily.Monospace, color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}

@Composable
fun FiberColorsTab() {
    val fiberColors = listOf(
        FiberColorItem(1, "Blue", Color(0xFF1E88E5)),
        FiberColorItem(2, "Orange", Color(0xFFF57C00)),
        FiberColorItem(3, "Green", Color(0xFF2E7D32)),
        FiberColorItem(4, "Brown", Color(0xFF8D6E63)),
        FiberColorItem(5, "Slate / Grey", Color(0xFF90A4AE)),
        FiberColorItem(6, "White", Color(0xFFFFFFFF), true), // border needed
        FiberColorItem(7, "Red", Color(0xFFD32F2F)),
        FiberColorItem(8, "Black", Color(0xFF000000)),
        FiberColorItem(9, "Yellow", Color(0xFFFFEB3B)),
        FiberColorItem(10, "Violet / Purple", Color(0xFF7B1FA2)),
        FiberColorItem(11, "Rose / Pink", Color(0xFFF06292)),
        FiberColorItem(12, "Aqua / Turquoise", Color(0xFF00ACC1))
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        GlowingCard {
            SectionHeader(title = "TIA-598 Standard 12-Fiber Color Code")
            
            fiberColors.forEach { item ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(item.color)
                            .then(
                                if (item.borderNeeded) Modifier.background(item.color).clip(CircleShape)
                                else Modifier
                            )
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = "${item.id}. ${item.name}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

data class FiberColorItem(
    val id: Int,
    val name: String,
    val color: Color,
    val borderNeeded: Boolean = false
)
