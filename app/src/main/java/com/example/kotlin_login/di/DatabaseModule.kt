package com.example.kotlin_login.di

import com.example.kotlin_login.data.datasource.SyncPassLocalDataSource
import com.example.kotlin_login.data.datasource.SyncPassLocalDataSourceImpl
import com.example.kotlin_login.data.local.AppDatabase
import com.example.kotlin_login.data.local.SyncPassDao

/**
 * Módulo de Inversión e Inyección de Dependencias Locales (DatabaseModule).
 * Provee instancias únicas (@Singleton) de AppDatabase, SyncPassDao y SyncPassLocalDataSource.
 */
object DatabaseModule {

    private val appDatabaseInstance: AppDatabase by lazy {
        AppDatabase()
    }

    private val localDataSourceInstance: SyncPassLocalDataSource by lazy {
        SyncPassLocalDataSourceImpl(provideSyncPassDao())
    }

    fun provideAppDatabase(): AppDatabase = appDatabaseInstance

    fun provideSyncPassDao(): SyncPassDao = provideAppDatabase().syncPassDao()

    fun provideLocalDataSource(): SyncPassLocalDataSource = localDataSourceInstance
}
