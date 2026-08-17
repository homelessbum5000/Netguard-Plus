package com.example.data

import kotlinx.coroutines.flow.Flow

interface AppNetworkRuleDao {
    fun getAllRules(): Flow<List<AppNetworkRuleEntity>>
    suspend fun getRuleByPackage(packageName: String): AppNetworkRuleEntity?
    suspend fun insertRules(rules: List<AppNetworkRuleEntity>)
    suspend fun insertOrUpdate(rule: AppNetworkRuleEntity)
    suspend fun updateRule(rule: AppNetworkRuleEntity)
    suspend fun setAllWifiState(enabled: Boolean)
    suspend fun setSystemWifiState(enabled: Boolean)
    suspend fun setAllMobileDataState(enabled: Boolean)
    suspend fun setSystemMobileDataState(enabled: Boolean)
    suspend fun incrementBlockedPackets(packageName: String)
}

interface DnsQueryLogDao {
    fun getRecentLogs(): Flow<List<DnsQueryLogEntity>>
    suspend fun insertLog(log: DnsQueryLogEntity)
    suspend fun clearAllLogs()
    fun getTotalQueriesCount(): Flow<Int>
    fun getBlockedQueriesCount(): Flow<Int>
}

interface DomainFilterDao {
    fun getAllFilters(): Flow<List<DomainFilterEntity>>
    suspend fun getActiveBlacklists(): List<DomainFilterEntity>
    suspend fun getActiveWhitelists(): List<DomainFilterEntity>
    suspend fun insertFilter(filter: DomainFilterEntity)
    suspend fun insertAll(filters: List<DomainFilterEntity>)
    suspend fun toggleFilterState(id: Long, enabled: Boolean)
    suspend fun deleteFilter(id: Long)
    suspend fun incrementHitCount(domain: String)
}

interface ThreatLogDao {
    fun getAllThreatLogs(): Flow<List<ThreatLogEntity>>
    suspend fun insertThreatLog(log: ThreatLogEntity)
    suspend fun clearAllThreatLogs()
}
