package com.example.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.LinearScale
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.AvScanLogEntity
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
import com.example.ui.viewmodels.AvScannerViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AvScannerScreen(
    viewModel: AvScannerViewModel,
    modifier: Modifier = Modifier
) {
    val targetInput by viewModel.targetInput.collectAsStateWithLifecycle()
    val targetType by viewModel.targetType.collectAsStateWithLifecycle()
    val isScanning by viewModel.isScanning.collectAsStateWithLifecycle()
    val scanProgress by viewModel.scanProgress.collectAsStateWithLifecycle()
    val currentEngine by viewModel.currentEngine.collectAsStateWithLifecycle()
    val avScanLogs by viewModel.avScanLogs.collectAsStateWithLifecycle()
    val lastScanResult by viewModel.lastScanResult.collectAsStateWithLifecycle()

    val context = LocalContext.current
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            val fileName = it.lastPathSegment ?: "local_file.bin"
            viewModel.onFilePicked(fileName, 1024L * 256)
        }
    }


    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(CyberDarkBg)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item { Spacer(modifier = Modifier.height(8.dp)) }

        // Sophisticated Dark Header
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
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "NetGuard AV Scanner",
                                style = MaterialTheme.typography.headlineSmall.copy(
                                    fontWeight = FontWeight.Light,
                                    fontFamily = FontFamily.Serif
                                ),
                                color = CyberTextPrimary
                            )
                            Text(
                                text = "BITDEFENDER & MULTI-ENGINE MALWARE SHIELD",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.5.sp
                                ),
                                color = CyberEmeraldPrimary
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(CyberEmeraldPrimary.copy(alpha = 0.15f))
                                .border(1.dp, CyberEmeraldPrimary, CircleShape)
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "8 ENGINES",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 10.sp
                                ),
                                color = CyberEmeraldPrimary
                            )
                        }
                    }

                    Text(
                        text = "Scan files, URLs, APK packages, file hashes, and domains against 8 enterprise detection engines including Bitdefender, Kaspersky, CrowdStrike Falcon, and Malwarebytes.",
                        style = MaterialTheme.typography.bodySmall,
                        color = CyberTextMuted
                    )
                }
            }
        }

        // Input & Target Selector Box
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
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "TARGET TYPE",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
                        color = CyberTextMuted
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf("PACKAGE", "URL", "HASH", "IP", "FILE").forEach { type ->
                            FilterChip(
                                selected = targetType == type,
                                onClick = { viewModel.targetType.value = type },
                                label = { Text(type, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = CyberEmeraldPrimary,
                                    selectedLabelColor = Color.Black,
                                    containerColor = CyberDarkBg,
                                    labelColor = CyberTextSecondary
                                ),
                                shape = RoundedCornerShape(16.dp)
                            )
                        }
                    }

                    OutlinedTextField(
                        value = targetInput,
                        onValueChange = { viewModel.targetInput.value = it },
                        placeholder = {
                            Text(
                                when (targetType) {
                                    "PACKAGE" -> "e.g. com.suspicious.app.apk"
                                    "URL" -> "e.g. https://phishing-site.com"
                                    "HASH" -> "e.g. e3b0c44298fc1c149afbf4c8996fb924"
                                    "IP" -> "e.g. 185.220.101.5"
                                    else -> "e.g. /sdcard/Download/payload.bin"
                                },
                                color = CyberTextMuted,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        },
                        leadingIcon = {
                            Icon(imageVector = Icons.Default.Radar, contentDescription = "Scan target", tint = CyberEmeraldPrimary)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("av_target_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = CyberDarkBg,
                            unfocusedContainerColor = CyberDarkBg,
                            focusedBorderColor = CyberEmeraldPrimary,
                            unfocusedBorderColor = CyberCardBorder,
                            focusedTextColor = CyberTextPrimary,
                            unfocusedTextColor = CyberTextPrimary
                        ),
                        shape = RoundedCornerShape(16.dp),
                        singleLine = true
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { filePickerLauncher.launch("*/*") },
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .testTag("pick_local_file_button"),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = CyberEmeraldPrimary
                            ),
                            border = androidx.compose.foundation.BorderStroke(1.dp, CyberEmeraldPrimary),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(imageVector = Icons.Default.AttachFile, contentDescription = "Pick File", tint = CyberEmeraldPrimary)
                                Text("PICK FILE", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                            }
                        }

                        Button(
                            onClick = { viewModel.startMultiEngineScan() },
                            enabled = !isScanning && targetInput.isNotBlank(),
                            modifier = Modifier
                                .weight(2f)
                                .height(48.dp)
                                .testTag("start_av_scan_button"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = CyberEmeraldPrimary,
                                disabledContainerColor = CyberCardBorder
                            ),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(imageVector = Icons.Default.Shield, contentDescription = "Scan", tint = Color.Black)
                                Text(
                                    text = if (isScanning) "SCANNING..." else "ANALYZE (8 AV ENGINES)",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = Color.Black
                                )
                            }
                        }
                    }


                    if (isScanning) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.padding(top = 4.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = currentEngine,
                                    style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace),
                                    color = CyberEmeraldVariant
                                )
                                Text(
                                    text = "${(scanProgress * 100).toInt()}%",
                                    style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace),
                                    color = CyberEmeraldPrimary
                                )
                            }

                            LinearProgressIndicator(
                                progress = { scanProgress },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(3.dp)),
                                color = CyberEmeraldPrimary,
                                trackColor = CyberDarkBg,
                            )
                        }
                    }
                }
            }
        }

        // Active Scan Result Banner
        lastScanResult?.let { result ->
            item {
                AvScanResultCard(result = result)
            }
        }

        // Recent Scan History Title
        item {
            Text(
                text = "ANTIVIRUS SCAN HISTORY (${avScanLogs.size})",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.2.sp
                ),
                color = CyberEmeraldPrimary
            )
        }

        items(avScanLogs, key = { it.id }) { scan ->
            AvScanResultCard(result = scan)
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }
    }
}

@Composable
private fun AvScanResultCard(result: AvScanLogEntity) {
    val statusColor = when (result.verdict) {
        "MALICIOUS" -> CyberAlertRed
        "SUSPICIOUS" -> CyberWarningAmber
        else -> CyberEmeraldPrimary
    }

    val dateFormat = SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault())
    val dateStr = dateFormat.format(Date(result.timestamp))

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, statusColor.copy(alpha = 0.3f), RoundedCornerShape(20.dp)),
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
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(statusColor.copy(alpha = 0.15f))
                            .border(1.dp, statusColor, RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = result.verdict,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 10.sp
                            ),
                            color = statusColor
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(CyberDarkBg)
                            .padding(horizontal = 6.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = result.targetType,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontFamily = FontFamily.Monospace,
                                fontSize = 9.sp
                            ),
                            color = CyberTextMuted
                        )
                    }
                }

                Text(
                    text = dateStr,
                    style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace),
                    color = CyberTextMuted
                )
            }

            Text(
                text = result.targetInput,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                ),
                color = CyberTextPrimary
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Family: ${result.threatFamily}",
                    style = MaterialTheme.typography.labelSmall,
                    color = CyberTextSecondary
                )

                Text(
                    text = "Risk: ${result.riskScore}/100",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    ),
                    color = statusColor
                )
            }

            // Engine Results List
            Text(
                text = "Engines: ${result.engineResults}",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace
                ),
                color = CyberTextMuted,
                maxLines = 2
            )

            Text(
                text = result.details,
                style = MaterialTheme.typography.bodySmall,
                color = CyberTextMuted
            )
        }
    }
}
