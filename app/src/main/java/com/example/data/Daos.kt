package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface AppNetworkRuleDao {
    @Query("SELECT * FROM app_network_rules ORDER BY isSystemApp ASC, appName ASC")
    fun getAllRules(): Flow<List<AppNetworkRuleEntity>>

    @Query("SELECT * FROM app_network_rules WHERE isSystemApp = :isSystem ORDER BY appName ASC")
    fun getRulesBySystemType(isSystem: Boolean): Flow<List<AppNetworkRuleEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRule(rule: AppNetworkRuleEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(rules: List<AppNetworkRuleEntity>)

    @Query("UPDATE app_network_rules SET wifiAllowed = :wifi, mobileDataAllowed = :mobile, lastUpdated = :now WHERE packageName = :pkg")
    suspend fun updateNetworkState(pkg: String, wifi: Boolean, mobile: Boolean, now: Long = System.currentTimeMillis())

    @Query("SELECT COUNT(*) FROM app_network_rules")
    suspend fun getCount(): Int
}

@Dao
interface DomainFilterDao {
    @Query("SELECT * FROM domain_filters WHERE isWhitelist = :isWhitelist ORDER BY dateAdded DESC")
    fun getFilters(isWhitelist: Boolean): Flow<List<DomainFilterEntity>>

    @Query("SELECT * FROM domain_filters ORDER BY dateAdded DESC")
    fun getAllFilters(): Flow<List<DomainFilterEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFilter(filter: DomainFilterEntity)

    @Query("DELETE FROM domain_filters WHERE id = :id")
    suspend fun deleteFilterById(id: Int)

    @Query("UPDATE domain_filters SET isEnabled = :enabled WHERE id = :id")
    suspend fun toggleFilterState(id: Int, enabled: Boolean)

    @Query("UPDATE domain_filters SET hitCount = hitCount + 1 WHERE domain = :domain")
    suspend fun incrementHitCount(domain: String)

    @Query("SELECT COUNT(*) FROM domain_filters")
    suspend fun getCount(): Int
}

@Dao
interface DnsQueryLogDao {
    @Query("SELECT * FROM dns_query_logs ORDER BY timestamp DESC LIMIT :limit")
    fun getRecentLogs(limit: Int = 150): Flow<List<DnsQueryLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: DnsQueryLogEntity)

    @Query("DELETE FROM dns_query_logs")
    suspend fun clearLogs()

    @Query("SELECT COUNT(*) FROM dns_query_logs WHERE blocked = 1")
    fun getBlockedCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM dns_query_logs")
    fun getTotalCount(): Flow<Int>
}

@Dao
interface ThreatLogDao {
    @Query("SELECT * FROM threat_logs ORDER BY timestamp DESC LIMIT 100")
    fun getRecentThreats(): Flow<List<ThreatLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertThreat(threat: ThreatLogEntity)

    @Query("DELETE FROM threat_logs")
    suspend fun clearThreats()
}

@Dao
interface AvScanLogDao {
    @Query("SELECT * FROM av_scan_logs ORDER BY timestamp DESC LIMIT 100")
    fun getRecentScans(): Flow<List<AvScanLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScan(scan: AvScanLogEntity)

    @Query("DELETE FROM av_scan_logs")
    suspend fun clearScans()
}

@Dao
interface NotificationDao {
    @Query("SELECT * FROM notifications ORDER BY timestamp DESC LIMIT 50")
    fun getNotifications(): Flow<List<NotificationEntity>>

    @Query("SELECT COUNT(*) FROM notifications WHERE isRead = 0")
    fun getUnreadCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: NotificationEntity)

    @Query("UPDATE notifications SET isRead = 1 WHERE id = :id")
    suspend fun markAsRead(id: Long)

    @Query("UPDATE notifications SET isRead = 1")
    suspend fun markAllAsRead()

    @Query("DELETE FROM notifications")
    suspend fun clearNotifications()
}

@Dao
interface QuarantineDao {
    @Query("SELECT * FROM quarantine_items ORDER BY timestamp DESC")
    fun getQuarantineItems(): Flow<List<QuarantineEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: QuarantineEntity)

    @Query("UPDATE quarantine_items SET status = :status WHERE id = :id")
    suspend fun updateStatus(id: Long, status: String)

    @Query("DELETE FROM quarantine_items WHERE id = :id")
    suspend fun deleteItem(id: Long)
}


