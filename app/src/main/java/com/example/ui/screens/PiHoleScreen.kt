package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PlaylistAddCheck
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.example.data.DnsQueryLogEntity
import com.example.data.DomainFilterEntity
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
import com.example.ui.viewmodels.PiHoleViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults

@Composable
fun PiHoleScreen(
    viewModel: PiHoleViewModel,
    modifier: Modifier = Modifier
) {
    val blacklists by viewModel.blacklists.collectAsStateWithLifecycle()
    val whitelists by viewModel.whitelists.collectAsStateWithLifecycle()
    val dnsLogs by viewModel.dnsLogs.collectAsStateWithLifecycle()
    val totalBlocked by viewModel.totalBlocked.collectAsStateWithLifecycle()
    val totalQueries by viewModel.totalQueries.collectAsStateWithLifecycle()
    val isLiveRunning by viewModel.isLiveStreamRunning.collectAsStateWithLifecycle()

    val dnsCryptEnabled by viewModel.dnsCryptEnabled.collectAsStateWithLifecycle()
    val selectedDnsProtocol by viewModel.selectedDnsProtocol.collectAsStateWithLifecycle()
    val upstreamServer by viewModel.upstreamServer.collectAsStateWithLifecycle()
    val dnssecActive by viewModel.dnssecActive.collectAsStateWithLifecycle()
    val dnsLatencyMs by viewModel.dnsLatencyMs.collectAsStateWithLifecycle()

    val newDomain by viewModel.newDomainInput.collectAsStateWithLifecycle()
    val newComment by viewModel.newDomainCommentInput.collectAsStateWithLifecycle()
    val selectedTab by viewModel.selectedTab.collectAsStateWithLifecycle()

    var searchQuery by remember { mutableStateOf("") }

    val blockRate = if (totalQueries > 0) (totalBlocked.toFloat() / totalQueries.toFloat() * 100f) else 0f

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(CyberDarkBg)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item { Spacer(modifier = Modifier.height(8.dp)) }

        // Pi-hole DNS Header & Metrics Banner
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
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Pi-hole DNS Engine",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Light,
                            fontFamily = FontFamily.Serif
                        ),
                        color = CyberTextPrimary
                    )
                    Text(
                        text = "SYSTEM-WIDE AD & TRACKER BLOCKING",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.2.sp
                        ),
                        color = CyberEmeraldPrimary
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        PiHoleStatBox(
                            title = "Queries Blocked",
                            value = "$totalBlocked",
                            accentColor = CyberAlertRed,
                            modifier = Modifier.weight(1f)
                        )
                        PiHoleStatBox(
                            title = "Block Rate",
                            value = String.format("%.1f%%", blockRate),
                            accentColor = CyberEmeraldPrimary,
                            modifier = Modifier.weight(1f)
                        )
                        PiHoleStatBox(
                            title = "Active Rules",
                            value = "${blacklists.size + whitelists.size}",
                            accentColor = CyberEmeraldVariant,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // DNSCrypt & Encrypted DNS Protocol Card ("All connections encrypted and private")
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, CyberEmeraldPrimary.copy(alpha = 0.5f), RoundedCornerShape(24.dp)),
                colors = CardDefaults.cardColors(containerColor = CyberCardBg),
                shape = RoundedCornerShape(24.dp)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "DNSCrypt & Private Encryption",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = CyberTextPrimary
                            )
                            Text(
                                text = "UPSTREAM SERVER: $upstreamServer ($dnsLatencyMs ms)",
                                style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace),
                                color = CyberEmeraldVariant
                            )
                        }

                        Switch(
                            checked = dnsCryptEnabled,
                            onCheckedChange = { viewModel.toggleDnsCrypt(it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.Black,
                                checkedTrackColor = CyberEmeraldPrimary,
                                uncheckedThumbColor = Color.White,
                                uncheckedTrackColor = CyberCardBorder
                            )
                        )
                    }

                    Text(
                        text = "Authenticates and encrypts DNS traffic between your mobile device and upstream resolvers. Prevents spoofing, man-in-the-middle attacks, and ISP DNS leaks.",
                        style = MaterialTheme.typography.bodySmall,
                        color = CyberTextMuted
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf("DNSCrypt v2", "DNS-over-HTTPS (DoH)", "DNS-over-TLS (DoT)").forEach { proto ->
                            FilterChip(
                                selected = selectedDnsProtocol.contains(proto.split(" ").first()),
                                onClick = { viewModel.setDnsProtocol(proto) },
                                label = { Text(proto, style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold)) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = CyberEmeraldPrimary,
                                    selectedLabelColor = Color.Black,
                                    containerColor = CyberDarkBg,
                                    labelColor = CyberTextSecondary
                                ),
                                shape = RoundedCornerShape(14.dp)
                            )
                        }
                    }
                }
            }
        }

        // Custom DoH / DNSCrypt Server Management Card
        item {
            val customDnsInput by viewModel.customDnsInput.collectAsStateWithLifecycle()
            val customDoHEnabled by viewModel.customDoHEnabled.collectAsStateWithLifecycle()
            val customServers by viewModel.customDnsServers.collectAsStateWithLifecycle()

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, CyberCardBorder, RoundedCornerShape(24.dp)),
                colors = CardDefaults.cardColors(containerColor = CyberCardBg),
                shape = RoundedCornerShape(24.dp)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Custom DoH / DNSCrypt Servers",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = CyberTextPrimary
                            )
                            Text(
                                text = "MANAGE SECURE ENCRYPTED DNS LOOKUPS",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
                                color = CyberPrimaryCyan
                            )
                        }

                        Switch(
                            checked = customDoHEnabled,
                            onCheckedChange = { viewModel.toggleCustomDoH(it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.Black,
                                checkedTrackColor = CyberPrimaryCyan,
                                uncheckedThumbColor = Color.White,
                                uncheckedTrackColor = CyberCardBorder
                            ),
                            modifier = Modifier.testTag("custom_doh_toggle")
                        )
                    }

                    Text(
                        text = "Add and manage custom DNS over HTTPS (DoH) or DNSCrypt servers for zero-knowledge encrypted domain resolution.",
                        style = MaterialTheme.typography.bodySmall,
                        color = CyberTextMuted
                    )

                    OutlinedTextField(
                        value = customDnsInput,
                        onValueChange = { viewModel.customDnsInput.value = it },
                        placeholder = { Text("e.g. https://dns.adguard.com/dns-query", color = CyberTextMuted) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("custom_doh_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = CyberDarkBg,
                            unfocusedContainerColor = CyberDarkBg,
                            focusedBorderColor = CyberPrimaryCyan,
                            unfocusedBorderColor = CyberCardBorder,
                            focusedTextColor = CyberTextPrimary,
                            unfocusedTextColor = CyberTextPrimary
                        ),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )

                    Button(
                        onClick = { viewModel.addCustomDnsServer() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("add_custom_doh_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = CyberPrimaryCyan),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = "Add", tint = Color.Black, modifier = Modifier.size(16.dp))
                            Text("Add Custom DoH Server", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), color = Color.Black)
                        }
                    }

                    if (customServers.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "CONFIGURED RESOLVERS (${customServers.size})",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = CyberTextSecondary
                        )
                        customServers.forEach { server ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(CyberDarkBg)
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = server,
                                    style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace),
                                    color = CyberTextPrimary,
                                    modifier = Modifier.weight(1f),
                                    maxLines = 1
                                )
                                IconButton(
                                    onClick = { viewModel.removeCustomDnsServer(server) }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Remove",
                                        tint = CyberAlertRed,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }



        // Tab Navigation Bar
        item {
            val tabs = listOf("BLACKLIST", "WHITELIST", "LIVE LOGS")
            val currentTabIndex = when (selectedTab) {
                "WHITELIST" -> 1
                "LIVE_LOGS" -> 2
                else -> 0
            }

            TabRow(
                selectedTabIndex = currentTabIndex,
                containerColor = CyberCardBg,
                contentColor = CyberPrimaryCyan,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[currentTabIndex]),
                        color = CyberPrimaryCyan,
                        height = 3.dp
                    )
                },
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.dp, CyberCardBorder, RoundedCornerShape(12.dp))
            ) {
                Tab(
                    selected = currentTabIndex == 0,
                    onClick = { viewModel.selectedTab.value = "BLACKLIST" },
                    modifier = Modifier.testTag("tab_blacklist"),
                    text = {
                        Text(
                            "Blacklist (${blacklists.size})",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = if (currentTabIndex == 0) CyberPrimaryCyan else CyberTextSecondary
                        )
                    }
                )
                Tab(
                    selected = currentTabIndex == 1,
                    onClick = { viewModel.selectedTab.value = "WHITELIST" },
                    modifier = Modifier.testTag("tab_whitelist"),
                    text = {
                        Text(
                            "Whitelist (${whitelists.size})",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = if (currentTabIndex == 1) CyberPrimaryCyan else CyberTextSecondary
                        )
                    }
                )
                Tab(
                    selected = currentTabIndex == 2,
                    onClick = { viewModel.selectedTab.value = "LIVE_LOGS" },
                    modifier = Modifier.testTag("tab_live_logs"),
                    text = {
                        Text(
                            "Live Stream",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = if (currentTabIndex == 2) CyberPrimaryCyan else CyberTextSecondary
                        )
                    }
                )
            }
        }

        // Add Domain Box (Show for Blacklist & Whitelist tabs)
        if (selectedTab != "LIVE_LOGS") {
            item {
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
                        Text(
                            text = "ADD DOMAIN FILTER RULE",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = CyberPrimaryCyan
                        )

                        OutlinedTextField(
                            value = newDomain,
                            onValueChange = { viewModel.newDomainInput.value = it },
                            placeholder = { Text("e.g. telemetry.tracker.com", color = CyberTextMuted) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("pihole_domain_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = CyberDarkBg,
                                unfocusedContainerColor = CyberDarkBg,
                                focusedBorderColor = CyberPrimaryCyan,
                                unfocusedBorderColor = CyberCardBorder,
                                focusedTextColor = CyberTextPrimary,
                                unfocusedTextColor = CyberTextPrimary
                            ),
                            shape = RoundedCornerShape(10.dp),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = newComment,
                            onValueChange = { viewModel.newDomainCommentInput.value = it },
                            placeholder = { Text("Optional rule comment (e.g. Ad server block)", color = CyberTextMuted) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = CyberDarkBg,
                                unfocusedContainerColor = CyberDarkBg,
                                focusedBorderColor = CyberPrimaryCyan,
                                unfocusedBorderColor = CyberCardBorder,
                                focusedTextColor = CyberTextPrimary,
                                unfocusedTextColor = CyberTextPrimary
                            ),
                            shape = RoundedCornerShape(10.dp),
                            singleLine = true
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = { viewModel.addDomain(isWhitelist = false) },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("add_blacklist_button"),
                                colors = ButtonDefaults.buttonColors(containerColor = CyberAlertRed),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(imageVector = Icons.Default.Block, contentDescription = "Blacklist", tint = Color.White, modifier = Modifier.size(16.dp))
                                    Text("Add to Blacklist", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                                }
                            }

                            Button(
                                onClick = { viewModel.addDomain(isWhitelist = true) },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("add_whitelist_button"),
                                colors = ButtonDefaults.buttonColors(containerColor = CyberSecondaryEmerald),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(imageVector = Icons.Default.CheckCircle, contentDescription = "Whitelist", tint = Color.Black, modifier = Modifier.size(16.dp))
                                    Text("Add to Whitelist", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = Color.Black)
                                }
                            }
                        }
                    }
                }
            }
        }

        // Live Log Control Bar (Show when LIVE_LOGS is active)
        if (selectedTab == "LIVE_LOGS") {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(if (isLiveRunning) CyberSecondaryEmerald else CyberWarningAmber)
                        )
                        Text(
                            text = if (isLiveRunning) "LIVE QUERY STREAMING" else "STREAM PAUSED",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            ),
                            color = if (isLiveRunning) CyberSecondaryEmerald else CyberWarningAmber
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        IconButton(
                            onClick = { viewModel.toggleLiveStream() },
                            modifier = Modifier.testTag("toggle_live_stream_button")
                        ) {
                            Icon(
                                imageVector = if (isLiveRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = "Pause Stream",
                                tint = CyberPrimaryCyan
                            )
                        }

                        IconButton(
                            onClick = { viewModel.clearLogs() },
                            modifier = Modifier.testTag("clear_logs_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Clear Logs",
                                tint = CyberAlertRed
                            )
                        }
                    }
                }
            }
        }

        // Main List Content based on Selected Tab
        when (selectedTab) {
            "BLACKLIST" -> {
                items(blacklists, key = { it.id }) { rule ->
                    DomainRuleCard(
                        rule = rule,
                        onToggle = { enabled -> viewModel.toggleDomainState(rule.id, enabled) },
                        onDelete = { viewModel.deleteDomain(rule.id) }
                    )
                }
            }
            "WHITELIST" -> {
                items(whitelists, key = { it.id }) { rule ->
                    DomainRuleCard(
                        rule = rule,
                        onToggle = { enabled -> viewModel.toggleDomainState(rule.id, enabled) },
                        onDelete = { viewModel.deleteDomain(rule.id) }
                    )
                }
            }
            "LIVE_LOGS" -> {
                items(dnsLogs, key = { it.id }) { queryLog ->
                    LiveDnsQueryCard(
                        log = queryLog,
                        onQuickBlacklist = { viewModel.quickBlacklistDomain(queryLog.domain) },
                        onQuickWhitelist = { viewModel.quickWhitelistDomain(queryLog.domain) }
                    )
                }
            }
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }
    }
}

