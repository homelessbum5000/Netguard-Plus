package com.example.data

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

class AppDatabase private constructor(context: Context) : SQLiteOpenHelper(
    context.applicationContext,
    "netguard_plus.db",
    null,
    1
) {
    private val dbMutex = Mutex()

    private val _appRulesFlow = MutableStateFlow<List<AppNetworkRuleEntity>>(emptyList())
    private val _dnsLogsFlow = MutableStateFlow<List<DnsQueryLogEntity>>(emptyList())
    private val _domainFiltersFlow = MutableStateFlow<List<DomainFilterEntity>>(emptyList())
    private val _threatLogsFlow = MutableStateFlow<List<ThreatLogEntity>>(emptyList())

    init {
        // Initial load of flows from SQLite
        refreshAllData()
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS app_network_rules (
                packageName TEXT PRIMARY KEY,
                appName TEXT NOT NULL,
                isSystemApp INTEGER NOT NULL,
                wifiAllowed INTEGER NOT NULL,
                mobileDataAllowed INTEGER NOT NULL,
                uid INTEGER NOT NULL,
                bytesTransferred INTEGER NOT NULL,
                blockedPacketsCount INTEGER NOT NULL
            )
            """.trimIndent()
        )

        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS dns_query_logs (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                domain TEXT NOT NULL,
                appName TEXT NOT NULL,
                packageName TEXT NOT NULL,
                blocked INTEGER NOT NULL,
                reason TEXT NOT NULL,
                timestamp INTEGER NOT NULL,
                isDnsCrypt INTEGER NOT NULL,
                protocol TEXT NOT NULL,
                latencyMs INTEGER NOT NULL
            )
            """.trimIndent()
        )

        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS domain_filters (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                domain TEXT NOT NULL,
                isWhitelist INTEGER NOT NULL,
                isEnabled INTEGER NOT NULL,
                comment TEXT NOT NULL,
                category TEXT NOT NULL,
                hitCount INTEGER NOT NULL
            )
            """.trimIndent()
        )

        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS threat_logs (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                threatTitle TEXT NOT NULL,
                severity TEXT NOT NULL,
                module TEXT NOT NULL,
                details TEXT NOT NULL,
                timestamp INTEGER NOT NULL
            )
            """.trimIndent()
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS app_network_rules")
        db.execSQL("DROP TABLE IF EXISTS dns_query_logs")
        db.execSQL("DROP TABLE IF EXISTS domain_filters")
        db.execSQL("DROP TABLE IF EXISTS threat_logs")
        onCreate(db)
    }

    private fun refreshAllData() {
        try {
            val db = readableDatabase
            _appRulesFlow.value = queryAllRules(db)
            _dnsLogsFlow.value = queryRecentLogs(db)
            _domainFiltersFlow.value = queryAllFilters(db)
            _threatLogsFlow.value = queryAllThreatLogs(db)
        } catch (_: Exception) {
            // Database might still be initializing
        }
    }

    private fun queryAllRules(db: SQLiteDatabase): List<AppNetworkRuleEntity> {
        val list = mutableListOf<AppNetworkRuleEntity>()
        val cursor: Cursor = db.rawQuery(
            "SELECT packageName, appName, isSystemApp, wifiAllowed, mobileDataAllowed, uid, bytesTransferred, blockedPacketsCount FROM app_network_rules ORDER BY appName ASC",
            null
        )
        cursor.use {
            while (it.moveToNext()) {
                list.add(
                    AppNetworkRuleEntity(
                        packageName = it.getString(0),
                        appName = it.getString(1),
                        isSystemApp = it.getInt(2) == 1,
                        wifiAllowed = it.getInt(3) == 1,
                        mobileDataAllowed = it.getInt(4) == 1,
                        uid = it.getInt(5),
                        bytesTransferred = it.getLong(6),
                        blockedPacketsCount = it.getInt(7)
                    )
                )
            }
        }
        return list
    }

    private fun queryRecentLogs(db: SQLiteDatabase): List<DnsQueryLogEntity> {
        val list = mutableListOf<DnsQueryLogEntity>()
        val cursor: Cursor = db.rawQuery(
            "SELECT id, domain, appName, packageName, blocked, reason, timestamp, isDnsCrypt, protocol, latencyMs FROM dns_query_logs ORDER BY timestamp DESC LIMIT 200",
            null
        )
        cursor.use {
            while (it.moveToNext()) {
                list.add(
                    DnsQueryLogEntity(
                        id = it.getLong(0),
                        domain = it.getString(1),
                        appName = it.getString(2),
                        packageName = it.getString(3),
                        blocked = it.getInt(4) == 1,
                        reason = it.getString(5),
                        timestamp = it.getLong(6),
                        isDnsCrypt = it.getInt(7) == 1,
                        protocol = it.getString(8),
                        latencyMs = it.getLong(9)
                    )
                )
            }
        }
        return list
    }

    private fun queryAllFilters(db: SQLiteDatabase): List<DomainFilterEntity> {
        val list = mutableListOf<DomainFilterEntity>()
        val cursor: Cursor = db.rawQuery(
            "SELECT id, domain, isWhitelist, isEnabled, comment, category, hitCount FROM domain_filters ORDER BY id DESC",
            null
        )
        cursor.use {
            while (it.moveToNext()) {
                list.add(
                    DomainFilterEntity(
                        id = it.getLong(0),
                        domain = it.getString(1),
                        isWhitelist = it.getInt(2) == 1,
                        isEnabled = it.getInt(3) == 1,
                        comment = it.getString(4),
                        category = it.getString(5),
                        hitCount = it.getInt(6)
                    )
                )
            }
        }
        return list
    }

    private fun queryAllThreatLogs(db: SQLiteDatabase): List<ThreatLogEntity> {
        val list = mutableListOf<ThreatLogEntity>()
        val cursor: Cursor = db.rawQuery(
            "SELECT id, threatTitle, severity, module, details, timestamp FROM threat_logs ORDER BY timestamp DESC LIMIT 100",
            null
        )
        cursor.use {
            while (it.moveToNext()) {
                list.add(
                    ThreatLogEntity(
                        id = it.getLong(0),
                        threatTitle = it.getString(1),
                        severity = it.getString(2),
                        module = it.getString(3),
                        details = it.getString(4),
                        timestamp = it.getLong(5)
                    )
                )
            }
        }
        return list
    }

    val appNetworkRuleDao: AppNetworkRuleDao = object : AppNetworkRuleDao {
        override fun getAllRules(): Flow<List<AppNetworkRuleEntity>> = _appRulesFlow.asStateFlow()

        override suspend fun getRuleByPackage(packageName: String): AppNetworkRuleEntity? = withContext(Dispatchers.IO) {
            dbMutex.withLock {
                _appRulesFlow.value.find { it.packageName == packageName }
            }
        }

        override suspend fun insertRules(rules: List<AppNetworkRuleEntity>) = withContext(Dispatchers.IO) {
            dbMutex.withLock {
                val db = writableDatabase
                db.beginTransaction()
                try {
                    for (rule in rules) {
                        val cv = ContentValues().apply {
                            put("packageName", rule.packageName)
                            put("appName", rule.appName)
                            put("isSystemApp", if (rule.isSystemApp) 1 else 0)
                            put("wifiAllowed", if (rule.wifiAllowed) 1 else 0)
                            put("mobileDataAllowed", if (rule.mobileDataAllowed) 1 else 0)
                            put("uid", rule.uid)
                            put("bytesTransferred", rule.bytesTransferred)
                            put("blockedPacketsCount", rule.blockedPacketsCount)
                        }
                        db.insertWithOnConflict(
                            "app_network_rules",
                            null,
                            cv,
                            SQLiteDatabase.CONFLICT_REPLACE
                        )
                    }
                    db.setTransactionSuccessful()
                } finally {
                    db.endTransaction()
                }
                _appRulesFlow.value = queryAllRules(db)
            }
        }

        override suspend fun insertOrUpdate(rule: AppNetworkRuleEntity) = withContext(Dispatchers.IO) {
            insertRules(listOf(rule))
        }

        override suspend fun updateRule(rule: AppNetworkRuleEntity) = withContext(Dispatchers.IO) {
            insertRules(listOf(rule))
        }

        override suspend fun setAllWifiState(enabled: Boolean) = withContext(Dispatchers.IO) {
            dbMutex.withLock {
                val db = writableDatabase
                val cv = ContentValues().apply {
                    put("wifiAllowed", if (enabled) 1 else 0)
                }
                db.update("app_network_rules", cv, null, null)
                _appRulesFlow.value = queryAllRules(db)
            }
        }

        override suspend fun setSystemWifiState(enabled: Boolean) = withContext(Dispatchers.IO) {
            dbMutex.withLock {
                val db = writableDatabase
                val cv = ContentValues().apply {
                    put("wifiAllowed", if (enabled) 1 else 0)
                }
                db.update("app_network_rules", cv, "isSystemApp = 1", null)
                _appRulesFlow.value = queryAllRules(db)
            }
        }

        override suspend fun setAllMobileDataState(enabled: Boolean) = withContext(Dispatchers.IO) {
            dbMutex.withLock {
                val db = writableDatabase
                val cv = ContentValues().apply {
                    put("mobileDataAllowed", if (enabled) 1 else 0)
                }
                db.update("app_network_rules", cv, null, null)
                _appRulesFlow.value = queryAllRules(db)
            }
        }

        override suspend fun setSystemMobileDataState(enabled: Boolean) = withContext(Dispatchers.IO) {
            dbMutex.withLock {
                val db = writableDatabase
                val cv = ContentValues().apply {
                    put("mobileDataAllowed", if (enabled) 1 else 0)
                }
                db.update("app_network_rules", cv, "isSystemApp = 1", null)
                _appRulesFlow.value = queryAllRules(db)
            }
        }

        override suspend fun incrementBlockedPackets(packageName: String) = withContext(Dispatchers.IO) {
            dbMutex.withLock {
                val db = writableDatabase
                db.execSQL(
                    "UPDATE app_network_rules SET blockedPacketsCount = blockedPacketsCount + 1 WHERE packageName = ?",
                    arrayOf(packageName)
                )
                _appRulesFlow.value = queryAllRules(db)
            }
        }
    }

    val dnsQueryLogDao: DnsQueryLogDao = object : DnsQueryLogDao {
        override fun getRecentLogs(): Flow<List<DnsQueryLogEntity>> = _dnsLogsFlow.asStateFlow()

        override suspend fun insertLog(log: DnsQueryLogEntity) = withContext(Dispatchers.IO) {
            dbMutex.withLock {
                val db = writableDatabase
                val cv = ContentValues().apply {
                    put("domain", log.domain)
                    put("appName", log.appName)
                    put("packageName", log.packageName)
                    put("blocked", if (log.blocked) 1 else 0)
                    put("reason", log.reason)
                    put("timestamp", log.timestamp)
                    put("isDnsCrypt", if (log.isDnsCrypt) 1 else 0)
                    put("protocol", log.protocol)
                    put("latencyMs", log.latencyMs)
                }
                db.insert("dns_query_logs", null, cv)
                _dnsLogsFlow.value = queryRecentLogs(db)
            }
        }

        override suspend fun clearAllLogs() = withContext(Dispatchers.IO) {
            dbMutex.withLock {
                val db = writableDatabase
                db.delete("dns_query_logs", null, null)
                _dnsLogsFlow.value = emptyList()
            }
        }

        override fun getTotalQueriesCount(): Flow<Int> = _dnsLogsFlow.map { it.size }

        override fun getBlockedQueriesCount(): Flow<Int> = _dnsLogsFlow.map { list -> list.count { it.blocked } }
    }

    val domainFilterDao: DomainFilterDao = object : DomainFilterDao {
        override fun getAllFilters(): Flow<List<DomainFilterEntity>> = _domainFiltersFlow.asStateFlow()

        override suspend fun getActiveBlacklists(): List<DomainFilterEntity> = withContext(Dispatchers.IO) {
            dbMutex.withLock {
                _domainFiltersFlow.value.filter { !it.isWhitelist && it.isEnabled }
            }
        }

        override suspend fun getActiveWhitelists(): List<DomainFilterEntity> = withContext(Dispatchers.IO) {
            dbMutex.withLock {
                _domainFiltersFlow.value.filter { it.isWhitelist && it.isEnabled }
            }
        }

        override suspend fun insertFilter(filter: DomainFilterEntity) = withContext(Dispatchers.IO) {
            dbMutex.withLock {
                val db = writableDatabase
                val cv = ContentValues().apply {
                    put("domain", filter.domain)
                    put("isWhitelist", if (filter.isWhitelist) 1 else 0)
                    put("isEnabled", if (filter.isEnabled) 1 else 0)
                    put("comment", filter.comment)
                    put("category", filter.category)
                    put("hitCount", filter.hitCount)
                }
                db.insertWithOnConflict(
                    "domain_filters",
                    null,
                    cv,
                    SQLiteDatabase.CONFLICT_REPLACE
                )
                _domainFiltersFlow.value = queryAllFilters(db)
            }
        }

        override suspend fun insertAll(filters: List<DomainFilterEntity>) = withContext(Dispatchers.IO) {
            dbMutex.withLock {
                val db = writableDatabase
                db.beginTransaction()
                try {
                    for (filter in filters) {
                        val cv = ContentValues().apply {
                            put("domain", filter.domain)
                            put("isWhitelist", if (filter.isWhitelist) 1 else 0)
                            put("isEnabled", if (filter.isEnabled) 1 else 0)
                            put("comment", filter.comment)
                            put("category", filter.category)
                            put("hitCount", filter.hitCount)
                        }
                        db.insertWithOnConflict(
                            "domain_filters",
                            null,
                            cv,
                            SQLiteDatabase.CONFLICT_REPLACE
                        )
                    }
                    db.setTransactionSuccessful()
                } finally {
                    db.endTransaction()
                }
                _domainFiltersFlow.value = queryAllFilters(db)
            }
        }

        override suspend fun toggleFilterState(id: Long, enabled: Boolean) = withContext(Dispatchers.IO) {
            dbMutex.withLock {
                val db = writableDatabase
                val cv = ContentValues().apply {
                    put("isEnabled", if (enabled) 1 else 0)
                }
                db.update("domain_filters", cv, "id = ?", arrayOf(id.toString()))
                _domainFiltersFlow.value = queryAllFilters(db)
            }
        }

        override suspend fun deleteFilter(id: Long) = withContext(Dispatchers.IO) {
            dbMutex.withLock {
                val db = writableDatabase
                db.delete("domain_filters", "id = ?", arrayOf(id.toString()))
                _domainFiltersFlow.value = queryAllFilters(db)
            }
        }

        override suspend fun incrementHitCount(domain: String) = withContext(Dispatchers.IO) {
            dbMutex.withLock {
                val db = writableDatabase
                db.execSQL(
                    "UPDATE domain_filters SET hitCount = hitCount + 1 WHERE domain = ?",
                    arrayOf(domain)
                )
                _domainFiltersFlow.value = queryAllFilters(db)
            }
        }
    }

    val threatLogDao: ThreatLogDao = object : ThreatLogDao {
        override fun getAllThreatLogs(): Flow<List<ThreatLogEntity>> = _threatLogsFlow.asStateFlow()

        override suspend fun insertThreatLog(log: ThreatLogEntity) = withContext(Dispatchers.IO) {
            dbMutex.withLock {
                val db = writableDatabase
                val cv = ContentValues().apply {
                    put("threatTitle", log.threatTitle)
                    put("severity", log.severity)
                    put("module", log.module)
                    put("details", log.details)
                    put("timestamp", log.timestamp)
                }
                db.insert("threat_logs", null, cv)
                _threatLogsFlow.value = queryAllThreatLogs(db)
            }
        }

        override suspend fun clearAllThreatLogs() = withContext(Dispatchers.IO) {
            dbMutex.withLock {
                val db = writableDatabase
                db.delete("threat_logs", null, null)
                _threatLogsFlow.value = emptyList()
            }
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: AppDatabase(context).also { INSTANCE = it }
            }
        }
    }
}
