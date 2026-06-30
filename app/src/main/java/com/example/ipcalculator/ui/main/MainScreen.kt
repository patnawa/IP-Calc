package com.example.ipcalculator.ui.main

import androidx.activity.compose.BackHandler
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ipcalculator.AppLanguage
import com.example.ipcalculator.Translator
import com.example.ipcalculator.theme.ThemeController
import com.example.ipcalculator.ui.screens.*
import com.example.ipcalculator.ui.components.GlowingCard

sealed class Screen(val key: String, val icon: ImageVector, val category: String) {
    object Subnet : Screen("subnet", Icons.Default.Settings, "cat_design")
    object Vlsm : Screen("vlsm", Icons.Default.Add, "cat_design")
    object Supernet : Screen("supernet", Icons.Default.Home, "cat_design")
    object DesignWizard : Screen("design_wizard", Icons.Default.Build, "cat_design")
    
    object PingScan : Screen("ping_scan", Icons.Default.PlayArrow, "cat_diag")
    object DnsWhois : Screen("dns_whois", Icons.Default.Search, "cat_diag")
    object IpChecker : Screen("ip_checker", Icons.Default.Check, "cat_diag")
    object Compare : Screen("compare", Icons.Default.Warning, "cat_diag")
    
    object Converter : Screen("converter", Icons.Default.Refresh, "cat_conv")
    object Eui64 : Screen("eui64", Icons.Default.Send, "cat_conv")
    object MacLookup : Screen("mac_oui", Icons.Default.Search, "cat_conv")
    object CiscoAcl : Screen("cisco_acl", Icons.Default.Lock, "cat_conv")
    
    object Quiz : Screen("quiz", Icons.Default.Star, "cat_learn")
    object CheatSheets : Screen("cheat_sheets", Icons.Default.List, "cat_learn")
    object CidrChart : Screen("cidr_chart", Icons.Default.Menu, "cat_learn")
    object Ports : Screen("ports", Icons.Default.Share, "cat_learn")
    object About : Screen("about", Icons.Default.Info, "cat_learn")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    modifier: Modifier = Modifier
) {
    val allScreens = listOf(
        Screen.Subnet, Screen.Vlsm, Screen.Supernet, Screen.DesignWizard,
        Screen.PingScan, Screen.DnsWhois, Screen.IpChecker, Screen.Compare,
        Screen.Converter, Screen.Eui64, Screen.MacLookup, Screen.CiscoAcl,
        Screen.Quiz, Screen.CheatSheets, Screen.CidrChart, Screen.Ports, Screen.About
    )

    var activeScreenKey by rememberSaveable { mutableStateOf<String?>(null) }
    var searchQuery by rememberSaveable { mutableStateOf("") }
    
    // Category Expandable States
    var designExpanded by rememberSaveable { mutableStateOf(true) }
    var diagExpanded by rememberSaveable { mutableStateOf(true) }
    var convExpanded by rememberSaveable { mutableStateOf(true) }
    var learnExpanded by rememberSaveable { mutableStateOf(true) }
    
    val activeScreen = allScreens.find { it.key == activeScreenKey }

    // System Back Press Handler
    if (activeScreen != null) {
        BackHandler {
            activeScreenKey = null
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = if (activeScreen != null) Translator.t(activeScreen.key) else Translator.t("app_title"),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    if (activeScreen != null) {
                        IconButton(onClick = { activeScreenKey = null }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Go Back"
                            )
                        }
                    } else {
                        IconButton(onClick = { ThemeController.isDarkTheme = !ThemeController.isDarkTheme }) {
                            Icon(
                                imageVector = if (ThemeController.isDarkTheme) Icons.Default.LightMode else Icons.Default.DarkMode,
                                contentDescription = "Toggle Theme"
                            )
                        }
                    }
                },
                actions = {
                    if (activeScreen != null) {
                        IconButton(onClick = { ThemeController.isDarkTheme = !ThemeController.isDarkTheme }) {
                            Icon(
                                imageVector = if (ThemeController.isDarkTheme) Icons.Default.LightMode else Icons.Default.DarkMode,
                                contentDescription = "Toggle Theme"
                            )
                        }
                    }
                    LanguageSelectorDropdown()
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSurface,
                    actionIconContentColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Crossfade(targetState = activeScreen, label = "screenCrossfade") { screen ->
                if (screen == null) {
                    DashboardView(
                        screens = allScreens,
                        searchQuery = searchQuery,
                        onSearchChange = { searchQuery = it },
                        onScreenClick = { activeScreenKey = it.key },
                        designExpanded = designExpanded,
                        onDesignToggle = { designExpanded = it },
                        diagExpanded = diagExpanded,
                        onDiagToggle = { diagExpanded = it },
                        convExpanded = convExpanded,
                        onConvToggle = { convExpanded = it },
                        learnExpanded = learnExpanded,
                        onLearnToggle = { learnExpanded = it }
                    )
                } else {
                    when (screen) {
                        Screen.Subnet -> SubnetCalculatorScreen()
                        Screen.Vlsm -> VlsmCalculatorScreen()
                        Screen.Supernet -> SupernettingScreen()
                        Screen.DesignWizard -> DesignWizardScreen()
                        Screen.PingScan -> PingScannerScreen()
                        Screen.DnsWhois -> DnsLookupScreen()
                        Screen.IpChecker -> IpCheckerScreen()
                        Screen.Compare -> SubnetCompareScreen()
                        Screen.Converter -> IpConverterScreen()
                        Screen.Eui64 -> Eui64Screen()
                        Screen.MacLookup -> MacLookupScreen()
                        Screen.CiscoAcl -> AclGeneratorScreen()
                        Screen.Quiz -> QuizScreen()
                        Screen.CheatSheets -> CheatSheetsScreen()
                        Screen.CidrChart -> CidrReferenceScreen()
                        Screen.Ports -> CommonPortsScreen()
                        Screen.About -> AboutScreen()
                    }
                }
            }
        }
    }
}

