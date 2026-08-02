package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.outlined.BugReport
import androidx.compose.material.icons.outlined.Dns
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material.icons.outlined.Terminal
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.screens.AppFirewallScreen
import com.example.ui.screens.AvScannerScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.PiHoleScreen
import com.example.ui.screens.SystemInspectorScreen
import com.example.ui.theme.CyberCardBg
import com.example.ui.theme.CyberCardBorder
import com.example.ui.theme.CyberDarkBg
import com.example.ui.theme.CyberEmeraldPrimary
import com.example.ui.theme.CyberPrimaryCyan
import com.example.ui.theme.CyberTextMuted
import com.example.ui.theme.CyberTextPrimary
import com.example.ui.theme.CyberTextSecondary
import com.example.ui.theme.GrapheneGuardTheme
import com.example.ui.viewmodels.AvScannerViewModel
import com.example.ui.viewmodels.NetworkFirewallViewModel
import com.example.ui.viewmodels.PiHoleViewModel
import com.example.ui.viewmodels.SecurityDashboardViewModel
import com.example.ui.viewmodels.SystemInspectorViewModel

enum class NavigationTab(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val testTag: String
) {
    DASHBOARD("Dashboard", Icons.Filled.Shield, Icons.Outlined.Shield, "nav_tab_dashboard"),
    AV_SCANNER("AV Scan", Icons.Filled.BugReport, Icons.Outlined.BugReport, "nav_tab_av_scan"),
    FIREWALL("Firewall", Icons.Filled.Lock, Icons.Outlined.Lock, "nav_tab_firewall"),
    PIHOLE("Pi-Hole", Icons.Filled.Dns, Icons.Outlined.Dns, "nav_tab_pihole"),
    SYSTEM("System", Icons.Filled.Terminal, Icons.Outlined.Terminal, "nav_tab_system")
}

class MainActivity : ComponentActivity() {
    private val dashboardViewModel: SecurityDashboardViewModel by viewModels()
    private val avScannerViewModel: AvScannerViewModel by viewModels()
    private val firewallViewModel: NetworkFirewallViewModel by viewModels()
    private val piHoleViewModel: PiHoleViewModel by viewModels()
    private val systemViewModel: SystemInspectorViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            GrapheneGuardTheme {
                var currentTab by remember { mutableStateOf(NavigationTab.DASHBOARD) }

                Scaffold(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(CyberDarkBg),
                    bottomBar = {
                        NavigationBar(
                            containerColor = CyberCardBg,
                            contentColor = CyberEmeraldPrimary,
                            modifier = Modifier
                                .border(1.dp, CyberCardBorder)
                                .windowInsetsPadding(WindowInsets.navigationBars),
                            tonalElevation = 8.dp
                        ) {
                            NavigationTab.entries.forEach { tab ->
                                val isSelected = currentTab == tab
                                NavigationBarItem(
                                    selected = isSelected,
                                    onClick = { currentTab = tab },
                                    modifier = Modifier.testTag(tab.testTag),
                                    icon = {
                                        Icon(
                                            imageVector = if (isSelected) tab.selectedIcon else tab.unselectedIcon,
                                            contentDescription = tab.title,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    },
                                    label = {
                                        Text(
                                            text = tab.title,
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                                fontSize = 10.sp
                                            )
                                        )
                                    },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = Color.Black,
                                        selectedTextColor = CyberEmeraldPrimary,
                                        indicatorColor = CyberEmeraldPrimary,
                                        unselectedIconColor = CyberTextMuted,
                                        unselectedTextColor = CyberTextMuted
                                    )
                                )
                            }
                        }
                    }
                ) { innerPadding ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .statusBarsPadding()
                    ) {
                        Crossfade(
                            targetState = currentTab,
                            label = "TabCrossfade",
                            modifier = Modifier.fillMaxSize()
                        ) { targetTab ->
                            when (targetTab) {
                                NavigationTab.DASHBOARD -> DashboardScreen(viewModel = dashboardViewModel)
                                NavigationTab.AV_SCANNER -> AvScannerScreen(viewModel = avScannerViewModel)
                                NavigationTab.FIREWALL -> AppFirewallScreen(viewModel = firewallViewModel)
                                NavigationTab.PIHOLE -> PiHoleScreen(viewModel = piHoleViewModel)
                                NavigationTab.SYSTEM -> SystemInspectorScreen(viewModel = systemViewModel)
                            }
                        }
                    }
                }
            }
        }
    }
}

