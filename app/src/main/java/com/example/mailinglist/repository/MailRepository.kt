package com.example.mailinglist.repository

import com.example.mailinglist.Constants
import com.example.mailinglist.model.Mail
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.async


class MailRepository : MailService {
    override suspend fun getAllMails(): List<Mail> {
        return MainScope().async {
            return@async Store().getMails(Constants.FOLDER_INBOX, "[abelana]")
        }.await()
    }
}
