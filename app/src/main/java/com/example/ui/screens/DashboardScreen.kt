package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.VideocamOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.ThreatLogEntity
import com.example.ui.theme.CyberAlertRed
import com.example.ui.theme.CyberCardBg
import com.example.ui.theme.CyberCardBorder
import com.example.ui.theme.CyberDarkBg
import com.example.ui.theme.CyberEmeraldPrimary
import com.example.ui.theme.CyberEmeraldVariant
import com.example.ui.theme.CyberPrimaryCyan
import com.example.ui.theme.CyberTextMuted
import com.example.ui.theme.CyberTextPrimary
import com.example.ui.theme.CyberTextSecondary
import com.example.ui.theme.CyberWarningAmber
import com.example.ui.viewmodels.SecurityDashboardViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Restore
import com.example.ui.theme.CyberSecondaryEmerald
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.IconButton
import androidx.compose.material3.TextButton

@Composable
fun DashboardScreen(
    viewModel: SecurityDashboardViewModel,
    modifier: Modifier = Modifier
) {
    val isProtected by viewModel.isProtectionActive.collectAsStateWithLifecycle()
    val aiAllocation by viewModel.aiCpuAllocation.collectAsStateWithLifecycle()
    val aiStatus by viewModel.aiEfficiencyStatus.collectAsStateWithLifecycle()
    val cameraKilled by viewModel.cameraKillswitch.collectAsStateWithLifecycle()
    val micKilled by viewModel.micKillswitch.collectAsStateWithLifecycle()
    val hardenedMalloc by viewModel.hardenedMallocEnabled.collectAsStateWithLifecycle()
    val totalBlocked by viewModel.totalBlockedQueries.collectAsStateWithLifecycle()
    val totalQueries by viewModel.totalQueries.collectAsStateWithLifecycle()
    val threatLogs by viewModel.threatLogs.collectAsStateWithLifecycle()
    val notifications by viewModel.notifications.collectAsStateWithLifecycle()
    val unreadCount by viewModel.unreadNotifCount.collectAsStateWithLifecycle()
    val quarantineItems by viewModel.quarantineItems.collectAsStateWithLifecycle()
    val bytesIn by viewModel.bytesReceived.collectAsStateWithLifecycle()
    val bytesOut by viewModel.bytesSent.collectAsStateWithLifecycle()
    val torRouting by viewModel.torRoutingEnabled.collectAsStateWithLifecycle()
    val exploitProtection by viewModel.exploitProtection.collectAsStateWithLifecycle()
    val scopedStorage by viewModel.scopedStorage.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(CyberDarkBg)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { Spacer(modifier = Modifier.height(8.dp)) }

        // Top Banner / Status Hero with Notification Badge
        item {
            ShieldStatusHero(
                isProtected = isProtected,
                unreadCount = unreadCount,
                onToggleProtection = { viewModel.toggleProtection(it) },
                onClearNotifs = { viewModel.markAllNotificationsRead() }
            )
        }

        // Notification Log History (if unread or present)
        if (notifications.isNotEmpty()) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, CyberCardBorder, RoundedCornerShape(20.dp)),
                    colors = CardDefaults.cardColors(containerColor = CyberCardBg),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
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
                                Icon(imageVector = Icons.Default.Notifications, contentDescription = "Alerts", tint = CyberEmeraldPrimary)
                                Text(
                                    text = "PUSH NOTIFICATION LOGS",
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
                                    color = CyberEmeraldPrimary
                                )
                            }
                            if (unreadCount > 0) {
                                TextButton(onClick = { viewModel.markAllNotificationsRead() }) {
                                    Text("Mark Read", style = MaterialTheme.typography.labelSmall, color = CyberEmeraldVariant)
                                }
                            }
                        }

                        notifications.take(3).forEach { notif ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(if (!notif.isRead) CyberEmeraldPrimary.copy(alpha = 0.08f) else Color.Transparent, RoundedCornerShape(12.dp))
                                    .padding(8.dp),
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(if (notif.severity == "CRITICAL") CyberAlertRed else CyberEmeraldPrimary)
                                )
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(notif.title, style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), color = CyberTextPrimary)
                                    Text(notif.message, style = MaterialTheme.typography.bodySmall, color = CyberTextSecondary)
                                }
                            }
                        }
                    }
                }
            }
        }

        // 35% AI Security CPU Allocation Engine Card
        item {
            AiCpuAllocationCard(
                aiAllocation = aiAllocation.toFloat(),
                aiStatus = aiStatus,
                isProtected = isProtected,
                bytesIn = bytesIn,
                bytesOut = bytesOut
            )
        }

        // Multi-Engine AV Scan Status Dashboard Component
        item {
            AvScanDashboardCard(viewModel = viewModel)
        }

        // Auto-Quarantine & AI Firewall Port Blocking Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, CyberWarningAmber.copy(alpha = 0.5f), RoundedCornerShape(20.dp)),
                colors = CardDefaults.cardColors(containerColor = CyberCardBg),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Automated Security & AI Port Defense",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = CyberTextPrimary
                    )
                    Text(
                        text = "INSTANT THREAT CONTAINMENT & NEURAL PORT FILTERING",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
                        color = CyberWarningAmber
                    )

                    androidx.compose.material3.Button(
                        onClick = { viewModel.autoQuarantineEverything() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("auto_quarantine_all_button"),
                        colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = CyberAlertRed),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Security, contentDescription = "Quarantine", tint = Color.White, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Auto-Quarantine Everything",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = Color.White)
                        )
                    }

                    androidx.compose.material3.Button(
                        onClick = { viewModel.aiBlockFirewallPorts() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("ai_block_ports_button"),
                        colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = CyberPrimaryCyan),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Lock, contentDescription = "AI Ports", tint = Color.Black, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Use AI to Block Vulnerable Firewall Ports",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = Color.Black)
                        )
                    }
                }
            }
        }

        // Quarantine System
        if (quarantineItems.isNotEmpty()) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, CyberAlertRed.copy(alpha = 0.4f), RoundedCornerShape(20.dp)),
                    colors = CardDefaults.cardColors(containerColor = CyberCardBg),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "ISOLATED QUARANTINE VAULT (${quarantineItems.size})",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
                            color = CyberAlertRed
                        )

                        quarantineItems.forEach { qItem ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(CyberDarkBg, RoundedCornerShape(12.dp))
                                    .padding(10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(qItem.itemName, style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace), color = CyberTextPrimary)
                                    Text("Threat: ${qItem.threatFamily} (Risk: ${qItem.riskScore}/100)", style = MaterialTheme.typography.bodySmall, color = CyberAlertRed)
                                }

                                Row {
                                    IconButton(onClick = { viewModel.restoreQuarantineItem(qItem.id) }) {
                                        Icon(imageVector = Icons.Default.Restore, contentDescription = "Restore", tint = CyberEmeraldPrimary)
                                    }
                                    IconButton(onClick = { viewModel.deleteQuarantineItem(qItem.id) }) {
                                        Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = CyberAlertRed)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Quick Hardware & Privacy Killswitches Grid
        item {
            Text(
                text = "GRAPHENEOS PRIVACY CONTROLS",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.2.sp
                ),
                color = CyberEmeraldPrimary
            )
            Spacer(modifier = Modifier.height(8.dp))

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    KillswitchCard(
                        title = "Camera Sensor",
                        statusText = if (cameraKilled) "HARDWARE KILLED" else "PERMISSIONS REGULATED",
                        isKilled = cameraKilled,
                        icon = if (cameraKilled) Icons.Default.VideocamOff else Icons.Default.CameraAlt,
                        onToggle = { viewModel.toggleCameraKillswitch(!cameraKilled) },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("camera_killswitch_card")
                    )

                    KillswitchCard(
                        title = "Microphone Sensor",
                        statusText = if (micKilled) "MIC SILENCED" else "PERMISSIONS REGULATED",
                        isKilled = micKilled,
                        icon = if (micKilled) Icons.Default.MicOff else Icons.Default.Mic,
                        onToggle = { viewModel.toggleMicKillswitch(!micKilled) },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("mic_killswitch_card")
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    KillswitchCard(
                        title = "Hardened Malloc",
                        statusText = if (hardenedMalloc) "ZERO-FILL ACTIVE" else "STANDARD MEMORY",
                        isKilled = !hardenedMalloc,
                        icon = Icons.Default.Memory,
                        onToggle = { viewModel.toggleHardenedMalloc(!hardenedMalloc) },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("malloc_killswitch_card")
                    )
                }
            }
        }

        // Tails & GrapheneOS Advanced Hardening & Onion Circuit Hub
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, CyberEmeraldPrimary.copy(alpha = 0.5f), RoundedCornerShape(20.dp)),
                colors = CardDefaults.cardColors(containerColor = CyberCardBg),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "TAILS & GRAPHENEOS HARDENING HUB",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
                                color = CyberEmeraldPrimary
                            )
                            Text(
                                text = "Zero-trust onion routing & memory safety",
                                style = MaterialTheme.typography.labelSmall,
                                color = CyberTextMuted
                            )
                        }
                        Box(
                            modifier = Modifier
                                .background(CyberEmeraldPrimary.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = if (torRouting) "TOR 3-HOP ACTIVE" else "DIRECT MESH",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace),
                                color = CyberEmeraldPrimary
                            )
                        }
                    }

                    // Tor Circuit Node Simulation
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(CyberDarkBg, RoundedCornerShape(12.dp))
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "ONION ROUTING CIRCUIT (TAILS OS SIM)",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace),
                            color = CyberTextSecondary
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("1. Guard (Frankfurt DE)", style = MaterialTheme.typography.bodySmall, color = CyberTextPrimary)
                            Text("⚡ 14ms", style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace), color = CyberEmeraldPrimary)
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("2. Relay (Zurich CH)", style = MaterialTheme.typography.bodySmall, color = CyberTextPrimary)
                            Text("⚡ 22ms", style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace), color = CyberEmeraldPrimary)
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("3. Exit (Reykjavik IS)", style = MaterialTheme.typography.bodySmall, color = CyberTextPrimary)
                            Text("⚡ 38ms", style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace), color = CyberEmeraldPrimary)
                        }
                    }

                    // Toggles for Tor, Exploit Protection, Scoped Storage
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Tor Onion Routing", style = MaterialTheme.typography.bodyMedium, color = CyberTextPrimary)
                        Switch(
                            checked = torRouting,
                            onCheckedChange = { viewModel.toggleTorRouting() },
                            colors = SwitchDefaults.colors(checkedThumbColor = CyberEmeraldPrimary, checkedTrackColor = CyberEmeraldVariant)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("GrapheneOS Exploit Protection", style = MaterialTheme.typography.bodyMedium, color = CyberTextPrimary)
                        Switch(
                            checked = exploitProtection,
                            onCheckedChange = { viewModel.toggleExploitProtection() },
                            colors = SwitchDefaults.colors(checkedThumbColor = CyberEmeraldPrimary, checkedTrackColor = CyberEmeraldVariant)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Strict Scoped Storage Isolation", style = MaterialTheme.typography.bodyMedium, color = CyberTextPrimary)
                        Switch(
                            checked = scopedStorage,
                            onCheckedChange = { viewModel.toggleScopedStorage() },
                            colors = SwitchDefaults.colors(checkedThumbColor = CyberEmeraldPrimary, checkedTrackColor = CyberEmeraldVariant)
                        )
                    }

                    // Tails Amnesic RAM Wipe Button
                    androidx.compose.material3.Button(
                        onClick = { viewModel.amnesiaWipe() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("tails_amnesia_wipe_button"),
                        colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = CyberAlertRed.copy(alpha = 0.2f)),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, CyberAlertRed)
                    ) {
                        Icon(imageVector = Icons.Default.Block, contentDescription = "Wipe", tint = CyberAlertRed, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "EXECUTE TAILS AMNESIAC RAM WIPE",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = CyberAlertRed)
                        )
                    }
                }
            }
        }

        // Real-time DNS & Protection Quick Counters
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatMetricCard(
                    title = "Ads Blocked",
                    value = "$totalBlocked",
                    subtitle = "Out of $totalQueries DNS queries",
                    icon = Icons.Default.Block,
                    accentColor = CyberAlertRed,
                    modifier = Modifier.weight(1f)
                )

                StatMetricCard(
                    title = "AI Threat Scans",
                    value = "${threatLogs.size}",
                    subtitle = "35% CPU Active Engine",
                    icon = Icons.Default.BugReport,
                    accentColor = CyberEmeraldPrimary,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Live Security Log Stream
        item {
            Text(
                text = "REAL-TIME SECURITY & KILL CHAIN TIMELINE",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.2.sp
                ),
                color = CyberEmeraldPrimary
            )
        }

        items(threatLogs, key = { it.id }) { log ->
            ThreatLogItemCard(log = log)
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }
    }
}

