package com.example.ipcalculator.ui.main

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Box
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
import com.example.ipcalculator.AppLanguage
import com.example.ipcalculator.Translator
import com.example.ipcalculator.ui.screens.*

sealed class Tab(val key: String, val icon: ImageVector) {
    object Subnet : Tab("subnet", Icons.Default.Settings)
    object Vlsm : Tab("vlsm", Icons.Default.Add)
    object Converter : Tab("converter", Icons.Default.Refresh)
    object Supernet : Tab("supernet", Icons.Default.Home)
    object CidrChart : Tab("cidr_chart", Icons.Default.List)
    object IpChecker : Tab("ip_checker", Icons.Default.Check)
    object Compare : Tab("compare", Icons.Default.Warning)
    object Ports : Tab("ports", Icons.Default.Build)
    object Eui64 : Tab("eui64", Icons.Default.Send)
    object MacLookup : Tab("mac_oui", Icons.Default.Search)
    object Quiz : Tab("quiz", Icons.Default.Star)
    object About : Tab("about", Icons.Default.Info)
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
                            text = Translator.t("app_title"),
                            style = MaterialTheme.typography.titleLarge
                        )
                    },
                    actions = {
                        LanguageSelectorDropdown()
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
                            icon = { Icon(imageVector = tab.icon, contentDescription = Translator.t(tab.key)) },
                            text = { Text(Translator.t(tab.key)) }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Crossfade(targetState = selectedTab, label = "tabCrossfade") { tab ->
                when (tab) {
                    Tab.Subnet -> SubnetCalculatorScreen()
                    Tab.Vlsm -> VlsmCalculatorScreen()
                    Tab.Converter -> IpConverterScreen()
                    Tab.Supernet -> SupernettingScreen()
                    Tab.CidrChart -> CidrReferenceScreen()
                    Tab.IpChecker -> IpCheckerScreen()
                    Tab.Compare -> SubnetCompareScreen()
                    Tab.Ports -> CommonPortsScreen()
                    Tab.Eui64 -> Eui64Screen()
                    Tab.MacLookup -> MacLookupScreen()
                    Tab.Quiz -> QuizScreen()
                    Tab.About -> AboutScreen()
                }
            }
        }
    }
}

@Composable
fun LanguageSelectorDropdown() {
    var expanded by remember { mutableStateOf(false) }
    
    Box {
        IconButton(onClick = { expanded = true }) {
            Icon(imageVector = Icons.Default.Translate, contentDescription = "Language")
        }
        
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            AppLanguage.values().forEach { lang ->
                DropdownMenuItem(
                    text = { Text(lang.displayName) },
                    onClick = {
                        Translator.currentLanguage = lang
                        expanded = false
                    }
                )
            }
        }
    }
}
