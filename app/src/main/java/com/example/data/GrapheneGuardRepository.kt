package com.example.data

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class GrapheneGuardRepository(
    private val database: AppDatabase,
    private val context: Context
) {
    private val appRuleDao = database.appNetworkRuleDao
    private val dnsLogDao = database.dnsQueryLogDao
    private val domainFilterDao = database.domainFilterDao
    private val threatLogDao = database.threatLogDao

    val allAppRules: Flow<List<AppNetworkRuleEntity>> = appRuleDao.getAllRules()
    val recentDnsLogs: Flow<List<DnsQueryLogEntity>> = dnsLogDao.getRecentLogs()
    val allDomainFilters: Flow<List<DomainFilterEntity>> = domainFilterDao.getAllFilters()
    val allThreatLogs: Flow<List<ThreatLogEntity>> = threatLogDao.getAllThreatLogs()

    val totalDnsCount: Flow<Int> = dnsLogDao.getTotalQueriesCount()
    val blockedDnsCount: Flow<Int> = dnsLogDao.getBlockedQueriesCount()

    suspend fun initializeIfEmpty() = withContext(Dispatchers.IO) {
        // Populate installed apps if table is empty
        val pm = context.packageManager
        val installed = pm.getInstalledApplications(PackageManager.GET_META_DATA)
        val initialRules = mutableListOf<AppNetworkRuleEntity>()

        for (app in installed) {
            val isSystem = (app.flags and ApplicationInfo.FLAG_SYSTEM) != 0
            val appName = pm.getApplicationLabel(app).toString()
            initialRules.add(
                AppNetworkRuleEntity(
                    packageName = app.packageName,
                    appName = appName,
                    isSystemApp = isSystem,
                    wifiAllowed = true,
                    mobileDataAllowed = !isSystem || app.packageName.contains("browser") || app.packageName.contains("play"),
                    uid = app.uid
                )
            )
        }

        if (initialRules.isNotEmpty()) {
            appRuleDao.insertRules(initialRules)
        }

        // Initialize default Pi-Hole blocklist & whitelist
        val defaultFilters = listOf(
            DomainFilterEntity(domain = "telemetry.google.com", isWhitelist = false, comment = "Google Telemetry Tracker", category = "Telemetry", hitCount = 142),
            DomainFilterEntity(domain = "graph.facebook.com", isWhitelist = false, comment = "Meta Analytics Beacon", category = "Social Tracker", hitCount = 89),
            DomainFilterEntity(domain = "ads.tiktok.com", isWhitelist = false, comment = "TikTok Ad Network", category = "Ad Server", hitCount = 67),
            DomainFilterEntity(domain = "app-measurement.com", isWhitelist = false, comment = "Firebase In-App Tracker", category = "Analytics", hitCount = 215),
            DomainFilterEntity(domain = "fls-na.amazon.com", isWhitelist = false, comment = "Amazon Logging Endpoint", category = "Telemetry", hitCount = 44),
            DomainFilterEntity(domain = "grapheneos.org", isWhitelist = true, comment = "GrapheneOS Core Repository", category = "System Core", hitCount = 12),
            DomainFilterEntity(domain = "fdroid.org", isWhitelist = true, comment = "F-Droid Open Source Mirror", category = "App Store", hitCount = 28)
        )
        domainFilterDao.insertAll(defaultFilters)

        // Initialize sample threat logs
        val defaultThreats = listOf(
            ThreatLogEntity(threatTitle = "DNSCrypt Cryptographic Tunnel Active", severity = "INFO", module = "DNSCrypt", details = "Authenticated session established with Quad9 (9.9.9.9) using Curve25519 encryption."),
            ThreatLogEntity(threatTitle = "Background Telemetry Request Blocked", severity = "MEDIUM", module = "Pi-Hole DNS", details = "Intercepted and dropped query for 'app-measurement.com' via synthetic 0.0.0.0 answer."),
            ThreatLogEntity(threatTitle = "Isolated App Mobile Data Restriction", severity = "LOW", module = "App Firewall", details = "Blocked unmetered background cellular sync for System Package manager."),
            ThreatLogEntity(threatTitle = "Hardened Malloc Guard Enabled", severity = "INFO", module = "System", details = "Memory zeroing, ASLR randomization, and hardened heap canary verification active.")
        )
        for (threat in defaultThreats) {
            threatLogDao.insertThreatLog(threat)
        }
    }

    suspend fun toggleWifi(rule: AppNetworkRuleEntity, enabled: Boolean) = withContext(Dispatchers.IO) {
        appRuleDao.updateRule(rule.copy(wifiAllowed = enabled))
    }

    suspend fun toggleMobileData(rule: AppNetworkRuleEntity, enabled: Boolean) = withContext(Dispatchers.IO) {
        appRuleDao.updateRule(rule.copy(mobileDataAllowed = enabled))
    }

    suspend fun toggleCamera(rule: AppNetworkRuleEntity, blocked: Boolean) = withContext(Dispatchers.IO) {
        appRuleDao.updateRule(rule.copy(cameraBlocked = blocked))
    }

    suspend fun toggleMicrophone(rule: AppNetworkRuleEntity, blocked: Boolean) = withContext(Dispatchers.IO) {
        appRuleDao.updateRule(rule.copy(microphoneBlocked = blocked))
    }

    suspend fun toggleBluetooth(rule: AppNetworkRuleEntity, blocked: Boolean) = withContext(Dispatchers.IO) {
        appRuleDao.updateRule(rule.copy(bluetoothBlocked = blocked))
    }

    suspend fun setAllWifiState(enabled: Boolean, forSystemOnly: Boolean = false) = withContext(Dispatchers.IO) {
        if (forSystemOnly) {
            appRuleDao.setSystemWifiState(enabled)
        } else {
            appRuleDao.setAllWifiState(enabled)
        }
    }

    suspend fun setAllMobileDataState(enabled: Boolean, forSystemOnly: Boolean = false) = withContext(Dispatchers.IO) {
        if (forSystemOnly) {
            appRuleDao.setSystemMobileDataState(enabled)
        } else {
            appRuleDao.setAllMobileDataState(enabled)
        }
    }

    suspend fun setAllCameraBlocked(blocked: Boolean) = withContext(Dispatchers.IO) {
        appRuleDao.setAllCameraBlocked(blocked)
    }

    suspend fun setAllMicrophoneBlocked(blocked: Boolean) = withContext(Dispatchers.IO) {
        appRuleDao.setAllMicrophoneBlocked(blocked)
    }

    suspend fun setAllBluetoothBlocked(blocked: Boolean) = withContext(Dispatchers.IO) {
        appRuleDao.setAllBluetoothBlocked(blocked)
    }

    suspend fun addDomainFilter(domain: String, isWhitelist: Boolean, comment: String = "", category: String = "Custom Filter") = withContext(Dispatchers.IO) {
        domainFilterDao.insertFilter(
            DomainFilterEntity(
                domain = domain.trim().lowercase(),
                isWhitelist = isWhitelist,
                comment = comment,
                category = category
            )
        )
    }

    suspend fun toggleDomainState(id: Long, enabled: Boolean) = withContext(Dispatchers.IO) {
        domainFilterDao.toggleFilterState(id, enabled)
    }

    suspend fun deleteDomain(id: Long) = withContext(Dispatchers.IO) {
        domainFilterDao.deleteFilter(id)
    }

    suspend fun clearLogs() = withContext(Dispatchers.IO) {
        dnsLogDao.clearAllLogs()
    }

    suspend fun addThreatLog(title: String, severity: String, module: String, details: String) = withContext(Dispatchers.IO) {
        threatLogDao.insertThreatLog(
            ThreatLogEntity(
                threatTitle = title,
                severity = severity,
                module = module,
                details = details
            )
        )
    }

    suspend fun clearThreatLogs() = withContext(Dispatchers.IO) {
        threatLogDao.clearAllThreatLogs()
    }

    suspend fun quickBlacklist(domain: String) = withContext(Dispatchers.IO) {
        addDomainFilter(domain, isWhitelist = false, comment = "Quick Blacklisted from Logs", category = "User Block")
    }

    suspend fun quickWhitelist(domain: String) = withContext(Dispatchers.IO) {
        addDomainFilter(domain, isWhitelist = true, comment = "Quick Whitelisted from Logs", category = "User Whitelist")
    }
}
