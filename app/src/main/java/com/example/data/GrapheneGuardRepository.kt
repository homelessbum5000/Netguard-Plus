package com.example.data

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GrapheneGuardRepository(
    private val appDatabase: AppDatabase,
    private val context: Context
) {
    private val appRuleDao = appDatabase.appNetworkRuleDao()
    private val domainFilterDao = appDatabase.domainFilterDao()
    private val dnsLogDao = appDatabase.dnsQueryLogDao()
    private val threatDao = appDatabase.threatLogDao()
    private val avScanDao = appDatabase.avScanLogDao()
    private val notificationDao = appDatabase.notificationDao()
    private val quarantineDao = appDatabase.quarantineDao()

    val allAppRules: Flow<List<AppNetworkRuleEntity>> = appRuleDao.getAllRules()
    val userAppRules: Flow<List<AppNetworkRuleEntity>> = appRuleDao.getRulesBySystemType(false)
    val systemAppRules: Flow<List<AppNetworkRuleEntity>> = appRuleDao.getRulesBySystemType(true)

    val blacklists: Flow<List<DomainFilterEntity>> = domainFilterDao.getFilters(isWhitelist = false)
    val whitelists: Flow<List<DomainFilterEntity>> = domainFilterDao.getFilters(isWhitelist = true)

    val dnsLogs: Flow<List<DnsQueryLogEntity>> = dnsLogDao.getRecentLogs()
    val totalBlockedQueries: Flow<Int> = dnsLogDao.getBlockedCount()
    val totalQueries: Flow<Int> = dnsLogDao.getTotalCount()

    val threatLogs: Flow<List<ThreatLogEntity>> = threatDao.getRecentThreats()
    val avScanLogs: Flow<List<AvScanLogEntity>> = avScanDao.getRecentScans()
    val notifications: Flow<List<NotificationEntity>> = notificationDao.getNotifications()
    val unreadNotificationCount: Flow<Int> = notificationDao.getUnreadCount()
    val quarantineItems: Flow<List<QuarantineEntity>> = quarantineDao.getQuarantineItems()

    suspend fun initializeDefaultDataIfNeeded() = withContext(Dispatchers.IO) {
        if (appRuleDao.getCount() == 0) {
            seedAppNetworkRules()
        }
        if (domainFilterDao.getCount() == 0) {
            seedDomainFilters()
        }
        seedInitialThreatLogsIfNeeded()
        seedInitialAvScansIfNeeded()
        seedInitialNotificationsIfNeeded()
        seedInitialQuarantineIfNeeded()
    }


    private suspend fun seedAppNetworkRules() {
        val pm = context.packageManager
        val installedApps = try {
            pm.getInstalledApplications(PackageManager.GET_META_DATA)
        } catch (e: Exception) {
            emptyList()
        }

        val rulesList = mutableListOf<AppNetworkRuleEntity>()

        if (installedApps.isNotEmpty()) {
            installedApps.take(40).forEach { appInfo ->
                val appName = try {
                    pm.getApplicationLabel(appInfo).toString()
                } catch (e: Exception) {
                    appInfo.packageName
                }
                val isSystem = (appInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0 ||
                        (appInfo.flags and ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0

                rulesList.add(
                    AppNetworkRuleEntity(
                        packageName = appInfo.packageName,
                        appName = if (appName.isBlank()) appInfo.packageName else appName,
                        isSystemApp = isSystem,
                        wifiAllowed = true,
                        mobileDataAllowed = true,
                        category = if (isSystem) "System Component" else "User Installed",
                        uid = appInfo.uid,
                        iconType = if (isSystem) "system" else "user"
                    )
                )
            }
        }

        // Add essential fallback system apps & standard apps if installed list is small
        val mockSystemApps = listOf(
            AppNetworkRuleEntity("com.android.systemui", "Android System UI", true, wifiAllowed = true, mobileDataAllowed = true, category = "Core OS", uid = 1000),
            AppNetworkRuleEntity("org.grapheneos.vanadium", "Vanadium Browser", false, wifiAllowed = true, mobileDataAllowed = true, category = "Browser", uid = 10182),
            AppNetworkRuleEntity("com.google.android.gms", "Google Play Services", true, wifiAllowed = true, mobileDataAllowed = false, category = "System Framework", uid = 10020),
            AppNetworkRuleEntity("com.android.vending", "Google Play Store", true, wifiAllowed = true, mobileDataAllowed = true, category = "App Store", uid = 10045),
            AppNetworkRuleEntity("com.android.providers.downloads", "Download Manager", true, wifiAllowed = true, mobileDataAllowed = true, category = "System Download", uid = 10008),
            AppNetworkRuleEntity("com.android.shell", "Android Shell & CLI", true, wifiAllowed = false, mobileDataAllowed = false, category = "System Utility", uid = 2000),
            AppNetworkRuleEntity("org.grapheneos.camera", "GrapheneOS Secure Camera", false, wifiAllowed = false, mobileDataAllowed = false, category = "Privacy Tool", uid = 10190),
            AppNetworkRuleEntity("org.grapheneos.pdfviewer", "GrapheneOS PDF Viewer", false, wifiAllowed = false, mobileDataAllowed = false, category = "Sandboxed Viewer", uid = 10191),
            AppNetworkRuleEntity("com.signal.sec", "Signal Private Messenger", false, wifiAllowed = true, mobileDataAllowed = true, category = "Encrypted Chat", uid = 10195),
            AppNetworkRuleEntity("org.torproject.torbrowser", "Tor Browser Security", false, wifiAllowed = true, mobileDataAllowed = true, category = "Anonymity", uid = 10201)
        )

        mockSystemApps.forEach { mockApp ->
            if (rulesList.none { it.packageName == mockApp.packageName }) {
                rulesList.add(mockApp)
            }
        }

        appRuleDao.insertAll(rulesList)
    }

    private suspend fun seedDomainFilters() {
        val defaultBlacklist = listOf(
            DomainFilterEntity(domain = "analytics.google.com", isWhitelist = false, category = "Telemetry", comment = "Google Telemetry Tracker"),
            DomainFilterEntity(domain = "telemetry.microsoft.com", isWhitelist = false, category = "Telemetry", comment = "Windows/MS Diagnostic Stream"),
            DomainFilterEntity(domain = "ad.doubleclick.net", isWhitelist = false, category = "Ads", comment = "DoubleClick Ad Network"),
            DomainFilterEntity(domain = "pixel.facebook.com", isWhitelist = false, category = "Trackers", comment = "Meta Pixel Fingerprinting"),
            DomainFilterEntity(domain = "app-measurement.com", isWhitelist = false, category = "Analytics", comment = "Firebase App Analytics Tracker"),
            DomainFilterEntity(domain = "graph.instagram.com", isWhitelist = false, category = "Trackers", comment = "Social Graph Telemetry"),
            DomainFilterEntity(domain = "metrics.icloud.com", isWhitelist = false, category = "Telemetry", comment = "Cloud Telemetry Node"),
            DomainFilterEntity(domain = "ads.tiktok.com", isWhitelist = false, category = "Ads", comment = "ByteDance Ad Server")
        )

        val defaultWhitelist = listOf(
            DomainFilterEntity(domain = "grapheneos.org", isWhitelist = true, category = "Core OS", comment = "GrapheneOS Official Repository"),
            DomainFilterEntity(domain = "fdroid.org", isWhitelist = true, category = "App Store", comment = "F-Droid Open Source Store"),
            DomainFilterEntity(domain = "signal.org", isWhitelist = true, category = "Messaging", comment = "Signal Encryption Servers"),
            DomainFilterEntity(domain = "proton.me", isWhitelist = true, category = "Privacy Mail", comment = "Proton Encrypted Services"),
            DomainFilterEntity(domain = "github.com", isWhitelist = true, category = "Developer", comment = "Git Code Repositories"),
            DomainFilterEntity(domain = "archlinux.org", isWhitelist = true, category = "Security", comment = "Linux Mirrors"),
            DomainFilterEntity(domain = "ai.studio", isWhitelist = true, category = "AI Engine", comment = "Google AI Studio Orchestrator")
        )

        defaultBlacklist.forEach { domainFilterDao.insertFilter(it) }
        defaultWhitelist.forEach { domainFilterDao.insertFilter(it) }

        // Seed initial DNS log stream
        val initialLogs = listOf(
            DnsQueryLogEntity(domain = "grapheneos.org", appName = "Vanadium Browser", blocked = false, reason = "Whitelisted Domain", queryType = "HTTPS"),
            DnsQueryLogEntity(domain = "analytics.google.com", appName = "System Component", blocked = true, reason = "Pi-hole Blacklist Rule #14", queryType = "A"),
            DnsQueryLogEntity(domain = "signal.org", appName = "Signal", blocked = false, reason = "Whitelisted Domain", queryType = "AAAA"),
            DnsQueryLogEntity(domain = "ad.doubleclick.net", appName = "Game App", blocked = true, reason = "Pi-hole Ad Blocklist", queryType = "A"),
            DnsQueryLogEntity(domain = "pixel.facebook.com", appName = "Background Process", blocked = true, reason = "Tracker Blocked", queryType = "HTTPS")
        )
        initialLogs.forEach { dnsLogDao.insertLog(it) }
    }

    private suspend fun seedInitialThreatLogsIfNeeded() {
        val count = threatDao.getRecentThreats()
        val initialThreats = listOf(
            ThreatLogEntity(
                threatTitle = "35% AI Security Allocation Active",
                severity = "INFO",
                module = "AI CPU Orchestrator",
                details = "35% dedicated CPU core capacity allocated to real-time memory sanitization and background network inspection."
            ),
            ThreatLogEntity(
                threatTitle = "Telemetry Connection Intercepted",
                severity = "HIGH",
                module = "Pi-hole DNS Firewall",
                details = "Blocked out-of-band telemetry request from background system component to analytics.google.com."
            ),
            ThreatLogEntity(
                threatTitle = "Hardened Malloc Guard Enabled",
                severity = "INFO",
                module = "GrapheneOS Memory Shield",
                details = "Zero-fill on allocation, write-after-free protection, and canary guard pages enabled across all user apps."
            ),
            ThreatLogEntity(
                threatTitle = "Dynamic Code Execution Blocked",
                severity = "MEDIUM",
                module = "Executive Sandbox",
                details = "Prevented unauthorized executable memory page allocation for untrusted background process."
            )
        )
        initialThreats.forEach { threatDao.insertThreat(it) }
    }

    suspend fun updateAppNetworkRule(packageName: String, wifi: Boolean, mobileData: Boolean) = withContext(Dispatchers.IO) {
        appRuleDao.updateNetworkState(packageName, wifi, mobileData)
    }

    suspend fun addDomainFilter(domain: String, isWhitelist: Boolean, category: String = "Custom Rule", comment: String = "") = withContext(Dispatchers.IO) {
        val filter = DomainFilterEntity(
            domain = domain.lowercase().trim(),
            isWhitelist = isWhitelist,
            category = category,
            comment = comment
        )
        domainFilterDao.insertFilter(filter)
    }

    suspend fun deleteDomainFilter(id: Int) = withContext(Dispatchers.IO) {
        domainFilterDao.deleteFilterById(id)
    }

    suspend fun toggleDomainFilter(id: Int, isEnabled: Boolean) = withContext(Dispatchers.IO) {
        domainFilterDao.toggleFilterState(id, isEnabled)
    }

    suspend fun logDnsQuery(domain: String, appName: String, blocked: Boolean, reason: String) = withContext(Dispatchers.IO) {
        dnsLogDao.insertLog(
            DnsQueryLogEntity(
                domain = domain,
                appName = appName,
                blocked = blocked,
                reason = reason
            )
        )
    }

    suspend fun clearDnsLogs() = withContext(Dispatchers.IO) {
        dnsLogDao.clearLogs()
    }

    suspend fun addThreatLog(title: String, severity: String, module: String, details: String) = withContext(Dispatchers.IO) {
        threatDao.insertThreat(
            ThreatLogEntity(
                threatTitle = title,
                severity = severity,
                module = module,
                details = details
            )
        )
    }

    private suspend fun seedInitialAvScansIfNeeded() {
        val initialScans = listOf(
            AvScanLogEntity(
                targetInput = "com.malicious.spyware.apk",
                targetType = "PACKAGE",
                riskScore = 94,
                verdict = "MALICIOUS",
                threatFamily = "Trojan.AndroidOS.Spyware.Gen",
                engineResults = "Bitdefender: Detected, Kaspersky: Detected, CrowdStrike: High Risk, Malwarebytes: Detected, Windows Defender: Detected, ESET: Clean, Avast: Detected, Heuristic: Anomaly",
                enginesClean = 1,
                enginesTotal = 8,
                details = "Flagged by 7/8 AV engines for unauthorized SMS intercept and silent C2 communication."
            ),
            AvScanLogEntity(
                targetInput = "http://phishing-banking-update.xyz/login",
                targetType = "URL",
                riskScore = 88,
                verdict = "MALICIOUS",
                threatFamily = "Phish.HTML.CredentialStealer",
                engineResults = "Bitdefender: Phishing, Kaspersky: Phishing, CrowdStrike: Suspicious, Malwarebytes: Phishing, Windows Defender: Clean, ESET: Phishing, Avast: Phishing, Heuristic: Phishing",
                enginesClean = 1,
                enginesTotal = 8,
                details = "Zero-day phishing domain imitating financial OAuth flow."
            ),
            AvScanLogEntity(
                targetInput = "org.grapheneos.vanadium",
                targetType = "PACKAGE",
                riskScore = 0,
                verdict = "CLEAN",
                threatFamily = "Clean.VerifiedSystemPackage",
                engineResults = "Bitdefender: Clean, Kaspersky: Clean, CrowdStrike: Clean, Malwarebytes: Clean, Windows Defender: Clean, ESET: Clean, Avast: Clean, Heuristic: Clean",
                enginesClean = 8,
                enginesTotal = 8,
                details = "Official GrapheneOS sandboxed browser binary signature verified."
            )
        )
        initialScans.forEach { avScanDao.insertScan(it) }
    }

    suspend fun saveAvScanResult(
        targetInput: String,
        targetType: String,
        riskScore: Int,
        verdict: String,
        threatFamily: String,
        engineResults: String,
        enginesClean: Int,
        enginesTotal: Int,
        details: String
    ) = withContext(Dispatchers.IO) {
        avScanDao.insertScan(
            AvScanLogEntity(
                targetInput = targetInput,
                targetType = targetType,
                riskScore = riskScore,
                verdict = verdict,
                threatFamily = threatFamily,
                engineResults = engineResults,
                enginesClean = enginesClean,
                enginesTotal = enginesTotal,
                details = details
            )
        )
    }

    private suspend fun seedInitialNotificationsIfNeeded() {
        val initialNotifs = listOf(
            NotificationEntity(
                title = "Critical Threat Intercepted",
                message = "AV Engine blocked Trojan.AndroidOS.Spyware.Gen (Risk score: 94). Item moved to Quarantine.",
                severity = "CRITICAL"
            ),
            NotificationEntity(
                title = "DNSCrypt Connection Established",
                message = "Encrypted DNS-over-HTTPS tunnel active via Quad9 (9.9.9.9) & Cloudflare DNSCrypt.",
                severity = "INFO"
            ),
            NotificationEntity(
                title = "GrapheneOS Sandbox Active",
                message = "Exec-spawning sandbox & Storage Scopes successfully enforcing zero-trust per-app permissions.",
                severity = "INFO"
            )
        )
        initialNotifs.forEach { notificationDao.insertNotification(it) }
    }

    private suspend fun seedInitialQuarantineIfNeeded() {
        val initialQuarantine = listOf(
            QuarantineEntity(
                itemName = "com.malicious.spyware.apk",
                itemType = "APP_PACKAGE",
                threatFamily = "Trojan.AndroidOS.Spyware.Gen",
                riskScore = 94,
                status = "QUARANTINED"
            ),
            QuarantineEntity(
                itemName = "http://phishing-banking-update.xyz/login",
                itemType = "URL",
                threatFamily = "Phish.HTML.CredentialStealer",
                riskScore = 88,
                status = "QUARANTINED"
            )
        )
        initialQuarantine.forEach { quarantineDao.insertItem(it) }
    }

    suspend fun markNotificationAsRead(id: Long) = withContext(Dispatchers.IO) {
        notificationDao.markAsRead(id)
    }

    suspend fun markAllNotificationsAsRead() = withContext(Dispatchers.IO) {
        notificationDao.markAllAsRead()
    }

    suspend fun addNotification(title: String, message: String, severity: String) = withContext(Dispatchers.IO) {
        notificationDao.insertNotification(
            NotificationEntity(
                title = title,
                message = message,
                severity = severity
            )
        )
    }

    suspend fun quarantineItem(name: String, type: String, threatFamily: String, riskScore: Int) = withContext(Dispatchers.IO) {
        quarantineDao.insertItem(
            QuarantineEntity(
                itemName = name,
                itemType = type,
                threatFamily = threatFamily,
                riskScore = riskScore,
                status = "QUARANTINED"
            )
        )
    }

    suspend fun updateQuarantineStatus(id: Long, status: String) = withContext(Dispatchers.IO) {
        quarantineDao.updateStatus(id, status)
    }

    suspend fun deleteQuarantineItem(id: Long) = withContext(Dispatchers.IO) {
        quarantineDao.deleteItem(id)
    }
}