@Composable
private fun PiHoleStatBox(
    title: String,
    value: String,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(CyberDarkBg)
            .border(1.dp, CyberCardBorder, RoundedCornerShape(10.dp))
            .padding(10.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                color = CyberTextMuted
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = FontFamily.Monospace
                ),
                color = accentColor
            )
        }
    }
}

@Composable
private fun DomainRuleCard(
    rule: DomainFilterEntity,
    onToggle: (Boolean) -> Unit,
    onDelete: () -> Unit
) {
    val accentColor = if (rule.isWhitelist) CyberSecondaryEmerald else CyberAlertRed

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, CyberCardBorder, RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = CyberCardBg),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
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
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(accentColor.copy(alpha = 0.15f))
                        .border(1.dp, accentColor, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (rule.isWhitelist) Icons.Default.CheckCircle else Icons.Default.Block,
                        contentDescription = rule.domain,
                        tint = accentColor,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = rule.domain,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        ),
                        color = CyberTextPrimary,
                        maxLines = 1
                    )

                    if (rule.comment.isNotBlank()) {
                        Text(
                            text = rule.comment,
                            style = MaterialTheme.typography.bodySmall,
                            color = CyberTextSecondary
                        )
                    }

                    Text(
                        text = "${rule.category} • ${rule.hitCount} Hits",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                        color = CyberTextMuted
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.testTag("delete_rule_${rule.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete Rule",
                        tint = CyberAlertRed.copy(alpha = 0.8f)
                    )
                }
            }
        }
    }
}

