package com.example.mailinglist.repository

import com.example.mailinglist.Constants
import com.example.mailinglist.model.Mail
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.async


class MailRepository : MailService {
    override suspend fun getAllMails(): List<Mail> {
        return MainScope().async {
            val unsortedMails = Store().getMails(Constants.FOLDER_INBOX, "[abelana]")
            return@async (unsortedMails.sortedBy { mail -> mail.sentDate }).reversed()
        }.await()
    }
}