@Composable
private fun ShieldStatusHero(
    isProtected: Boolean,
    unreadCount: Int,
    onToggleProtection: (Boolean) -> Unit,
    onClearNotifs: () -> Unit
) {
    val statusColor by animateColorAsState(
        if (isProtected) CyberEmeraldPrimary else CyberAlertRed,
        label = "statusColor"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, statusColor.copy(alpha = 0.5f), RoundedCornerShape(28.dp)),
        colors = CardDefaults.cardColors(containerColor = CyberCardBg),
        shape = RoundedCornerShape(28.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                BadgedBox(
                    badge = {
                        if (unreadCount > 0) {
                            Badge(containerColor = CyberAlertRed, contentColor = Color.White) {
                                Text("$unreadCount")
                            }
                        }
                    }
                ) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(statusColor.copy(alpha = 0.15f))
                            .border(1.5.dp, statusColor, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isProtected) Icons.Default.Shield else Icons.Default.PowerSettingsNew,
                            contentDescription = "Shield Status",
                            tint = statusColor,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }

                Column {
                    Text(
                        text = if (isProtected) "NetGuard Plus" else "Protection Paused",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Light,
                            fontFamily = FontFamily.Serif
                        ),
                        color = CyberTextPrimary
                    )
                    Text(
                        text = if (isProtected) "NetGuard Firewall, DNSCrypt & AV Active" else "Network firewall & Pi-hole DNS disabled",
                        style = MaterialTheme.typography.bodySmall,
                        color = CyberTextSecondary
                    )
                }
            }

            Switch(
                checked = isProtected,
                onCheckedChange = onToggleProtection,
                modifier = Modifier.testTag("master_protection_switch"),
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.Black,
                    checkedTrackColor = CyberEmeraldPrimary,
                    uncheckedThumbColor = Color.White,
                    uncheckedTrackColor = CyberCardBorder
                )
            )
        }
    }
}

