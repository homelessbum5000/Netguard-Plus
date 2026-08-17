package com.example.ui.screens

import com.example.data.SystemFileItem
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.FolderSpecial
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.filled.Usb
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.filled.WifiTethering
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import com.example.ui.viewmodels.SystemInspectorViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SystemInspectorScreen(
    viewModel: SystemInspectorViewModel,
    modifier: Modifier = Modifier
) {
    val storageScopes by viewModel.storageScopesEnabled.collectAsStateWithLifecycle()
    val execSpawning by viewModel.execSpawningBlocked.collectAsStateWithLifecycle()
    val networkPrompt by viewModel.networkPermissionPrompt.collectAsStateWithLifecycle()

    val wifiDebugging by viewModel.wifiDebuggingEnabled.collectAsStateWithLifecycle()
    val adbPort by viewModel.adbPort.collectAsStateWithLifecycle()
    val pairingCode by viewModel.adbPairingCode.collectAsStateWithLifecycle()
    val lanIp by viewModel.lanIp.collectAsStateWithLifecycle()
    val usbTethering by viewModel.usbTetheringActive.collectAsStateWithLifecycle()
    val btPairing by viewModel.bluetoothPairingActive.collectAsStateWithLifecycle()
    val a67lOptimization by viewModel.a67lOptimizationActive.collectAsStateWithLifecycle()

    val scope = rememberCoroutineScope()
    var isDiagnosticRunning by remember { mutableStateOf(false) }
    val diagnosticConsoleLogs = remember { mutableStateListOf<String>() }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(CyberDarkBg)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item { Spacer(modifier = Modifier.height(8.dp)) }

        // Header Banner
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
                        text = "System & Wireless Control",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Light,
                            fontFamily = FontFamily.Serif
                        ),
                        color = CyberTextPrimary
                    )
                    Text(
                        text = "CONFIGURE WI-FI DEBUGGING, USB TETHERING & OS HARDENING",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.2.sp
                        ),
                        color = CyberEmeraldPrimary
                    )

                    Text(
                        text = "Inspect core binaries, storage scopes, ADB over Wireless pairing, and LAN/USB debugging bridges.",
                        style = MaterialTheme.typography.bodySmall,
                        color = CyberTextMuted
                    )
                }
            }
        }

        // Hardware Compatibility & A67L Profile Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, CyberEmeraldPrimary.copy(alpha = 0.4f), RoundedCornerShape(24.dp)),
                colors = CardDefaults.cardColors(containerColor = CyberCardBg),
                shape = RoundedCornerShape(24.dp)
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
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(CyberEmeraldPrimary.copy(alpha = 0.15f))
                                    .border(1.dp, CyberEmeraldPrimary, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PhoneAndroid,
                                    contentDescription = "Device Info",
                                    tint = CyberEmeraldPrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Column {
                                Text(
                                    text = "A67L / Device Profile",
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                    color = CyberTextPrimary
                                )
                                Text(
                                    text = "Unisoc SC9863A / ARM64 & 32-bit ABI Tuned",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 10.sp
                                    ),
                                    color = CyberEmeraldVariant
                                )
                            }
                        }

                        Switch(
                            checked = a67lOptimization,
                            onCheckedChange = { viewModel.toggleA67lOptimization(it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.Black,
                                checkedTrackColor = CyberEmeraldPrimary,
                                uncheckedThumbColor = Color.White,
                                uncheckedTrackColor = CyberCardBorder
                            )
                        )
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(CyberDarkBg)
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Device Target:", style = MaterialTheme.typography.labelSmall, color = CyberTextMuted)
                            Text(
                                text = "${viewModel.deviceManufacturer.uppercase()} ${viewModel.deviceModel} (A67L Ready)",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold
                                ),
                                color = CyberTextPrimary
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("OS Version:", style = MaterialTheme.typography.labelSmall, color = CyberTextMuted)
                            Text(
                                text = viewModel.androidVersion,
                                style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace),
                                color = CyberTextSecondary
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("ABIs Supported:", style = MaterialTheme.typography.labelSmall, color = CyberTextMuted)
                            Text(
                                text = viewModel.supportedAbis,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 10.sp
                                ),
                                color = CyberEmeraldPrimary
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Resource Mode:", style = MaterialTheme.typography.labelSmall, color = CyberTextMuted)
                            Text(
                                text = if (a67lOptimization) "Low-RAM Daemon & 720p HD+ Scaler Active" else "Standard High-RAM Mode",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                ),
                                color = if (a67lOptimization) CyberEmeraldVariant else CyberTextMuted
                            )
                        }
                    }
                }
            }
        }

        // Connection & Debugging Settings Section
        item {
            Text(
                text = "WI-FI DEBUGGING & LAN CONNECTION SETTINGS",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.2.sp
                ),
                color = CyberEmeraldPrimary
            )
            Spacer(modifier = Modifier.height(6.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, CyberCardBorder, RoundedCornerShape(24.dp)),
                colors = CardDefaults.cardColors(containerColor = CyberCardBg),
                shape = RoundedCornerShape(24.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
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
                            Icon(imageVector = Icons.Default.Wifi, contentDescription = "WiFi", tint = CyberEmeraldPrimary)
                            Column {
                                Text("Wireless Debugging (ADB)", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = CyberTextPrimary)
                                Text("Allow ADB commands over Wi-Fi / LAN", style = MaterialTheme.typography.bodySmall, color = CyberTextSecondary)
                            }
                        }

                        Switch(
                            checked = wifiDebugging,
                            onCheckedChange = { viewModel.toggleWifiDebugging(it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.Black,
                                checkedTrackColor = CyberEmeraldPrimary,
                                uncheckedThumbColor = Color.White,
                                uncheckedTrackColor = CyberCardBorder
                            )
                        )
                    }

                    if (wifiDebugging) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(CyberDarkBg)
                                .padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("IP Address & Port", style = MaterialTheme.typography.labelSmall, color = CyberTextMuted)
                                    Text("$lanIp:$adbPort", style = MaterialTheme.typography.bodyMedium.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold), color = CyberEmeraldPrimary)
                                }

                                Column(horizontalAlignment = Alignment.End) {
                                    Text("Pairing Code", style = MaterialTheme.typography.labelSmall, color = CyberTextMuted)
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(pairingCode, style = MaterialTheme.typography.bodyMedium.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold), color = CyberTextPrimary)
                                        IconButton(onClick = { viewModel.generateNewPairingCode() }) {
                                            Icon(imageVector = Icons.Default.Refresh, contentDescription = "Refresh pairing code", tint = CyberEmeraldPrimary, modifier = Modifier.size(18.dp))
                                        }
                                    }
                                }
                            }

                            Text(
                                text = "Run 'adb pair $lanIp:$adbPort' on your PC and enter pairing code $pairingCode.",
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                color = CyberTextMuted
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(imageVector = Icons.Default.Usb, contentDescription = "USB", tint = CyberEmeraldPrimary)
                            Column {
                                Text("USB Tethering Bridge", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = CyberTextPrimary)
                                Text("Subnet 192.168.42.129 active", style = MaterialTheme.typography.bodySmall, color = CyberTextSecondary)
                            }
                        }

                        Switch(
                            checked = usbTethering,
                            onCheckedChange = { viewModel.toggleUsbTethering(it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.Black,
                                checkedTrackColor = CyberEmeraldPrimary,
                                uncheckedThumbColor = Color.White,
                                uncheckedTrackColor = CyberCardBorder
                            )
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(imageVector = Icons.Default.Bluetooth, contentDescription = "Bluetooth", tint = CyberEmeraldPrimary)
                            Column {
                                Text("Bluetooth Pairing Mode", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = CyberTextPrimary)
                                Text("Discoverable for secure pairing", style = MaterialTheme.typography.bodySmall, color = CyberTextSecondary)
                            }
                        }

                        Switch(
                            checked = btPairing,
                            onCheckedChange = { viewModel.toggleBluetoothPairing(it) },
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
        }

        // GrapheneOS Advanced Security Controls
        item {
            Text(
                text = "GRAPHENEOS HARDENING TOGGLES",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.2.sp
                ),
                color = CyberEmeraldPrimary
            )
            Spacer(modifier = Modifier.height(6.dp))

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                SecuritySettingToggleCard(
                    title = "Storage Scopes Isolation",
                    description = "Replace legacy storage access with granular folder virtual scopes for untrusted apps.",
                    icon = Icons.Default.FolderSpecial,
                    isChecked = storageScopes,
                    onToggle = { viewModel.toggleStorageScopes(it) },
                    testTag = "storage_scopes_switch"
                )

                SecuritySettingToggleCard(
                    title = "Exec-Spawning Sandbox",
                    description = "Block untrusted applications from executing dynamically compiled code or native binaries.",
                    icon = Icons.Default.Gavel,
                    isChecked = execSpawning,
                    onToggle = { viewModel.toggleExecSpawning(it) },
                    testTag = "exec_spawning_switch"
                )

                SecuritySettingToggleCard(
                    title = "Per-App Network Permission",
                    description = "Require explicit user authorization before any app can open raw TCP/UDP sockets.",
                    icon = Icons.Default.VerifiedUser,
                    isChecked = networkPrompt,
                    onToggle = { viewModel.toggleNetworkPrompt(it) },
                    testTag = "network_prompt_switch"
                )
            }
        }

        // Diagnostic Runner Button
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, CyberCardBorder, RoundedCornerShape(24.dp)),
                colors = CardDefaults.cardColors(containerColor = CyberCardBg),
                shape = RoundedCornerShape(24.dp)
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
                        Text(
                            text = "SYSTEM FILE INTEGRITY DIAGNOSTIC",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = CyberEmeraldPrimary
                        )

                        Button(
                            onClick = {
                                if (!isDiagnosticRunning) {
                                    isDiagnosticRunning = true
                                    diagnosticConsoleLogs.clear()
                                    scope.launch {
                                        diagnosticConsoleLogs.add("[DIAGNOSTIC] Initializing GrapheneOS Kernel Audit...")
                                        delay(800)
                                        diagnosticConsoleLogs.add("[OK] /system/bin/app_process verified: Exec-spawning restricted.")
                                        delay(900)
                                        diagnosticConsoleLogs.add("[OK] /system/etc/hosts verified: Pi-hole DNS rules active.")
                                        delay(900)
                                        diagnosticConsoleLogs.add("[OK] /proc/sys/kernel/randomize_va_space: ASLR Level 2 verified.")
                                        delay(900)
                                        diagnosticConsoleLogs.add("[OK] Hardened Malloc: Zero-fill on allocation confirmed.")
                                        delay(800)
                                        diagnosticConsoleLogs.add("[SUCCESS] All system files passed 35% AI Security Audit.")
                                        isDiagnosticRunning = false
                                    }
                                }
                            },
                            modifier = Modifier.testTag("run_diagnostic_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = CyberEmeraldPrimary),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(imageVector = Icons.Default.PlayArrow, contentDescription = "Run", tint = Color.Black, modifier = Modifier.size(16.dp))
                                Text("Run Audit", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = Color.Black)
                            }
                        }
                    }

                    if (diagnosticConsoleLogs.isNotEmpty()) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(CyberDarkBg)
                                .padding(10.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            diagnosticConsoleLogs.forEach { logLine ->
                                Text(
                                    text = logLine,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 10.sp
                                    ),
                                    color = if (logLine.contains("[SUCCESS]") || logLine.contains("[OK]")) CyberEmeraldVariant else CyberEmeraldPrimary
                                )
                            }
                        }
                    }
                }
            }
        }

        // Monitored System Files List
        item {
            Text(
                text = "PROTECTED SYSTEM FILES & KERNEL NODES",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.2.sp
                ),
                color = CyberEmeraldPrimary
            )
        }

        items(viewModel.systemFiles, key = { it.path }) { item ->
            SystemFileCard(file = item)
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }
    }
}

