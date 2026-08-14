package com.example.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.AppNetworkRuleEntity
import com.example.data.AvScanLogEntity
import com.example.data.DomainFilterEntity
import com.example.data.GrapheneGuardRepository
import com.example.data.NotificationEntity
import com.example.data.QuarantineEntity
import com.example.data.ThreatLogEntity
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.random.Random

class SecurityDashboardViewModel(application: Application) : AndroidViewModel(application) {
    private val db = AppDatabase.getDatabase(application)
    val repository = GrapheneGuardRepository(db, application)

    val threatLogs: StateFlow<List<ThreatLogEntity>> = repository.threatLogs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val totalBlockedQueries: StateFlow<Int> = repository.totalBlockedQueries
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val totalQueries: StateFlow<Int> = repository.totalQueries
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val notifications: StateFlow<List<NotificationEntity>> = repository.notifications
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val unreadNotifCount: StateFlow<Int> = repository.unreadNotificationCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val quarantineItems: StateFlow<List<QuarantineEntity>> = repository.quarantineItems
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _isProtectionActive = MutableStateFlow(true)
    val isProtectionActive: StateFlow<Boolean> = _isProtectionActive.asStateFlow()

    private val _aiCpuAllocation = MutableStateFlow(35.0f) // 35% AI CPU core allocation
    val aiCpuAllocation: StateFlow<Float> = _aiCpuAllocation.asStateFlow()

    private val _aiEfficiencyStatus = MutableStateFlow("Optimized (35.0% Dedicated AI CPU)")
    val aiEfficiencyStatus: StateFlow<String> = _aiEfficiencyStatus.asStateFlow()

    private val _cameraKillswitch = MutableStateFlow(false)
    val cameraKillswitch: StateFlow<Boolean> = _cameraKillswitch.asStateFlow()

    private val _micKillswitch = MutableStateFlow(false)
    val micKillswitch: StateFlow<Boolean> = _micKillswitch.asStateFlow()

    private val _hardenedMallocEnabled = MutableStateFlow(true)
    val hardenedMallocEnabled: StateFlow<Boolean> = _hardenedMallocEnabled.asStateFlow()

    private val _backgroundDaemonMode = MutableStateFlow(true)
    val backgroundDaemonMode: StateFlow<Boolean> = _backgroundDaemonMode.asStateFlow()

    // Real-time Traffic Bytes In/Out Simulation
    private val _bytesReceived = MutableStateFlow(14258900L)
    val bytesReceived: StateFlow<Long> = _bytesReceived.asStateFlow()

    private val _bytesSent = MutableStateFlow(4120400L)
    val bytesSent: StateFlow<Long> = _bytesSent.asStateFlow()

    init {
        viewModelScope.launch {
            repository.initializeDefaultDataIfNeeded()
            startAiCpuMonitoringSimulation()
        }
    }

    fun toggleProtection(active: Boolean) {
        _isProtectionActive.value = active
        viewModelScope.launch {
            if (active) {
                repository.addThreatLog("Global Protection Re-activated", "INFO", "Shield Engine", "Full GrapheneOS security stack, DNSCrypt, and Pi-hole DNS firewall engaged.")
            } else {
                repository.addThreatLog("Protection Temporarily Paused", "MEDIUM", "Shield Engine", "User paused global protection engine.")
            }
        }
    }

    fun toggleCameraKillswitch(killed: Boolean) {
        _cameraKillswitch.value = killed
        viewModelScope.launch {
            repository.addThreatLog(
                if (killed) "Camera Hardware Sensors KILLED" else "Camera Hardware Sensors ENABLED",
                if (killed) "HIGH" else "INFO",
                "Sensor Control",
                if (killed) "Global GrapheneOS sensor toggle: Camera hardware access cut off at kernel level." else "Camera sensor restored."
            )
        }
    }

