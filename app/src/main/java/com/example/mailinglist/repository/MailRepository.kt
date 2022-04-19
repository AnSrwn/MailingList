package com.example.mailinglist.repository

import com.example.mailinglist.Credentials
import com.example.mailinglist.model.Mail
import com.sun.mail.imap.IMAPStore
import jakarta.mail.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.util.*


const val STORE_TYPE = "imap"
const val IMAP_HOST = "imap.gmail.com"
const val PORT = 993
const val OAUTH2_ACCESS_TOKEN = ""

class MailRepository : MailService {
    override suspend fun getAllMails(): Array<Mail> {
        val mails: Array<Mail> = emptyArray()
        try {
            // get session object
            val props = Properties()
            props["mail.imap.ssl.enable"] = "true" // required for Gmail
            props["mail.imap.auth.mechanisms"] = "XOAUTH2"
            val emailSession: Session = Session.getDefaultInstance(props)

            // authorization


            // connect with imap server
            val emailStore: IMAPStore = emailSession.getStore(STORE_TYPE) as IMAPStore
            val coroutineScope = MainScope()
            coroutineScope.launch {
                withContext(Dispatchers.IO) {
                    emailStore.connect(IMAP_HOST, PORT, Credentials.USER, OAUTH2_ACCESS_TOKEN)
                }

                // get inbox folder
                val inboxFolder = emailStore.getFolder("INBOX")
                inboxFolder.open(Folder.READ_ONLY)

                // fetch mails
                val messages: Array<Message> = inboxFolder.messages
                for ((index, message) in messages.withIndex()) {
                    mails[index] = Mail(message.subject, message.content.toString())
                }
            }
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

}
