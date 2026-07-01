package com.example.facebooksignin.di

import com.example.facebooksignin.FacebookRepository
import com.example.facebooksignin.model.AuthApi
import com.example.facebooksignin.model.SessionManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    @Provides
    @Singleton
    fun provideFacebookRepository(
        authApi: AuthApi,
        sessionManager: SessionManager
    ): FacebookRepository {
        return FacebookRepository(
            authApi = authApi,
            sessionManager = sessionManager
        )
    }
}