@Composable
private fun AiCpuAllocationCard(
    aiAllocation: Float,
    aiStatus: String,
    isProtected: Boolean,
    bytesIn: Long,
    bytesOut: Long
) {
    val animatedProgress by animateFloatAsState(
        targetValue = if (isProtected) (aiAllocation / 100f) else 0f,
        animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing),
        label = "aiProgress"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, CyberCardBorder, RoundedCornerShape(24.dp)),
        colors = CardDefaults.cardColors(containerColor = CyberCardBg),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Memory,
                        contentDescription = "AI CPU",
                        tint = CyberEmeraldPrimary,
                        modifier = Modifier.size(22.dp)
                    )
                    Text(
                        text = "35% Dedicated AI CPU Core",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = CyberTextPrimary
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(CyberEmeraldPrimary.copy(alpha = 0.15f))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = String.format("%.1f%% CPU", aiAllocation),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.ExtraBold,
                            fontFamily = FontFamily.Monospace
                        ),
                        color = CyberEmeraldPrimary
                    )
                }
            }

            Text(
                text = "Dedicated background AI thread inspecting per-app network sockets and running local malware heuristics.",
                style = MaterialTheme.typography.bodySmall,
                color = CyberTextSecondary
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("RX: ${bytesIn / 1024} KB", style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace), color = CyberEmeraldVariant)
                Text("TX: ${bytesOut / 1024} KB", style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace), color = CyberEmeraldPrimary)
            }

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                LinearProgressIndicator(
                    progress = { animatedProgress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = CyberEmeraldPrimary,
                    trackColor = CyberCardBorder
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Engine Status: $aiStatus",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isProtected) CyberEmeraldVariant else CyberWarningAmber
                    )
                    Text(
                        text = "Lightweight Daemon",
                        style = MaterialTheme.typography.labelSmall,
                        color = CyberTextMuted
                    )
                }
            }
        }
    }
}


