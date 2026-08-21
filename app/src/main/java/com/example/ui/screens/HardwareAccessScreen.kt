package com.example.ui.screens

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.BluetoothDisabled
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.VideocamOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
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
import com.example.ui.theme.CyberPrimaryCyan
import com.example.ui.theme.CyberSecondaryEmerald
import com.example.ui.theme.CyberTextMuted
import com.example.ui.theme.CyberTextPrimary
import com.example.ui.theme.CyberTextSecondary
import com.example.ui.theme.CyberWarningAmber
import com.example.ui.viewmodels.NetworkFirewallViewModel

@Composable
fun HardwareAccessScreen(
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
                        text = "Hardware Access Controls",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Light,
                            fontFamily = FontFamily.Serif
                        ),
                        color = CyberTextPrimary
                    )
                    Text(
                        text = "GRAPHENEOS PERMISSION SANDBOX",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.2.sp
                        ),
                        color = CyberEmeraldPrimary
                    )

                    Text(
                        text = "Revoke or grant hardware permissions (Bluetooth, Camera, Microphone) per application. Policy rules are immediately persisted and enforced at the kernel sandbox layer.",
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
                    .testTag("hardware_access_search_input"),
                placeholder = {
                    Text(
                        "Search app label or package",
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

        // Filter Chips
        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                item {
                    HardwareFilterChip(
                        label = "All Apps",
                        selected = selectedFilter == "ALL",
                        onClick = { viewModel.selectedFilter.value = "ALL" }
                    )
                }
                item {
                    HardwareFilterChip(
                        label = "User Apps",
                        selected = selectedFilter == "USER",
                        onClick = { viewModel.selectedFilter.value = "USER" }
                    )
                }
                item {
                    HardwareFilterChip(
                        label = "System Apps",
                        selected = selectedFilter == "SYSTEM",
                        onClick = { viewModel.selectedFilter.value = "SYSTEM" }
                    )
                }
                item {
                    HardwareFilterChip(
                        label = "Sandboxed",
                        selected = selectedFilter == "BLOCKED",
                        onClick = { viewModel.selectedFilter.value = "BLOCKED" }
                    )
                }
            }
        }

        // Batch Control Buttons
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                HardwareBatchButton(
                    text = "Disable Camera All",
                    onClick = { viewModel.setAllCameraBlocked(true) },
                    modifier = Modifier.weight(1f)
                )
                HardwareBatchButton(
                    text = "Disable Mic All",
                    onClick = { viewModel.setAllMicrophoneBlocked(true) },
                    modifier = Modifier.weight(1f)
                )
                HardwareBatchButton(
                    text = "Disable BT All",
                    onClick = { viewModel.setAllBluetoothBlocked(true) },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Installed App Rules Cards with Individual Toggles
        items(appRules, key = { it.packageName }) { rule ->
            HardwareAppCard(
                rule = rule,
                onToggleBluetooth = { isGranted ->
                    viewModel.toggleBluetooth(rule, blocked = !isGranted)
                },
                onToggleCamera = { isGranted ->
                    viewModel.toggleCamera(rule, blocked = !isGranted)
                },
                onToggleMicrophone = { isGranted ->
                    viewModel.toggleMicrophone(rule, blocked = !isGranted)
                }
            )
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }
    }
}

@Composable
private fun HardwareFilterChip(
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
            selectedContainerColor = CyberEmeraldPrimary,
            selectedLabelColor = Color.Black,
            containerColor = CyberCardBg,
            labelColor = CyberTextSecondary
        ),
        border = FilterChipDefaults.filterChipBorder(
            enabled = true,
            selected = selected,
            borderColor = CyberCardBorder,
            selectedBorderColor = CyberEmeraldPrimary
        ),
        shape = RoundedCornerShape(20.dp)
    )
}

@Composable
private fun HardwareBatchButton(
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
            .padding(vertical = 10.dp, horizontal = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp
            ),
            color = CyberEmeraldPrimary,
            maxLines = 1
        )
    }
}

@Composable
private fun HardwareAppCard(
    rule: AppNetworkRuleEntity,
    onToggleBluetooth: (Boolean) -> Unit,
    onToggleCamera: (Boolean) -> Unit,
    onToggleMicrophone: (Boolean) -> Unit
) {
    val isBtGranted = !rule.bluetoothBlocked
    val isCamGranted = !rule.cameraBlocked
    val isMicGranted = !rule.microphoneBlocked

    val statusText = when {
        !isBtGranted && !isCamGranted && !isMicGranted -> "ALL HARDWARE PERMISSIONS REVOKED (SANDBOXED)"
        !isBtGranted || !isCamGranted || !isMicGranted -> "PARTIAL HARDWARE RESTRICTION APPLIED"
        else -> "FULL HARDWARE ACCESS ALLOWED"
    }

    val statusColor = when {
        !isBtGranted && !isCamGranted && !isMicGranted -> CyberAlertRed
        !isBtGranted || !isCamGranted || !isMicGranted -> CyberWarningAmber
        else -> CyberSecondaryEmerald
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, CyberCardBorder, RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = CyberCardBg),
        shape = RoundedCornerShape(16.dp)
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
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
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

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(CyberDarkBg)
                    .padding(vertical = 2.dp)
            ) {
                // Bluetooth Toggle Row
                HardwareToggleItem(
                    label = "Bluetooth Access",
                    isGranted = isBtGranted,
                    activeIcon = Icons.Default.Bluetooth,
                    inactiveIcon = Icons.Default.BluetoothDisabled,
                    onToggle = onToggleBluetooth,
                    testTag = "bluetooth_toggle_${rule.packageName}"
                )

                Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(CyberCardBorder))

                // Camera Toggle Row
                HardwareToggleItem(
                    label = "Camera Access",
                    isGranted = isCamGranted,
                    activeIcon = Icons.Default.CameraAlt,
                    inactiveIcon = Icons.Default.VideocamOff,
                    onToggle = onToggleCamera,
                    testTag = "camera_toggle_${rule.packageName}"
                )

                Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(CyberCardBorder))

                // Microphone Toggle Row
                HardwareToggleItem(
                    label = "Microphone Access",
                    isGranted = isMicGranted,
                    activeIcon = Icons.Default.Mic,
                    inactiveIcon = Icons.Default.MicOff,
                    onToggle = onToggleMicrophone,
                    testTag = "mic_toggle_${rule.packageName}"
                )
            }

            Text(
                text = statusText,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 10.sp
                ),
                color = statusColor
            )
        }
    }
}

@Composable
private fun HardwareToggleItem(
    label: String,
    isGranted: Boolean,
    activeIcon: androidx.compose.ui.graphics.vector.ImageVector,
    inactiveIcon: androidx.compose.ui.graphics.vector.ImageVector,
    onToggle: (Boolean) -> Unit,
    testTag: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggle(!isGranted) }
            .testTag(testTag)
            .padding(vertical = 8.dp, horizontal = 12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = if (isGranted) activeIcon else inactiveIcon,
                contentDescription = label,
                tint = if (isGranted) CyberSecondaryEmerald else CyberAlertRed,
                modifier = Modifier.size(20.dp)
            )

            Column {
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                    color = CyberTextPrimary
                )
                Text(
                    text = if (isGranted) "Permission Granted" else "Permission Revoked",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                    color = if (isGranted) CyberSecondaryEmerald else CyberAlertRed
                )
            }
        }

        Switch(
            checked = isGranted,
            onCheckedChange = { onToggle(it) },
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