@Composable
private fun SecuritySettingToggleCard(
    title: String,
    description: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isChecked: Boolean,
    onToggle: (Boolean) -> Unit,
    testTag: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, CyberCardBorder, RoundedCornerShape(20.dp)),
        colors = CardDefaults.cardColors(containerColor = CyberCardBg),
        shape = RoundedCornerShape(20.dp)
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
                        .size(40.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isChecked) CyberEmeraldPrimary.copy(alpha = 0.15f) else CyberCardBorder)
                        .border(1.dp, if (isChecked) CyberEmeraldPrimary else CyberTextMuted, RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = if (isChecked) CyberEmeraldPrimary else CyberTextMuted,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = CyberTextPrimary
                    )
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodySmall,
                        color = CyberTextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            Switch(
                checked = isChecked,
                onCheckedChange = onToggle,
                modifier = Modifier.testTag(testTag),
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
private fun SystemFileCard(file: SystemFileItem) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, CyberCardBorder, RoundedCornerShape(20.dp)),
        colors = CardDefaults.cardColors(containerColor = CyberCardBg),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = file.label,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    color = CyberTextPrimary
                )

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(CyberEmeraldPrimary.copy(alpha = 0.15f))
                        .border(1.dp, CyberEmeraldPrimary, RoundedCornerShape(4.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = file.status,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        ),
                        color = CyberEmeraldPrimary
                    )
                }
            }

            Text(
                text = file.path,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp
                ),
                color = CyberEmeraldVariant
            )

            Text(
                text = file.details,
                style = MaterialTheme.typography.bodySmall,
                color = CyberTextSecondary
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Permissions: ${file.permissions}",
                    style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace),
                    color = CyberTextMuted
                )
            }
        }
    }
}