@Composable
fun DashboardView(
    screens: List<Screen>,
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    onScreenClick: (Screen) -> Unit,
    designExpanded: Boolean,
    onDesignToggle: (Boolean) -> Unit,
    diagExpanded: Boolean,
    onDiagToggle: (Boolean) -> Unit,
    convExpanded: Boolean,
    onConvToggle: (Boolean) -> Unit,
    learnExpanded: Boolean,
    onLearnToggle: (Boolean) -> Unit
) {
    val filteredScreens = screens.filter {
        val title = Translator.t(it.key).lowercase()
        val category = Translator.t(it.category).lowercase()
        title.contains(searchQuery.lowercase()) || category.contains(searchQuery.lowercase())
    }

    val isSearching = searchQuery.isNotEmpty()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Dynamic search input
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchChange,
                placeholder = { Text("Search tools...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                modifier = Modifier.weight(1f),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                )
            )

            // Expand/Collapse All Toggle Button
            IconButton(
                onClick = {
                    val target = !(designExpanded || diagExpanded || convExpanded || learnExpanded)
                    onDesignToggle(target)
                    onDiagToggle(target)
                    onConvToggle(target)
                    onLearnToggle(target)
                }
            ) {
                Icon(
                    imageVector = if (designExpanded || diagExpanded || convExpanded || learnExpanded) Icons.Default.UnfoldLess else Icons.Default.UnfoldMore,
                    contentDescription = "Toggle All Categories"
                )
            }
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            val categories = listOf(
                Triple("cat_design", designExpanded, onDesignToggle),
                Triple("cat_diag", diagExpanded, onDiagToggle),
                Triple("cat_conv", convExpanded, onConvToggle),
                Triple("cat_learn", learnExpanded, onLearnToggle)
            )

            categories.forEach { (catKey, isExpanded, onToggle) ->
                val catScreens = filteredScreens.filter { it.category == catKey }
                if (catScreens.isNotEmpty()) {
                    val finalExpanded = if (isSearching) true else isExpanded

                    // Category Header span all 2 columns
                    item(span = { androidx.compose.foundation.lazy.grid.GridItemSpan(2) }) {
                        ElevatedCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp, bottom = 4.dp)
                                .clickable { onToggle(!isExpanded) }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = Translator.t(catKey),
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Icon(
                                    imageVector = if (finalExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                    contentDescription = "Toggle Category"
                                )
                            }
                        }
                    }

                    if (finalExpanded) {
                        items(catScreens) { screen ->
                            ToolCard(screen = screen, onClick = { onScreenClick(screen) })
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ToolCard(screen: Screen, onClick: () -> Unit) {
    GlowingCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = screen.icon,
                contentDescription = Translator.t(screen.key),
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(28.dp)
            )
            Text(
                text = Translator.t(screen.key),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
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
