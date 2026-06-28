package com.example.ipcalculator.ui.main

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation3.runtime.NavKey
import com.example.ipcalculator.ui.screens.AboutScreen
import com.example.ipcalculator.ui.screens.IpConverterScreen
import com.example.ipcalculator.ui.screens.SubnetCalculatorScreen
import com.example.ipcalculator.ui.screens.SupernettingScreen
import com.example.ipcalculator.ui.screens.VlsmCalculatorScreen

sealed class Tab(val title: String, val icon: ImageVector) {
    object Subnet : Tab("Subnet", Icons.Default.Build)
    object Vlsm : Tab("VLSM/FLSM", Icons.Default.Add)
    object Converter : Tab("Converter", Icons.Default.Edit)
    object Supernet : Tab("Supernet", Icons.Default.Search)
    object About : Tab("About", Icons.Default.Info)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    onItemClick: (NavKey) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf<Tab>(Tab.Subnet) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                title = { 
                    Text(
                        text = "IP Calculator Suite", 
                        style = MaterialTheme.typography.titleLarge
                    ) 
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        bottomBar = {
            NavigationBar {
                val tabs = listOf(Tab.Subnet, Tab.Vlsm, Tab.Converter, Tab.Supernet, Tab.About)
                tabs.forEach { tab ->
                    NavigationBarItem(
                        selected = selectedTab == tab,
                        onClick = { selectedTab = tab },
                        icon = { Icon(imageVector = tab.icon, contentDescription = tab.title) },
                        label = { Text(tab.title) }
                    )
                }
            }
        }
    ) { innerPadding ->
        val screenModifier = Modifier.padding(innerPadding)
        when (selectedTab) {
            Tab.Subnet -> SubnetCalculatorScreen(modifier = screenModifier)
            Tab.Vlsm -> VlsmCalculatorScreen(modifier = screenModifier)
            Tab.Converter -> IpConverterScreen(modifier = screenModifier)
            Tab.Supernet -> SupernettingScreen(modifier = screenModifier)
            Tab.About -> AboutScreen(modifier = screenModifier)
        }
    }
}