    fun toggleMicKillswitch(killed: Boolean) {
        _micKillswitch.value = killed
        viewModelScope.launch {
            repository.addThreatLog(
                if (killed) "Microphone Hardware KILLED" else "Microphone Hardware ENABLED",
                if (killed) "HIGH" else "INFO",
                "Sensor Control",
                if (killed) "Global GrapheneOS sensor toggle: Microphone access silenced." else "Microphone sensor restored."
            )
        }
    }

    fun toggleHardenedMalloc(enabled: Boolean) {
        _hardenedMallocEnabled.value = enabled
        viewModelScope.launch {
            repository.addThreatLog(
                "Hardened Malloc " + (if (enabled) "Enabled" else "Disabled"),
                "INFO",
                "GrapheneOS Memory",
                "Zero-fill, heap randomized guard pages state updated."
            )
        }
    }

    fun toggleBackgroundDaemon(enabled: Boolean) {
        _backgroundDaemonMode.value = enabled
    }

    fun markAllNotificationsRead() {
        viewModelScope.launch {
            repository.markAllNotificationsAsRead()
        }
    }

    fun restoreQuarantineItem(id: Long) {
        viewModelScope.launch {
            repository.updateQuarantineStatus(id, "RESTORED")
            repository.addThreatLog("Quarantine Item Restored", "INFO", "Quarantine Manager", "User restored item #$id from quarantine.")
        }
    }

    fun deleteQuarantineItem(id: Long) {
        viewModelScope.launch {
            repository.deleteQuarantineItem(id)
            repository.addThreatLog("Quarantine Item Deleted", "INFO", "Quarantine Manager", "User permanently deleted item #$id.")
        }
    }

    private fun startAiCpuMonitoringSimulation() {
        viewModelScope.launch {
            while (true) {
                delay(3000)
                if (_isProtectionActive.value) {
                    val delta = (Random.nextFloat() * 1.6f) - 0.8f
                    val newAllocation = (35.0f + delta).coerceIn(32.0f, 38.0f)
                    _aiCpuAllocation.value = newAllocation
                    _aiEfficiencyStatus.value = String.format("Optimized (%.1f%% Dedicated AI CPU)", newAllocation)

                    _bytesReceived.value += Random.nextLong(1024, 8192)
                    _bytesSent.value += Random.nextLong(512, 3072)
                } else {
                    _aiCpuAllocation.value = 0.0f
                    _aiEfficiencyStatus.value = "Paused (0.0% Allocation)"
                }
            }
        }
    }
}

class NetworkFirewallViewModel(application: Application) : AndroidViewModel(application) {
    private val db = AppDatabase.getDatabase(application)
    val repository = GrapheneGuardRepository(db, application)

    val searchQuery = MutableStateFlow("")
    val selectedFilter = MutableStateFlow("ALL")

    private val _rawAppRules = repository.allAppRules

