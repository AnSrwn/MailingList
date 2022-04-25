package com.example.mailinglist.repository

import com.example.mailinglist.Constants
import com.example.mailinglist.model.Mail
import kotlinx.coroutines.*


class MailRepository private constructor(private val inboxFolder: Folder) : MailService {
    companion object {
        suspend operator fun invoke(): MailRepository {
            val folder = withContext(Dispatchers.IO) {
                Store().getFolder(Constants.FOLDER_INBOX)
            }

            return MailRepository(folder)
        }
    }

    override suspend fun getAllMails(): List<Mail> {
        val mails = mutableListOf<Deferred<Mail>>()

        withContext(Dispatchers.IO) {
            val messages = inboxFolder.getAllMessages()

            for (message in messages) {
                val mail = async { Mail(message) }
                mails.add(mail)
            }
        }

        return mails.awaitAll()
    }
}