@Composable
private fun KillswitchCard(
    title: String,
    statusText: String,
    isKilled: Boolean,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    val borderColor = if (isKilled) CyberAlertRed else CyberCardBorder
    val iconColor = if (isKilled) CyberAlertRed else CyberEmeraldPrimary

    Card(
        modifier = modifier
            .border(1.dp, borderColor, RoundedCornerShape(20.dp))
            .clickable { onToggle() },
        colors = CardDefaults.cardColors(containerColor = CyberCardBg),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(14.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = iconColor,
                    modifier = Modifier.size(22.dp)
                )
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(iconColor)
                )
            }

            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                color = CyberTextPrimary
            )

            Text(
                text = statusText,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                ),
                color = iconColor
            )
        }
    }
}

@Composable
private fun StatMetricCard(
    title: String,
    value: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.border(1.dp, CyberCardBorder, RoundedCornerShape(20.dp)),
        colors = CardDefaults.cardColors(containerColor = CyberCardBg),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = accentColor,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelMedium,
                    color = CyberTextSecondary
                )
            }

            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold),
                color = CyberTextPrimary
            )

            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall,
                color = CyberTextMuted
            )
        }
    }
}

@Composable
private fun ThreatLogItemCard(log: ThreatLogEntity) {
    val severityColor = when (log.severity) {
        "CRITICAL" -> CyberAlertRed
        "HIGH" -> CyberAlertRed
        "MEDIUM" -> CyberWarningAmber
        else -> CyberEmeraldPrimary
    }

    val dateFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
    val timeStr = dateFormat.format(Date(log.timestamp))

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, CyberCardBorder, RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = CyberCardBg),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(severityColor)
                    .padding(top = 4.dp)
            )

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = log.threatTitle,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = CyberTextPrimary
                    )

                    Text(
                        text = timeStr,
                        style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace),
                        color = CyberTextMuted
                    )
                }

                Text(
                    text = log.details,
                    style = MaterialTheme.typography.bodySmall,
                    color = CyberTextSecondary
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = log.module,
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                        color = CyberEmeraldPrimary
                    )
                    Text(
                        text = "•",
                        style = MaterialTheme.typography.labelSmall,
                        color = CyberTextMuted
                    )
                    Text(
                        text = log.severity,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = severityColor
                    )
                }
            }
        }
    }
}

