package com.example.mailinglist.data.network

import com.example.mailinglist.BuildConfig
import com.example.mailinglist.shared.Constants
import com.sun.mail.imap.IMAPStore
import jakarta.mail.Session
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*

class Store private constructor(private val mailStore: IMAPStore) {
    companion object {
        suspend operator fun invoke(): Store {
            val mailSession: Session = getSession()
            val store = mailSession.getStore(Constants.STORE_TYPE) as IMAPStore

            withContext(Dispatchers.IO) {
                connectEmailStore(store)
            }

            return Store(store)
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

    suspend fun getFolder(folderName: String): Folder {
        return Folder(mailStore, folderName)
    }
}
