package com.example.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.CellTower
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MobileOff
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SignalCellular4Bar
import androidx.compose.material.icons.filled.SignalCellularOff
import androidx.compose.material.icons.filled.SignalWifi4Bar
import androidx.compose.material.icons.filled.SignalWifiOff
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.AppNetworkRuleEntity
import com.example.ui.theme.CyberAlertRed
import com.example.ui.theme.CyberCardBg
import com.example.ui.theme.CyberCardBorder
import com.example.ui.theme.CyberDarkBg
import com.example.ui.theme.CyberEmeraldPrimary
import com.example.ui.theme.CyberEmeraldVariant
import com.example.ui.theme.CyberPrimaryCyan
import com.example.ui.theme.CyberSecondaryEmerald
import com.example.ui.theme.CyberTextMuted
import com.example.ui.theme.CyberTextPrimary
import com.example.ui.theme.CyberTextSecondary
import com.example.ui.theme.CyberWarningAmber
import com.example.ui.viewmodels.NetworkFirewallViewModel

@Composable
fun AppFirewallScreen(
    viewModel: NetworkFirewallViewModel,
    modifier: Modifier = Modifier
) {
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedFilter by viewModel.selectedFilter.collectAsStateWithLifecycle()
    val appRules by viewModel.filteredAppRules.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(CyberDarkBg)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item { Spacer(modifier = Modifier.height(8.dp)) }

        // Header Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, CyberCardBorder, RoundedCornerShape(28.dp)),
                colors = CardDefaults.cardColors(containerColor = CyberCardBg),
                shape = RoundedCornerShape(28.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "App Network Rules",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Light,
                            fontFamily = FontFamily.Serif
                        ),
                        color = CyberTextPrimary
                    )
                    Text(
                        text = "PER-APP FIREWALL CONTROLS",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.2.sp
                        ),
                        color = CyberEmeraldPrimary
                    )

                    Text(
                        text = "Toggle Wi-Fi and Mobile Data independently for every installed app, including low-level GrapheneOS system components.",
                        style = MaterialTheme.typography.bodySmall,
                        color = CyberTextMuted
                    )
                }
            }
        }

        // Search Bar
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.searchQuery.value = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("app_firewall_search_input"),
                placeholder = {
                    Text(
                        "Search app label or package (e.g. android.systemui)",
                        color = CyberTextMuted,
                        style = MaterialTheme.typography.bodyMedium
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = CyberEmeraldPrimary
                    )
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = CyberCardBg,
                    unfocusedContainerColor = CyberCardBg,
                    focusedBorderColor = CyberEmeraldPrimary,
                    unfocusedBorderColor = CyberCardBorder,
                    focusedTextColor = CyberTextPrimary,
                    unfocusedTextColor = CyberTextPrimary
                ),
                shape = RoundedCornerShape(16.dp),
                singleLine = true
            )
        }


        // Filter Chips Bar
        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                item {
                    AppFilterChip(
                        label = "All Apps",
                        selected = selectedFilter == "ALL",
                        onClick = { viewModel.selectedFilter.value = "ALL" }
                    )
                }
                item {
                    AppFilterChip(
                        label = "User Apps",
                        selected = selectedFilter == "USER",
                        onClick = { viewModel.selectedFilter.value = "USER" }
                    )
                }
                item {
                    AppFilterChip(
                        label = "System Apps",
                        selected = selectedFilter == "SYSTEM",
                        onClick = { viewModel.selectedFilter.value = "SYSTEM" }
                    )
                }
                item {
                    AppFilterChip(
                        label = "Blocked Apps",
                        selected = selectedFilter == "BLOCKED",
                        onClick = { viewModel.selectedFilter.value = "BLOCKED" }
                    )
                }
            }
        }

        // Quick Batch Actions
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                BatchActionButton(
                    text = "Block System Mobile",
                    onClick = { viewModel.setAllMobileDataState(enabled = false, forSystemOnly = true) },
                    modifier = Modifier.weight(1f)
                )
                BatchActionButton(
                    text = "Enable All WiFi",
                    onClick = { viewModel.setAllWifiState(enabled = true) },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // App List Count Label
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "CONFIGURED APPLICATIONS (${appRules.size})",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    ),
                    color = CyberPrimaryCyan
                )
            }
        }

        // App Rules Cards
        items(appRules, key = { it.packageName }) { rule ->
            AppNetworkRuleCard(
                rule = rule,
                onToggleWifi = { enabled -> viewModel.toggleWifi(rule, enabled) },
                onToggleMobile = { enabled -> viewModel.toggleMobileData(rule, enabled) }
            )
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }
    }
}

@Composable
private fun AppFilterChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
            )
        },
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = CyberPrimaryCyan,
            selectedLabelColor = Color.Black,
            containerColor = CyberCardBg,
            labelColor = CyberTextSecondary
        ),
        border = FilterChipDefaults.filterChipBorder(
            enabled = true,
            selected = selected,
            borderColor = CyberCardBorder,
            selectedBorderColor = CyberPrimaryCyan
        ),
        shape = RoundedCornerShape(20.dp)
    )
}

