package com.example.ipcalculator.ui.main

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.ipcalculator.ui.screens.*

sealed class Tab(val title: String, val icon: ImageVector) {
    object Subnet : Tab("Subnet", Icons.Default.Settings)
    object Vlsm : Tab("VLSM/FLSM", Icons.Default.Add)
    object Converter : Tab("Converter", Icons.Default.Refresh)
    object Supernet : Tab("Supernet", Icons.Default.Home)
    object CidrChart : Tab("CIDR Chart", Icons.Default.List)
    object IpChecker : Tab("IP Checker", Icons.Default.Check)
    object Compare : Tab("Compare", Icons.Default.Warning)
    object Ports : Tab("Ports", Icons.Default.Build)
    object Eui64 : Tab("EUI-64", Icons.Default.Send)
    object MacLookup : Tab("MAC OUI", Icons.Default.Search)
    object Quiz : Tab("Quiz", Icons.Default.Star)
    object About : Tab("About", Icons.Default.Info)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    modifier: Modifier = Modifier
) {
    val tabs = listOf(
        Tab.Subnet, Tab.Vlsm, Tab.Converter, Tab.Supernet,
        Tab.CidrChart, Tab.IpChecker, Tab.Compare, Tab.Ports,
        Tab.Eui64, Tab.MacLookup, Tab.Quiz, Tab.About
    )
    
    var selectedTabIndex by rememberSaveable { mutableStateOf(0) }
    val selectedTab = tabs[selectedTabIndex]

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            Column {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            text = "IP Calculator Suite",
                            style = MaterialTheme.typography.titleLarge
                        )
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        titleContentColor = MaterialTheme.colorScheme.primary
                    )
                )
                
                // Scrollable tab row underneath the title bar
                ScrollableTabRow(
                    selectedTabIndex = selectedTabIndex,
                    edgePadding = 16.dp,
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.primary
                ) {
                    tabs.forEachIndexed { index, tab ->
                        Tab(
                            selected = selectedTabIndex == index,
                            onClick = { selectedTabIndex = index },
                            icon = { Icon(imageVector = tab.icon, contentDescription = tab.title) },
                            text = { Text(tab.title) }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        val screenModifier = Modifier.padding(innerPadding)
        Crossfade(targetState = selectedTab, label = "tabCrossfade") { tab ->
            when (tab) {
                Tab.Subnet -> SubnetCalculatorScreen(modifier = screenModifier)
                Tab.Vlsm -> VlsmCalculatorScreen(modifier = screenModifier)
                Tab.Converter -> IpConverterScreen(modifier = screenModifier)
                Tab.Supernet -> SupernettingScreen(modifier = screenModifier)
                Tab.CidrChart -> CidrReferenceScreen()
                Tab.IpChecker -> IpCheckerScreen()
                Tab.Compare -> SubnetCompareScreen()
                Tab.Ports -> CommonPortsScreen()
                Tab.Eui64 -> Eui64Screen()
                Tab.MacLookup -> MacLookupScreen()
                Tab.Quiz -> QuizScreen()
                Tab.About -> AboutScreen(modifier = screenModifier)
            }
        }
    }
}
