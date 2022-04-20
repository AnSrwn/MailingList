package com.example.mailinglist.repository

import com.example.mailinglist.Constants
import com.example.mailinglist.Credentials
import com.example.mailinglist.model.Mail
import com.sun.mail.imap.IMAPStore
import jakarta.mail.*
import jakarta.mail.search.SearchTerm
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import java.io.IOException
import java.util.*


class MailRepository : MailService {
    override suspend fun getAllMails(): List<Mail> {
        var mails: List<Mail> = emptyList()

        try {
            val emailSession: Session = getSession()
            val emailStore: IMAPStore = emailSession.getStore(Constants.STORE_TYPE) as IMAPStore

            mails = MainScope().async {
                connectEmailStore(emailStore)
                val inboxFolder = getInboxFolder(emailStore)
                return@async getEmailsOfFolder(inboxFolder)
            }.await()

            //TODO Handle exceptions
        } catch (e: NoSuchProviderException) {
            e.printStackTrace()
        } catch (e: MessagingException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: AuthenticationFailedException) {
            e.printStackTrace()
        }

        return mails
    }

    private fun getSession(): Session {
        val props = Properties()
        props[Constants.SESSION_PROP_SSL_ENABLE] = "true"
//        props[Constants.SESSION_PROP_AUTH_MECHANISM] = "XOAUTH2"
        return Session.getDefaultInstance(props)
    }

    private suspend fun getEmailsOfFolder(
        folder: Folder
    ): List<Mail> {
        var messages: List<Message>
        var mails: List<Mail>
        withContext(Dispatchers.Default) {
//            messages = folder.messages.toList()

            val term: SearchTerm = object : SearchTerm() {
                override fun match(message: Message): Boolean {
                    try {
                        if (message.subject.contains("[abelana]")) {
                            return true
                        }
                    } catch (ex: MessagingException) {
                        ex.printStackTrace()
                    }
                    return false
                }
            }
            messages = folder.search(term).toList()

            mails = messages.map { message ->
                Mail(message)
            }
        }
        return mails
    }

    private suspend fun getInboxFolder(
        emailStore: IMAPStore
    ): Folder {
        var inboxFolder: Folder
        withContext(Dispatchers.Default) {
            inboxFolder = emailStore.getFolder(Constants.FOLDER_INBOX)
            inboxFolder.open(Folder.READ_ONLY)
        }
        return inboxFolder
    }

    private suspend fun connectEmailStore(emailStore: IMAPStore) {
        withContext(Dispatchers.Default) {
            emailStore.connect(
                Constants.IMAP_HOST_Lima,
                Constants.PORT,
                Credentials.USER_LIMA,
                Credentials.PASSWORD_LIMA
            )
        }
    }
}
