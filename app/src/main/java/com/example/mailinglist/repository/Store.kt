package com.example.mailinglist.repository

import com.example.mailinglist.BuildConfig
import com.example.mailinglist.Constants
import com.example.mailinglist.model.Mail
import com.sun.mail.imap.IMAPStore
import jakarta.mail.Folder
import jakarta.mail.Message
import jakarta.mail.MessagingException
import jakarta.mail.Session
import jakarta.mail.search.SearchTerm
import kotlinx.coroutines.*
import java.util.*

class Store {
    suspend fun getMails(
        folderName: String,
        subjectFilter: String
    ): List<Mail> {
        return MainScope().async {
            val mailStore = createStore()
            val folder = getFolder(mailStore, folderName)
            return@async getFilteredMailsOfFolder(folder, subjectFilter)
        }.await()
    }

    private suspend fun createStore(): IMAPStore {
        val mailSession: Session = getSession()
        val mailStore = mailSession.getStore(Constants.STORE_TYPE) as IMAPStore
        withContext(Dispatchers.IO) {
            connectEmailStore(mailStore)
        }

        return mailStore
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

    private suspend fun getFolder(
        emailStore: IMAPStore,
        folderName: String
    ): Folder {
        var folder: Folder
        withContext(Dispatchers.IO) {
            folder = emailStore.getFolder(folderName)
            folder.open(Folder.READ_ONLY)
        }
        return folder
    }

    private suspend fun getFilteredMailsOfFolder(
        folder: Folder,
        subjectFilter: String
    ): List<Mail> {
        var messages: List<Message>
        val mails = mutableListOf<Deferred<Mail>>()

        withContext(Dispatchers.IO) {
            val term: SearchTerm = getSearchTerm(subjectFilter)
            messages = folder.search(term).toList()

            coroutineScope {
                for (message in messages) {
                    val mail = async {
                        Mail(message)
                    }
                    mails.add(mail)
                }
            }
        }

        return mails.awaitAll()
    }

    private fun getSearchTerm(subjectFilter: String): SearchTerm {
        val term: SearchTerm = object : SearchTerm() {
            override fun match(message: Message): Boolean {
                try {
                    if (message.subject.contains(subjectFilter)) {
                        return true
                    }
                } catch (e: MessagingException) {
                    e.printStackTrace()
                }
                return false
            }
        }
        return term
    }
}
