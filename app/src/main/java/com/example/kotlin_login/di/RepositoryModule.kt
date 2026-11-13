package com.example.kotlin_login.di

import com.example.kotlin_login.data.repository.SyncPassRepositoryImpl
import com.example.kotlin_login.domain.repository.SyncPassRepository

/**
 * Módulo de Inversión de Dependencias de Repositorio (RepositoryModule).
 * Enlaza la interfaz de dominio SyncPassRepository con su implementación concreta SyncPassRepositoryImpl,
 * permitiendo que los ViewModels dependan solo de la abstracción del dominio.
 */
object RepositoryModule {

    private val repositoryInstance: SyncPassRepository by lazy {
        SyncPassRepositoryImpl(
            localDataSource = DatabaseModule.provideLocalDataSource(),
            remoteDataSource = NetworkModule.provideRemoteDataSource()
        )
    }

    fun provideSyncPassRepository(): SyncPassRepository = repositoryInstance
}
