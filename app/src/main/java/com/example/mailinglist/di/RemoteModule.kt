package com.example.mailinglist.di

import com.example.mailinglist.BuildConfig
import com.example.mailinglist.data.remote.mail.MailApi
import com.example.mailinglist.data.remote.mail.MailApiImpl
import com.example.mailinglist.data.repository.mail.MailRepository
import com.example.mailinglist.data.repository.mail.MailRepositoryImpl
import com.example.mailinglist.shared.Constants
import com.sun.mail.imap.IMAPStore
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import jakarta.mail.Session
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
annotation class MailRepository

@Qualifier
annotation class MailApi

@Qualifier
annotation class IMAPStore

@Module
@InstallIn(SingletonComponent::class)
abstract class RemoteModule {
    @Binds
    @com.example.mailinglist.di.MailRepository
    abstract fun bindMailRepository(mailRepositoryImpl: MailRepositoryImpl): MailRepository

    @Binds
    @com.example.mailinglist.di.MailApi
    abstract fun bindMailApi(mailApiImpl: MailApiImpl): MailApi

    companion object {
        @Singleton
        @Provides
        @com.example.mailinglist.di.IMAPStore
        fun provideIMAPStore(): IMAPStore {
            val mailSession: Session = getSession()
            val store = mailSession.getStore(Constants.STORE_TYPE) as IMAPStore

            runBlocking {
                connectEmailStore(store)
            }

            return store
        }

        private fun getSession(): Session {
            val props = Properties()
            props[Constants.SESSION_PROP_SSL_ENABLE] = "true"
            return Session.getDefaultInstance(props)
        }

        private suspend fun connectEmailStore(emailStore: IMAPStore) {
            withContext(Dispatchers.IO) {
                emailStore.connect(
                    Constants.IMAP_HOST_Lima,
                    Constants.PORT,
                    BuildConfig.USER,
                    BuildConfig.PASSWORD
                )
            }
        }
    }
}