@Composable
private fun AvScanDashboardCard(
    viewModel: SecurityDashboardViewModel
) {
    val avLogs by viewModel.avScanLogs.collectAsStateWithLifecycle()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, CyberEmeraldPrimary.copy(alpha = 0.5f), RoundedCornerShape(24.dp)),
        colors = CardDefaults.cardColors(containerColor = CyberCardBg),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Multi-Engine AV Scan Status",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = CyberTextPrimary
                    )
                    Text(
                        text = "8/8 ENTERPRISE AV ENGINES ACTIVE",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
                        color = CyberEmeraldPrimary
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(CyberSecondaryEmerald.copy(alpha = 0.15f))
                        .border(1.dp, CyberSecondaryEmerald, RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "SECURE",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold),
                        color = CyberSecondaryEmerald
                    )
                }
            }

            Text(
                text = "Real-time heuristic scanning across packages, memory, and URLs using ClamAV, YARA rules, and Neural Ensembles.",
                style = MaterialTheme.typography.bodySmall,
                color = CyberTextMuted
            )

            androidx.compose.material3.Button(
                onClick = { viewModel.runManualSystemScan() },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("manual_system_scan_button"),
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = CyberEmeraldPrimary),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(imageVector = Icons.Default.Security, contentDescription = "Scan", tint = Color.Black, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Initiate Manual System Scan",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = Color.Black)
                )
            }

            Text(
                text = "RECENTLY SCANNED APPLICATIONS (${avLogs.size})",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
                color = CyberPrimaryCyan
            )

            avLogs.take(3).forEach { log ->
                val isMalicious = log.verdict == "MALICIOUS"
                val riskColor = if (isMalicious) CyberAlertRed else CyberSecondaryEmerald
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(CyberDarkBg)
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = log.targetInput,
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace),
                            color = CyberTextPrimary,
                            maxLines = 1
                        )
                        Text(
                            text = "${log.threatFamily} • Risk Score: ${log.riskScore}/100",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                            color = riskColor
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(riskColor.copy(alpha = 0.15f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = log.verdict,
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, fontWeight = FontWeight.Bold),
                            color = riskColor
                        )
                    }
                }
            }
        }
    }
}

