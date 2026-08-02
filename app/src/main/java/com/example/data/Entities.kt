package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "app_network_rules")
data class AppNetworkRuleEntity(
    @PrimaryKey val packageName: String,
    val appName: String,
    val isSystemApp: Boolean,
    val wifiAllowed: Boolean = true,
    val mobileDataAllowed: Boolean = true,
    val category: String = "Application",
    val uid: Int = 10000,
    val iconType: String = "generic",
    val lastUpdated: Long = System.currentTimeMillis()
)

@Entity(tableName = "domain_filters")
data class DomainFilterEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val domain: String,
    val isWhitelist: Boolean, // true = Whitelist, false = Blacklist
    val isEnabled: Boolean = true,
    val category: String = "Adblock",
    val listType: String = "Custom",
    val comment: String = "",
    val dateAdded: Long = System.currentTimeMillis(),
    val hitCount: Int = 0
)

@Entity(tableName = "dns_query_logs")
data class DnsQueryLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val domain: String,
    val appName: String,
    val blocked: Boolean,
    val reason: String,
    val queryType: String = "A",
    val clientIp: String = "127.0.0.1"
)

@Entity(tableName = "threat_logs")
data class ThreatLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val threatTitle: String,
    val severity: String, // "CRITICAL", "HIGH", "MEDIUM", "INFO"
    val module: String,
    val details: String
)

@Entity(tableName = "av_scan_logs")
data class AvScanLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val targetInput: String,
    val targetType: String, // "URL", "HASH", "PACKAGE", "IP", "DOMAIN", "FILE"
    val riskScore: Int, // 0 to 100
    val verdict: String, // "CLEAN", "SUSPICIOUS", "MALICIOUS"
    val threatFamily: String,
    val engineResults: String, // JSON or comma-separated status for Bitdefender, Kaspersky, CrowdStrike, Malwarebytes, etc.
    val enginesClean: Int = 8,
    val enginesTotal: Int = 8,
    val details: String
)

@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val title: String,
    val message: String,
    val severity: String, // "CRITICAL", "HIGH", "INFO"
    val isRead: Boolean = false
)

@Entity(tableName = "quarantine_items")
data class QuarantineEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val itemName: String,
    val itemType: String, // "FILE", "APP_PACKAGE", "URL"
    val threatFamily: String,
    val riskScore: Int,
    val status: String = "QUARANTINED" // "QUARANTINED", "RESTORED", "DELETED"
)