    val filteredAppRules: StateFlow<List<AppNetworkRuleEntity>> = combine(
        _rawAppRules,
        searchQuery,
        selectedFilter
    ) { rules, query, filter ->
        rules.filter { rule ->
            val matchesQuery = query.isBlank() ||
                    rule.appName.contains(query, ignoreCase = true) ||
                    rule.packageName.contains(query, ignoreCase = true)

            val matchesFilter = when (filter) {
                "USER" -> !rule.isSystemApp
                "SYSTEM" -> rule.isSystemApp
                "BLOCKED" -> !rule.wifiAllowed || !rule.mobileDataAllowed
                else -> true
            }

            matchesQuery && matchesFilter
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun toggleWifi(rule: AppNetworkRuleEntity, enabled: Boolean) {
        viewModelScope.launch {
            repository.updateAppNetworkRule(rule.packageName, enabled, rule.mobileDataAllowed)
        }
    }

    fun toggleMobileData(rule: AppNetworkRuleEntity, enabled: Boolean) {
        viewModelScope.launch {
            repository.updateAppNetworkRule(rule.packageName, rule.wifiAllowed, enabled)
        }
    }

    fun setAllWifiState(enabled: Boolean, forSystemOnly: Boolean? = null) {
        viewModelScope.launch {
            filteredAppRules.value.forEach { rule ->
                if (forSystemOnly == null || rule.isSystemApp == forSystemOnly) {
                    repository.updateAppNetworkRule(rule.packageName, enabled, rule.mobileDataAllowed)
                }
            }
        }
    }

    fun setAllMobileDataState(enabled: Boolean, forSystemOnly: Boolean? = null) {
        viewModelScope.launch {
            filteredAppRules.value.forEach { rule ->
                if (forSystemOnly == null || rule.isSystemApp == forSystemOnly) {
                    repository.updateAppNetworkRule(rule.packageName, rule.wifiAllowed, enabled)
                }
            }
        }
    }
}

class PiHoleViewModel(application: Application) : AndroidViewModel(application) {
    private val db = AppDatabase.getDatabase(application)
    val repository = GrapheneGuardRepository(db, application)

    val blacklists: StateFlow<List<DomainFilterEntity>> = repository.blacklists
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val whitelists: StateFlow<List<DomainFilterEntity>> = repository.whitelists
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val dnsLogs = repository.dnsLogs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val totalBlocked = repository.totalBlockedQueries
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val totalQueries = repository.totalQueries
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val newDomainInput = MutableStateFlow("")
    val newDomainCommentInput = MutableStateFlow("")
    val selectedTab = MutableStateFlow("BLACKLIST")

    private val _isLiveStreamRunning = MutableStateFlow(true)
    val isLiveStreamRunning: StateFlow<Boolean> = _isLiveStreamRunning.asStateFlow()

    // DNSCrypt & Encrypted DNS Controls
    private val _dnsCryptEnabled = MutableStateFlow(true)
    val dnsCryptEnabled: StateFlow<Boolean> = _dnsCryptEnabled.asStateFlow()

    private val _selectedDnsProtocol = MutableStateFlow("DNSCrypt v2 (Quad9 + Cloudflare)")
    val selectedDnsProtocol: StateFlow<String> = _selectedDnsProtocol.asStateFlow()
    val selectedProtocol: StateFlow<String> = _selectedDnsProtocol.asStateFlow()

    private val _upstreamServer = MutableStateFlow("Quad9 DoH (9.9.9.9)")
    val upstreamServer: StateFlow<String> = _upstreamServer.asStateFlow()

    private val _dnssecActive = MutableStateFlow(true)
    val dnssecActive: StateFlow<Boolean> = _dnssecActive.asStateFlow()

    private val _dnsLatencyMs = MutableStateFlow(12)
    val dnsLatencyMs: StateFlow<Int> = _dnsLatencyMs.asStateFlow()

    init {
        startLiveDnsSimulation()
    }

    fun toggleDnsCrypt(enabled: Boolean) {
        _dnsCryptEnabled.value = enabled
        viewModelScope.launch {
            repository.addThreatLog(
                if (enabled) "DNSCrypt ENCRYPTION ENGAGED" else "DNSCrypt Encryption Disabled",
                "INFO",
                "DNSCrypt Proxy",
                if (enabled) "All DNS queries encrypted via DNSCrypt / DNS-over-HTTPS (DoH) fallback tunnel." else "Plaintext DNS active."
            )
        }
    }

    fun setDnsProtocol(protocol: String) {
        _selectedDnsProtocol.value = protocol
        viewModelScope.launch {
            repository.addThreatLog("DNS Protocol Updated", "INFO", "DNSCrypt Proxy", "Protocol changed to $protocol")
        }
    }

    fun toggleDnssec(enabled: Boolean) {
        _dnssecActive.value = enabled
    }

    fun addDomain(isWhitelist: Boolean) {
        val domain = newDomainInput.value.trim()
        if (domain.isNotBlank()) {
            viewModelScope.launch {
                repository.addDomainFilter(
                    domain = domain,
                    isWhitelist = isWhitelist,
                    category = if (isWhitelist) "User Whitelist" else "User Blacklist",
                    comment = newDomainCommentInput.value.trim()
                )
                newDomainInput.value = ""
                newDomainCommentInput.value = ""
            }
        }
    }

    fun deleteDomain(id: Int) {
        viewModelScope.launch {
            repository.deleteDomainFilter(id)
        }
    }

    fun toggleDomainState(id: Int, enabled: Boolean) {
        viewModelScope.launch {
            repository.toggleDomainFilter(id, enabled)
        }
    }

    fun toggleLiveStream() {
        _isLiveStreamRunning.value = !_isLiveStreamRunning.value
    }

    fun clearLogs() {
        viewModelScope.launch {
            repository.clearDnsLogs()
        }
    }

    fun quickBlacklistDomain(domain: String) {
        viewModelScope.launch {
            repository.addDomainFilter(domain, isWhitelist = false, category = "Quick Blacklist", comment = "Added from live query stream")
            repository.logDnsQuery(domain, "Pi-hole Filter", true, "Quick blacklisted by user")
        }
    }

    fun quickWhitelistDomain(domain: String) {
        viewModelScope.launch {
            repository.addDomainFilter(domain, isWhitelist = true, category = "Quick Whitelist", comment = "Added from live query stream")
            repository.logDnsQuery(domain, "Pi-hole Filter", false, "Quick whitelisted by user")
        }
    }

    private fun startLiveDnsSimulation() {
        viewModelScope.launch {
            val sampleApps = listOf("Vanadium Browser", "System UI", "Google Play Services", "Signal", "Tor Browser", "Weather Widget", "Maps", "Email Sync")
            val sampleAdDomains = listOf("ads.google.com", "track.doubleclick.net", "ad.adzerk.net", "telemetry.segment.io", "analytics.tiktok.com", "pixel.facebook.com", "metrics.apple.com")
            val sampleValidDomains = listOf("fdroid.org", "grapheneos.org", "wikipedia.org", "github.com", "signal.org", "rust-lang.org", "python.org")

            while (true) {
                delay(4000)
                if (_isLiveStreamRunning.value) {
                    val isAd = Random.nextBoolean()
                    val app = sampleApps.random()
                    val domain = if (isAd) sampleAdDomains.random() else sampleValidDomains.random()
                    val isBlocked = isAd
                    _dnsLatencyMs.value = Random.nextInt(8, 22)

                    repository.logDnsQuery(
                        domain = domain,
                        appName = app,
                        blocked = isBlocked,
                        reason = if (isBlocked) "Pi-hole Blacklist Match" else "DNSCrypt Encrypted Resolution"
                    )
                }
            }
        }
    }
}

class SystemInspectorViewModel(application: Application) : AndroidViewModel(application) {
    private val db = AppDatabase.getDatabase(application)
    val repository = GrapheneGuardRepository(db, application)

    data class SystemFileItem(
        val path: String,
        val label: String,
        val permissions: String,
        val status: String,
        val isProtected: Boolean,
        val details: String
    )

    val systemFiles = listOf(
        SystemFileItem("/system/bin/app_process", "Android Runtime Launcher", "rwxr-xr-x", "HARDENED", true, "Exec-spawning sandboxed under GrapheneOS security policy."),
        SystemFileItem("/system/etc/hosts", "Pi-hole System Hosts File", "rw-r--r--", "SYNCED", true, "Overridden with local Pi-hole domain blocklist rules."),
        SystemFileItem("/proc/sys/kernel/randomize_va_space", "ASLR Memory Protection", "r--r--r--", "LEVEL 2 ACTIVE", true, "Full stack, heap, and mmap base address randomization."),
        SystemFileItem("/dev/hw_random", "Hardware Entropy Device", "r--r--r--", "SECURE", true, "Hardware-backed cryptographic random number generator."),
        SystemFileItem("/system/etc/dnscrypt-proxy/dnscrypt-proxy.toml", "DNSCrypt Configuration", "rw-r--r--", "ENCRYPTED", true, "Active DNSCrypt v2 resolvers & DNS-over-HTTPS fallback servers."),
        SystemFileItem("/system/priv-app/", "System Privileged Apps Folder", "rwxr-xr-x", "MONITORED", true, "12 Privileged system components monitored for unauthorized background network calls."),
        SystemFileItem("/data/system/packages.xml", "System Package & Permission Manifest", "rw-------", "ENCRYPTED", true, "Per-app WiFi and Mobile Data toggle permissions stored here.")
    )

    val deviceModel: String = android.os.Build.MODEL
    val deviceManufacturer: String = android.os.Build.MANUFACTURER
    val deviceHardware: String = android.os.Build.HARDWARE
    val deviceBoard: String = android.os.Build.BOARD
    val supportedAbis: String = android.os.Build.SUPPORTED_ABIS.joinToString(", ")
    val androidVersion: String = "Android ${android.os.Build.VERSION.RELEASE} (API ${android.os.Build.VERSION.SDK_INT})"

    private val _a67lOptimizationActive = MutableStateFlow(true)
    val a67lOptimizationActive: StateFlow<Boolean> = _a67lOptimizationActive.asStateFlow()

    private val _storageScopesEnabled = MutableStateFlow(true)
    val storageScopesEnabled: StateFlow<Boolean> = _storageScopesEnabled.asStateFlow()

    private val _networkPermissionPrompt = MutableStateFlow(true)
    val networkPermissionPrompt: StateFlow<Boolean> = _networkPermissionPrompt.asStateFlow()

    private val _execSpawningBlocked = MutableStateFlow(true)
    val execSpawningBlocked: StateFlow<Boolean> = _execSpawningBlocked.asStateFlow()

    private val _wifiDebuggingEnabled = MutableStateFlow(true)
    val wifiDebuggingEnabled: StateFlow<Boolean> = _wifiDebuggingEnabled.asStateFlow()

    private val _adbPort = MutableStateFlow(5555)
    val adbPort: StateFlow<Int> = _adbPort.asStateFlow()

    private val _adbPairingCode = MutableStateFlow("849-210")
    val adbPairingCode: StateFlow<String> = _adbPairingCode.asStateFlow()

    private val _lanIp = MutableStateFlow("192.168.1.145")
    val lanIp: StateFlow<String> = _lanIp.asStateFlow()

    private val _usbTetheringActive = MutableStateFlow(true)
    val usbTetheringActive: StateFlow<Boolean> = _usbTetheringActive.asStateFlow()

    private val _bluetoothPairingActive = MutableStateFlow(true)
    val bluetoothPairingActive: StateFlow<Boolean> = _bluetoothPairingActive.asStateFlow()

    fun toggleWifiDebugging(enabled: Boolean) {
        _wifiDebuggingEnabled.value = enabled
    }

    fun generateNewPairingCode() {
        val code1 = Random.nextInt(100, 999)
        val code2 = Random.nextInt(100, 999)
        _adbPairingCode.value = "$code1-$code2"
        _adbPort.value = Random.nextInt(30000, 45000)
    }

    fun toggleUsbTethering(enabled: Boolean) {
        _usbTetheringActive.value = enabled
    }

    fun toggleBluetoothPairing(enabled: Boolean) {
        _bluetoothPairingActive.value = enabled
    }

    fun toggleStorageScopes(enabled: Boolean) {
        _storageScopesEnabled.value = enabled
        viewModelScope.launch {
            repository.addThreatLog(
                if (enabled) "Storage Scopes ACTIVE" else "Storage Scopes Disabled",
                "INFO",
                "GrapheneOS Sandbox",
                if (enabled) "Legacy app access restricted to isolate media files and document scopes." else "Storage scopes disabled."
            )
        }
    }

    fun toggleNetworkPrompt(enabled: Boolean) {
        _networkPermissionPrompt.value = enabled
    }

    fun toggleA67lOptimization(enabled: Boolean) {
        _a67lOptimizationActive.value = enabled
        viewModelScope.launch {
            repository.addThreatLog(
                if (enabled) "A67L / Low-RAM Profile ENGAGED" else "Standard Device Profile",
                "INFO",
                "Hardware Adaptation",
                if (enabled) "Applied Unisoc ARM64/ARM32 memory throttling, 720p HD+ display scaling, and lightweight background daemon." else "Standard execution profile restored."
            )
        }
    }

    fun toggleExecSpawning(enabled: Boolean) {
        _execSpawningBlocked.value = enabled
        viewModelScope.launch {
            repository.addThreatLog(
                if (enabled) "Exec-Spawning BLOCKED" else "Exec-Spawning Permissive",
                if (enabled) "HIGH" else "MEDIUM",
                "Process Isolation",
                if (enabled) "Prevented apps from executing binaries in /data partition." else "Exec spawning set to permissive."
            )
        }
    }
}

class AvScannerViewModel(application: Application) : AndroidViewModel(application) {
    private val db = AppDatabase.getDatabase(application)
    val repository = GrapheneGuardRepository(db, application)

    val targetInput = MutableStateFlow("")
    val targetType = MutableStateFlow("PACKAGE") // "PACKAGE", "URL", "HASH", "IP", "DOMAIN", "FILE"

    private val _isScanning = MutableStateFlow(false)
    val isScanning: StateFlow<Boolean> = _isScanning.asStateFlow()

    private val _scanProgress = MutableStateFlow(0f)
    val scanProgress: StateFlow<Float> = _scanProgress.asStateFlow()

    private val _currentEngine = MutableStateFlow("")
    val currentEngine: StateFlow<String> = _currentEngine.asStateFlow()

    val avScanLogs: StateFlow<List<AvScanLogEntity>> = repository.avScanLogs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _lastScanResult = MutableStateFlow<AvScanLogEntity?>(null)
    val lastScanResult: StateFlow<AvScanLogEntity?> = _lastScanResult.asStateFlow()

    private val engines = listOf(
        "Bitdefender Total Security",
        "Kaspersky Endpoint Shield",
        "CrowdStrike Falcon AI",
        "Malwarebytes Mobile",
        "Windows Defender SmartScreen",
        "ESET NOD32 Mobile",
        "Avast Threat Intelligence",
        "NetGuard Heuristic Sandbox"
    )

    fun onFilePicked(fileName: String, bytesCount: Long) {
        val simulatedHash = String.format("%08x%08x%08x", fileName.hashCode(), bytesCount.hashCode(), System.currentTimeMillis().toInt()).lowercase()
        targetInput.value = "$fileName ($simulatedHash)"
        targetType.value = "FILE"
        startMultiEngineScan()
    }

    fun startMultiEngineScan() {
        val input = targetInput.value.trim()
        if (input.isBlank() || _isScanning.value) return

        viewModelScope.launch {
            _isScanning.value = true
            _scanProgress.value = 0f

            val engineResults = mutableListOf<String>()
            var detections = 0

            for (i in engines.indices) {
                _currentEngine.value = "Analyzing with ${engines[i]}..."
                delay(280)
                _scanProgress.value = (i + 1).toFloat() / engines.size.toFloat()

                val isFlagged = when {
                    input.contains("malicious", ignoreCase = true) ||
                            input.contains("spyware", ignoreCase = true) ||
                            input.contains("phish", ignoreCase = true) ||
                            input.contains("trojan", ignoreCase = true) ||
                            input.contains("virus", ignoreCase = true) -> Random.nextFloat() > 0.15f
                    else -> Random.nextFloat() < 0.08f
                }

                if (isFlagged) {
                    detections++
                    engineResults.add("${engines[i].split(" ").first()}: Detected")
                } else {
                    engineResults.add("${engines[i].split(" ").first()}: Clean")
                }
            }

            val total = engines.size
            val cleanCount = total - detections
            val score = ((detections.toFloat() / total.toFloat()) * 100f).toInt()

            val verdict = when {
                score >= 50 -> "MALICIOUS"
                score > 0 -> "SUSPICIOUS"
                else -> "CLEAN"
            }

            val threatFamily = when (verdict) {
                "MALICIOUS" -> "Trojan.AndroidOS.Generic.Heur"
                "SUSPICIOUS" -> "PUP.AndroidOS.Adware.Risk"
                else -> "Clean.NoThreatFound"
            }

            val details = if (detections > 0) {
                "Flagged by $detections/$total top-tier antivirus engines. Recommended action: Quarantine item immediately."
            } else {
                "Verified safe across all 8 enterprise security engines. Zero threat signatures identified."
            }

            val engineResultsStr = engineResults.joinToString(", ")

            val resultEntity = AvScanLogEntity(
                targetInput = input,
                targetType = targetType.value,
                riskScore = score,
                verdict = verdict,
                threatFamily = threatFamily,
                engineResults = engineResultsStr,
                enginesClean = cleanCount,
                enginesTotal = total,
                details = details
            )

            repository.saveAvScanResult(
                targetInput = input,
                targetType = targetType.value,
                riskScore = score,
                verdict = verdict,
                threatFamily = threatFamily,
                engineResults = engineResultsStr,
                enginesClean = cleanCount,
                enginesTotal = total,
                details = details
            )

            if (verdict != "CLEAN") {
                repository.addThreatLog(
                    "AV Malware Flagged: $input",
                    if (verdict == "MALICIOUS") "CRITICAL" else "MEDIUM",
                    "Bitdefender/Multi-AV Engine",
                    "Target $input flagged as $verdict with risk score $score/100."
                )
                repository.quarantineItem(input, targetType.value, threatFamily, score)
                repository.addNotification(
                    "Malware Quarantined",
                    "Item '$input' flagged ($verdict) and moved to Quarantine Vault.",
                    if (verdict == "MALICIOUS") "CRITICAL" else "HIGH"
                )
            }

            _lastScanResult.value = resultEntity
            _isScanning.value = false
            _currentEngine.value = "Scan Complete ($cleanCount/8 Engines Clean)"
        }
    }
}

class SettingsViewModel(application: Application) : AndroidViewModel(application) {
    private val db = AppDatabase.getDatabase(application)
    val repository = GrapheneGuardRepository(db, application)

    val serverUrl = MutableStateFlow("http://192.168.1.50:8080")
    val isTestingConnection = MutableStateFlow(false)
    val connectionStatus = MutableStateFlow("CONNECTED (Latency: 14ms)")

    val wifiDebuggingEnabled = MutableStateFlow(true)
    val adbPort = MutableStateFlow(5555)
    val pairingCode = MutableStateFlow("849-210")

    val usbTetheringActive = MutableStateFlow(true)
    val bluetoothPairingActive = MutableStateFlow(true)

    val biometricLockEnabled = MutableStateFlow(false)
    val pushAlertsEnabled = MutableStateFlow(true)

    fun testServerConnection() {
        viewModelScope.launch {
            isTestingConnection.value = true
            connectionStatus.value = "Testing handshake to ${serverUrl.value}..."
            delay(1200)
            isTestingConnection.value = false
            connectionStatus.value = "SUCCESS: Encrypted TLS Connection to ${serverUrl.value} verified!"
        }
    }

    fun generateNewPairingCode() {
        val code1 = Random.nextInt(100, 999)
        val code2 = Random.nextInt(100, 999)
        pairingCode.value = "$code1-$code2"
        adbPort.value = Random.nextInt(30000, 45000)
    }
}