@Composable
private fun LiveDnsQueryCard(
    log: DnsQueryLogEntity,
    onQuickBlacklist: () -> Unit,
    onQuickWhitelist: () -> Unit
) {
    val statusColor = if (log.blocked) CyberAlertRed else CyberSecondaryEmerald
    val dateFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
    val timeStr = dateFormat.format(Date(log.timestamp))

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, CyberCardBorder, RoundedCornerShape(10.dp)),
        colors = CardDefaults.cardColors(containerColor = CyberCardBg),
        shape = RoundedCornerShape(10.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(statusColor.copy(alpha = 0.15f))
                            .border(1.dp, statusColor, RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = if (log.blocked) "BLOCKED" else "ALLOWED",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp
                            ),
                            color = statusColor
                        )
                    }

                    Text(
                        text = log.appName,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = CyberTextPrimary
                    )
                }

                Text(
                    text = timeStr,
                    style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace),
                    color = CyberTextMuted
                )
            }

            Text(
                text = log.domain,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.SemiBold
                ),
                color = CyberPrimaryCyan
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = log.reason,
                    style = MaterialTheme.typography.labelSmall,
                    color = CyberTextSecondary
                )

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(CyberAlertRed.copy(alpha = 0.15f))
                            .border(1.dp, CyberAlertRed, RoundedCornerShape(6.dp))
                            .clickable { onQuickBlacklist() }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text("+ Blacklist", style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold), color = CyberAlertRed)
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(CyberSecondaryEmerald.copy(alpha = 0.15f))
                            .border(1.dp, CyberSecondaryEmerald, RoundedCornerShape(6.dp))
                            .clickable { onQuickWhitelist() }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text("+ Whitelist", style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold), color = CyberSecondaryEmerald)
                    }
                }
            }
        }
    }
}
