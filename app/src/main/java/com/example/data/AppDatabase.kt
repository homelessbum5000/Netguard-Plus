package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        AppNetworkRuleEntity::class,
        DomainFilterEntity::class,
        DnsQueryLogEntity::class,
        ThreatLogEntity::class,
        AvScanLogEntity::class,
        NotificationEntity::class,
        QuarantineEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun appNetworkRuleDao(): AppNetworkRuleDao
    abstract fun domainFilterDao(): DomainFilterDao
    abstract fun dnsQueryLogDao(): DnsQueryLogDao
    abstract fun threatLogDao(): ThreatLogDao
    abstract fun avScanLogDao(): AvScanLogDao
    abstract fun notificationDao(): NotificationDao
    abstract fun quarantineDao(): QuarantineDao


    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "graphene_guard_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
