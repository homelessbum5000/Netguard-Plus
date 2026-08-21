package com.example.data

data class AppNetworkRuleEntity(
    val packageName: String,
    val appName: String,
    val isSystemApp: Boolean = false,
    val wifiAllowed: Boolean = true,
    val mobileDataAllowed: Boolean = true,
    val cameraBlocked: Boolean = false,
    val microphoneBlocked: Boolean = false,
    val bluetoothBlocked: Boolean = false,
    val uid: Int = 0,
    val bytesTransferred: Long = 0L,
    val blockedPacketsCount: Int = 0
)

data class DnsQueryLogEntity(
    val id: Long = 0,
    val domain: String,
    val appName: String,
    val packageName: String,
    val blocked: Boolean,
    val reason: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isDnsCrypt: Boolean = true,
    val protocol: String = "DNSCrypt v2",
    val latencyMs: Long = 18L
)

data class DomainFilterEntity(
    val id: Long = 0,
    val domain: String,
    val isWhitelist: Boolean = false,
    val isEnabled: Boolean = true,
    val comment: String = "",
    val category: String = "Custom Filter",
    val hitCount: Int = 0
)

data class ThreatLogEntity(
    val id: Long = 0,
    val threatTitle: String,
    val severity: String, // CRITICAL, HIGH, MEDIUM, LOW, INFO
    val module: String, // App Firewall, DNSCrypt, Pi-Hole DNS, AV Scanner, System
    val details: String,
    val timestamp: Long = System.currentTimeMillis()
)

data class AvScanLogEntity(
    val id: Long = 0,
    val targetInput: String,
    val targetType: String = "FILE", // FILE, PACKAGE, URL, HASH, IP
    val verdict: String = "CLEAN", // CLEAN, SUSPICIOUS, MALICIOUS
    val threatFamily: String = "None Detected",
    val riskScore: Int = 0,
    val engineResults: String = "NetGuard Heuristic (Clean), GrapheneOS Sandbox (Clean)",
    val details: String = "Static signature and ELF entropy verification passed without anomalous markers.",
    val timestamp: Long = System.currentTimeMillis()
)

data class SecurityNotificationItem(
    val id: Long,
    val title: String,
    val message: String,
    val severity: String,
    val isRead: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)

data class QuarantineVaultItem(
    val id: Long,
    val itemName: String,
    val threatFamily: String,
    val riskScore: Int,
    val path: String,
    val quarantineDate: Long = System.currentTimeMillis()
)

data class SystemFileItem(
    val label: String,
    val status: String,
    val path: String,
    val details: String,
    val permissions: String
)

data class DiagnosticItem(
    val title: String,
    val status: String,
    val message: String,
    val isPassed: Boolean
)