@Composable
private fun BatchActionButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(CyberCardBg)
            .border(1.dp, CyberCardBorder, RoundedCornerShape(10.dp))
            .clickable { onClick() }
            .padding(vertical = 10.dp, horizontal = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp
            ),
            color = CyberPrimaryCyan
        )
    }
}

@Composable
private fun AppNetworkRuleCard(
    rule: AppNetworkRuleEntity,
    onToggleWifi: (Boolean) -> Unit,
    onToggleMobile: (Boolean) -> Unit
) {
    val networkStatusText = when {
        rule.wifiAllowed && rule.mobileDataAllowed -> "Full Network Access"
        rule.wifiAllowed && !rule.mobileDataAllowed -> "Wi-Fi Only (Mobile Blocked)"
        !rule.wifiAllowed && rule.mobileDataAllowed -> "Mobile Data Only (Wi-Fi Blocked)"
        else -> "ISOLATED (ALL NETWORK BLOCKED)"
    }

    val networkStatusColor = when {
        rule.wifiAllowed && rule.mobileDataAllowed -> CyberSecondaryEmerald
        !rule.wifiAllowed && !rule.mobileDataAllowed -> CyberAlertRed
        else -> CyberWarningAmber
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, CyberCardBorder, RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = CyberCardBg),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (rule.isSystemApp) CyberCardBorder else CyberPrimaryCyan.copy(alpha = 0.15f))
                            .border(1.dp, if (rule.isSystemApp) CyberTextMuted else CyberPrimaryCyan, RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (rule.isSystemApp) Icons.Default.Android else Icons.Default.Lock,
                            contentDescription = rule.appName,
                            tint = if (rule.isSystemApp) CyberTextSecondary else CyberPrimaryCyan,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                text = rule.appName,
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                color = CyberTextPrimary,
                                maxLines = 1
                            )

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(if (rule.isSystemApp) CyberAlertRed.copy(alpha = 0.2f) else CyberSecondaryEmerald.copy(alpha = 0.2f))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = if (rule.isSystemApp) "SYSTEM" else "USER",
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, fontWeight = FontWeight.Bold),
                                    color = if (rule.isSystemApp) CyberAlertRed else CyberSecondaryEmerald
                                )
                            }
                        }

                        Text(
                            text = rule.packageName,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontFamily = FontFamily.Monospace,
                                fontSize = 10.sp
                            ),
                            color = CyberTextMuted,
                            maxLines = 1
                        )
                    }
                }
            }

            // Network Status Bar & Toggles Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(CyberDarkBg)
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Wifi Control Switch Button
                NetworkToggleButton(
                    label = "Wi-Fi",
                    isEnabled = rule.wifiAllowed,
                    activeIcon = Icons.Default.SignalWifi4Bar,
                    inactiveIcon = Icons.Default.SignalWifiOff,
                    onToggle = onToggleWifi,
                    testTag = "wifi_toggle_${rule.packageName}"
                )

                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(24.dp)
                        .background(CyberCardBorder)
                )

                // Mobile Data Control Switch Button
                NetworkToggleButton(
                    label = "Mobile",
                    isEnabled = rule.mobileDataAllowed,
                    activeIcon = Icons.Default.SignalCellular4Bar,
                    inactiveIcon = Icons.Default.SignalCellularOff,
                    onToggle = onToggleMobile,
                    testTag = "mobile_toggle_${rule.packageName}"
                )
            }

            Text(
                text = networkStatusText,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 10.sp
                ),
                color = networkStatusColor
            )
        }
    }
}

@Composable
private fun NetworkToggleButton(
    label: String,
    isEnabled: Boolean,
    activeIcon: androidx.compose.ui.graphics.vector.ImageVector,
    inactiveIcon: androidx.compose.ui.graphics.vector.ImageVector,
    onToggle: (Boolean) -> Unit,
    testTag: String
) {
    val activeColor = if (isEnabled) CyberSecondaryEmerald else CyberAlertRed

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .clickable { onToggle(!isEnabled) }
            .testTag(testTag)
            .padding(vertical = 4.dp, horizontal = 6.dp)
    ) {
        Icon(
            imageVector = if (isEnabled) activeIcon else inactiveIcon,
            contentDescription = label,
            tint = activeColor,
            modifier = Modifier.size(20.dp)
        )

        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
            color = CyberTextPrimary
        )

        Switch(
            checked = isEnabled,
            onCheckedChange = onToggle,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.Black,
                checkedTrackColor = CyberSecondaryEmerald,
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = CyberAlertRed.copy(alpha = 0.5f)
            ),
            modifier = Modifier.size(width = 36.dp, height = 24.dp)
        )
    }
}
