package com.sharma.mapsenseassignment.data.di

import com.sharma.mapsenseassignment.data.remote.APIs
import com.sharma.mapsenseassignment.data.repository.DefaultRemoteRepository
import com.sharma.mapsenseassignment.domain.repository.RemoteRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryProvider {

    @Provides
    fun provideRemoteRepository(apIs: APIs): RemoteRepository {
        return DefaultRemoteRepository(apIs)
    }
}