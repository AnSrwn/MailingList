package com.example.mailinglist.di

import android.content.Context
import com.example.mailinglist.shared.StorageManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
annotation class StorageManager

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Singleton
    @Provides
    fun provideIoDispatcher() = Dispatchers.IO

    @Singleton
    @Provides
    @com.example.mailinglist.di.StorageManager
    fun provideStorageManager(@ApplicationContext context: Context) = StorageManager(context)
}
